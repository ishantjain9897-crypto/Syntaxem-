package com.example.core.calculation

import com.example.core.calculation.evaluator.ExpressionEvaluator
import com.example.core.calculation.model.AngleMode
import com.example.core.calculation.model.Fraction
import com.example.core.calculation.model.Result
import com.example.core.calculation.parser.BinaryOperator
import com.example.core.calculation.parser.ExpressionNode
import com.example.core.calculation.parser.ExpressionParser
import org.junit.Assert.*
import org.junit.Test

class CalculationModuleTest {

    @Test
    fun `result data class stores both exact and decimal representations`() {
        val res = Result.success(
            expression = "1/2 + 1/4",
            exactValue = "3/4",
            decimalValue = 0.75,
            decimalFormatted = "0.75",
            isExact = true
        )
        assertTrue(res.isSuccess)
        assertEquals("1/2 + 1/4", res.expression)
        assertEquals("3/4", res.exactValue)
        assertEquals(0.75, res.decimalValue ?: 0.0, 0.0001)
        assertEquals("0.75", res.decimalFormatted)
        assertTrue(res.isExact)
    }

    @Test
    fun `fraction arithmetic simplifies correctly without precision loss`() {
        val f1 = Fraction(1, 3)
        val f2 = Fraction(1, 6)
        val sum = f1 + f2
        assertEquals(Fraction(1, 2), sum)
        assertEquals("1/2", sum.toExactString())
        assertEquals(0.5, sum.toDouble(), 0.0001)

        val prod = Fraction(3, 4) * Fraction(2, 3)
        assertEquals(Fraction(1, 2), prod)
    }

    @Test
    fun `parser builds AST respecting standard mathematical precedence`() {
        val parser = ExpressionParser("2 + 3 * 4")
        val ast = parser.parse()
        assertTrue(ast is ExpressionNode.BinaryOpNode)
        val root = ast as ExpressionNode.BinaryOpNode
        assertEquals(BinaryOperator.ADD, root.operator)
        assertTrue(root.left is ExpressionNode.NumberNode)
        assertEquals(2.0, (root.left as ExpressionNode.NumberNode).value, 0.0001)
        assertTrue(root.right is ExpressionNode.BinaryOpNode)
        val mult = root.right as ExpressionNode.BinaryOpNode
        assertEquals(BinaryOperator.MULTIPLY, mult.operator)
    }

    @Test
    fun `evaluator computes arithmetic with proper order of operations`() {
        val evaluator = ExpressionEvaluator()
        val result = evaluator.evaluate("2 + 3 * 4")
        assertTrue(result.isSuccess)
        assertEquals("14", result.exactValue)
        assertEquals(14.0, result.decimalValue ?: 0.0, 0.0001)
    }

    @Test
    fun `evaluator computes exact fractions for rational arithmetic`() {
        val evaluator = ExpressionEvaluator()
        val result = evaluator.evaluate("1/2 + 1/4")
        assertTrue(result.isSuccess)
        assertEquals("3/4", result.exactValue)
        assertEquals(0.75, result.decimalValue ?: 0.0, 0.0001)
        assertTrue(result.isExact)
    }

    @Test
    fun `evaluator computes exact trigonometry in degree mode`() {
        val evaluator = ExpressionEvaluator(angleMode = AngleMode.DEG)
        val resSin30 = evaluator.evaluate("sin(30)")
        assertTrue(resSin30.isSuccess)
        assertEquals("1/2", resSin30.exactValue)
        assertEquals(0.5, resSin30.decimalValue ?: 0.0, 0.0001)

        val resCos60 = evaluator.evaluate("cos(60)")
        assertTrue(resCos60.isSuccess)
        assertEquals("1/2", resCos60.exactValue)
    }

    @Test
    fun `evaluator computes exact square roots and powers`() {
        val evaluator = ExpressionEvaluator()
        val resSqrt = evaluator.evaluate("sqrt(16)")
        assertTrue(resSqrt.isSuccess)
        assertEquals("4", resSqrt.exactValue)
        assertEquals(4.0, resSqrt.decimalValue ?: 0.0, 0.0001)

        val resPow = evaluator.evaluate("2^3")
        assertTrue(resPow.isSuccess)
        assertEquals("8", resPow.exactValue)
    }

    @Test
    fun `evaluator safely handles division by zero and domain errors`() {
        val evaluator = ExpressionEvaluator()
        val divZero = evaluator.evaluate("10 / 0")
        assertFalse(divZero.isSuccess)
        assertTrue(divZero.errorMessage?.contains("Division by zero") == true)

        val negSqrt = evaluator.evaluate("sqrt(-9)")
        assertFalse(negSqrt.isSuccess)
        assertTrue(negSqrt.errorMessage?.contains("negative") == true)
    }
}
