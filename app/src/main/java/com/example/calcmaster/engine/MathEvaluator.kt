package com.example.calcmaster.engine

import com.example.calcmaster.model.AngleMode
import com.example.calcmaster.model.CalculationResult
import com.example.calcmaster.model.DerivationStep
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

class MathEvaluator {

    companion object {
        const val PI = Math.PI
        const val E = Math.E
        const val C_LIGHT = 299792458.0 // m/s
        const val H_PLANCK = 6.62607015e-34 // J*s
        const val G_GRAV = 6.67430e-11 // N*m^2/kg^2
        const val G_ACCEL = 9.80665 // m/s^2
    }

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): CalculationResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return CalculationResult(
                expression = expression,
                exactResult = "",
                decimalResult = "0",
                isSuccess = true
            )
        }

        // Check for quadratic equation pattern: e.g. "x^2 - 5x + 6 = 0" or "2x^2 + 4x - 6 = 0"
        if (trimmed.contains("=") || trimmed.contains("x^2") || trimmed.contains("x²")) {
            val quadResult = trySolveQuadratic(trimmed)
            if (quadResult != null) return quadResult
        }

        // Check for kinematics projectile query: e.g. "projectile u=20 theta=45"
        if (trimmed.startsWith("proj", ignoreCase = true) || trimmed.startsWith("kinematics", ignoreCase = true)) {
            val kinResult = trySolvePhysicsKinematics(trimmed)
            if (kinResult != null) return kinResult
        }

        // Check for vector operations: dot or cross product e.g. "dot([1,2,3], [4,5,6])" or "cross([1,0,0], [0,1,0])"
        if (trimmed.startsWith("dot", ignoreCase = true) || trimmed.startsWith("cross", ignoreCase = true) || trimmed.startsWith("vec", ignoreCase = true)) {
            val vecResult = trySolveVectorOperation(trimmed)
            if (vecResult != null) return vecResult
        }

        // Check for 2x2 matrix operations: e.g. "det([[1,2],[3,4]])" or "inv([[1,2],[3,4]])" or "det(1,2,3,4)"
        if (trimmed.startsWith("det", ignoreCase = true) || trimmed.startsWith("inv", ignoreCase = true) || trimmed.startsWith("matrix", ignoreCase = true)) {
            val matResult = trySolveMatrix2x2(trimmed)
            if (matResult != null) return matResult
        }

        return try {
            val tokens = tokenize(trimmed)
            val parser = Parser(tokens, angleMode)
            val (value, steps, domainWarning) = parser.parseAndEvaluate()

            val decimalStr = formatDecimal(value)
            val exactStr = formatExact(value, trimmed)

            // Determine linked concept
            var conceptTag: String? = null
            var linkedSim: String? = null
            var linkedGame: String? = null

            val lower = trimmed.lowercase()
            if (lower.contains("sin") || lower.contains("cos") || lower.contains("tan") || lower.contains("π") || lower.contains("pi")) {
                conceptTag = "Trigonometry & Unit Circle"
                linkedSim = "unit_circle"
                linkedGame = "unit_circle_quest"
            } else if (lower.contains("g") || lower.contains("9.8") || lower.contains("^2")) {
                conceptTag = "Kinematics & Motion"
                linkedSim = "projectile"
                linkedGame = "projectile_mission"
            }

            CalculationResult(
                expression = trimmed,
                exactResult = exactStr,
                decimalResult = decimalStr,
                steps = steps,
                domainNotice = domainWarning,
                conceptTag = conceptTag,
                linkedSimulation = linkedSim,
                linkedGame = linkedGame,
                isSuccess = true
            )
        } catch (e: DomainException) {
            CalculationResult(
                expression = trimmed,
                exactResult = "Undefined",
                decimalResult = "Domain Error",
                isSuccess = false,
                errorMessage = e.message
            )
        } catch (e: Exception) {
            CalculationResult(
                expression = trimmed,
                exactResult = "Error",
                decimalResult = "Syntax Error",
                isSuccess = false,
                errorMessage = e.message ?: "Invalid mathematical syntax"
            )
        }
    }

    private fun formatDecimal(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "+∞" else "-∞"
        if (abs(value - round(value)) < 1e-12) {
            return round(value).toLong().toString()
        }
        val bd = BigDecimal(value).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }

    private fun formatExact(value: Double, originalExpr: String): String {
        if (abs(value - round(value)) < 1e-12) {
            return round(value).toLong().toString()
        }
        // Test common exact fractions (e.g. 1/2, 1/3, 1/4, 3/4, 2/3, 5/6, 7/8, √2, √3, π/2...)
        for (den in 2..24) {
            val num = round(value * den).toInt()
            if (abs(value - num.toDouble() / den) < 1e-6) {
                return "$num/$den"
            }
        }
        // Test surds: e.g. value^2 is integer
        val sq = value * value
        val sqRound = round(sq)
        if (abs(sq - sqRound) < 1e-5 && sqRound > 0 && sqRound <= 100) {
            val sign = if (value < 0) "-" else ""
            return "${sign}√${sqRound.toInt()}"
        }
        return formatDecimal(value)
    }

    // Quadratic solver with structured step-by-step output
    private fun trySolveQuadratic(expr: String): CalculationResult? {
        val clean = expr.replace(" ", "").replace("=0", "")
        // Matches forms like ax^2+bx+c or ax²+bx+c
        val regex = Regex("""^([+-]?\d*\.?\d*)x[\^²]2([+-]?\d*\.?\d*)x([+-]?\d*\.?\d*)$""")
        val match = regex.find(clean) ?: return null

        val aStr = match.groupValues[1]
        val bStr = match.groupValues[2]
        val cStr = match.groupValues[3]

        val a = when (aStr) {
            "", "+" -> 1.0
            "-" -> -1.0
            else -> aStr.toDoubleOrNull() ?: 1.0
        }
        val b = when (bStr) {
            "", "+" -> 1.0
            "-" -> -1.0
            else -> bStr.toDoubleOrNull() ?: 0.0
        }
        val c = cStr.toDoubleOrNull() ?: 0.0

        if (a == 0.0) return null

        val delta = b * b - 4 * a * c
        val steps = mutableListOf<DerivationStep>()

        steps.add(
            DerivationStep(
                stepNumber = 1,
                ruleOrIdentity = "Standard Quadratic Form",
                applicabilityReason = "Equation in the form ax² + bx + c = 0",
                substitution = "Identified coefficients: a = $a, b = $b, c = $c",
                intermediateValue = "Form: $a·x² + ($b)·x + ($c) = 0",
                commonMistakeNote = "Watch the negative signs when substituting into the quadratic formula."
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 2,
                ruleOrIdentity = "Discriminant Δ = b² - 4ac",
                applicabilityReason = "Determines nature and count of real/complex roots",
                substitution = "Δ = ($b)² - 4·($a)·($c) = ${b * b} - ${4 * a * c}",
                intermediateValue = "Δ = $delta",
                commonMistakeNote = "Squaring a negative b yields a positive number (-b)² > 0."
            )
        )

        val exactRes: String
        val decimalRes: String

        if (delta > 0) {
            val sqrtDelta = sqrt(delta)
            val x1 = (-b + sqrtDelta) / (2 * a)
            val x2 = (-b - sqrtDelta) / (2 * a)
            steps.add(
                DerivationStep(
                    stepNumber = 3,
                    ruleOrIdentity = "Quadratic Formula x = (-b ± √Δ) / 2a",
                    applicabilityReason = "Since Δ > 0, there are two distinct real roots.",
                    substitution = "x = (-($b) ± √$delta) / (2·$a)",
                    intermediateValue = "x₁ = ${formatDecimal(x1)}, x₂ = ${formatDecimal(x2)}"
                )
            )
            exactRes = "x₁ = ${formatDecimal(x1)}, x₂ = ${formatDecimal(x2)}"
            decimalRes = "x ∈ {${formatDecimal(x1)}, ${formatDecimal(x2)}}"
        } else if (delta == 0.0) {
            val x = -b / (2 * a)
            steps.add(
                DerivationStep(
                    stepNumber = 3,
                    ruleOrIdentity = "Repeated Root x = -b / 2a",
                    applicabilityReason = "Since Δ = 0, exactly one real repeated root exists.",
                    substitution = "x = -($b) / (2·$a)",
                    intermediateValue = "x = ${formatDecimal(x)}"
                )
            )
            exactRes = "x = ${formatDecimal(x)}"
            decimalRes = "x = ${formatDecimal(x)} (double root)"
        } else {
            val realPart = -b / (2 * a)
            val imagPart = sqrt(-delta) / (2 * a)
            steps.add(
                DerivationStep(
                    stepNumber = 3,
                    ruleOrIdentity = "Complex Conjugate Roots x = (-b ± i√|Δ|) / 2a",
                    applicabilityReason = "Since Δ < 0, no real roots exist; roots are in the complex plane ℂ.",
                    substitution = "x = (-($b) ± i√${-delta}) / (2·$a)",
                    intermediateValue = "x = ${formatDecimal(realPart)} ± ${formatDecimal(abs(imagPart))}i"
                )
            )
            exactRes = "${formatDecimal(realPart)} ± ${formatDecimal(abs(imagPart))}i"
            decimalRes = "Complex roots: Re = ${formatDecimal(realPart)}, Im = ±${formatDecimal(abs(imagPart))}"
        }

        return CalculationResult(
            expression = expr,
            exactResult = exactRes,
            decimalResult = decimalRes,
            steps = steps,
            conceptTag = "Algebra: Quadratic Equations",
            linkedSimulation = "projectile",
            linkedGame = "algebra_arena",
            isSuccess = true
        )
    }

    private fun trySolvePhysicsKinematics(input: String): CalculationResult? {
        // e.g. "projectile u=20 theta=45"
        val lower = input.lowercase()
        val uMatch = Regex("""u\s*=\s*(\d+\.?\d*)""").find(lower)
        val thetaMatch = Regex("""theta\s*=\s*(\d+\.?\d*)""").find(lower) ?: Regex("""angle\s*=\s*(\d+\.?\d*)""").find(lower)
        val u = uMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 20.0
        val thetaDeg = thetaMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 45.0
        val thetaRad = Math.toRadians(thetaDeg)
        val g = G_ACCEL

        val range = (u * u * sin(2 * thetaRad)) / g
        val maxHeight = (u * u * sin(thetaRad).pow(2)) / (2 * g)
        val timeOfFlight = (2 * u * sin(thetaRad)) / g

        val steps = listOf(
            DerivationStep(
                stepNumber = 1,
                ruleOrIdentity = "Horizontal Range R = (u² sin 2θ) / g",
                applicabilityReason = "Ideal projectile motion launched from level ground without air drag",
                substitution = "R = ($u² · sin(2 · $thetaDeg°)) / $g = (${u * u} · ${formatDecimal(sin(2 * thetaRad))}) / $g",
                intermediateValue = "R = ${formatDecimal(range)} m",
                commonMistakeNote = "Ensure angle is converted to radians for trig calculation."
            ),
            DerivationStep(
                stepNumber = 2,
                ruleOrIdentity = "Maximum Height H = (u² sin²θ) / 2g",
                applicabilityReason = "Vertical velocity becomes zero at apex (v_y = 0)",
                substitution = "H = ($u² · (sin $thetaDeg°)²) / (2 · $g) = (${u * u} · ${formatDecimal(sin(thetaRad).pow(2))}) / ${2 * g}",
                intermediateValue = "H = ${formatDecimal(maxHeight)} m"
            ),
            DerivationStep(
                stepNumber = 3,
                ruleOrIdentity = "Time of Flight T = (2u sin θ) / g",
                applicabilityReason = "Total duration for projectile to return to initial launch elevation",
                substitution = "T = (2 · $u · sin $thetaDeg°) / $g",
                intermediateValue = "T = ${formatDecimal(timeOfFlight)} s"
            )
        )

        return CalculationResult(
            expression = "Projectile: u=${u} m/s, θ=${thetaDeg}°",
            exactResult = "R=${formatDecimal(range)}m, H=${formatDecimal(maxHeight)}m, T=${formatDecimal(timeOfFlight)}s",
            decimalResult = "Range: ${formatDecimal(range)} m | Max Height: ${formatDecimal(maxHeight)} m | Flight: ${formatDecimal(timeOfFlight)} s",
            steps = steps,
            conceptTag = "Physics: Projectile Motion (Class 11)",
            linkedSimulation = "projectile",
            linkedGame = "projectile_mission",
            isSuccess = true
        )
    }

    private fun trySolveVectorOperation(input: String): CalculationResult? {
        val numbers = Regex("""[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?""")
            .findAll(input)
            .mapNotNull { it.value.toDoubleOrNull() }
            .toList()

        val ax: Double
        val ay: Double
        val az: Double
        val bx: Double
        val by: Double
        val bz: Double

        when (numbers.size) {
            4 -> {
                ax = numbers[0]; ay = numbers[1]; az = 0.0
                bx = numbers[2]; by = numbers[3]; bz = 0.0
            }
            6 -> {
                ax = numbers[0]; ay = numbers[1]; az = numbers[2]
                bx = numbers[3]; by = numbers[4]; bz = numbers[5]
            }
            else -> return null
        }

        val isCross = input.startsWith("cross", ignoreCase = true)
        val magA = sqrt(ax * ax + ay * ay + az * az)
        val magB = sqrt(bx * bx + by * by + bz * bz)
        val dotProduct = ax * bx + ay * by + az * bz

        // Cross product: (AyBz - AzBy)i - (AxBz - AzBx)j + (AxBy - AyBx)k
        val cx = ay * bz - az * by
        val cy = az * bx - ax * bz
        val cz = ax * by - ay * bx
        val magCross = sqrt(cx * cx + cy * cy + cz * cz)

        val cosTheta = if (magA > 0 && magB > 0) (dotProduct / (magA * magB)).coerceIn(-1.0, 1.0) else 0.0
        val thetaRad = acos(cosTheta)
        val thetaDeg = Math.toDegrees(thetaRad)

        val steps = mutableListOf<DerivationStep>()

        steps.add(
            DerivationStep(
                stepNumber = 1,
                ruleOrIdentity = "Vector Identification & Magnitudes",
                applicabilityReason = "Class 11 Motion in a Plane / Vector Algebra",
                substitution = "A = (${formatDecimal(ax)}i + ${formatDecimal(ay)}j + ${formatDecimal(az)}k), B = (${formatDecimal(bx)}i + ${formatDecimal(by)}j + ${formatDecimal(bz)}k)",
                intermediateValue = "|A| = ${formatDecimal(magA)}, |B| = ${formatDecimal(magB)}"
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 2,
                ruleOrIdentity = "Scalar Dot Product A·B = AxBx + AyBy + AzBz",
                applicabilityReason = "Measures projection and collinear alignment",
                substitution = "A·B = (${formatDecimal(ax)}·${formatDecimal(bx)}) + (${formatDecimal(ay)}·${formatDecimal(by)}) + (${formatDecimal(az)}·${formatDecimal(bz)})",
                intermediateValue = "A·B = ${formatDecimal(dotProduct)}",
                commonMistakeNote = "Dot product is a scalar quantity (no unit vectors i, j, k in final result)."
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 3,
                ruleOrIdentity = "Angle Between Vectors: cos θ = (A·B) / (|A||B|)",
                applicabilityReason = "Derived from geometric definition of dot product",
                substitution = "cos θ = ${formatDecimal(dotProduct)} / (${formatDecimal(magA)} · ${formatDecimal(magB)})",
                intermediateValue = "θ = ${formatDecimal(thetaDeg)}° (${formatDecimal(thetaRad)} rad)"
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 4,
                ruleOrIdentity = "Vector Cross Product A×B",
                applicabilityReason = "Evaluated via 3×3 matrix determinant expansion along row 1",
                substitution = "det([i, j, k; ${formatDecimal(ax)}, ${formatDecimal(ay)}, ${formatDecimal(az)}; ${formatDecimal(bx)}, ${formatDecimal(by)}, ${formatDecimal(bz)}])",
                intermediateValue = "A×B = ${formatDecimal(cx)}i + ${formatDecimal(cy)}j + ${formatDecimal(cz)}k (|A×B| = ${formatDecimal(magCross)})",
                commonMistakeNote = "Cross product is anticommutative: A×B = -(B×A)."
            )
        )

        val exactRes = if (isCross) {
            "${formatDecimal(cx)}î + ${formatDecimal(cy)}ĵ + ${formatDecimal(cz)}k̂"
        } else {
            "${formatDecimal(dotProduct)} (θ = ${formatDecimal(thetaDeg)}°)"
        }

        val decimalRes = if (isCross) {
            "A×B = (${formatDecimal(cx)}, ${formatDecimal(cy)}, ${formatDecimal(cz)}) | |A×B| = ${formatDecimal(magCross)}"
        } else {
            "Dot: ${formatDecimal(dotProduct)} | θ = ${formatDecimal(thetaDeg)}° | |A| = ${formatDecimal(magA)}, |B| = ${formatDecimal(magB)}"
        }

        return CalculationResult(
            expression = input,
            exactResult = exactRes,
            decimalResult = decimalRes,
            steps = steps,
            conceptTag = "Class 11 Physics: Motion in a Plane (Vectors)",
            linkedSimulation = "projectile",
            linkedGame = "projectile_mission",
            isSuccess = true
        )
    }

    private fun trySolveMatrix2x2(input: String): CalculationResult? {
        val numbers = Regex("""[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?""")
            .findAll(input)
            .mapNotNull { it.value.toDoubleOrNull() }
            .toList()

        if (numbers.size < 4) return null
        val (a, b, c, d) = numbers.subList(0, 4)

        val det = a * d - b * c
        val trace = a + d
        val isInv = input.startsWith("inv", ignoreCase = true)

        val steps = mutableListOf<DerivationStep>()

        steps.add(
            DerivationStep(
                stepNumber = 1,
                ruleOrIdentity = "2×2 Matrix Representation",
                applicabilityReason = "Given matrix M = [[a, b], [c, d]]",
                substitution = "M = [[${formatDecimal(a)}, ${formatDecimal(b)}], [${formatDecimal(c)}, ${formatDecimal(d)}]]",
                intermediateValue = "Elements: a = ${formatDecimal(a)}, b = ${formatDecimal(b)}, c = ${formatDecimal(c)}, d = ${formatDecimal(d)}"
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 2,
                ruleOrIdentity = "Determinant det(M) = ad - bc",
                applicabilityReason = "Class 11/12 Determinant evaluation formula",
                substitution = "det(M) = (${formatDecimal(a)} · ${formatDecimal(d)}) - (${formatDecimal(b)} · ${formatDecimal(c)}) = ${formatDecimal(a * d)} - ${formatDecimal(b * c)}",
                intermediateValue = "det(M) = ${formatDecimal(det)}",
                commonMistakeNote = "Remember the subtraction sign between the main diagonal (ad) and anti-diagonal (bc)."
            )
        )

        steps.add(
            DerivationStep(
                stepNumber = 3,
                ruleOrIdentity = "Trace & Characteristic Polynomial",
                applicabilityReason = "Tr(M) = a + d, λ² - Tr(M)λ + det(M) = 0",
                substitution = "Tr(M) = ${formatDecimal(a)} + ${formatDecimal(d)} = ${formatDecimal(trace)}",
                intermediateValue = "λ² - (${formatDecimal(trace)})λ + (${formatDecimal(det)}) = 0"
            )
        )

        val exactRes: String
        val decimalRes: String

        if (abs(det) < 1e-12) {
            steps.add(
                DerivationStep(
                    stepNumber = 4,
                    ruleOrIdentity = "Invertibility Criterion",
                    applicabilityReason = "Matrix is singular because det(M) = 0",
                    substitution = "M⁻¹ does not exist (division by zero)",
                    intermediateValue = "M is Singular"
                )
            )
            exactRes = if (isInv) "Non-Invertible (Singular)" else formatDecimal(det)
            decimalRes = "det(M) = 0 (Matrix is Singular, No Inverse)"
        } else {
            val invA = d / det
            val invB = -b / det
            val invC = -c / det
            val invD = a / det

            steps.add(
                DerivationStep(
                    stepNumber = 4,
                    ruleOrIdentity = "Inverse Formula: M⁻¹ = (1/det(M)) · adj(M)",
                    applicabilityReason = "Adjugate matrix of 2×2 is [[d, -b], [-c, a]]",
                    substitution = "adj(M) = [[${formatDecimal(d)}, ${formatDecimal(-b)}], [${formatDecimal(-c)}, ${formatDecimal(a)}]]",
                    intermediateValue = "M⁻¹ = [[${formatDecimal(invA)}, ${formatDecimal(invB)}], [${formatDecimal(invC)}, ${formatDecimal(invD)}]]"
                )
            )

            exactRes = if (isInv) {
                "[[${formatDecimal(invA)}, ${formatDecimal(invB)}], [${formatDecimal(invC)}, ${formatDecimal(invD)}]]"
            } else {
                formatDecimal(det)
            }

            decimalRes = if (isInv) {
                "Inverse: [[${formatDecimal(invA)}, ${formatDecimal(invB)}], [${formatDecimal(invC)}, ${formatDecimal(invD)}]]"
            } else {
                "det(M) = ${formatDecimal(det)} | Tr(M) = ${formatDecimal(trace)}"
            }
        }

        return CalculationResult(
            expression = input,
            exactResult = exactRes,
            decimalResult = decimalRes,
            steps = steps,
            conceptTag = "Mathematics: Determinants & Matrices",
            linkedSimulation = "unit_circle",
            linkedGame = "algebra_arena",
            isSuccess = true
        )
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c in "+-*/÷×^%()!" -> {
                    tokens.add(c.toString())
                    i++
                }
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'e' || expr[i] == 'E')) {
                        if ((expr[i] == 'e' || expr[i] == 'E') && i + 1 < expr.length && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                            sb.append(expr[i])
                            sb.append(expr[i + 1])
                            i += 2
                        } else {
                            sb.append(expr[i])
                            i++
                        }
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() || c == 'π' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isLetter() || expr[i] == 'π' || expr[i].isDigit())) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                else -> {
                    tokens.add(c.toString())
                    i++
                }
            }
        }
        return tokens
    }
}

class DomainException(message: String) : RuntimeException(message)

private class Parser(
    private val tokens: List<String>,
    private val angleMode: AngleMode
) {
    private var pos = 0
    private val steps = mutableListOf<DerivationStep>()
    private var stepCount = 1
    private var domainWarning: String? = null

    fun parseAndEvaluate(): Triple<Double, List<DerivationStep>, String?> {
        val result = parseExpression()
        return Triple(result, steps, domainWarning)
    }

    private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
    private fun consume(): String = tokens[pos++]

    // expression = term (('+' | '-') term)*
    private fun parseExpression(): Double {
        var left = parseTerm()
        while (pos < tokens.size) {
            val op = peek()
            if (op == "+" || op == "-") {
                consume()
                val right = parseTerm()
                val opName = if (op == "+") "Addition" else "Subtraction"
                val res = if (op == "+") left + right else left - right
                steps.add(
                    DerivationStep(
                        stepNumber = stepCount++,
                        ruleOrIdentity = "Arithmetic $opName",
                        applicabilityReason = "Evaluated binary operation left to right",
                        substitution = "$left $op $right",
                        intermediateValue = res.toString()
                    )
                )
                left = res
            } else {
                break
            }
        }
        return left
    }

    // term = factor (('*' | '/' | '÷' | '×' | '%') factor)*
    private fun parseTerm(): Double {
        var left = parseFactor()
        while (pos < tokens.size) {
            val op = peek()
            if (op == "*" || op == "/" || op == "÷" || op == "×" || op == "%") {
                consume()
                val right = parseFactor()
                when (op) {
                    "*", "×" -> {
                        val res = left * right
                        steps.add(
                            DerivationStep(
                                stepNumber = stepCount++,
                                ruleOrIdentity = "Multiplication",
                                applicabilityReason = "Multiplication precedence over addition",
                                substitution = "$left × $right",
                                intermediateValue = res.toString()
                            )
                        )
                        left = res
                    }
                    "/", "÷" -> {
                        if (abs(right) < 1e-15) {
                            throw DomainException("Division by zero is undefined: $left ÷ 0. In real arithmetic, division by zero has no finite defined value.")
                        }
                        val res = left / right
                        steps.add(
                            DerivationStep(
                                stepNumber = stepCount++,
                                ruleOrIdentity = "Division",
                                applicabilityReason = "Division by non-zero divisor ($right ≠ 0)",
                                substitution = "$left ÷ $right",
                                intermediateValue = res.toString()
                            )
                        )
                        left = res
                    }
                    "%" -> {
                        val res = left % right
                        steps.add(
                            DerivationStep(
                                stepNumber = stepCount++,
                                ruleOrIdentity = "Modulo / Remainder",
                                applicabilityReason = "Modulo operation",
                                substitution = "$left mod $right",
                                intermediateValue = res.toString()
                            )
                        )
                        left = res
                    }
                }
            } else {
                break
            }
        }
        return left
    }

    // factor = power ('^' factor)?
    private fun parseFactor(): Double {
        var left = parseUnary()
        if (pos < tokens.size && peek() == "^") {
            consume()
            val right = parseFactor() // right-associative
            if (left < 0 && abs(right - round(right)) > 1e-10) {
                throw DomainException("Negative base $left raised to fractional power $right produces complex value outside real domain.")
            }
            val res = left.pow(right)
            steps.add(
                DerivationStep(
                    stepNumber = stepCount++,
                    ruleOrIdentity = "Exponentiation Law x^y",
                    applicabilityReason = "Evaluated power operation",
                    substitution = "$left ^ $right",
                    intermediateValue = res.toString()
                )
            )
            left = res
        }
        // Factorial check
        if (pos < tokens.size && peek() == "!") {
            consume()
            if (left < 0 || abs(left - round(left)) > 1e-10) {
                throw DomainException("Factorial is only defined for non-negative integers (n >= 0). For reals, use Gamma function Γ(n+1).")
            }
            val n = left.toInt()
            var fact = 1.0
            for (i in 2..n) fact *= i
            steps.add(
                DerivationStep(
                    stepNumber = stepCount++,
                    ruleOrIdentity = "Factorial Definition n! = 1 × 2 × ... × n",
                    applicabilityReason = "Permutations & Combinatorics evaluation",
                    substitution = "$n!",
                    intermediateValue = fact.toString()
                )
            )
            left = fact
        }
        return left
    }

    // unary = ('+' | '-') unary | primary
    private fun parseUnary(): Double {
        if (pos < tokens.size) {
            val token = peek()
            if (token == "-") {
                consume()
                val v = parseUnary()
                return -v
            } else if (token == "+") {
                consume()
                return parseUnary()
            }
        }
        return parsePrimary()
    }

    // primary = NUMBER | CONSTANT | '(' expression ')' | FUNCTION '(' expression ')'
    private fun parsePrimary(): Double {
        if (pos >= tokens.size) return 0.0
        val token = consume()

        // Parentheses
        if (token == "(") {
            val expr = parseExpression()
            if (pos < tokens.size && peek() == ")") {
                consume()
            }
            return expr
        }

        // Constants
        when (token.lowercase()) {
            "pi", "π" -> return MathEvaluator.PI
            "e" -> return MathEvaluator.E
            "c" -> return MathEvaluator.C_LIGHT
            "h" -> return MathEvaluator.H_PLANCK
            "g" -> return MathEvaluator.G_ACCEL
            "g_grav", "big_g" -> return MathEvaluator.G_GRAV
        }

        // Functions
        val lower = token.lowercase()
        if (isFunction(lower)) {
            val hasParen = pos < tokens.size && peek() == "("
            if (hasParen) consume()
            val arg = parseExpression()
            if (hasParen && pos < tokens.size && peek() == ")") consume()

            return evaluateFunction(lower, arg)
        }

        // Number
        return token.toDoubleOrNull() ?: 0.0
    }

    private fun isFunction(name: String): Boolean {
        return name in listOf("sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "ln", "log", "log10", "sqrt", "cbrt", "abs")
    }

    private fun evaluateFunction(func: String, arg: Double): Double {
        val angleRad = when (angleMode) {
            AngleMode.DEG -> Math.toRadians(arg)
            AngleMode.RAD -> arg
            AngleMode.GRAD -> arg * (PI / 200.0)
        }

        val res = when (func) {
            "sin" -> sin(angleRad)
            "cos" -> cos(angleRad)
            "tan" -> {
                if (abs(cos(angleRad)) < 1e-12) {
                    throw DomainException("tan(θ) is undefined at odd multiples of 90° (π/2) because cos(θ) = 0.")
                }
                tan(angleRad)
            }
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) {
                    throw DomainException("Domain error: arcsin(x) requires -1 ≤ x ≤ 1.")
                }
                val r = asin(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(r) else r
            }
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) {
                    throw DomainException("Domain error: arccos(x) requires -1 ≤ x ≤ 1.")
                }
                val r = acos(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(r) else r
            }
            "atan" -> {
                val r = atan(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(r) else r
            }
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "ln" -> {
                if (arg <= 0) {
                    throw DomainException("Domain error: natural logarithm ln(x) requires argument x > 0.")
                }
                ln(arg)
            }
            "log", "log10" -> {
                if (arg <= 0) {
                    throw DomainException("Domain error: log10(x) requires argument x > 0.")
                }
                log10(arg)
            }
            "sqrt" -> {
                if (arg < 0) {
                    throw DomainException("Square root of negative number √$arg has no real solution; in complex plane it is i·√${-arg}.")
                }
                sqrt(arg)
            }
            "cbrt" -> cbrt(arg)
            "abs" -> abs(arg)
            else -> arg
        }

        steps.add(
            DerivationStep(
                stepNumber = stepCount++,
                ruleOrIdentity = "Function Evaluation $func(x)",
                applicabilityReason = "Evaluated transcendental/special function ($angleMode mode)",
                substitution = "$func($arg)",
                intermediateValue = res.toString()
            )
        )

        return res
    }
}
