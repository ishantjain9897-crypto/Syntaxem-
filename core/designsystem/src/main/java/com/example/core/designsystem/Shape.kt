package com.example.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

object CalcMasterShapes {
    val DisplayBezel = RoundedCornerShape(22.dp)
    val TactileKey = RoundedCornerShape(12.dp)
    val StatusPill = RoundedCornerShape(6.dp)
    val FormulaChip = RoundedCornerShape(8.dp)
    val LabCard = RoundedCornerShape(16.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
