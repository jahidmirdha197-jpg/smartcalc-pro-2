package com.example.domain.math

import com.example.data.preferences.AngleUnit
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

sealed class EvalResult {
    data class Success(val formatted: String, val rawValue: BigDecimal) : EvalResult()
    data class Error(val message: String) : EvalResult()
    data object Empty : EvalResult()
}

object MathEngine {
    private val MATH_CTX = MathContext(16, RoundingMode.HALF_UP)
    private val DISPLAY_ROUND_CTX = MathContext(12, RoundingMode.HALF_UP)

    private sealed interface Token {
        data class Number(val text: String) : Token
        data class Constant(val symbol: String) : Token
        data class Function(val name: String) : Token
        data class Operator(val symbol: Char) : Token
        data object LeftParen : Token
        data object RightParen : Token
        data object Percent : Token
        data object Factorial : Token
    }

    private data class EvalNode(
        val value: BigDecimal,
        val isBarePercent: Boolean = false
    )

    fun evaluate(
        expression: String,
        angleUnit: AngleUnit = AngleUnit.DEG,
        decimalPrecision: Int = 8,
        useThousandsSeparator: Boolean = true
    ): EvalResult {
        val cleaned = expression.trim()
        if (cleaned.isEmpty()) return EvalResult.Empty

        return try {
            val tokens = tokenize(cleaned)
            if (tokens.isEmpty()) return EvalResult.Empty
            val normalizedTokens = insertImplicitMultiplication(autoCloseParens(tokens))
            val parser = Parser(normalizedTokens, angleUnit)
            val node = parser.parseExpression()
            if (parser.hasRemaining()) {
                return EvalResult.Error("Invalid expression")
            }
            val normalizedValue = normalizePrecision(node.value)
            val formatted = formatBigDecimal(
                value = normalizedValue,
                decimalPrecision = decimalPrecision,
                useThousandsSeparator = useThousandsSeparator
            )
            EvalResult.Success(formatted, normalizedValue)
        } catch (e: MathEvalException) {
            EvalResult.Error(e.message ?: "Math Error")
        } catch (_: ArithmeticException) {
            EvalResult.Error("Cannot divide by zero")
        } catch (_: Exception) {
            EvalResult.Error("Invalid expression")
        }
    }

    fun formatNumberString(
        raw: BigDecimal,
        decimalPrecision: Int = 8,
        useThousandsSeparator: Boolean = true
    ): String = formatBigDecimal(normalizePrecision(raw), decimalPrecision, useThousandsSeparator)

    private class MathEvalException(message: String) : RuntimeException(message)

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val n = input.length

        while (i < n) {
            val ch = input[i]
            when {
                ch.isWhitespace() || ch == ',' -> {
                    i++
                }
                ch.isDigit() || ch == '.' -> {
                    val start = i
                    var hasDot = ch == '.'
                    i++
                    while (i < n) {
                        val c = input[i]
                        if (c.isDigit()) {
                            i++
                        } else if (c == '.' && !hasDot) {
                            hasDot = true
                            i++
                        } else if ((c == 'E' || c == 'e') && i + 1 < n &&
                            (input[i + 1].isDigit() || input[i + 1] == '+' || input[i + 1] == '-')
                        ) {
                            // Scientific notation literal (e.g. 1.5E-4)
                            i++
                            if (input[i] == '+' || input[i] == '-') i++
                            if (i >= n || !input[i].isDigit()) {
                                throw MathEvalException("Invalid exponent")
                            }
                            while (i < n && input[i].isDigit()) i++
                            break
                        } else {
                            break
                        }
                    }
                    val numStr = input.substring(start, i)
                    if (numStr == ".") throw MathEvalException("Invalid number")
                    tokens.add(Token.Number(if (numStr.startsWith(".")) "0$numStr" else numStr))
                }
                ch == 'π' -> {
                    tokens.add(Token.Constant("π"))
                    i++
                }
                ch == '√' -> {
                    tokens.add(Token.Function("sqrt"))
                    i++
                }
                ch == '∛' -> {
                    tokens.add(Token.Function("cbrt"))
                    i++
                }
                ch == '(' -> {
                    tokens.add(Token.LeftParen)
                    i++
                }
                ch == ')' -> {
                    tokens.add(Token.RightParen)
                    i++
                }
                ch == '%' -> {
                    tokens.add(Token.Percent)
                    i++
                }
                ch == '!' -> {
                    tokens.add(Token.Factorial)
                    i++
                }
                ch == '+' -> {
                    tokens.add(Token.Operator('+'))
                    i++
                }
                ch == '-' || ch == '−' -> {
                    tokens.add(Token.Operator('-'))
                    i++
                }
                ch == '*' || ch == '×' -> {
                    tokens.add(Token.Operator('*'))
                    i++
                }
                ch == '/' || ch == '÷' -> {
                    tokens.add(Token.Operator('/'))
                    i++
                }
                ch == '^' -> {
                    tokens.add(Token.Operator('^'))
                    i++
                }
                ch.isLetter() -> {
                    val start = i
                    while (i < n && (input[i].isLetter() || input[i].isDigit() || input[i] == '⁻' || input[i] == '¹')) {
                        i++
                    }
                    val word = input.substring(start, i)
                    when (word) {
                        "e" -> tokens.add(Token.Constant("e"))
                        "pi" -> tokens.add(Token.Constant("π"))
                        "sin", "cos", "tan",
                        "asin", "acos", "atan",
                        "sin⁻¹", "cos⁻¹", "tan⁻¹",
                        "sinh", "cosh", "tanh",
                        "ln", "log", "sqrt", "cbrt", "abs" -> tokens.add(Token.Function(word))
                        else -> throw MathEvalException("Unknown symbol: $word")
                    }
                }
                else -> throw MathEvalException("Invalid character: $ch")
            }
        }
        return tokens
    }

    private fun autoCloseParens(tokens: List<Token>): List<Token> {
        var balance = 0
        for (t in tokens) {
            when (t) {
                Token.LeftParen -> balance++
                Token.RightParen -> if (balance > 0) balance--
                else -> Unit
            }
        }
        if (balance <= 0) return tokens
        return tokens + List(balance) { Token.RightParen }
    }

    private fun insertImplicitMultiplication(tokens: List<Token>): List<Token> {
        if (tokens.isEmpty()) return tokens
        val out = ArrayList<Token>(tokens.size + 4)
        for (i in tokens.indices) {
            val current = tokens[i]
            if (i > 0) {
                val prev = tokens[i - 1]
                val prevEndsValue = prev is Token.Number ||
                    prev is Token.Constant ||
                    prev is Token.RightParen ||
                    prev is Token.Percent ||
                    prev is Token.Factorial
                val currStartsValue = current is Token.Number ||
                    current is Token.Constant ||
                    current is Token.Function ||
                    current is Token.LeftParen

                if (prevEndsValue && currStartsValue) {
                    out.add(Token.Operator('*'))
                }
            }
            out.add(current)
        }
        return out
    }

    private class Parser(
        private val tokens: List<Token>,
        private val angleUnit: AngleUnit
    ) {
        private var pos = 0

        fun hasRemaining(): Boolean = pos < tokens.size

        private fun peek(): Token? = tokens.getOrNull(pos)

        private fun consume(): Token = tokens[pos++]

        fun parseExpression(): EvalNode {
            var left = parseTerm()
            while (true) {
                val op = peek()
                if (op is Token.Operator && (op.symbol == '+' || op.symbol == '-')) {
                    consume()
                    val right = parseTerm()
                    val nextVal = if (right.isBarePercent) {
                        val delta = left.value.multiply(right.value, MATH_CTX)
                        if (op.symbol == '+') left.value.add(delta, MATH_CTX)
                        else left.value.subtract(delta, MATH_CTX)
                    } else {
                        if (op.symbol == '+') left.value.add(right.value, MATH_CTX)
                        else left.value.subtract(right.value, MATH_CTX)
                    }
                    left = EvalNode(nextVal, isBarePercent = false)
                } else {
                    break
                }
            }
            return left
        }

        private fun parseTerm(): EvalNode {
            var left = parsePower()
            while (true) {
                val op = peek()
                if (op is Token.Operator && (op.symbol == '*' || op.symbol == '/')) {
                    consume()
                    val right = parsePower()
                    val nextVal = if (op.symbol == '*') {
                        left.value.multiply(right.value, MATH_CTX)
                    } else {
                        if (right.value.compareTo(BigDecimal.ZERO) == 0) {
                            throw MathEvalException("Cannot divide by zero")
                        }
                        left.value.divide(right.value, MATH_CTX)
                    }
                    left = EvalNode(nextVal, isBarePercent = false)
                } else {
                    break
                }
            }
            return left
        }

        // Right-associative exponentiation with higher precedence than unary minus?
        // In calculators, -2^2 = -(2^2) = -4, so unary sits above or below power carefully:
        // Standard math: unary minus has lower precedence than ^, except (-2)^2.
        private fun parsePower(): EvalNode {
            val base = parseUnary()
            val op = peek()
            return if (op is Token.Operator && op.symbol == '^') {
                consume()
                val exp = parsePower()
                val result = powSafe(base.value, exp.value)
                EvalNode(result, isBarePercent = false)
            } else {
                base
            }
        }

        private fun parseUnary(): EvalNode {
            val op = peek()
            if (op is Token.Operator && (op.symbol == '+' || op.symbol == '-')) {
                consume()
                val operand = parseUnary()
                val value = if (op.symbol == '-') operand.value.negate() else operand.value
                return EvalNode(value, isBarePercent = operand.isBarePercent)
            }
            return parsePostfix()
        }

        private fun parsePostfix(): EvalNode {
            var node = parsePrimary()
            while (true) {
                when (peek()) {
                    Token.Percent -> {
                        consume()
                        val pctVal = node.value.divide(BigDecimal(100), MATH_CTX)
                        node = EvalNode(pctVal, isBarePercent = true)
                    }
                    Token.Factorial -> {
                        consume()
                        node = EvalNode(factorialSafe(node.value), isBarePercent = false)
                    }
                    else -> break
                }
            }
            return node
        }

        private fun parsePrimary(): EvalNode {
            val token = peek() ?: throw MathEvalException("Incomplete expression")
            return when (token) {
                is Token.Number -> {
                    consume()
                    val bd = token.text.toBigDecimalOrNull()
                        ?: throw MathEvalException("Invalid number")
                    EvalNode(bd)
                }
                is Token.Constant -> {
                    consume()
                    val value = when (token.symbol) {
                        "π" -> BigDecimal(Math.PI, MATH_CTX)
                        "e" -> BigDecimal(Math.E, MATH_CTX)
                        else -> throw MathEvalException("Unknown constant")
                    }
                    EvalNode(value)
                }
                is Token.Function -> {
                    consume()
                    val arg = if (peek() == Token.LeftParen) {
                        consume()
                        val inner = parseExpression()
                        if (peek() != Token.RightParen) {
                            throw MathEvalException("Mismatched brackets")
                        }
                        consume()
                        inner
                    } else {
                        parseUnary()
                    }
                    EvalNode(evalFunction(token.name, arg.value))
                }
                Token.LeftParen -> {
                    consume()
                    val inner = parseExpression()
                    if (peek() != Token.RightParen) {
                        throw MathEvalException("Mismatched brackets")
                    }
                    consume()
                    EvalNode(inner.value, isBarePercent = false)
                }
                else -> throw MathEvalException("Unexpected symbol")
            }
        }

        private fun evalFunction(name: String, arg: BigDecimal): BigDecimal {
            val d = arg.toDouble()
            if (d.isNaN() || d.isInfinite()) throw MathEvalException("Math Error")

            val rad = if (angleUnit == AngleUnit.DEG) Math.toRadians(d) else d

            val res = when (name) {
                "sin" -> snapTrig(sin(rad))
                "cos" -> snapTrig(cos(rad))
                "tan" -> {
                    val c = snapTrig(cos(rad))
                    if (c == 0.0) throw MathEvalException("Undefined")
                    snapTrig(sin(rad) / c)
                }
                "asin", "sin⁻¹" -> {
                    if (d < -1.0 || d > 1.0) throw MathEvalException("Domain Error")
                    val r = asin(d)
                    if (angleUnit == AngleUnit.DEG) Math.toDegrees(r) else r
                }
                "acos", "cos⁻¹" -> {
                    if (d < -1.0 || d > 1.0) throw MathEvalException("Domain Error")
                    val r = acos(d)
                    if (angleUnit == AngleUnit.DEG) Math.toDegrees(r) else r
                }
                "atan", "tan⁻¹" -> {
                    val r = atan(d)
                    if (angleUnit == AngleUnit.DEG) Math.toDegrees(r) else r
                }
                "sinh" -> sinh(d)
                "cosh" -> cosh(d)
                "tanh" -> tanh(d)
                "ln" -> {
                    if (d <= 0.0) throw MathEvalException("Domain Error")
                    ln(d)
                }
                "log" -> {
                    if (d <= 0.0) throw MathEvalException("Domain Error")
                    log10(d)
                }
                "sqrt" -> {
                    if (d < 0.0) throw MathEvalException("Domain Error")
                    sqrt(d)
                }
                "cbrt" -> cbrt(d)
                "abs" -> return arg.abs()
                else -> throw MathEvalException("Unknown function")
            }

            if (res.isNaN() || res.isInfinite()) throw MathEvalException("Overflow")
            return BigDecimal.valueOf(res).round(DISPLAY_ROUND_CTX)
        }

        private fun powSafe(base: BigDecimal, exp: BigDecimal): BigDecimal {
            // Exact integer power when small integer exponent
            val expPlain = exp.stripTrailingZeros()
            if (expPlain.scale() <= 0) {
                val expLong = runCatching { expPlain.longValueExact() }.getOrNull()
                if (expLong != null && expLong in -999..999) {
                    if (base.compareTo(BigDecimal.ZERO) == 0 && expLong <= 0L) {
                        if (expLong == 0L) return BigDecimal.ONE
                        throw MathEvalException("Cannot divide by zero")
                    }
                    return base.pow(expLong.toInt(), MATH_CTX)
                }
            }
            val b = base.toDouble()
            val e = exp.toDouble()
            if (b < 0 && e % 1.0 != 0.0) throw MathEvalException("Domain Error")
            val p = b.pow(e)
            if (p.isNaN() || p.isInfinite()) throw MathEvalException("Overflow")
            return BigDecimal.valueOf(p).round(DISPLAY_ROUND_CTX)
        }

        private fun factorialSafe(arg: BigDecimal): BigDecimal {
            val stripped = arg.stripTrailingZeros()
            if (stripped.scale() > 0 || stripped.signum() < 0) {
                throw MathEvalException("Invalid factorial")
            }
            val n = runCatching { stripped.intValueExact() }.getOrElse {
                throw MathEvalException("Overflow")
            }
            if (n > 170) throw MathEvalException("Overflow")
            var acc = BigDecimal.ONE
            for (i in 2..n) {
                acc = acc.multiply(BigDecimal(i), MATH_CTX)
            }
            return acc
        }

        private fun snapTrig(value: Double): Double =
            if (abs(value) < 1e-12) 0.0 else value
    }

    private fun normalizePrecision(value: BigDecimal): BigDecimal {
        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
        // Snap tiny noise around integers or clean decimals produced by transcendental ops
        val rounded = value.round(DISPLAY_ROUND_CTX).stripTrailingZeros()
        return if (rounded.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else rounded
    }

    private fun formatBigDecimal(
        value: BigDecimal,
        decimalPrecision: Int,
        useThousandsSeparator: Boolean
    ): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0"
        val absVal = value.abs()
        val maxStandard = BigDecimal("1000000000000") // 1e12
        val minStandard = BigDecimal("0.00000001")    // 1e-8

        if (absVal >= maxStandard || (absVal > BigDecimal.ZERO && absVal < minStandard)) {
            val sciSymbols = DecimalFormatSymbols(Locale.US)
            val pattern = buildString {
                append("0.")
                repeat(decimalPrecision.coerceAtMost(8)) { append("#") }
                append("E0")
            }
            return DecimalFormat(pattern, sciSymbols).format(value)
        }

        val scaled = value.setScale(decimalPrecision, RoundingMode.HALF_UP).stripTrailingZeros()
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = buildString {
            append(if (useThousandsSeparator) "#,##0" else "0")
            if (decimalPrecision > 0) {
                append(".")
                repeat(decimalPrecision) { append("#") }
            }
        }
        val df = DecimalFormat(pattern, symbols)
        df.roundingMode = RoundingMode.HALF_UP
        val formatted = df.format(scaled)
        return if (formatted == "-0") "0" else formatted
    }
}
