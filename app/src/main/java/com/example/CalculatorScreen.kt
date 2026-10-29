package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.engine.MathEvaluator
import com.example.calcmaster.model.*

/**
 * Material 3 Scientific Calculator Interface with Premium Scientific Instrument Aesthetic.
 * Includes natural expression display, coordinate grid bezel, dual exact/decimal readout,
 * and responsive tactile keypad grid for numeric and scientific operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    initialExpression: String = "",
    onNavigateToSimulation: (String) -> Unit = {},
    onNavigateToGame: (String) -> Unit = {},
    onNavigateToFeynman: (String) -> Unit = {}
) {
    var expression by remember { mutableStateOf(if (initialExpression.isNotEmpty()) initialExpression else "2 * sin(30) + 5") }
    var angleMode by remember { mutableStateOf(AngleMode.DEG) }
    var memoryValue by remember { mutableDoubleStateOf(0.0) }
    var calculationResult by remember { mutableStateOf<CalculationResult?>(null) }
    var history by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var showStepsSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }

    val evaluator = remember { MathEvaluator() }

    val appendToken = { token: String ->
        expression = if (expression == "0" && token != ".") token else expression + token
    }

    LaunchedEffect(expression, angleMode) {
        if (expression.isNotEmpty() && !expression.endsWith("=") && !expression.endsWith("+ ") && !expression.endsWith("- ") && !expression.endsWith("* ") && !expression.endsWith("/ ")) {
            calculationResult = evaluator.evaluate(expression, angleMode)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CalcMaster 2026",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "INSTRUMENT V2.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Angle Mode Toggle: DEG / RAD
                    FilterChip(
                        selected = angleMode == AngleMode.DEG,
                        onClick = {
                            angleMode = if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
                        },
                        label = {
                            Text(
                                text = angleMode.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        modifier = Modifier.testTag("angle_mode_chip")
                    )

                    // Calculation History Tape
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("history_btn")
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Calculation History Tape",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B0F19), Color(0xFF111827))
                    )
                )
        ) {
            val isTabletOrWide = maxWidth > 600.dp

            if (isTabletOrWide) {
                // Wide / Tablet Layout: Side-by-side Display and Extended Keypad
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CalculatorDisplayArea(
                            expression = expression,
                            angleMode = angleMode,
                            memoryValue = memoryValue,
                            calculationResult = calculationResult,
                            onShowSteps = { showStepsSheet = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1.2f)) {
                        CalculatorKeypadGrid(
                            onAppendToken = appendToken,
                            onClear = {
                                expression = ""
                                calculationResult = null
                            },
                            onDelete = {
                                if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            },
                            onToggleSign = {
                                expression = if (expression.startsWith("-")) expression.drop(1) else "-$expression"
                            },
                            onMemoryClear = { memoryValue = 0.0 },
                            onMemoryRecall = { appendToken(memoryValue.toString()) },
                            onMemoryAdd = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue += current
                            },
                            onMemorySub = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue -= current
                            },
                            onEvaluate = {
                                if (expression.isNotEmpty()) {
                                    val res = evaluator.evaluate(expression, angleMode)
                                    calculationResult = res
                                    if (res.isSuccess) {
                                        history = listOf(HistoryItem(expression = expression, result = res.decimalResult, conceptTag = res.conceptTag)) + history
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                // Standard Phone Layout: Precision Vertical Tier
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Display Area
                    Box(modifier = Modifier.weight(1.05f)) {
                        CalculatorDisplayArea(
                            expression = expression,
                            angleMode = angleMode,
                            memoryValue = memoryValue,
                            calculationResult = calculationResult,
                            onShowSteps = { showStepsSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Textbook Shortcut Formulas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CalculatorQuickChip(label = "Quad: x²-5x+6=0") {
                            expression = "x^2 - 5x + 6 = 0"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Proj: u=30 θ=45°") {
                            expression = "projectile u=30 theta=45"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Vec Dot: [1,2,3]·[4,5,6]") {
                            expression = "dot([1, 2, 3], [4, 5, 6])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Vec Cross: [1,0,0]×[0,1,0]") {
                            expression = "cross([1, 0, 0], [0, 1, 0])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Matrix Det: [[2,3],[1,4]]") {
                            expression = "det([[2, 3], [1, 4]])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Matrix Inv: [[1,2],[3,4]]") {
                            expression = "inv([[1, 2], [3, 4]])"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                        CalculatorQuickChip(label = "Trig: sin²+cos²") {
                            expression = "sin(45)^2 + cos(45)^2"
                            calculationResult = evaluator.evaluate(expression, angleMode)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Responsive Keypad Grid
                    Box(modifier = Modifier.weight(2.05f)) {
                        CalculatorKeypadGrid(
                            onAppendToken = appendToken,
                            onClear = {
                                expression = ""
                                calculationResult = null
                            },
                            onDelete = {
                                if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            },
                            onToggleSign = {
                                expression = if (expression.startsWith("-")) expression.drop(1) else "-$expression"
                            },
                            onMemoryClear = { memoryValue = 0.0 },
                            onMemoryRecall = { appendToken(memoryValue.toString()) },
                            onMemoryAdd = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue += current
                            },
                            onMemorySub = {
                                val current = calculationResult?.decimalResult?.toDoubleOrNull() ?: 0.0
                                memoryValue -= current
                            },
                            onEvaluate = {
                                if (expression.isNotEmpty()) {
                                    val res = evaluator.evaluate(expression, angleMode)
                                    calculationResult = res
                                    if (res.isSuccess) {
                                        history = listOf(HistoryItem(expression = expression, result = res.decimalResult, conceptTag = res.conceptTag)) + history
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Step-by-Step Derivation Bottom Sheet
    if (showStepsSheet && calculationResult != null) {
        ModalBottomSheet(
            onDismissRequest = { showStepsSheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Verified Derivation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Text(
                    text = "Deterministic Engine Step-by-Step Solution",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(14.dp))

                calculationResult?.steps?.forEach { step ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Step ${step.stepNumber}: ${step.ruleOrIdentity}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Applicability: ${step.applicabilityReason}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Text(
                                    text = step.substitution,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Result: ${step.intermediateValue}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            step.commonMistakeNote?.let { mistake ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Common Pitfall: $mistake", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // History Tape Bottom Sheet
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculation History Tape",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { history = emptyList() }) {
                            Text("Clear", color = Color(0xFFF43F5E))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recent calculations yet", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        history.forEach { item ->
                            Surface(
                                onClick = {
                                    expression = item.expression
                                    calculationResult = evaluator.evaluate(expression, angleMode)
                                    showHistorySheet = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = item.expression, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF8FAFC))
                                        item.conceptTag?.let {
                                            Text(text = it, style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                        }
                                    }
                                    Text(
                                        text = "= ${item.result}",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Display Area for Math Expressions & Calculation Results.
 */
@Composable
fun CalculatorDisplayArea(
    expression: String,
    angleMode: AngleMode,
    memoryValue: Double,
    calculationResult: CalculationResult?,
    onShowSteps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .testTag("calc_display_card")
            .drawBehind {
                // Subtle scientific millimeter coordinate grid
                val step = 20.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0x0638BDF8),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x0638BDF8),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF080C14)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Bezel Status Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = angleMode.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (memoryValue != 0.0) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "M",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "PRECISION 1e-12",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF64748B)
                )
            }

            // Natural Mathematical Expression View
            val exprScroll = rememberScrollState()
            LaunchedEffect(expression) {
                exprScroll.scrollTo(exprScroll.maxValue)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScroll),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = if (expression.isEmpty()) Color(0xFF64748B) else Color(0xFFF8FAFC),
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("calc_expression_text")
                )
            }

            // Digital LCD Result Output
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                calculationResult?.let { res ->
                    if (res.isSuccess) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (res.exactResult.isNotEmpty() && res.exactResult != res.decimalResult) {
                                Text(
                                    text = "= ${res.exactResult} ≈ ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = res.decimalResult,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.testTag("calc_result_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            res.conceptTag?.let { tag ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(tag, style = MaterialTheme.typography.labelSmall, color = Color(0xFFF8FAFC))
                                    }
                                }
                            } ?: Spacer(modifier = Modifier.width(1.dp))

                            if (res.steps.isNotEmpty()) {
                                FilledTonalButton(
                                    onClick = onShowSteps,
                                    modifier = Modifier.testTag("view_steps_btn"),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF0369A1).copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Steps (${res.steps.size})", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                }
                            }
                        }
                    } else {
                        Text(
                            text = res.errorMessage ?: "Calculation Error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF43F5E),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

/**
 * Responsive Grid Layout for Numeric and Scientific Keys.
 */
@Composable
fun CalculatorKeypadGrid(
    onAppendToken: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySub: () -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("calc_keypad"),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Scientific Row 1: sin, cos, tan, ln, log
        CalculatorKeypadRow {
            CalculatorKey(label = "sin", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("sin(") }
            CalculatorKey(label = "cos", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("cos(") }
            CalculatorKey(label = "tan", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("tan(") }
            CalculatorKey(label = "ln", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("ln(") }
            CalculatorKey(label = "log", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF818CF8)) { onAppendToken("log(") }
        }

        // Scientific Row 2: √, x^y, π, e, g
        CalculatorKeypadRow {
            CalculatorKey(label = "√", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("sqrt(") }
            CalculatorKey(label = "^", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("^") }
            CalculatorKey(label = "π", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("π") }
            CalculatorKey(label = "e", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFF38BDF8)) { onAppendToken("e") }
            CalculatorKey(label = "g", bgGradient = listOf(Color(0xFF1E2238), Color(0xFF15192C)), textColor = Color(0xFFF59E0B)) { onAppendToken("g") }
        }

        // Memory & Functions Strip: MC, MR, M+, M-, %
        CalculatorKeypadRow {
            CalculatorKey(label = "MC", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryClear() }
            CalculatorKey(label = "MR", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryRecall() }
            CalculatorKey(label = "M+", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemoryAdd() }
            CalculatorKey(label = "M-", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF94A3B8)) { onMemorySub() }
            CalculatorKey(label = "%", bgGradient = listOf(Color(0xFF182234), Color(0xFF111927)), textColor = Color(0xFF38BDF8)) { onAppendToken("%") }
        }

        // Main Keypad:
        // Row 1: AC, DEL, (, ), ÷
        CalculatorKeypadRow {
            CalculatorKey(label = "AC", bgGradient = listOf(Color(0xFFE11D48), Color(0xFFBE123C)), textColor = Color.White) { onClear() }
            CalculatorKey(label = "DEL", bgGradient = listOf(Color(0xFF334155), Color(0xFF1E293B)), textColor = Color(0xFFF8FAFC)) { onDelete() }
            CalculatorKey(label = "(", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onAppendToken("(") }
            CalculatorKey(label = ")", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onAppendToken(")") }
            CalculatorKey(label = "÷", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" / ") }
        }

        // Row 2: 7, 8, 9, !, ×
        CalculatorKeypadRow {
            CalculatorKey(label = "7", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("7") }
            CalculatorKey(label = "8", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("8") }
            CalculatorKey(label = "9", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("9") }
            CalculatorKey(label = "!", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF38BDF8)) { onAppendToken("!") }
            CalculatorKey(label = "×", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" * ") }
        }

        // Row 3: 4, 5, 6, x², -
        CalculatorKeypadRow {
            CalculatorKey(label = "4", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("4") }
            CalculatorKey(label = "5", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("5") }
            CalculatorKey(label = "6", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("6") }
            CalculatorKey(label = "x²", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF38BDF8)) { onAppendToken("^2") }
            CalculatorKey(label = "-", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" - ") }
        }

        // Row 4: 1, 2, 3, ±, +
        CalculatorKeypadRow {
            CalculatorKey(label = "1", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("1") }
            CalculatorKey(label = "2", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("2") }
            CalculatorKey(label = "3", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("3") }
            CalculatorKey(label = "±", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF162030)), textColor = Color(0xFF94A3B8)) { onToggleSign() }
            CalculatorKey(label = "+", bgGradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)), textColor = Color.White) { onAppendToken(" + ") }
        }

        // Row 5: 0, ., = (Prominent Execution Jewel Key)
        CalculatorKeypadRow {
            CalculatorKey(label = "0", modifier = Modifier.weight(2f), bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken("0") }
            CalculatorKey(label = ".", bgGradient = listOf(Color(0xFF1E293B), Color(0xFF172130)), textColor = Color.White) { onAppendToken(".") }
            CalculatorKey(
                label = "=",
                modifier = Modifier.weight(2f),
                bgGradient = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                textColor = Color.White,
                onClick = onEvaluate
            )
        }
    }
}

@Composable
fun CalculatorKeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        content = content
    )
}

@Composable
fun RowScope.CalculatorKey(
    label: String,
    modifier: Modifier = Modifier.weight(1f),
    bgGradient: List<Color>,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp) // Minimum 48dp touch target
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(bgGradient))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag("key_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = if (label.all { it.isDigit() || it == '.' }) FontFamily.Monospace else FontFamily.Default,
            color = textColor
        )
    }
}

@Composable
fun CalculatorQuickChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
