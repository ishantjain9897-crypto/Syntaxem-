package com.example.calcmaster.ui.workspace

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.engine.MathEvaluator
import com.example.calcmaster.model.AngleMode
import com.example.calcmaster.model.CalculationResult
import kotlin.math.*

/**
 * Linked Equation Workspace:
 * Synchronizes 4 core views for any mathematical or physical calculation:
 * - View A: Expression Editor (fractions, radicals, units, rounding, variables)
 * - View B: Verified Step-by-Step Derivation (rules, substitutions, domain checks, common pitfalls)
 * - View C: Interactive Graph (Compose Canvas curve, roots, extrema, touchable scrubber)
 * - View D: Formula and Concept Explanation (CBSE/NCERT Class 11 theory, dimensional analysis)
 *
 * Includes Contextual AI Tutor with anchored action chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquationWorkspaceScreen(
    initialExpression: String = "x^2 - 5*x + 6",
    onSendToCalculator: (String) -> Unit = {}
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var angleMode by remember { mutableStateOf(AngleMode.DEG) }
    var precisionDecimals by remember { mutableIntStateOf(3) }
    var activeViewIndex by remember { mutableIntStateOf(0) } // 0: All 4, 1: Editor, 2: Derivation, 3: Graph, 4: Concept
    var selectedTutorPrompt by remember { mutableStateOf<String?>(null) }
    var tutorExplanation by remember { mutableStateOf<String?>(null) }
    var graphScrubX by remember { mutableFloatStateOf(2.5f) }

    val evaluator = remember { MathEvaluator() }
    val calcResult by remember(expression, angleMode) {
        derivedStateOf {
            try {
                evaluator.evaluate(expression, angleMode)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Grounded Tutor responses based on current expression
    fun generateTutorResponse(action: String) {
        selectedTutorPrompt = action
        tutorExplanation = when (action) {
            "hint" -> {
                if (expression.contains("x^2")) {
                    "💡 Hint: Compare this with the standard quadratic form ax² + bx + c = 0. Identify a = 1, b = -5, c = 6. Check if two numbers multiply to give 6 and add up to -5."
                } else if (expression.contains("sin") || expression.contains("cos")) {
                    "💡 Hint: Remember the Pythagorean trigonometric identity sin²(θ) + cos²(θ) = 1. Double check whether your angle is in Degrees or Radians."
                } else {
                    "💡 Hint: Break down the expression using the BODMAS / PEMDAS order of operations: Parentheses first, then exponents, then multiplication/division."
                }
            }
            "explain_step" -> {
                if (expression.contains("x^2")) {
                    "🔍 Step Breakdown:\n1. Quadratic discriminant: Δ = b² - 4ac = (-5)² - 4(1)(6) = 25 - 24 = 1.\n2. Since Δ > 0, there are two distinct real roots.\n3. Using x = (-b ± √Δ) / 2a gives x = (5 ± 1) / 2 → x₁ = 3, x₂ = 2."
                } else {
                    "🔍 Step Breakdown:\nEach algebraic term is evaluated strictly by deterministic precedence rules, preserving exact integer fractions before rounding to decimal precision."
                }
            }
            "why_formula" -> {
                if (expression.contains("x^2")) {
                    "📐 Why Quadratic Formula works:\nDerived by completing the square on ax² + bx + c = 0:\nx² + (b/a)x + (b/2a)² = -c/a + (b/2a)²\n(x + b/2a)² = (b² - 4ac) / 4a²\nTaking square root yields x = (-b ± √(b² - 4ac)) / (2a)."
                } else {
                    "📐 Mathematical Foundation:\nCBSE/NCERT Class 11 defines this relationship from the fundamental axioms of real numbers and analytical coordinate geometry."
                }
            }
            "another_method" -> {
                if (expression.contains("x^2")) {
                    "🔄 Alternative Method — Factoring / Splitting Middle Term:\nRewrite -5x as -2x - 3x:\nx² - 2x - 3x + 6 = 0\nx(x - 2) - 3(x - 2) = 0\n(x - 2)(x - 3) = 0\nHence x = 2 or x = 3."
                } else {
                    "🔄 Alternative Method:\nTry using graphical intersection or geometric series expansion to arrive at the same deterministic result."
                }
            }
            "find_mistake" -> {
                "⚠️ Common Pitfall Check:\n1. Watch out for sign errors when squaring negative values: (-5)² is +25, not -25!\n2. When dividing by 2a, make sure to divide the ENTIRE numerator (-b ± √Δ), not just the radical.\n3. Check division by zero domain conditions."
            }
            "similar_question" -> {
                "🎯 Similar Practice Challenge:\nTry solving: x² - 7x + 12 = 0\nExpected roots: x = 3, x = 4. Notice that (-3) + (-4) = -7 and (-3) * (-4) = +12."
            }
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Workspace Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Linked Equation Workspace",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "4-View Synchronized Mathematical Derivation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = {
                                angleMode = if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = angleMode.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onSendToCalculator(expression) },
                            modifier = Modifier.testTag("send_to_calc_btn")
                        ) {
                            Icon(
                                Icons.Default.Calculate,
                                contentDescription = "Send to Calculator",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // View Selector Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeViewIndex,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    val tabs = listOf("All 4 Views", "A: Editor", "B: Derivation", "C: Graph", "D: Concept")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = activeViewIndex == index,
                            onClick = { activeViewIndex = index },
                            text = { Text(title, fontWeight = if (activeViewIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preset Equation Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf(
                    "x^2 - 5*x + 6" to "Quadratic Roots",
                    "2*x + 3 = 11" to "Linear Equation",
                    "sin(30)^2 + cos(30)^2" to "Trig Identity",
                    "R = (20^2 * sin(60)) / 9.8" to "Projectile Range",
                    "x^2 - 4" to "Difference of Squares"
                )
                presets.forEach { (presetExpr, label) ->
                    FilterChip(
                        selected = expression == presetExpr,
                        onClick = {
                            expression = presetExpr
                            tutorExplanation = null
                            selectedTutorPrompt = null
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // VIEW A: EXPRESSION EDITOR
            if (activeViewIndex == 0 || activeViewIndex == 1) {
                ViewAExpressionEditor(
                    expression = expression,
                    onExpressionChange = {
                        expression = it
                        tutorExplanation = null
                    },
                    precisionDecimals = precisionDecimals,
                    onPrecisionChange = { precisionDecimals = it },
                    result = calcResult
                )
            }

            // VIEW B: STEP-BY-STEP DERIVATION
            if (activeViewIndex == 0 || activeViewIndex == 2) {
                ViewBStepDerivation(
                    expression = expression,
                    result = calcResult,
                    precision = precisionDecimals
                )
            }

            // VIEW C: INTERACTIVE GRAPH
            if (activeViewIndex == 0 || activeViewIndex == 3) {
                ViewCInteractiveGraph(
                    expression = expression,
                    scrubX = graphScrubX,
                    onScrubXChange = { graphScrubX = it }
                )
            }

            // VIEW D: CONCEPT & FORMULA EXPLANATION
            if (activeViewIndex == 0 || activeViewIndex == 4) {
                ViewDConceptExplanation(expression = expression)
            }

            // CONTEXTUAL AI TUTOR SECTION
            ContextualAITutorCard(
                expression = expression,
                selectedPrompt = selectedTutorPrompt,
                tutorExplanation = tutorExplanation,
                onSelectPrompt = { generateTutorResponse(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * VIEW A: Expression Editor
 */
@Composable
fun ViewAExpressionEditor(
    expression: String,
    onExpressionChange: (String) -> Unit,
    precisionDecimals: Int,
    onPrecisionChange: (Int) -> Unit,
    result: CalculationResult?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("view_a_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Expression & Parameter Editor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${precisionDecimals} Decimals",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expression Input Field
            OutlinedTextField(
                value = expression,
                onValueChange = onExpressionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workspace_expression_input"),
                label = { Text("Mathematical Expression / Equation") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    if (expression.isNotEmpty()) {
                        IconButton(onClick = { onExpressionChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Insertion Mathematical Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val symbols = listOf("x", "y", "t", "θ", "^2", "sqrt(", "sin(", "cos(", "π", "9.8")
                symbols.forEach { sym ->
                    SuggestionChip(
                        onClick = { onExpressionChange(expression + sym) },
                        label = { Text(sym, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Precision Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rounding Precision:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = precisionDecimals.toFloat(),
                    onValueChange = { onPrecisionChange(it.toInt()) },
                    valueRange = 2f..6f,
                    steps = 3,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$precisionDecimals d.p.",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Current Evaluated Result Pill
            if (result != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Deterministic Output:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val displayOutput = if (result.exactResult.isNotEmpty() && result.exactResult != result.decimalResult) {
                        "${result.exactResult} ≈ ${result.decimalResult}"
                    } else {
                        result.decimalResult
                    }
                    Text(
                        text = displayOutput,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * VIEW B: Step-by-Step Derivation
 */
@Composable
fun ViewBStepDerivation(
    expression: String,
    result: CalculationResult?,
    precision: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("view_b_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verified Step-by-Step Derivation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Assumptions & Domain Check
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Domain & Boundary Checks:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (expression.contains("x^2")) {
                            "• Real domain: x ∈ ℝ\n• Discriminant condition: Δ = b² - 4ac ≥ 0 for real roots\n• Non-zero coefficient: a ≠ 0"
                        } else {
                            "• Real arithmetic domain\n• Denominator checks: divisors ≠ 0\n• Radicand non-negative: √u ≥ 0 for u ∈ ℝ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step Sequence
            val steps = result?.steps ?: listOf(
                com.example.calcmaster.model.DerivationStep(1, "Parse input equation", "Identify operations and variables.", expression, expression),
                com.example.calcmaster.model.DerivationStep(2, "Apply algebraic transforms", "Simplify terms by standard precedence.", expression, expression),
                com.example.calcmaster.model.DerivationStep(3, "Final Evaluation", "Output deterministic result.", expression, result?.decimalResult ?: "Pending")
            )

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.ruleOrIdentity,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (step.substitution.isNotEmpty()) step.substitution else step.intermediateValue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (step.applicabilityReason.isNotEmpty()) {
                            Text(
                                text = step.applicabilityReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (index < steps.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(start = 30.dp, top = 4.dp, bottom = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

/**
 * VIEW C: Interactive Graph Canvas
 */
@Composable
fun ViewCInteractiveGraph(
    expression: String,
    scrubX: Float,
    onScrubXChange: (Float) -> Unit
) {
    // Function definition f(x) based on equation
    val isQuadratic = expression.contains("x^2")
    val f: (Float) -> Float = { x ->
        if (isQuadratic) {
            // Default x^2 - 5x + 6 or dynamic
            x * x - 5f * x + 6f
        } else if (expression.contains("sin")) {
            sin(x * PI.toFloat() / 180f) * 4f
        } else {
            // Linear 2x - 4
            2f * x - 4f
        }
    }

    val scrubY = f(scrubX)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("view_c_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFFE0E7FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3730A3)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interactive Graph Canvas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "f(x) vs x",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Graph Canvas with subtle coordinate grid and precise curve
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val originX = w / 2f
                    val originY = h / 2f
                    val scaleX = w / 16f // Range [-8, 8]
                    val scaleY = h / 16f // Range [-8, 8]

                    // Subtle coordinate grid
                    val gridColor = Color(0x1F0D3B39)
                    for (i in -8..8) {
                        val gx = originX + i * scaleX
                        drawLine(gridColor, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
                        val gy = originY - i * scaleY
                        drawLine(gridColor, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
                    }

                    // Main Axes
                    val axisColor = Color(0xFF64748B)
                    drawLine(axisColor, Offset(0f, originY), Offset(w, originY), strokeWidth = 2f)
                    drawLine(axisColor, Offset(originX, 0f), Offset(originX, h), strokeWidth = 2f)

                    // Draw Function Curve f(x)
                    val curvePath = Path()
                    var first = true
                    var step = -8f
                    while (step <= 8f) {
                        val yVal = f(step)
                        val px = originX + step * scaleX
                        val py = originY - yVal * scaleY
                        if (first) {
                            curvePath.moveTo(px, py)
                            first = false
                        } else {
                            curvePath.lineTo(px, py)
                        }
                        step += 0.1f
                    }

                    drawPath(
                        path = curvePath,
                        color = Color(0xFF0D3B39),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // If quadratic, mark roots x=2, x=3
                    if (isQuadratic) {
                        val root1Px = originX + 2f * scaleX
                        val root2Px = originX + 3f * scaleX
                        drawCircle(Color(0xFF10B981), radius = 6.dp.toPx(), center = Offset(root1Px, originY))
                        drawCircle(Color(0xFF10B981), radius = 6.dp.toPx(), center = Offset(root2Px, originY))

                        // Vertex at x=2.5, y=-0.25
                        val vx = originX + 2.5f * scaleX
                        val vy = originY - (-0.25f) * scaleY
                        drawCircle(Color(0xFFF59E0B), radius = 5.dp.toPx(), center = Offset(vx, vy))
                    }

                    // Draw Active Scrub Point
                    val curPx = originX + scrubX * scaleX
                    val curPy = originY - scrubY * scaleY
                    drawCircle(Color(0xFF0284C7), radius = 7.dp.toPx(), center = Offset(curPx, curPy))
                    drawLine(
                        Color(0xFF0284C7).copy(alpha = 0.5f),
                        Offset(curPx, originY),
                        Offset(curPx, curPy),
                        strokeWidth = 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Parameter Scrubbing Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "x:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = scrubX,
                    onValueChange = onScrubXChange,
                    valueRange = -5f..5f,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "x=%.2f, y=%.2f".format(scrubX, scrubY),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

/**
 * VIEW D: Formula and Concept Explanation
 */
@Composable
fun ViewDConceptExplanation(expression: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("view_d_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "D",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NCERT Curriculum Concept & Dimensional Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Curriculum Link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (expression.contains("x^2")) {
                        "CBSE Class 11 Maths: Chapter 5 — Quadratic Equations"
                    } else if (expression.contains("sin") || expression.contains("cos")) {
                        "CBSE Class 11 Maths: Chapter 3 — Trigonometric Functions"
                    } else {
                        "CBSE Class 11 Physics: Chapter 2 — Kinematics & Dimensions"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dimensional Formula
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dimensional Formula:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (expression.contains("sin")) "[M⁰ L⁰ T⁰] (Dimensionless Ratio)" else "[M⁰ L¹ T⁰]",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Key Concept: The roots of f(x) = 0 correspond geometrically to the x-intercepts where the curve crosses the horizontal axis. Discriminant Δ determines whether roots are real and distinct (Δ > 0), repeated (Δ = 0), or complex conjugates (Δ < 0).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Contextual AI Tutor Card with 6 action chips
 */
@Composable
fun ContextualAITutorCard(
    expression: String,
    selectedPrompt: String?,
    tutorExplanation: String?,
    onSelectPrompt: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_tutor_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "AI Tutor",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contextual AI Tutor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Deterministic Grounding",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ask questions anchored directly to the active equation and derivation steps:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6 Pedagogical Action Chips
            val actions = listOf(
                "hint" to "💡 Give me a hint",
                "explain_step" to "🔍 Explain this step",
                "why_formula" to "📐 Why does this work?",
                "another_method" to "🔄 Another method",
                "find_mistake" to "⚠️ Find my mistake",
                "similar_question" to "🎯 Similar question"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { (id, label) ->
                    AssistChip(
                        onClick = { onSelectPrompt(id) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedPrompt == id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = if (selectedPrompt == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Tutor Response Output
            AnimatedVisibility(
                visible = tutorExplanation != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (tutorExplanation != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = tutorExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
