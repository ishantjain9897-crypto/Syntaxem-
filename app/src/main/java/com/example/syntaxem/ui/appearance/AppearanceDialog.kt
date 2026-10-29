package com.example.syntaxem.ui.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.designsystem.SyntaxemColorSet
import com.example.core.designsystem.SyntaxemPalette
import com.example.core.designsystem.SyntaxemTheme
import com.example.core.designsystem.SyntaxemThemeManager

@Composable
fun AppearanceDialog(
    onDismiss: () -> Unit
) {
    val activePalette = SyntaxemTheme.palette
    val currentColorSet by SyntaxemThemeManager.currentColorSet.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("dialog_appearance"),
            shape = RoundedCornerShape(24.dp),
            color = activePalette.surface,
            border = BorderStroke(1.dp, activePalette.border),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = activePalette.primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Appearance",
                                    tint = activePalette.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Appearance & Themes",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = activePalette.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Choose your scientific UI color set",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = activePalette.textSecondary
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_appearance")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = activePalette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active palette telemetry banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = activePalette.surfaceVariant,
                    border = BorderStroke(1.dp, activePalette.border.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE PALETTE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = activePalette.secondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            )
                            Text(
                                text = activePalette.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = activePalette.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Preview dots
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            activePalette.previewColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(BorderStroke(1.dp, activePalette.border), CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of color set options
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(SyntaxemPalette.ALL_PALETTES) { palette ->
                        val isSelected = currentColorSet == palette.id
                        ColorSetCard(
                            palette = palette,
                            isSelected = isSelected,
                            currentActivePalette = activePalette,
                            onSelect = {
                                SyntaxemThemeManager.setColorSet(palette.id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_appearance_apply"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = activePalette.primary,
                        contentColor = if (activePalette.isDark) Color.White else Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSetCard(
    palette: SyntaxemPalette,
    isSelected: Boolean,
    currentActivePalette: SyntaxemPalette,
    onSelect: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) currentActivePalette.primary else currentActivePalette.border,
        animationSpec = tween(durationMillis = 200),
        label = "color_card_border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("appearance_colorset_${palette.id.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                currentActivePalette.surfaceVariant
            } else {
                currentActivePalette.surface
            }
        ),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = palette.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = currentActivePalette.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (palette.isDark) Color(0xFF1E263D) else Color(0xFFEFF2EC),
                        border = BorderStroke(1.dp, currentActivePalette.border)
                    ) {
                        Text(
                            text = if (palette.isDark) "Dark" else "Light",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (palette.isDark) Color(0xFF8E9BB5) else Color(0xFF59635D),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = currentActivePalette.primary,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = palette.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = currentActivePalette.textSecondary,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Swatch Preview Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.previewColors.forEachIndexed { index, color ->
                    val label = when (index) {
                        0 -> "Canvas"
                        1 -> "Surface"
                        2 -> "Primary"
                        3 -> "Accent"
                        else -> "Highlight"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (index == 0 || index == 1) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.75f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
