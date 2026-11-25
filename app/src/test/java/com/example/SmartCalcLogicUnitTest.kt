package com.example

import com.example.data.preferences.AngleUnit
import com.example.domain.math.EvalResult
import com.example.domain.math.MathEngine
import com.example.domain.tools.AgeCalculator
import com.example.domain.tools.BmiCalculator
import com.example.domain.tools.BmiCategory
import com.example.domain.tools.CurrencyData
import com.example.domain.tools.DiscountCalculator
import com.example.domain.tools.GstCalculator
import com.example.domain.tools.LoanCalculator
import com.example.domain.tools.PercentageCalculator
import com.example.domain.tools.PercentageMode
import com.example.domain.tools.TipCalculator
import com.example.domain.tools.UnitCategory
import com.example.domain.tools.UnitConverterLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class SmartCalcLogicUnitTest {

    @Test
    fun standardArithmeticAndDecimalPrecision() {
        val add = MathEngine.evaluate("0.1+0.2", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("0.3", add.formatted)

        val precedence = MathEngine.evaluate("2+3×4−6÷2", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("11", precedence.formatted)

        val implicit = MathEngine.evaluate("2(3+4)", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("14", implicit.formatted)
    }

    @Test
    fun percentageAndScientificOperations() {
        val pctAdd = MathEngine.evaluate("200+10%", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("220", pctAdd.formatted)

        val pctMul = MathEngine.evaluate("200×15%", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("30", pctMul.formatted)

        val sin30 = MathEngine.evaluate("sin(30)", AngleUnit.DEG, useThousandsSeparator = false) as EvalResult.Success
        assertEquals("0.5", sin30.formatted)

        val cos90 = MathEngine.evaluate("cos(90)", AngleUnit.DEG, useThousandsSeparator = false) as EvalResult.Success
        assertEquals("0", cos90.formatted)

        val fact = MathEngine.evaluate("5!", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("120", fact.formatted)

        val logVal = MathEngine.evaluate("log(1000)", useThousandsSeparator = false) as EvalResult.Success
        assertEquals("3", logVal.formatted)
    }

    @Test
    fun safeErrorHandling() {
        val divZero = MathEngine.evaluate("10÷0")
        assertTrue(divZero is EvalResult.Error)

        val domainErr = MathEngine.evaluate("√(−4)")
        assertTrue(domainErr is EvalResult.Error)

        val tan90 = MathEngine.evaluate("tan(90)", AngleUnit.DEG)
        assertTrue(tan90 is EvalResult.Error)
    }

    @Test
    fun smartToolsCalculationsAreAccurate() {
        val pct = PercentageCalculator.calculate(PercentageMode.PERCENT_OF, 25.0, 400.0)
        assertEquals("100", pct?.primaryValue)

        val discount = DiscountCalculator.calculate(200.0, 25.0, 10.0)
        assertNotNull(discount)
        assertEquals(50.0, discount!!.discountAmount, 0.001)
        assertEquals(165.0, discount.finalPrice, 0.001)

        val gstIncl = GstCalculator.calculate(118.0, 18.0, isInclusive = true)
        assertNotNull(gstIncl)
        assertEquals(100.0, gstIncl!!.netAmount, 0.01)
        assertEquals(18.0, gstIncl.taxAmount, 0.01)

        val loan = LoanCalculator.calculate(12000.0, 0.0, 12)
        assertNotNull(loan)
        assertEquals(1000.0, loan!!.monthlyEmi, 0.01)

        val tip = TipCalculator.calculate(100.0, 20.0, 4)
        assertNotNull(tip)
        assertEquals(30.0, tip!!.perPersonTotal, 0.01)

        val bmi = BmiCalculator.calculateMetric(70.0, 175.0)
        assertNotNull(bmi)
        assertEquals(BmiCategory.NORMAL, bmi!!.category)

        val tempUnits = UnitConverterLogic.unitsFor(UnitCategory.TEMPERATURE)
        val cToF = UnitConverterLogic.convert(UnitCategory.TEMPERATURE, 100.0, tempUnits[0], tempUnits[1])
        assertEquals(212.0, cToF, 0.001)

        val usdToUsd = CurrencyData.convert(50.0, "USD", "USD", CurrencyData.defaultRatesMap)
        assertEquals(50.0, usdToUsd, 0.001)

        val birth = Calendar.getInstance().apply { set(2000, Calendar.MAY, 10) }.timeInMillis
        val target = Calendar.getInstance().apply { set(2026, Calendar.MAY, 10) }.timeInMillis
        val age = AgeCalculator.calculate(birth, target)
        assertNotNull(age)
        assertEquals(26, age!!.years)
        assertEquals(0, age.months)
        assertEquals(0, age.days)
    }
}
