package com.example.core.calculation.evaluator

import com.example.core.calculation.model.AngleMode
import com.example.core.calculation.model.Fraction
import com.example.core.calculation.model.Result
import com.example.core.calculation.model.Step
import com.example.core.calculation.parser.*
import kotlin.math.*

/**
 * Value representation during evaluation: carries both exact fraction/surd and numerical double.
 */
data class EvalValue(
    val decimal: Double,
    val fraction: Fraction? = null,
    val exactSurd: String? = null
) {
    fun toExactString(): String {
        if (exactSurd != null) return exactSurd
        if (fraction != null) return fraction.toExactString()
        return if (decimal % 1.0 == 0.0) decimal.toLong().toString() else decimal.toString()
    }
}

/**
 * Evaluates parsed AST ExpressionNode into verified Result.
 */
class ExpressionEvaluator(
    private val angleMode: AngleMode = AngleMode.DEG,
    private val variables: Map<String, Double> = defaultConstants()
) {
    companion object {
        fun defaultConstants(): Map<String, Double> = mapOf(
            "pi" to Math.PI,
            "π" to Math.PI,
            "e" to Math.E,
            "g" to 9.80665
        )
    }

    fun evaluate(expressionString: String): Result {
        return try {
            val parser = ExpressionParser(expressionString)
            val ast = parser.parse()
            val steps = mutableListOf<Step>()
            val evalValue = evalNode(ast, steps)

            val exactStr = evalValue.toExactString()
            val decVal = evalValue.decimal
            val decFormatted = if (abs(decVal - round(decVal)) < 1e-9) {
                round(decVal).toLong().toString()
            } else {
                "%.6f".format(decVal).trimEnd('0').trimEnd('.')
            }

            Result.success(
                expression = expressionString,
                exactValue = exactStr,
                decimalValue = decVal,
                decimalFormatted = decFormatted,
                isExact = evalValue.fraction != null || evalValue.exactSurd != null,
                steps = steps
            )
        } catch (e: ArithmeticException) {
            Result.error(expressionString, e.message ?: "Arithmetic evaluation error")
        } catch (e: IllegalArgumentException) {
            Result.error(expressionString, e.message ?: "Parse error")
        } catch (e: Exception) {
            Result.error(expressionString, "Evaluation failed: ${e.message}")
        }
    }

    private fun evalNode(node: ExpressionNode, steps: MutableList<Step>): EvalValue {
        return when (node) {
            is ExpressionNode.NumberNode -> {
                EvalValue(
                    decimal = node.value,
                    fraction = node.fraction
                )
            }

            is ExpressionNode.VariableNode -> {
                val constant = variables[node.name.lowercase()]
                    ?: throw IllegalArgumentException("Undefined variable or constant: '${node.name}'")
                EvalValue(
                    decimal = constant,
                    exactSurd = if (node.name.lowercase() == "pi" || node.name == "π") "π" else if (node.name.lowercase() == "e") "e" else null
                )
            }

            is ExpressionNode.UnaryOpNode -> {
                val inner = evalNode(node.operand, steps)
                when (node.operator) {
                    UnaryOperator.PLUS -> inner
                    UnaryOperator.NEGATE -> {
                        EvalValue(
                            decimal = -inner.decimal,
                            fraction = inner.fraction?.let { Fraction(-it.numerator, it.denominator) },
                            exactSurd = inner.exactSurd?.let { "-$it" }
                        )
                    }
                }
            }

            is ExpressionNode.BinaryOpNode -> {
                val left = evalNode(node.left, steps)
                val right = evalNode(node.right, steps)

                when (node.operator) {
                    BinaryOperator.ADD -> {
                        val exactFrac = if (left.fraction != null && right.fraction != null) {
                            left.fraction + right.fraction
                        } else null
                        val dec = left.decimal + right.decimal
                        steps.add(
                            Step(
                                stepIndex = steps.size + 1,
                                description = "Addition",
                                intermediateExpression = "${left.toExactString()} + ${right.toExactString()} = ${exactFrac?.toExactString() ?: dec}",
                                ruleApplied = "Sum Axiom",
                                axiomOrIdentity = "Field Axiom of Addition in ℝ",
                                whyItApplies = "Combines real terms under commutative and associative addition.",
                                commonPitfall = "Watch out for negative signs when adding negative terms: a + (-b) = a - b."
                            )
                        )
                        EvalValue(decimal = dec, fraction = exactFrac)
                    }

                    BinaryOperator.SUBTRACT -> {
                        val exactFrac = if (left.fraction != null && right.fraction != null) {
                            left.fraction - right.fraction
                        } else null
                        val dec = left.decimal - right.decimal
                        steps.add(
                            Step(
                                stepIndex = steps.size + 1,
                                description = "Subtraction",
                                intermediateExpression = "${left.toExactString()} - ${right.toExactString()} = ${exactFrac?.toExactString() ?: dec}",
                                ruleApplied = "Difference Axiom",
                                axiomOrIdentity = "Additive Inverse: a - b = a + (-b)",
                                whyItApplies = "Transforms subtraction into addition of the negated quantity.",
                                commonPitfall = "Dropping parentheses across subtraction: -(a + b) is not equal to -a + b."
                            )
                        )
                        EvalValue(decimal = dec, fraction = exactFrac)
                    }

                    BinaryOperator.MULTIPLY -> {
                        val exactFrac = if (left.fraction != null && right.fraction != null) {
                            left.fraction * right.fraction
                        } else null
                        val dec = left.decimal * right.decimal
                        steps.add(
                            Step(
                                stepIndex = steps.size + 1,
                                description = "Multiplication",
                                intermediateExpression = "${left.toExactString()} * ${right.toExactString()} = ${exactFrac?.toExactString() ?: dec}",
                                ruleApplied = "Product Rule",
                                axiomOrIdentity = "Field Axiom of Multiplication in ℝ",
                                whyItApplies = "Multiplies magnitudes and scales exact fraction numerators/denominators.",
                                commonPitfall = "Assuming multiplication has strict precedence over division when reading left to right (BODMAS/PEMDAS rule)."
                            )
                        )
                        EvalValue(decimal = dec, fraction = exactFrac)
                    }

                    BinaryOperator.DIVIDE -> {
                        if (abs(right.decimal) < 1e-12) {
                            throw ArithmeticException("Division by zero: ${left.toExactString()} / 0 is undefined in ℝ")
                        }
                        val exactFrac = if (left.fraction != null && right.fraction != null && right.fraction.numerator != 0L) {
                            left.fraction / right.fraction
                        } else null
                        val dec = left.decimal / right.decimal
                        steps.add(
                            Step(
                                stepIndex = steps.size + 1,
                                description = "Division",
                                intermediateExpression = "${left.toExactString()} / ${right.toExactString()} = ${exactFrac?.toExactString() ?: dec}",
                                ruleApplied = "Quotient Simplification",
                                axiomOrIdentity = "Multiplicative Inverse Division: a / b = a · (1/b)",
                                whyItApplies = "Partitions the dividend into fractional parts; requires non-zero divisor.",
                                commonPitfall = "Dividing by zero or forgetting to invert the fraction when dividing fractions ((a/b)/(c/d) = (a·d)/(b·c))."
                            )
                        )
                        EvalValue(decimal = dec, fraction = exactFrac)
                    }

                    BinaryOperator.POWER -> {
                        val dec = left.decimal.pow(right.decimal)
                        val exactFrac = if (left.fraction != null && right.decimal % 1.0 == 0.0) {
                            val exp = right.decimal.toInt()
                            if (exp >= 0) {
                                Fraction(left.fraction.numerator.toDouble().pow(exp).toLong(), left.fraction.denominator.toDouble().pow(exp).toLong()).simplify()
                            } else {
                                null
                            }
                        } else null
                        steps.add(
                            Step(
                                stepIndex = steps.size + 1,
                                description = "Exponentiation",
                                intermediateExpression = "${left.toExactString()} ^ ${right.toExactString()} = ${exactFrac?.toExactString() ?: dec}",
                                ruleApplied = "Power Rule",
                                axiomOrIdentity = "Law of Indices: aⁿ = a · a · ... (n times)",
                                whyItApplies = "Computes repeated multiplication of base; higher precedence than binary multiplication.",
                                commonPitfall = "Confusing (-x)² = +x² with -x² = -(x²); order of operations evaluates powers before unary negation unless parenthesized."
                            )
                        )
                        EvalValue(decimal = dec, fraction = exactFrac)
                    }
                }
            }

            is ExpressionNode.FunctionCallNode -> {
                evalFunction(node.functionName, node.arguments, steps)
            }

            is ExpressionNode.EquationNode -> {
                // Evaluates left and right sides
                val left = evalNode(node.left, steps)
                val right = evalNode(node.right, steps)
                val diff = left.decimal - right.decimal
                EvalValue(
                    decimal = diff,
                    exactSurd = "${left.toExactString()} = ${right.toExactString()}"
                )
            }
        }
    }

    private fun evalFunction(
        name: String,
        args: List<ExpressionNode>,
        steps: MutableList<Step>
    ): EvalValue {
        val argValues = args.map { evalNode(it, steps) }
        val fn = name.lowercase()

        return when (fn) {
            "sin" -> {
                require(argValues.size == 1) { "sin takes exactly 1 argument" }
                val arg = argValues[0]
                val deg = if (angleMode == AngleMode.DEG) arg.decimal else arg.decimal * 180.0 / Math.PI
                val normalizedDeg = ((deg % 360.0) + 360.0) % 360.0

                // Check exact trigonometry
                val (exact, dec) = when {
                    abs(normalizedDeg - 0.0) < 1e-6 || abs(normalizedDeg - 180.0) < 1e-6 || abs(normalizedDeg - 360.0) < 1e-6 -> "0" to 0.0
                    abs(normalizedDeg - 30.0) < 1e-6 || abs(normalizedDeg - 150.0) < 1e-6 -> "1/2" to 0.5
                    abs(normalizedDeg - 45.0) < 1e-6 || abs(normalizedDeg - 135.0) < 1e-6 -> "√2/2" to (sqrt(2.0) / 2.0)
                    abs(normalizedDeg - 60.0) < 1e-6 || abs(normalizedDeg - 120.0) < 1e-6 -> "√3/2" to (sqrt(3.0) / 2.0)
                    abs(normalizedDeg - 90.0) < 1e-6 -> "1" to 1.0
                    abs(normalizedDeg - 210.0) < 1e-6 || abs(normalizedDeg - 330.0) < 1e-6 -> "-1/2" to -0.5
                    abs(normalizedDeg - 270.0) < 1e-6 -> "-1" to -1.0
                    else -> {
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg.decimal) else arg.decimal
                        null to sin(rad)
                    }
                }
                steps.add(
                    Step(
                        stepIndex = steps.size + 1,
                        description = "Trigonometric Sine",
                        intermediateExpression = "sin(${arg.toExactString()}) = ${exact ?: dec}",
                        ruleApplied = if (exact != null) "Exact Unit Circle Special Angle" else "Taylor Approximation",
                        axiomOrIdentity = "Unit Circle Definition: y = sin θ",
                        whyItApplies = "Maps angle θ to the vertical y-coordinate projection on a unit circle (radius = 1).",
                        commonPitfall = "Mismatching Degrees vs Radians on the calculator, or forgetting ASTC quadrant sign rules."
                    )
                )
                EvalValue(
                    decimal = dec,
                    exactSurd = exact,
                    fraction = if (exact == "1/2") Fraction(1, 2) else if (exact == "-1/2") Fraction(-1, 2) else if (exact == "0") Fraction(0, 1) else if (exact == "1") Fraction(1, 1) else null
                )
            }

            "cos" -> {
                require(argValues.size == 1) { "cos takes exactly 1 argument" }
                val arg = argValues[0]
                val deg = if (angleMode == AngleMode.DEG) arg.decimal else arg.decimal * 180.0 / Math.PI
                val normalizedDeg = ((deg % 360.0) + 360.0) % 360.0

                val (exact, dec) = when {
                    abs(normalizedDeg - 0.0) < 1e-6 || abs(normalizedDeg - 360.0) < 1e-6 -> "1" to 1.0
                    abs(normalizedDeg - 60.0) < 1e-6 || abs(normalizedDeg - 300.0) < 1e-6 -> "1/2" to 0.5
                    abs(normalizedDeg - 45.0) < 1e-6 || abs(normalizedDeg - 315.0) < 1e-6 -> "√2/2" to (sqrt(2.0) / 2.0)
                    abs(normalizedDeg - 30.0) < 1e-6 || abs(normalizedDeg - 330.0) < 1e-6 -> "√3/2" to (sqrt(3.0) / 2.0)
                    abs(normalizedDeg - 90.0) < 1e-6 || abs(normalizedDeg - 270.0) < 1e-6 -> "0" to 0.0
                    abs(normalizedDeg - 120.0) < 1e-6 || abs(normalizedDeg - 240.0) < 1e-6 -> "-1/2" to -0.5
                    abs(normalizedDeg - 180.0) < 1e-6 -> "-1" to -1.0
                    else -> {
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg.decimal) else arg.decimal
                        null to cos(rad)
                    }
                }
                steps.add(
                    Step(
                        stepIndex = steps.size + 1,
                        description = "Trigonometric Cosine",
                        intermediateExpression = "cos(${arg.toExactString()}) = ${exact ?: dec}",
                        ruleApplied = if (exact != null) "Exact Unit Circle Special Angle" else "Taylor Approximation",
                        axiomOrIdentity = "Unit Circle Definition: x = cos θ",
                        whyItApplies = "Maps angle θ to the horizontal x-coordinate projection on a unit circle (radius = 1).",
                        commonPitfall = "Assuming cos(A + B) = cos A + cos B; the cosine sum identity is cos(A+B) = cos A cos B - sin A sin B."
                    )
                )
                EvalValue(
                    decimal = dec,
                    exactSurd = exact,
                    fraction = if (exact == "1/2") Fraction(1, 2) else if (exact == "-1/2") Fraction(-1, 2) else if (exact == "0") Fraction(0, 1) else if (exact == "1") Fraction(1, 1) else null
                )
            }

            "tan" -> {
                require(argValues.size == 1) { "tan takes exactly 1 argument" }
                val arg = argValues[0]
                val deg = if (angleMode == AngleMode.DEG) arg.decimal else arg.decimal * 180.0 / Math.PI
                val normalizedDeg = ((deg % 360.0) + 360.0) % 360.0
                if (abs(normalizedDeg - 90.0) < 1e-6 || abs(normalizedDeg - 270.0) < 1e-6) {
                    throw ArithmeticException("tan(${arg.toExactString()}) is undefined (division by zero cos=0)")
                }
                val (exact, dec) = when {
                    abs(normalizedDeg - 45.0) < 1e-6 || abs(normalizedDeg - 225.0) < 1e-6 -> "1" to 1.0
                    abs(normalizedDeg - 0.0) < 1e-6 || abs(normalizedDeg - 180.0) < 1e-6 -> "0" to 0.0
                    else -> {
                        val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg.decimal) else arg.decimal
                        null to tan(rad)
                    }
                }
                EvalValue(
                    decimal = dec,
                    exactSurd = exact,
                    fraction = if (exact == "1") Fraction(1, 1) else if (exact == "0") Fraction(0, 1) else null
                )
            }

            "sqrt" -> {
                require(argValues.size == 1) { "sqrt takes exactly 1 argument" }
                val arg = argValues[0]
                if (arg.decimal < 0.0) {
                    throw ArithmeticException("Cannot take real square root of negative number: ${arg.toExactString()} < 0")
                }
                val dec = sqrt(arg.decimal)
                val isInt = abs(dec - round(dec)) < 1e-9
                val exact = if (isInt) {
                    round(dec).toLong().toString()
                } else {
                    "√${arg.toExactString()}"
                }
                EvalValue(
                    decimal = dec,
                    exactSurd = exact,
                    fraction = if (isInt) Fraction(round(dec).toLong(), 1L) else null
                )
            }

            "ln" -> {
                require(argValues.size == 1) { "ln takes exactly 1 argument" }
                val arg = argValues[0]
                if (arg.decimal <= 0.0) throw ArithmeticException("ln is only defined for x > 0")
                EvalValue(decimal = ln(arg.decimal))
            }

            "log" -> {
                require(argValues.size == 1) { "log takes exactly 1 argument" }
                val arg = argValues[0]
                if (arg.decimal <= 0.0) throw ArithmeticException("log10 is only defined for x > 0")
                EvalValue(decimal = log10(arg.decimal))
            }

            "abs" -> {
                require(argValues.size == 1) { "abs takes exactly 1 argument" }
                val arg = argValues[0]
                EvalValue(decimal = abs(arg.decimal), fraction = arg.fraction?.let { Fraction(abs(it.numerator), it.denominator) })
            }

            else -> throw IllegalArgumentException("Unsupported mathematical function: '$name'")
        }
    }
}
