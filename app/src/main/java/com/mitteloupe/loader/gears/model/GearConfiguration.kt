package com.mitteloupe.loader.gears.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GearConfiguration(
    val overflow: Boolean = false,
    val minimumRadius: Dp = 13f.dp,
    val maximumRadius: Dp = 32f.dp,
    val toothDepth: Dp = 3f.dp,
    val toothWidth: Dp = 4f.dp,
    val toothRoundness: Dp = 1f.dp,
)
