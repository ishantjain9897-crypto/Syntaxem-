package com.example.orbitlab.ui.assistant

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orbitlab.model.OrbitLabCurriculum
import com.example.orbitlab.model.PredictInquiry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIMissionAssistantScreen(
    onNavigateToSimulator: () -> Unit = {}
) {
    val inquiries = remember { OrbitLabCurriculum.inquiries }
    var selectedInquiryIndex by remember { mutableIntStateOf(0) }
    val currentInquiry = inquiries[selectedInquiryIndex]

    // Predict and verify state
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasVerifiedPrediction by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI MISSION ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "Grounded Scientific Guide • Predict & Verify",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Mission Guide Introduction Banner
            item {
                Spacer(modifier = Modifier.height(4.dp))
                GuideBannerCard()
            }

            // 2. Selectable Inquiry Challenges Selector
            item {
                InquirySelectorCard(
                    inquiries = inquiries,
                    selectedIndex = selectedInquiryIndex,
                    onSelect = { idx ->
                        selectedInquiryIndex = idx
                        selectedOptionIndex = null
                        hasVerifiedPrediction = false
                    }
                )
            }

            // 3. Active Predict-and-Verify Card
            item {
                PredictAndVerifyCard(
                    inquiry = currentInquiry,
                    selectedOption = selectedOptionIndex,
                    hasVerified = hasVerifiedPrediction,
                    onSelectOption = { idx ->
                        if (!hasVerifiedPrediction) {
                            selectedOptionIndex = idx
                        }
                    },
                    onVerify = {
                        hasVerifiedPrediction = true
                    },
                    onReset = {
                        selectedOptionIndex = null
                        hasVerifiedPrediction = false
                    },
                    onLaunchSimulator = onNavigateToSimulator
                )
            }

            // 4. Grounded Principles & Model Distinctions
            item {
                ModelVsRealityDeepDiveCard()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GuideBannerCard() {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("guide_banner_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "SCIENTIFIC REASONING ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Grounded in first-principles physics. Predictions are systematically checked against deterministic Newtonian mechanics.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InquirySelectorCard(
    inquiries: List<PredictInquiry>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = "INQUIRY EXPERIMENTS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            inquiries.forEachIndexed { index, inq ->
                val isSelected = index == selectedIndex
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(index) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Lab ${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = inq.formulaBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Predict and verify card: presents scientific inquiry question, choice options, and deterministic verification.
 */
@Composable
fun PredictAndVerifyCard(
    inquiry: PredictInquiry,
    selectedOption: Int?,
    hasVerified: Boolean,
    onSelectOption: (Int) -> Unit,
    onVerify: () -> Unit,
    onReset: () -> Unit,
    onLaunchSimulator: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("predict_verify_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "STEP 1: PREDICT BEFORE TESTING",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = inquiry.question,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = inquiry.context,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options list
            inquiry.options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = index == inquiry.correctIndex

                val borderColor = when {
                    !hasVerified && isSelected -> MaterialTheme.colorScheme.primary
                    hasVerified && isCorrect -> Color(0xFF10B981)
                    hasVerified && isSelected && !isCorrect -> Color(0xFFF43F5E)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                val bgColor = when {
                    !hasVerified && isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    hasVerified && isCorrect -> Color(0xFF10B981).copy(alpha = 0.15f)
                    hasVerified && isSelected && !isCorrect -> Color(0xFFF43F5E).copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !hasVerified) { onSelectOption(index) }
                        .testTag("option_$index"),
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = if (!hasVerified) { { onSelectOption(index) } } else null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verify Action Button
            if (!hasVerified) {
                Button(
                    onClick = onVerify,
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth().testTag("btn_verify_prediction"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Execute Deterministic Verification")
                }
            } else {
                // Step 2: Deterministic Verification & Model Explanation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val wasCorrect = selectedOption == inquiry.correctIndex

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (wasCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (wasCorrect) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (wasCorrect) "PREDICTION VERIFIED BY ENGINE" else "DISCREPANCY IDENTIFIED",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (wasCorrect) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                        )
                    }

                    Text(
                        text = inquiry.deterministicExplanation,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = inquiry.modelVsRealExplanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReset,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retry Inquiry")
                        }
                        Button(
                            onClick = onLaunchSimulator,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Inspect in Sim")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelVsRealityDeepDiveCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SCIENTIFIC EPISTEMOLOGY: MODELS VS REALITY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "In physics education, we begin with an idealized model where variables can be isolated (e.g. vacuum conditions, point masses, frictionless planes).",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "1. Idealized Model: Trajectory is a pure symmetric parabola y = ax² + bx + c. Range is symmetric about apex.\n2. Atmospheric Reality: Drag force F_d = ½ ρ v² C_d A acts anti-parallel to instantaneous velocity vector, steepening the descent curve and reducing range by 15-40% depending on projectile ballistic coefficient.",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
