package com.example.domain.tools

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

object NumberFormatter {
    private val symbols = DecimalFormatSymbols(Locale.US)

    fun format(value: Double, decimals: Int = 2, grouping: Boolean = true): String {
        if (value.isNaN() || value.isInfinite()) return "—"
        val pattern = buildString {
            append(if (grouping) "#,##0" else "0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("#") }
            }
        }
        val df = DecimalFormat(pattern, symbols)
        val res = df.format(value)
        return if (res == "-0") "0" else res
    }

    fun formatFixed(value: Double, decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return "—"
        val pattern = buildString {
            append("#,##0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("0") }
            }
        }
        return DecimalFormat(pattern, symbols).format(value)
    }
}

// 1. Percentage Calculator
enum class PercentageMode(val title: String, val subtitle: String) {
    PERCENT_OF("X% of Y", "Find percentage of a value"),
    WHAT_PERCENT("X is ?% of Y", "Find what percent X is of Y"),
    PERCENT_CHANGE("% Change", "Increase or decrease from X to Y")
}

data class PercentageResult(
    val primaryValue: String,
    val formulaSummary: String,
    val secondaryNote: String
)

object PercentageCalculator {
    fun calculate(mode: PercentageMode, x: Double?, y: Double?): PercentageResult? {
        if (x == null || y == null) return null
        return when (mode) {
            PercentageMode.PERCENT_OF -> {
                val res = (x / 100.0) * y
                PercentageResult(
                    primaryValue = NumberFormatter.format(res, 4),
                    formulaSummary = "${NumberFormatter.format(x, 2)}% of ${NumberFormatter.format(y, 2)} = ${NumberFormatter.format(res, 4)}",
                    secondaryNote = "Remaining value: ${NumberFormatter.format(y - res, 4)}"
                )
            }
            PercentageMode.WHAT_PERCENT -> {
                if (y == 0.0) return null
                val pct = (x / y) * 100.0
                PercentageResult(
                    primaryValue = "${NumberFormatter.format(pct, 2)}%",
                    formulaSummary = "${NumberFormatter.format(x, 2)} is ${NumberFormatter.format(pct, 2)}% of ${NumberFormatter.format(y, 2)}",
                    secondaryNote = "Ratio: ${NumberFormatter.format(x / y, 4)}"
                )
            }
            PercentageMode.PERCENT_CHANGE -> {
                if (x == 0.0) return null
                val diff = y - x
                val pct = (diff / x) * 100.0
                val direction = if (diff >= 0) "Increase" else "Decrease"
                val sign = if (pct > 0) "+" else ""
                PercentageResult(
                    primaryValue = "$sign${NumberFormatter.format(pct, 2)}%",
                    formulaSummary = "$direction from ${NumberFormatter.format(x, 2)} to ${NumberFormatter.format(y, 2)}",
                    secondaryNote = "Net difference: $sign${NumberFormatter.format(diff, 2)}"
                )
            }
        }
    }
}

// 2. Discount Calculator
data class DiscountResult(
    val finalPrice: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val effectiveSavingsPercent: Double
)

object DiscountCalculator {
    fun calculate(originalPrice: Double?, discountPercent: Double?, extraTaxPercent: Double = 0.0): DiscountResult? {
        if (originalPrice == null || discountPercent == null || originalPrice < 0) return null
        val clampedDiscount = discountPercent.coerceIn(0.0, 100.0)
        val clampedTax = extraTaxPercent.coerceAtLeast(0.0)
        val saved = originalPrice * (clampedDiscount / 100.0)
        val discountedPrice = originalPrice - saved
        val tax = discountedPrice * (clampedTax / 100.0)
        val finalPrice = discountedPrice + tax
        val effectiveSavings = if (originalPrice > 0) ((originalPrice - finalPrice) / originalPrice) * 100.0 else 0.0
        return DiscountResult(
            finalPrice = finalPrice,
            discountAmount = saved,
            taxAmount = tax,
            effectiveSavingsPercent = effectiveSavings
        )
    }
}

// 3. GST & VAT Calculator
data class GstResult(
    val netAmount: Double,
    val taxAmount: Double,
    val halfTaxAmount: Double,
    val grossTotal: Double
)

object GstCalculator {
    fun calculate(amount: Double?, ratePercent: Double?, isInclusive: Boolean): GstResult? {
        if (amount == null || ratePercent == null || amount < 0 || ratePercent < 0) return null
        return if (isInclusive) {
            val net = amount / (1.0 + ratePercent / 100.0)
            val tax = amount - net
            GstResult(
                netAmount = net,
                taxAmount = tax,
                halfTaxAmount = tax / 2.0,
                grossTotal = amount
            )
        } else {
            val tax = amount * (ratePercent / 100.0)
            val gross = amount + tax
            GstResult(
                netAmount = amount,
                taxAmount = tax,
                halfTaxAmount = tax / 2.0,
                grossTotal = gross
            )
        }
    }
}

// 4. Currency Converter (Offline-ready baseline + live refreshable rates)
data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val defaultRatePerUsd: Double
)

object CurrencyData {
    val currencies = listOf(
        CurrencyInfo("USD", "US Dollar", "$", "\uD83C\uDDFA\uD83C\uDDF8", 1.0),
        CurrencyInfo("EUR", "Euro", "€", "\uD83C\uDDEA\uD83C\uDDFA", 0.92),
        CurrencyInfo("GBP", "British Pound", "£", "\uD83C\uDDEC\uD83C\uDDE7", 0.79),
        CurrencyInfo("JPY", "Japanese Yen", "¥", "\uD83C\uDDEF\uD83C\uDDF5", 153.5),
        CurrencyInfo("INR", "Indian Rupee", "₹", "\uD83C\uDDEE\uD83C\uDDF3", 84.4),
        CurrencyInfo("BDT", "Bangladeshi Taka", "৳", "\uD83C\uDDE7\uD83C\uDDE9", 119.5),
        CurrencyInfo("AUD", "Australian Dollar", "A$", "\uD83C\uDDE6\uD83C\uDDFA", 1.53),
        CurrencyInfo("CAD", "Canadian Dollar", "C$", "\uD83C\uDDE8\uD83C\uDDE6", 1.39),
        CurrencyInfo("CHF", "Swiss Franc", "Fr", "\uD83C\uDDE8\uD83C\uDDED", 0.88),
        CurrencyInfo("CNY", "Chinese Yuan", "¥", "\uD83C\uDDE8\uD83C\uDDF3", 7.22),
        CurrencyInfo("SGD", "Singapore Dollar", "S$", "\uD83C\uDDF8\uD83C\uDDEC", 1.34),
        CurrencyInfo("AED", "UAE Dirham", "د.إ", "\uD83C\uDDE6\uD83C\uDDEA", 3.6725),
        CurrencyInfo("SAR", "Saudi Riyal", "﷼", "\uD83C\uDDF8\uD83C\uDDE6", 3.75),
        CurrencyInfo("KRW", "South Korean Won", "₩", "\uD83C\uDDF0\uD83C\uDDF7", 1395.0),
        CurrencyInfo("BRL", "Brazilian Real", "R$", "\uD83C\uDDE7\uD83C\uDDF7", 5.75),
        CurrencyInfo("MXN", "Mexican Peso", "Mex$", "\uD83C\uDDF2\uD83C\uDDFD", 20.1),
        CurrencyInfo("TRY", "Turkish Lira", "₺", "\uD83C\uDDF9\uD83C\uDDF7", 34.3),
        CurrencyInfo("ZAR", "South African Rand", "R", "\uD83C\uDDFF\uD83C\uDDE6", 18.0),
        CurrencyInfo("NZD", "New Zealand Dollar", "NZ$", "\uD83C\uDDF3\uD83C\uDDFF", 1.68),
        CurrencyInfo("HKD", "Hong Kong Dollar", "HK$", "\uD83C\uDDED\uD83C\uDDF0", 7.78),
        CurrencyInfo("SEK", "Swedish Krona", "kr", "\uD83C\uDDF8\uD83C\uDDEA", 10.85),
        CurrencyInfo("NOK", "Norwegian Krone", "kr", "\uD83C\uDDF3\uD83C\uDDF4", 11.0),
        CurrencyInfo("THB", "Thai Baht", "฿", "\uD83C\uDDF9\uD83C\uDDED", 34.4),
        CurrencyInfo("IDR", "Indonesian Rupiah", "Rp", "\uD83C\uDDEE\uD83C\uDDE9", 15850.0)
    )

    val defaultRatesMap: Map<String, Double> = currencies.associate { it.code to it.defaultRatePerUsd }

    fun convert(amount: Double, fromCode: String, toCode: String, ratesPerUsd: Map<String, Double>): Double {
        val fromRate = ratesPerUsd[fromCode] ?: 1.0
        val toRate = ratesPerUsd[toCode] ?: 1.0
        if (fromRate == 0.0) return 0.0
        return (amount / fromRate) * toRate
    }
}

// 5. Unit Converter
enum class UnitCategory(val label: String) {
    LENGTH("Length"),
    MASS("Weight"),
    TEMPERATURE("Temp"),
    AREA("Area"),
    VOLUME("Volume"),
    SPEED("Speed"),
    DATA("Data"),
    TIME("Time")
}

data class UnitDefinition(
    val id: String,
    val name: String,
    val symbol: String,
    val toBaseFactor: Double = 1.0
)

object UnitConverterLogic {
    private val unitsByCategory: Map<UnitCategory, List<UnitDefinition>> = mapOf(
        UnitCategory.LENGTH to listOf(
            UnitDefinition("m", "Meter", "m", 1.0),
            UnitDefinition("km", "Kilometer", "km", 1000.0),
            UnitDefinition("cm", "Centimeter", "cm", 0.01),
            UnitDefinition("mm", "Millimeter", "mm", 0.001),
            UnitDefinition("mi", "Mile", "mi", 1609.344),
            UnitDefinition("yd", "Yard", "yd", 0.9144),
            UnitDefinition("ft", "Foot", "ft", 0.3048),
            UnitDefinition("in", "Inch", "in", 0.0254)
        ),
        UnitCategory.MASS to listOf(
            UnitDefinition("kg", "Kilogram", "kg", 1.0),
            UnitDefinition("g", "Gram", "g", 0.001),
            UnitDefinition("mg", "Milligram", "mg", 0.000001),
            UnitDefinition("lb", "Pound", "lb", 0.45359237),
            UnitDefinition("oz", "Ounce", "oz", 0.028349523125),
            UnitDefinition("t", "Metric Ton", "t", 1000.0)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitDefinition("C", "Celsius", "°C"),
            UnitDefinition("F", "Fahrenheit", "°F"),
            UnitDefinition("K", "Kelvin", "K")
        ),
        UnitCategory.AREA to listOf(
            UnitDefinition("sqm", "Square Meter", "m²", 1.0),
            UnitDefinition("sqkm", "Square Kilometer", "km²", 1_000_000.0),
            UnitDefinition("sqft", "Square Foot", "ft²", 0.09290304),
            UnitDefinition("sqyd", "Square Yard", "yd²", 0.83612736),
            UnitDefinition("acre", "Acre", "ac", 4046.8564224),
            UnitDefinition("ha", "Hectare", "ha", 10_000.0)
        ),
        UnitCategory.VOLUME to listOf(
            UnitDefinition("l", "Liter", "L", 1.0),
            UnitDefinition("ml", "Milliliter", "mL", 0.001),
            UnitDefinition("gal", "US Gallon", "gal", 3.785411784),
            UnitDefinition("cup", "US Cup", "cup", 0.2365882365),
            UnitDefinition("floz", "Fluid Ounce", "fl oz", 0.02957352956),
            UnitDefinition("m3", "Cubic Meter", "m³", 1000.0)
        ),
        UnitCategory.SPEED to listOf(
            UnitDefinition("mps", "Meters / sec", "m/s", 1.0),
            UnitDefinition("kmh", "Kilometers / hr", "km/h", 1.0 / 3.6),
            UnitDefinition("mph", "Miles / hr", "mph", 0.44704),
            UnitDefinition("knot", "Knot", "kn", 0.514444444)
        ),
        UnitCategory.DATA to listOf(
            UnitDefinition("b", "Byte", "B", 1.0),
            UnitDefinition("kb", "Kilobyte", "KB", 1024.0),
            UnitDefinition("mb", "Megabyte", "MB", 1024.0 * 1024.0),
            UnitDefinition("gb", "Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
            UnitDefinition("tb", "Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
        ),
        UnitCategory.TIME to listOf(
            UnitDefinition("s", "Second", "s", 1.0),
            UnitDefinition("min", "Minute", "min", 60.0),
            UnitDefinition("hr", "Hour", "hr", 3600.0),
            UnitDefinition("day", "Day", "d", 86400.0),
            UnitDefinition("wk", "Week", "wk", 604800.0)
        )
    )

    fun unitsFor(category: UnitCategory): List<UnitDefinition> =
        unitsByCategory[category].orEmpty()

    fun convert(category: UnitCategory, value: Double, from: UnitDefinition, to: UnitDefinition): Double {
        if (category == UnitCategory.TEMPERATURE) {
            val celsius = when (from.id) {
                "C" -> value
                "F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            return when (to.id) {
                "C" -> celsius
                "F" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }
        val baseValue = value * from.toBaseFactor
        return baseValue / to.toBaseFactor
    }
}

// 6. Age Calculator (Calendar-based, compatible with minSdk 24+)
data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val daysUntilNextBirthday: Int,
    val nextBirthdayDayOfWeek: String
)

object AgeCalculator {
    fun calculate(birthMillis: Long, targetMillis: Long): AgeResult? {
        val birth = Calendar.getInstance().apply {
            timeInMillis = birthMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val target = Calendar.getInstance().apply {
            timeInMillis = targetMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (birth.after(target)) return null

        var years = target.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = target.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        var days = target.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val prevMonth = (target.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
            }
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }

        val diffMillis = target.timeInMillis - birth.timeInMillis
        val totalDays = diffMillis / (1000L * 60L * 60L * 24L)
        val totalWeeks = totalDays / 7L
        val totalMonths = (years * 12L) + months
        val totalHours = totalDays * 24L

        // Next birthday
        val nextBday = (birth.clone() as Calendar).apply {
            set(Calendar.YEAR, target.get(Calendar.YEAR))
            if (before(target) || (get(Calendar.MONTH) == target.get(Calendar.MONTH) && get(Calendar.DAY_OF_MONTH) == target.get(Calendar.DAY_OF_MONTH))) {
                if (before(target)) add(Calendar.YEAR, 1)
            }
        }
        val bdayDays = ((nextBday.timeInMillis - target.timeInMillis) / (1000L * 60L * 60L * 24L)).toInt()
        val dayName = nextBday.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""

        return AgeResult(
            years = years,
            months = months,
            days = days,
            totalMonths = totalMonths,
            totalWeeks = totalWeeks,
            totalDays = totalDays,
            totalHours = totalHours,
            daysUntilNextBirthday = bdayDays,
            nextBirthdayDayOfWeek = dayName
        )
    }
}

// 7. Loan & EMI Calculator
data class LoanResult(
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principalAmount: Double,
    val principalRatio: Float
)

object LoanCalculator {
    fun calculate(principal: Double?, annualRatePercent: Double?, tenureMonths: Int): LoanResult? {
        if (principal == null || annualRatePercent == null || principal <= 0 || tenureMonths <= 0 || annualRatePercent < 0) {
            return null
        }
        val monthlyRate = annualRatePercent / 12.0 / 100.0
        val emi = if (monthlyRate == 0.0) {
            principal / tenureMonths
        } else {
            val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
            principal * monthlyRate * factor / (factor - 1.0)
        }
        val totalPayment = emi * tenureMonths
        val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)
        val principalRatio = if (totalPayment > 0) (principal / totalPayment).toFloat().coerceIn(0f, 1f) else 1f
        return LoanResult(
            monthlyEmi = emi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principalAmount = principal,
            principalRatio = principalRatio
        )
    }
}

// 8. Tip Calculator
data class TipResult(
    val tipAmount: Double,
    val totalBill: Double,
    val perPersonTotal: Double,
    val perPersonTip: Double
)

object TipCalculator {
    fun calculate(billAmount: Double?, tipPercent: Double, splitPeople: Int): TipResult? {
        if (billAmount == null || billAmount < 0 || splitPeople <= 0) return null
        val clampedTip = tipPercent.coerceAtLeast(0.0)
        val tip = billAmount * (clampedTip / 100.0)
        val total = billAmount + tip
        return TipResult(
            tipAmount = tip,
            totalBill = total,
            perPersonTotal = total / splitPeople,
            perPersonTip = tip / splitPeople
        )
    }
}

// 9. BMI Calculator
enum class BmiCategory(val label: String, val advice: String) {
    UNDERWEIGHT("Underweight", "Consider nutrient-rich meals and strength training."),
    NORMAL("Healthy Weight", "Great balance! Maintain your regular activity and diet."),
    OVERWEIGHT("Overweight", "A balanced calorie deficit and cardio can help."),
    OBESE("Obese", "Consult a healthcare specialist for a tailored wellness plan.")
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory,
    val minHealthyKg: Double,
    val maxHealthyKg: Double
)

object BmiCalculator {
    fun calculateMetric(weightKg: Double?, heightCm: Double?): BmiResult? {
        if (weightKg == null || heightCm == null || weightKg <= 0 || heightCm <= 0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        val category = when {
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }
        return BmiResult(
            bmi = bmi,
            category = category,
            minHealthyKg = 18.5 * heightM * heightM,
            maxHealthyKg = 24.9 * heightM * heightM
        )
    }

    fun calculateImperial(weightLbs: Double?, feet: Double?, inches: Double?): BmiResult? {
        if (weightLbs == null || feet == null || inches == null) return null
        val totalInches = (feet * 12.0) + inches
        if (weightLbs <= 0 || totalInches <= 0) return null
        val weightKg = weightLbs * 0.45359237
        val heightCm = totalInches * 2.54
        return calculateMetric(weightKg, heightCm)
    }
}
