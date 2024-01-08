package com.mitteloupe.loader.gears

import android.graphics.RectF
import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_2
import com.mitteloupe.loader.gears.mechanism.RectangleFiller
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.model.GearConfiguration
import com.mitteloupe.loader.gears.model.GearType
import com.mitteloupe.loader.gears.model.ProgressState

@Composable
fun GearsLoader(
    modifier: Modifier = Modifier,
    gearConfiguration: GearConfiguration = GearsLoaderDefaults.gearConfiguration,
    @FloatRange(from = 0.0, to = 1.0) toothRoundness: Float = GearsLoaderDefaults.TOOTH_ROUNDNESS,
    holeRadius: Dp = GearsLoaderDefaults.holeRadius,
    rotationTimeMilliseconds: Int = GearsLoaderDefaults.ROTATION_TIME_MILLISECONDS,
    color: Color = GearsLoaderDefaults.color,
    trackColor: Color = GearsLoaderDefaults.trackColor,
    gearType: GearType = GearsLoaderDefaults.gearType,
    progressState: ProgressState = ProgressState.Indeterminate,
    transitionTimeMilliseconds: Int = DefaultDurationMillis,
    rectangleFiller: RectangleFiller = RectangleFiller(GearMesher())
) {
    val activeBrush = SolidColor(color)
    val inactiveBrush = SolidColor(trackColor)
    fun Boolean.brush(): Brush = if (this) activeBrush else inactiveBrush
    var gearConfigurationState by remember { mutableStateOf(gearConfiguration) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var usedSizeWidth by rememberSaveable { mutableIntStateOf(0) }
    var usedSizeHeight by rememberSaveable { mutableIntStateOf(0) }

    val currentLocalDensity = LocalDensity.current

    val savedGears = rememberSaveable<List<Gear>>(
        stateSaver = listSaver(
            save = { gears -> gears },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateOf(emptyList()) }

    val gears by remember(size) {
        derivedStateOf {
            var lastGearConfigurationState = gearConfigurationState
            with(currentLocalDensity) {
                if (size.width != usedSizeWidth || size.height != usedSizeHeight ||
                    (usedSizeWidth == 0 && usedSizeHeight == 0) ||
                    gearConfigurationState != lastGearConfigurationState
                ) {
                    lastGearConfigurationState = gearConfigurationState
                    if (size.width != 0 && size.height != 0) {
                        usedSizeWidth = size.width
                        usedSizeHeight = size.height
                        savedGears.value = rectangleFiller.fill(
                            rectangle = RectF(
                                0f,
                                0f,
                                size.width.toDp().value,
                                size.height.toDp().value
                            ).apply {
                                if (gearConfiguration.overflow) {
                                    inset(
                                        -gearConfigurationState.maximumRadius.value,
                                        -gearConfigurationState.maximumRadius.value
                                    )
                                }
                            },
                            minimumRadius = gearConfigurationState.minimumRadius.value,
                            maximumRadius = gearConfigurationState.maximumRadius.value,
                            toothDepth = gearConfigurationState.toothDepth.value,
                            toothWidth = gearConfigurationState.toothWidth.value
                        ).sortedBy { it.center.y }
                    }
                }
                savedGears.value
            }
        }
    }

    LaunchedEffect(gearConfiguration) {
        usedSizeWidth = 0
        usedSizeHeight = 0
        gearConfigurationState = gearConfiguration
    }

    LaunchedEffect(
        progressState
    ) {
        savedGears.value = gears
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Gear rotation transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = PI_FLOAT_2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = rotationTimeMilliseconds, easing = LinearEasing)
        ),
        label = "Gear rotation animation"
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
            .clipToBounds()
    ) {
        gears.forEach { gear ->
            with(LocalDensity.current) {
                val isActive = progressState.stateAtPosition(
                    size.width.toFloat(),
                    gear.center.x.dp.toPx()
                ) != 0f

                var oldBrush by remember {
                    mutableStateOf(isActive.brush())
                }
                var alphaState by remember {
                    mutableFloatStateOf(0f)
                }
                var activeState by remember {
                    mutableStateOf(isActive)
                }
                LaunchedEffect(isActive) {
                    oldBrush = activeState.brush()
                    activeState = isActive
                    alphaState = 0f
                    animate(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = transitionTimeMilliseconds,
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        alphaState = value
                    }
                }

                if (alphaState < 1.0f) {
                    Gear(
                        gear = gear,
                        rotation = rotation,
                        toothRoundness = toothRoundness,
                        holeRadius = holeRadius,
                        brush = oldBrush,
                        gearType = gearType
                    )
                }

                Gear(
                    gear = gear,
                    rotation = rotation,
                    toothRoundness = toothRoundness,
                    holeRadius = holeRadius,
                    brush = activeState.brush(),
                    gearType = gearType,
                    alpha = alphaState
                )
            }
        }
    }
}

object GearsLoaderDefaults {
    val gearConfiguration = GearConfiguration(
        overflow = false,
        minimumRadius = 10f.dp,
        maximumRadius = 32f.dp,
        toothDepth = 2.5f.dp,
        toothWidth = 6f.dp
    )

    const val TOOTH_ROUNDNESS = 0.3f

    val holeRadius: Dp = 3f.dp

    const val ROTATION_TIME_MILLISECONDS = 700

    val color: Color
        @ReadOnlyComposable @Composable get() = MaterialTheme.colorScheme.primary

    val trackColor: Color
        @ReadOnlyComposable @Composable get() = MaterialTheme.colorScheme.surfaceVariant

    val gearType: GearType = GearType.Square
}
