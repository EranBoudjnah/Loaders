package com.mitteloupe.loader.gears.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_HALF
import com.mitteloupe.loader.gears.mechanism.radians
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.model.GearType
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private const val FLOAT_INACCURACY_CORRECTION = .999999f

@Composable
internal fun Gear(
    gear: Gear,
    rotation: Float,
    toothRoundness: Float,
    holeRadius: Dp,
    gearType: GearType,
    brush: Brush,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    alpha: Float = 1f
) {
    val numberOfTeeth = gear.teethCount
    val toothAngle = (360f / numberOfTeeth).radians * FLOAT_INACCURACY_CORRECTION
    val halfToothAngle = toothAngle / 2f

    val currentDensity = LocalDensity.current
    val path by remember(gear, toothRoundness, holeRadius, gearType) {
        derivedStateOf {
            with(currentDensity) {
                Path().apply {
                    val radiusPx = gear.radius.dp.toPx()
                    val toothDepthPx = gear.toothDepth.dp.toPx()
                    val innerRadius = radiusPx - toothDepthPx
                    val centerPx = gear.radius.dp.toPx()
                    when (gearType) {
                        GearType.Sharp -> {
                            (0 until numberOfTeeth).forEach { toothIndex ->
                                val startAngle = (toothIndex.toFloat() * toothAngle)

                                drawSharpTooth(
                                    toothIndex,
                                    centerPx,
                                    centerPx,
                                    startAngle,
                                    halfToothAngle,
                                    innerRadius,
                                    radiusPx,
                                    toothRoundness
                                )
                            }
                        }

                        GearType.Square -> {
                            (0 until numberOfTeeth).forEach { toothIndex ->
                                val startAngle = (toothIndex.toFloat() * toothAngle)
                                val endAngle = startAngle + toothAngle

                                drawSquareTooth(
                                    toothIndex = toothIndex,
                                    gearCenterX = centerPx,
                                    gearCenterY = centerPx,
                                    startAngle = startAngle,
                                    endAngle = endAngle,
                                    toothWidth = gear.toothWidth.dp.toPx(),
                                    innerRadius = innerRadius,
                                    outerRadius = radiusPx,
                                    toothRoundness = toothRoundness
                                )
                            }
                        }
                    }

                    close()

                    val holeRadiusPx = holeRadius.toPx()
                    addOval(
                        Rect(
                            centerPx - holeRadiusPx,
                            centerPx - holeRadiusPx,
                            centerPx + holeRadiusPx,
                            centerPx + holeRadiusPx
                        )
                    )

                    fillType = PathFillType.EvenOdd
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .size((gear.radius * 2f).dp)
            .offset((gear.center.x - gear.radius).dp, (gear.center.y - gear.radius).dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha,
                transformOrigin = TransformOrigin(0.25f, 0.25f)
            )
    ) {
        rotateRad(gear.rotation - PI_FLOAT_HALF - rotation * gear.relativeSpeed) {
            drawPath(path = path, brush = brush)
        }
    }
}

private fun Path.drawSharpTooth(
    toothIndex: Int,
    gearCenterX: Float,
    gearCenterY: Float,
    startAngle: Float,
    halfToothAngle: Float,
    innerRadius: Float,
    outerRadius: Float,
    toothRoundness: Float
) {
    val startAngleCos = cos(startAngle)
    val innerStartX = gearCenterX + startAngleCos * innerRadius
    val outerStartX = gearCenterX + startAngleCos * outerRadius
    val startAngleSin = sin(startAngle)
    val innerStartY = gearCenterY + startAngleSin * innerRadius
    val outerStartY = gearCenterY + startAngleSin * outerRadius

    val midAngle = startAngle + halfToothAngle
    val midAngleCos = cos(midAngle)
    val innerMidX = gearCenterX + midAngleCos * innerRadius
    val outerMidX = gearCenterX + midAngleCos * outerRadius
    val midAngleSin = sin(midAngle)
    val innerMidY = gearCenterY + midAngleSin * innerRadius
    val outerMidY = gearCenterY + midAngleSin * outerRadius

    if (toothIndex == 0) {
        moveTo(innerStartX, innerStartY)
    }

    val controlPoint1X = (innerMidX - innerStartX) / 3f * toothRoundness
    val controlPoint1Y = (innerMidY - innerStartY) / 3f * toothRoundness

    val controlPoint2X = outerMidX - innerStartX - (outerMidX - outerStartX) / 2f * toothRoundness
    val controlPoint2Y = outerMidY - innerStartY - (outerMidY - outerStartY) / 2f * toothRoundness

    relativeCubicTo(
        controlPoint1X,
        controlPoint1Y,
        controlPoint2X,
        controlPoint2Y,
        outerMidX - innerStartX,
        outerMidY - innerStartY
    )

    val endAngle = startAngle + halfToothAngle * 2f
    val endAngleCos = cos(endAngle)
    val innerEndX = gearCenterX + endAngleCos * innerRadius
    val outerEndX = gearCenterX + endAngleCos * outerRadius
    val endAngleSin = sin(endAngle)
    val innerEndY = gearCenterY + endAngleSin * innerRadius
    val outerEndY = gearCenterY + endAngleSin * outerRadius

    val controlPoint3X = (outerEndX - outerMidX) / 2f * toothRoundness
    val controlPoint3Y = (outerEndY - outerMidY) / 2f * toothRoundness

    val controlPoint4X = innerEndX - outerMidX - (innerEndX - innerMidX) / 3f * toothRoundness
    val controlPoint4Y = innerEndY - outerMidY - (innerEndY - innerMidY) / 3f * toothRoundness

    relativeCubicTo(
        controlPoint3X,
        controlPoint3Y,
        controlPoint4X,
        controlPoint4Y,
        innerEndX - outerMidX,
        innerEndY - outerMidY
    )
}

private fun Path.drawSquareTooth(
    toothIndex: Int,
    gearCenterX: Float,
    gearCenterY: Float,
    startAngle: Float,
    endAngle: Float,
    toothWidth: Float,
    innerRadius: Float,
    outerRadius: Float,
    toothRoundness: Float
) {
    val roundnessRadius = min(outerRadius - innerRadius, toothWidth / 2f) / 2f * toothRoundness

    val midAngle = (startAngle + endAngle) / 2f
    val midAngleCos = cos(midAngle)
    val midAngleSin = sin(midAngle)

    val baseStartCircleX = gearCenterX + midAngleCos * (innerRadius + roundnessRadius) +
        midAngleSin * (toothWidth / 4f + roundnessRadius)
    val baseStartCircleY = gearCenterY + midAngleSin * (innerRadius + roundnessRadius) -
        midAngleCos * (toothWidth / 4f + roundnessRadius)

    arcToRad(
        Rect(
            baseStartCircleX - roundnessRadius,
            baseStartCircleY - roundnessRadius,
            baseStartCircleX + roundnessRadius,
            baseStartCircleY + roundnessRadius
        ),
        midAngle + PI_FLOAT,
        -PI_FLOAT_HALF,
        toothIndex == 0
    )

    val toothStartCircleX = gearCenterX + midAngleCos * (outerRadius - roundnessRadius) +
        midAngleSin * (toothWidth / 4f - roundnessRadius)
    val toothStartCircleY = gearCenterY + midAngleSin * (outerRadius - roundnessRadius) -
        midAngleCos * (toothWidth / 4f - roundnessRadius)

    arcToRad(
        Rect(
            toothStartCircleX - roundnessRadius,
            toothStartCircleY - roundnessRadius,
            toothStartCircleX + roundnessRadius,
            toothStartCircleY + roundnessRadius
        ),
        midAngle - PI_FLOAT_HALF,
        PI_FLOAT_HALF,
        false
    )

    val toothEndCircleX = gearCenterX + midAngleCos * (outerRadius - roundnessRadius) -
        midAngleSin * (toothWidth / 4f - roundnessRadius)
    val toothEndCircleY = gearCenterY + midAngleSin * (outerRadius - roundnessRadius) +
        midAngleCos * (toothWidth / 4f - roundnessRadius)

    arcToRad(
        Rect(
            toothEndCircleX - roundnessRadius,
            toothEndCircleY - roundnessRadius,
            toothEndCircleX + roundnessRadius,
            toothEndCircleY + roundnessRadius
        ),
        midAngle,
        PI_FLOAT_HALF,
        false
    )

    val baseEndCircleX = gearCenterX + midAngleCos * (innerRadius + roundnessRadius) -
        midAngleSin * (toothWidth / 4f + roundnessRadius)
    val baseEndCircleY = gearCenterY + midAngleSin * (innerRadius + roundnessRadius) +
        midAngleCos * (toothWidth / 4f + roundnessRadius)

    arcToRad(
        Rect(
            baseEndCircleX - roundnessRadius,
            baseEndCircleY - roundnessRadius,
            baseEndCircleX + roundnessRadius,
            baseEndCircleY + roundnessRadius
        ),
        midAngle - PI_FLOAT_HALF,
        -PI_FLOAT_HALF,
        false
    )
}
