package com.example.calcmaster.lumenflow.calculator

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.calcmaster.lumenflow.conceptreplay.ConceptReplayCard
import com.example.calcmaster.lumenflow.conceptreplay.ConceptRememberDialog
import com.example.calcmaster.lumenflow.conceptreplay.detectConceptTopic
import com.example.core.calculation.evaluator.ExpressionEvaluator
import com.example.core.calculation.model.AngleMode
import com.example.core.calculation.model.Result
import com.example.syntaxem.model.ConceptReplayHistoryRepository
import com.example.syntaxem.model.ConceptReplaySession
import com.example.syntaxem.model.ReplayPhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumenCalculatorScreen(
    initialExpression: String = "",
    onNavigateToLabSim: (String) -> Unit = {},
    onNavigateToLabGame: (String) -> Unit = {},
    onNavigateToLabWithRange: (Double) -> Unit = {},
    onOpenTutor: (contextTitle: String, formula: String) -> Unit = { _, _ -> }
) {
    var expression by remember { mutableStateOf(initialExpression.ifEmpty { "20^2 * sin(2 * 45) / 9.8" }) }
    var expressionHistory by remember { mutableStateOf(listOf("x^2 - 5*x + 6", "sin(30)", "20^2 * sin(90) / 9.8")) }
    var undoStack by remember { mutableStateOf(listOf<String>()) }
    var redoStack by remember { mutableStateOf(listOf<String>()) }

    var memoryValue by remember { mutableDoubleStateOf(0.0) }
    var evaluationResult by remember { mutableStateOf<Result?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAngleDegrees by remember { mutableStateOf(true) }
    var precisionDigits by remember { mutableIntStateOf(4) }
    var activeSubMode by remember { mutableStateOf(CalcSubMode.KEYPAD) }
    var showRememberDialog by remember { mutableStateOf(false) }
    var rememberConceptName by remember { mutableStateOf("") }

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
            if (res.isSuccess) {
                if (!expressionHistory.contains(expression)) {
                    expressionHistory = listOf(expression) + expressionHistory.take(15)
                }
                val topicInfo = detectConceptTopic(expression)
                ConceptReplayHistoryRepository.recordCalculation(
                    expression = expression,
                    result = res,
                    topicName = topicInfo.topicName
                )
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Evaluation Error"
            evaluationResult = null
        }
    }

    fun updateExpression(newExpr: String) {
        undoStack = undoStack + expression
        redoStack = emptyList()
        expression = newExpr
    }

    fun handleUndo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.last()
            undoStack = undoStack.dropLast(1)
            redoStack = redoStack + expression
            expression = prev
        }
    }

    fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.last()
            redoStack = redoStack.dropLast(1)
            undoStack = undoStack + expression
            expression = next
        }
    }

    LaunchedEffect(expression, isAngleDegrees) {
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
                            text = "Lumen Flow • Exact & Decimal Workspace",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Degree / Radian Toggle
                    FilterChip(
                        selected = isAngleDegrees,
                        onClick = { isAngleDegrees = !isAngleDegrees },
                        label = { Text(if (isAngleDegrees) "DEG" else "RAD", fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp).testTag("btn_lumen_deg_rad")
                    )

                    // Contextual Tutor Shortcut
                    IconButton(
                        onClick = { onOpenTutor("Calculation: $expression", expression) },
                        modifier = Modifier.testTag("btn_calc_tutor")
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = "Tutor Assistance", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Mode Selector Tabs (Keypad, Derivation Steps, History, Check My Work)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalcSubMode.values().forEach { mode ->
                    val isSelected = activeSubMode == mode
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeSubMode = mode },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // High Precision LCD Display Box
            Card(
                modifier = Modifier.fillMaxWidth().testTag("lumen_calc_display"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header row with memory indicator and undo/redo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (memoryValue != 0.0) {
                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(
                                        text = "M = ${memoryValue}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                    )
                                }
                            }
                            Text(
                                text = "EXACT ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(onClick = { handleUndo() }, enabled = undoStack.isNotEmpty(), modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { handleRedo() }, enabled = redoStack.isNotEmpty(), modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Redo, contentDescription = "Redo", modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { updateExpression(expression.dropLast(1)) }, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Backspace, contentDescription = "Backspace", modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { updateExpression("") }, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Expression input line
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("lumen_calc_expression"),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Evaluated Result line
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontFamily = FontFamily.Monospace
                            ),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (evaluationResult != null && evaluationResult!!.decimalValue != null) {
                        val dec = evaluationResult!!.decimalValue!!
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                            if (evaluationResult!!.exactValue.isNotBlank() && evaluationResult!!.exactValue != dec.toString()) {
                                Text(
                                    text = "= ${evaluationResult!!.exactValue}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = "= ${"%.${precisionDigits}f".format(dec)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.testTag("lumen_calc_result")
                            )
                        }
                    } else {
                        Text(
                            text = "= 0",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-Mode Content Area
            when (activeSubMode) {
                CalcSubMode.KEYPAD -> {
                    LumenKeypadSection(
                        onKeyClick = { key ->
                            when (key) {
                                "C" -> updateExpression("")
                                "=" -> evaluateCurrent()
                                "MC" -> memoryValue = 0.0
                                "MR" -> updateExpression(expression + memoryValue.toString())
                                "M+" -> memoryValue += (evaluationResult?.decimalValue ?: 0.0)
                                "MS" -> memoryValue = (evaluationResult?.decimalValue ?: 0.0)
                                else -> updateExpression(expression + key)
                            }
                        }
                    )
                }
                CalcSubMode.CONCEPT_REPLAY -> {
                    ConceptReplayCard(
                        expression = expression,
                        evaluationResult = evaluationResult,
                        onSeeSimulation = onNavigateToLabSim,
                        onPlayGame = onNavigateToLabGame,
                        onRemember = {
                            rememberConceptName = it
                            showRememberDialog = true
                        },
                        onOpenTutor = onOpenTutor,
                        onSeeDerivation = { activeSubMode = CalcSubMode.DERIVATION }
                    )
                }
                CalcSubMode.DERIVATION -> {
                    DerivationsView(
                        result = evaluationResult,
                        expression = expression,
                        onExportToLab = {
                            val dec = evaluationResult?.decimalValue ?: 40.0
                            onNavigateToLabWithRange(dec)
                        },
                        onNavigateToLabSim = onNavigateToLabSim,
                        onNavigateToLabGame = onNavigateToLabGame,
                        onRecordMistake = { mistakeText ->
                            rememberConceptName = mistakeText
                            showRememberDialog = true
                        }
                    )
                }
                CalcSubMode.HISTORY -> {
                    HistoryView(
                        history = expressionHistory,
                        onSelectHistory = { updateExpression(it) },
                        onClearHistory = {
                            expressionHistory = emptyList()
                            ConceptReplayHistoryRepository.clearAll()
                        }
                    )
                }
                CalcSubMode.CHECK_WORK -> {
                    CheckMyWorkView(
                        currentExpression = expression,
                        onAskTutor = { onOpenTutor("Check My Work: $expression", expression) }
                    )
                }
            }

            if (showRememberDialog) {
                ConceptRememberDialog(
                    expression = expression,
                    detectedConcept = rememberConceptName,
                    onDismiss = { showRememberDialog = false },
                    onSaved = { showRememberDialog = false }
                )
            }
        }
    }
}

enum class CalcSubMode(val label: String) {
    KEYPAD("Keypad"),
    CONCEPT_REPLAY("Concept Replay"),
    DERIVATION("Steps"),
    CHECK_WORK("Check Work"),
    HISTORY("History")
}

@Composable
fun LumenKeypadSection(onKeyClick: (String) -> Unit) {
    val keyRows = listOf(
        listOf("MC", "MR", "M+", "MS", "pi"),
        listOf("sin(", "cos(", "tan(", "^", "sqrt("),
        listOf("(", ")", "e", "/", "*"),
        listOf("7", "8", "9", "-", "ln("),
        listOf("4", "5", "6", "+", "log("),
        listOf("1", "2", "3", "0", "."),
        listOf("C", "abs(", "asin(", "acos(", "=")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                row.forEach { key ->
                    val isAction = key == "="
                    val isClear = key == "C"
                    val isMemory = key.startsWith("M")
                    val isOp = key in listOf("+", "-", "*", "/", "^")

                    val containerColor = when {
                        isAction -> MaterialTheme.colorScheme.primary
                        isClear -> MaterialTheme.colorScheme.errorContainer
                        isMemory -> MaterialTheme.colorScheme.secondaryContainer
                        isOp -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val textColor = when {
                        isAction -> MaterialTheme.colorScheme.onPrimary
                        isClear -> MaterialTheme.colorScheme.onErrorContainer
                        isMemory -> MaterialTheme.colorScheme.onSecondaryContainer
                        isOp -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clickable { onKeyClick(key) }
                            .testTag("key_$key"),
                        shape = RoundedCornerShape(8.dp),
                        color = containerColor,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = if (key.all { it.isDigit() || it == '.' }) FontFamily.Monospace else FontFamily.Default,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class DerivationExplanationMode(val label: String, val tag: String) {
    HINT("Hint", "btn_mode_hint"),
    OUTLINE("Outline", "btn_mode_outline"),
    FULL_SOLUTION("Full Solution", "btn_mode_full")
}

@Composable
fun DerivationsView(
    result: Result?,
    expression: String,
    onExportToLab: () -> Unit,
    onNavigateToLabSim: (String) -> Unit = {},
    onNavigateToLabGame: (String) -> Unit = {},
    onRecordMistake: (String) -> Unit = {}
) {
    var explanationMode by remember { mutableStateOf(DerivationExplanationMode.FULL_SOLUTION) }
    var expandedStepIndices by remember { mutableStateOf(setOf<Int>()) }
    val topic = remember(expression) { detectConceptTopic(expression) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mode Selector Bar (Hint, Outline, Full Solution)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "EXPLANATION MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DerivationExplanationMode.values().forEach { mode ->
                            val isSelected = explanationMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { explanationMode = mode }
                                    .testTag(mode.tag),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = mode.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mode Content Rendering
        when (explanationMode) {
            DerivationExplanationMode.HINT -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("derivation_hint_section"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFDB043), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GUIDED HINTS (Zero Spoilers)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "Think through these structured prompts before looking at the complete mathematical solution.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Hint 1: Order of Operations & Operator Binding",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = "In '$expression', identify which operation has the highest precedence: evaluate functions or exponents first before division or addition.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Hint 2: Special Values & Curriculum Identities",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = "For ${topic.topicName}, recall: ${topic.keyFormula}. Check whether your trigonometric angle is a standard special angle (0°, 30°, 45°, 60°, 90°).",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Hint 3: Invariants & Domain Constraints",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = "Verify whether denominators remain non-zero and square roots take non-negative real radicands under ℝ.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            DerivationExplanationMode.OUTLINE -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("derivation_outline_section"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SOLUTION ROADMAP & MILESTONES",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "High-level milestones needed to simplify and solve this mathematical expression.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Milestone 1
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(text = "M1", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Expression Parsing & Operator Hierarchy", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "Analyze AST branches for terms: '$expression'", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            if (result != null && result.steps.isNotEmpty()) {
                                result.steps.forEach { step ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                            Text(text = "M${step.stepIndex + 1}", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = "${step.description}: Apply ${step.ruleApplied ?: "transformation"}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                            Text(text = step.intermediateExpression, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }

                            // Final Milestone
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF192A24)) {
                                    Text(text = "END", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2ED8A7)))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Reconcile Exact & Decimal Result", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "= ${result?.exactValue ?: "Exact"} (~${result?.decimalFormatted ?: "0"})", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color(0xFF2ED8A7))
                                }
                            }
                        }
                    }
                }
            }

            DerivationExplanationMode.FULL_SOLUTION -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("derivation_full_solution_section"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VERIFIED STEP-BY-STEP DERIVATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF192A24)) {
                                    Text(
                                        text = "100% Deterministic",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color(0xFF2ED8A7))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            if (result != null && result.steps.isNotEmpty()) {
                                result.steps.forEach { step ->
                                    val isExpanded = expandedStepIndices.contains(step.stepIndex)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                                        Text(
                                                            text = "${step.stepIndex}",
                                                            color = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = step.description, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                }

                                                // Why this step? button
                                                OutlinedButton(
                                                    onClick = {
                                                        expandedStepIndices = if (isExpanded) {
                                                            expandedStepIndices - step.stepIndex
                                                        } else {
                                                            expandedStepIndices + step.stepIndex
                                                        }
                                                    },
                                                    modifier = Modifier.testTag("btn_why_step_${step.stepIndex}"),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.HelpOutline,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(text = if (isExpanded) "Hide Why" else "Why this step?", fontSize = 10.sp)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = step.intermediateExpression,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )

                                            // "Why this step?" Expanded Explanation Card
                                            AnimatedVisibility(visible = isExpanded) {
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 8.dp)
                                                        .testTag("card_why_step_${step.stepIndex}"),
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MaterialTheme.colorScheme.surface,
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        // Axiom / Identity
                                                        Row(verticalAlignment = Alignment.Top) {
                                                            Text(text = "Axiom/Rule: ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                                            Text(
                                                                text = step.axiomOrIdentity ?: (step.ruleApplied ?: "Mathematical Axiom in ℝ"),
                                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                                            )
                                                        }

                                                        // Why it applies
                                                        Row(verticalAlignment = Alignment.Top) {
                                                            Text(text = "Why it applies: ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2ED8A7)))
                                                            Text(
                                                                text = step.whyItApplies ?: "Enables algebraic reduction to a simpler equivalent form.",
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        }

                                                        // Common Pitfall
                                                        Row(verticalAlignment = Alignment.Top) {
                                                            Text(text = "Common pitfall: ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFF6B8B)))
                                                            Text(
                                                                text = step.commonPitfall ?: "Operator precedence confusion or dropping negative signs across parentheses.",
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        }

                                                        // Log Misconception button
                                                        TextButton(
                                                            onClick = {
                                                                onRecordMistake("${step.ruleApplied ?: step.description}: ${step.commonPitfall ?: "Transformation caution"}")
                                                            },
                                                            modifier = Modifier.align(Alignment.End),
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFF6B8B))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Log to Mistake Journal", fontSize = 10.sp, color = Color(0xFFFF6B8B))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Direct numerical derivation via recursive AST evaluation.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Expression: $expression",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                )
                                Text(
                                    text = "Result: ${result?.decimalFormatted ?: "Pending"}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Linked Action Buttons
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "LINKED EXPERIMENT & APPLICATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onNavigateToLabSim(topic.simId) },
                            modifier = Modifier.weight(1f).testTag("btn_derivation_to_sim"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Visual Lab", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onNavigateToLabGame(topic.gameId) },
                            modifier = Modifier.weight(1f).testTag("btn_derivation_to_game"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Game Arena", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onExportToLab,
                        modifier = Modifier.fillMaxWidth().testTag("btn_export_to_lab"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Result to Flight Simulator")
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryView(
    history: List<String>,
    onSelectHistory: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    val sessions = ConceptReplayHistoryRepository.sessions

    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "LOCAL CONCEPT REPLAY HISTORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = "100% offline & private to your device", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onClearHistory) { Text("Clear All", fontSize = 11.sp) }
            }
        }

        if (sessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No saved calculations yet. Perform expressions to populate offline history.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(sessions) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectHistory(session.expression) }
                        .testTag("history_session_${session.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                Text(
                                    text = session.topicName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF192A24)) {
                                Text(
                                    text = session.currentPhase.label,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2ED8A7))
                                )
                            }
                        }

                        Text(
                            text = session.expression,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "= ${session.exactValue}  (~${session.decimalFormatted})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Icon(imageVector = Icons.Default.NorthWest, contentDescription = "Load into Calculator", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }

                        if (!session.studentNotes.isNullOrBlank()) {
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFF6B8B))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = session.studentNotes,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

@Composable
fun CheckMyWorkView(
    currentExpression: String,
    onAskTutor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CHECK MY WORKING MODE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "Submit intermediate algebraic transformations to identify the earliest divergent step without penalization.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "Current Working: $currentExpression",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
            )

            Button(
                onClick = onAskTutor,
                modifier = Modifier.fillMaxWidth().testTag("btn_verify_working_tutor"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Step with Grounded AI Tutor")
            }
        }
    }
}
