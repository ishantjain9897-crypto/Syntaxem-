package com.example.calcmaster.lumenflow.conceptreplay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calcmaster.lumenflow.model.LumenLearningRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptRememberDialog(
    expression: String,
    detectedConcept: String,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var studentReflection by remember { mutableStateOf("") }
    var selectedMisconceptionTag by remember { mutableStateOf<String?>(null) }

    val presetTags = remember(detectedConcept) {
        when {
            detectedConcept.contains("Trig", ignoreCase = true) -> listOf(
                "Quadrant II cosine is negative (-)",
                "sin(30°) = 0.5 vs sin(60°) = √3/2",
                "tan is undefined at 90° (cos θ = 0)"
            )
            detectedConcept.contains("Projectile", ignoreCase = true) -> listOf(
                "Range scales with v₀² (quadratic velocity)",
                "Vertical velocity v_y = 0 at maximum height",
                "Complementary angles (30° & 60°) produce equal range"
            )
            detectedConcept.contains("Quadratic", ignoreCase = true) -> listOf(
                "Discriminant D < 0 implies complex conjugate roots",
                "Sign error when substituting -b",
                "Product of roots is c/a, sum is -b/a"
            )
            detectedConcept.contains("Friction", ignoreCase = true) || detectedConcept.contains("Newton", ignoreCase = true) -> listOf(
                "Static friction self-adjusts up to μ_s N",
                "Kinetic friction is strictly less than max static",
                "Normal force N = mg on flat plane"
            )
            else -> listOf(
                "Order of operations (BODMAS / PEMDAS)",
                "Degree vs Radian mode discrepancy",
                "Exact radical simplification"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REMEMBER • MISTAKE JOURNAL",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Privately log a concept insight or common pitfall for low-pressure spaced repetition. No lost streaks or anxiety penalties.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Problem / Working:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = expression,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
                }

                Text(
                    text = "Quick Misconception Tags (Tap to apply):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presetTags) { tag ->
                        val isSelected = selectedMisconceptionTag == tag
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedMisconceptionTag = if (isSelected) null else tag
                                if (!isSelected) {
                                    studentReflection = tag
                                }
                            },
                            label = { Text(tag, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = studentReflection,
                    onValueChange = { studentReflection = it },
                    label = { Text("Personal note or takeaway (optional)") },
                    placeholder = { Text("e.g. Remember to double check the sign convention in Quadrant II") },
                    modifier = Modifier.fillMaxWidth().testTag("input_remember_note"),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalExplanation = studentReflection.ifBlank {
                        selectedMisconceptionTag ?: "Concept reflection on $detectedConcept"
                    }
                    LumenLearningRepository.addMistake(
                        question = "Review of $expression",
                        studentAnswer = "Initial Working",
                        verifiedAnswer = "Verified Derivation",
                        errorConcept = detectedConcept,
                        explanation = finalExplanation
                    )
                    onSaved()
                },
                modifier = Modifier.testTag("btn_save_to_journal"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save to Mistake Journal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
