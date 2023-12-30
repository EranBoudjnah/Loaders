package com.mitteloupe.loader.gears

import android.graphics.RectF
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.composable.Gear
import com.mitteloupe.loader.gears.mechanism.GearMesher
import com.mitteloupe.loader.gears.mechanism.RectangleFiller
import java.time.Instant
import kotlinx.coroutines.delay

private val startTime = Instant.now().toEpochMilli()

@Composable
fun GearsLoader(
    modifier: Modifier = Modifier,
    overflow: Boolean = false,
    minimumRadius: Dp = 13f.dp,
    maximumRadius: Dp = 32f.dp,
    holeRadius: Dp = 3f.dp,
    toothDepth: Dp = 3f.dp,
    toothWidth: Dp = 4f.dp,
    toothRoundness: Dp = 1f.dp,
    gearColor: Brush = SolidColor(Color.Gray)
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    val currentLocalDensity = LocalDensity.current

    val gears by remember {
        derivedStateOf {
            with(currentLocalDensity) {
                if (size.width == 0 || size.height == 0) {
                    emptyList()
                } else {
                    RectangleFiller(GearMesher()).fill(
                        rectangle = RectF(
                            0f,
                            0f,
                            size.width.toDp().value,
                            size.height.toDp().value
                        ).apply {
                            if (overflow) {
                                inset(-maximumRadius.value, -maximumRadius.value)
                            }
                        },
                        minimumRadius = minimumRadius.value,
                        maximumRadius = maximumRadius.value,
                        toothDepth = toothDepth.value,
                        toothWidth = toothWidth.value
                    ).sortedBy { it.center.y }
                }
            }
        }
    }

    var rotation by remember {
        mutableFloatStateOf(
            (Instant.now().toEpochMilli() - startTime).toFloat() / 250f
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            rotation = (Instant.now().toEpochMilli() - startTime).toFloat() / 250f
            delay(20)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
            .clipToBounds()
    ) {
        gears.forEach { gear ->
            Gear(
                gear = gear,
                toothDepth = toothDepth,
                rotation = rotation,
                toothRoundness = toothRoundness,
                holeRadius = holeRadius,
                brush = gearColor
            )
        }
    }
}
