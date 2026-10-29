package com.example.orbitlab.ui.calculator

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.calculation.evaluator.ExpressionEvaluator
import com.example.core.calculation.model.AngleMode
import com.example.core.calculation.model.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitCalcScreen(
    initialExpression: String = "",
    onNavigateToSimulatorWithRange: (Double) -> Unit = {}
) {
    var expression by remember { mutableStateOf(initialExpression.ifEmpty { "25^2 * sin(2 * 45) / 9.81" }) }
    var evaluationResult by remember { mutableStateOf<Result?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var precisionDigits by remember { mutableIntStateOf(4) }
    var isAngleDegrees by remember { mutableStateOf(true) }
    var isLaboratoryWorkspaceMode by remember { mutableStateOf(false) }

    // Evaluator instance from :core:calculation
    val evaluator = remember(isAngleDegrees) {
        ExpressionEvaluator(angleMode = if (isAngleDegrees) AngleMode.DEG else AngleMode.RAD)
    }

    fun evaluateCurrent() {
        if (expression.isBlank()) {
            evaluationResult = null
            errorMessage = null
            return
        }
        try {
            val res = evaluator.evaluate(expression)
            evaluationResult = res
            errorMessage = res.errorMessage
        } catch (e: Exception) {
            errorMessage = e.message ?: "Evaluation Error"
            evaluationResult = null
        }
    }

    LaunchedEffect(expression) {
        evaluateCurrent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SCIENTIFIC CALCULATOR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = if (isLaboratoryWorkspaceMode) "Laboratory Workspace Mode" else "Compact Keypad Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Deg / Rad toggle
                    FilterChip(
                        selected = isAngleDegrees,
                        onClick = { isAngleDegrees = !isAngleDegrees },
                        label = { Text(if (isAngleDegrees) "DEG" else "RAD", fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp).testTag("btn_deg_rad")
                    )

                    // Compact vs Full Workspace Mode Toggle
                    IconButton(
                        onClick = { isLaboratoryWorkspaceMode = !isLaboratoryWorkspaceMode },
                        modifier = Modifier.testTag("btn_toggle_workspace_mode")
                    ) {
                        Icon(
                            imageVector = if (isLaboratoryWorkspaceMode) Icons.Default.ViewCompact else Icons.Default.ViewAgenda,
                            contentDescription = "Toggle Workspace Mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // 1. High-Precision Scientific LCD Display Card
            DisplayCard(
                expression = expression,
                result = evaluationResult,
                errorMessage = errorMessage,
                precision = precisionDigits,
                onExpressionChange = { expression = it },
                onClear = {
                    expression = ""
                    evaluationResult = null
                    errorMessage = null
                },
                onBackspace = {
                    if (expression.isNotEmpty()) {
                        expression = expression.dropLast(1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Physics & Laboratory Quick Presets Row
            PresetFormulasRow(
                onSelectPreset = { preset ->
                    expression = preset
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Precision Settings Bar
            PrecisionSettingsRow(
                precision = precisionDigits,
                onPrecisionChange = { precisionDigits = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Workspace View or Keypad View
            if (isLaboratoryWorkspaceMode) {
                LaboratoryWorkspaceView(
                    expression = expression,
                    result = evaluationResult,
                    onAppend = { token -> expression += token },
                    onNavigateToSim = {
                        val num = evaluationResult?.decimalValue ?: 50.0
                        onNavigateToSimulatorWithRange(num)
                    }
                )
            } else {
                CompactKeypadView(
                    onKeyClick = { key ->
                        when (key) {
                            "C" -> {
                                expression = ""
                                evaluationResult = null
                                errorMessage = null
                            }
                            "=" -> evaluateCurrent()
                            else -> expression += key
                        }
                    }
                )
            }
        }
    }
}

/**
 * LCD display card showing editable expression, exact fraction representation, and formatted decimal.
 */
@Composable
fun DisplayCard(
    expression: String,
    result: Result?,
    errorMessage: String?,
    precision: Int,
    onExpressionChange: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calc_display_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121417) // Graphite display
        ),
        border = BorderStroke(1.dp, Color(0xFF222C3A))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Action buttons row (Clear, Backspace)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "EXACT DUAL-ENGINE",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF22D3EE),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBackspace, modifier = Modifier.size(32.dp).testTag("btn_backspace")) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onClear, modifier = Modifier.size(32.dp).testTag("btn_clear_all")) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expression input
            Text(
                text = if (expression.isEmpty()) "0" else expression,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0)
                ),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth().testTag("calc_expression_text"),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Evaluated Result Display
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFF43F5E),
                        fontFamily = FontFamily.Monospace
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (result != null && result.decimalValue != null) {
                val dec = result.decimalValue!!
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                    // Exact form (e.g. 5/2 or sqrt(2))
                    if (result.exactValue.isNotBlank() && result.exactValue != dec.toString()) {
                        Text(
                            text = "= ${result.exactValue}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFF59E0B),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    // Decimal form formatted to precision digits
                    Text(
                        text = "= ${"%.${precision}f".format(dec)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF22D3EE),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.testTag("calc_result_text")
                    )
                }
            } else {
                Text(
                    text = "= 0",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF475569),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Quick preset formulas for physics calculations.
 */
@Composable
fun PresetFormulasRow(onSelectPreset: (String) -> Unit) {
    val presets = listOf(
        "Projectile Range" to "25^2 * sin(2 * 45) / 9.81",
        "Max Height" to "(25 * sin(45))^2 / (2 * 9.81)",
        "Kinetic Energy" to "0.5 * 2.5 * 18^2",
        "Gravitational Force" to "6.674e-11 * (5.97e24 * 7.34e22) / (3.84e8)^2",
        "Escape Velocity" to "sqrt(2 * 9.81 * 6.37e6)"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(presets) { (label, formula) ->
            SuggestionChip(
                onClick = { onSelectPreset(formula) },
                label = { Text(label, fontSize = 11.sp) }
            )
        }
    }
}

/**
 * Precision configuration slider and step controls.
 */
@Composable
fun PrecisionSettingsRow(
    precision: Int,
    onPrecisionChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Precision: $precision decimal places",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(2, 4, 6, 8).forEach { p ->
                val isSelected = precision == p
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onPrecisionChange(p) }
                ) {
                    Text(
                        text = "${p}dp",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

/**
 * Standard compact scientific keypad.
 */
@Composable
fun CompactKeypadView(onKeyClick: (String) -> Unit) {
    val keyRows = listOf(
        listOf("sin(", "cos(", "tan(", "^", "sqrt("),
        listOf("(", ")", "pi", "e", "/"),
        listOf("7", "8", "9", "*", "ln("),
        listOf("4", "5", "6", "-", "log("),
        listOf("1", "2", "3", "+", "="),
        listOf("C", "0", ".", "abs(", "=")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyClick(key) }
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isAction = text == "="
    val isClear = text == "C"
    val isOperator = text in listOf("+", "-", "*", "/", "^")
    val isFunction = text.endsWith("(") || text in listOf("pi", "e")

    val containerColor = when {
        isAction -> Color(0xFFD97706) // Amber equal button
        isClear -> Color(0xFFE11D48)  // Crimson clear
        isOperator -> Color(0xFF1E293B)
        isFunction -> Color(0xFF1E293B)
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isAction || isClear -> Color.White
        isOperator -> Color(0xFF22D3EE)
        isFunction -> Color(0xFFFBBF24)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() }
            .testTag("key_$text"),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (text.all { it.isDigit() || it == '.' }) FontFamily.Monospace else FontFamily.Default,
                    color = contentColor
                )
            )
        }
    }
}

/**
 * Full laboratory workspace view with variable definitions, linked simulation, and derivative steps.
 */
@Composable
fun LaboratoryWorkspaceView(
    expression: String,
    result: Result?,
    onAppend: (String) -> Unit,
    onNavigateToSim: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "VARIABLE DEFINITIONS & CONSTANTS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• g = 9.80665 m/s² (Standard terrestrial gravity)")
                    Text("• G = 6.67430 × 10⁻¹¹ N·m²/kg² (Newton's constant)")
                    Text("• c = 2.99792 × 10⁸ m/s (Speed of light)")
                    Text("• π = 3.1415926535... (Circle constant)")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LINK TO FLIGHT SIMULATOR",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Send this calculated scalar result (${result?.decimalValue ?: 0.0}) directly as the target range in the Variable Flight Simulator.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToSim,
                        modifier = Modifier.fillMaxWidth().testTag("btn_send_to_sim")
                    ) {
                        Text("Export Result to Flight Simulator")
                    }
                }
            }
        }
    }
}
