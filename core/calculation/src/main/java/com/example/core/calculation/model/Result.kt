package com.example.core.calculation.model

/**
 * Fundamental exact fraction representation for mathematical precision.
 * Prevents floating point drift (e.g., 1/3 + 1/6 = 1/2 rather than 0.49999999999999994).
 */
data class Fraction(
    val numerator: Long,
    val denominator: Long = 1L
) : Comparable<Fraction> {

    init {
        require(denominator != 0L) { "Denominator cannot be zero in fraction" }
    }

    private fun gcd(a: Long, b: Long): Long {
        var n1 = kotlin.math.abs(a)
        var n2 = kotlin.math.abs(b)
        while (n2 != 0L) {
            val t = n2
            n2 = n1 % n2
            n1 = t
        }
        return if (n1 == 0L) 1L else n1
    }

    fun simplify(): Fraction {
        val divisor = gcd(numerator, denominator)
        val sign = if (denominator < 0) -1 else 1
        return Fraction(
            numerator = (numerator / divisor) * sign,
            denominator = kotlin.math.abs(denominator / divisor)
        )
    }

    operator fun plus(other: Fraction): Fraction {
        val num = this.numerator * other.denominator + other.numerator * this.denominator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun minus(other: Fraction): Fraction {
        val num = this.numerator * other.denominator - other.numerator * this.denominator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun times(other: Fraction): Fraction {
        val num = this.numerator * other.numerator
        val den = this.denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun div(other: Fraction): Fraction {
        require(other.numerator != 0L) { "Division by zero fraction" }
        val num = this.numerator * other.denominator
        val den = this.denominator * other.numerator
        return Fraction(num, den).simplify()
    }

    fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

    fun toExactString(): String {
        val s = simplify()
        return if (s.denominator == 1L) {
            s.numerator.toString()
        } else {
            "${s.numerator}/${s.denominator}"
        }
    }

    override fun compareTo(other: Fraction): Int {
        val lhs = this.numerator * other.denominator
        val rhs = other.numerator * this.denominator
        return lhs.compareTo(rhs)
    }
}

/**
 * Step-by-step derivation record with rich pedagogical context.
 */
data class Step(
    val stepIndex: Int,
    val description: String,
    val intermediateExpression: String,
    val ruleApplied: String? = null,
    val axiomOrIdentity: String? = null,
    val whyItApplies: String? = null,
    val commonPitfall: String? = null
)

/**
 * Angle mode for trigonometric calculations.
 */
enum class AngleMode {
    DEG,
    RAD
}

/**
 * Calculation Result data class storing both exact and decimal representations.
 */
data class Result(
    val expression: String,
    val exactValue: String,
    val decimalValue: Double?,
    val decimalFormatted: String,
    val isExact: Boolean = true,
    val steps: List<Step> = emptyList(),
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        fun success(
            expression: String,
            exactValue: String,
            decimalValue: Double,
            decimalFormatted: String = if (decimalValue % 1.0 == 0.0) decimalValue.toLong().toString() else "%.6f".format(decimalValue).trimEnd('0').trimEnd('.'),
            isExact: Boolean = true,
            steps: List<Step> = emptyList()
        ): Result {
            return Result(
                expression = expression,
                exactValue = exactValue,
                decimalValue = decimalValue,
                decimalFormatted = decimalFormatted,
                isExact = isExact,
                steps = steps,
                isSuccess = true,
                errorMessage = null
            )
        }

        fun error(
            expression: String,
            errorMessage: String
        ): Result {
            return Result(
                expression = expression,
                exactValue = "Undefined",
                decimalValue = null,
                decimalFormatted = "Error",
                isExact = false,
                steps = emptyList(),
                isSuccess = false,
                errorMessage = errorMessage
            )
        }
    }
}
