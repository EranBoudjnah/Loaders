package com.mitteloupe.loader.gears.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_HALF
import com.mitteloupe.loader.gears.mechanism.radians
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.model.GearType
import kotlin.math.cos
import kotlin.math.sin

private const val FLOAT_INACCURACY_CORRECTION = .999999f

@Composable
internal fun Gear(
    gear: Gear,
    toothDepth: Dp,
    rotation: Float,
    toothRoundness: Dp,
    holeRadius: Dp,
    gearType: GearType,
    brush: Brush,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    alpha: Float = 1f
) {
    val path by remember {
        mutableStateOf(
            Path().apply { fillType = PathFillType.EvenOdd }
        )
    }
    val numberOfTeeth = gear.teethCount
    val toothAngle = (360f / numberOfTeeth).radians * FLOAT_INACCURACY_CORRECTION
    val halfToothAngle = toothAngle / 2f
    val quarterToothAngle = toothAngle / 4f
    var relativeRotation = gear.rotation - PI_FLOAT_HALF - rotation * gear.relativeSpeed

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
        val radiusPx = (gear.radius.dp).toPx()
        val toothDepthPx = toothDepth.toPx()
        val innerRadius = radiusPx - toothDepthPx
        val centerPx = (gear.radius.dp).toPx()
        with(path) {
            reset()
            when (gearType) {
                GearType.Sharp -> {
                    (0 until numberOfTeeth).forEach { toothIndex ->
                        val startAngle = (toothIndex.toFloat() * toothAngle) + relativeRotation

                        drawSharpTooth(
                            toothIndex,
                            centerPx,
                            centerPx,
                            startAngle,
                            halfToothAngle,
                            innerRadius,
                            radiusPx,
                            toothRoundness.toPx()
                        )
                    }
                }

                GearType.Square -> {
                    relativeRotation += quarterToothAngle
                    (0 until numberOfTeeth).forEach { toothIndex ->
                        val startAngle = (toothIndex.toFloat() * toothAngle) + relativeRotation
                        val endAngle = startAngle + toothAngle

                        drawSquareTooth(
                            toothIndex,
                            centerPx,
                            centerPx,
                            startAngle,
                            endAngle,
                            quarterToothAngle,
                            halfToothAngle,
                            innerRadius,
                            radiusPx
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
        drawPath(path = path, brush = brush)
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

    val endAngle = startAngle + halfToothAngle * 2f
    val endAngleCos = cos(endAngle)
    val innerEndX = gearCenterX + endAngleCos * innerRadius
    val outerEndX = gearCenterX + endAngleCos * outerRadius
    val endAngleSin = sin(endAngle)
    val innerEndY = gearCenterY + endAngleSin * innerRadius
    val outerEndY = gearCenterY + endAngleSin * outerRadius

    if (toothIndex == 0) {
        moveTo(innerStartX, innerStartY)
    }

    val controlPoint1X = (innerMidX - innerStartX) / 6f * toothRoundness
    val controlPoint1Y = (innerMidY - innerStartY) / 6f * toothRoundness

    val controlPoint2X = outerMidX - innerStartX - (outerMidX - outerStartX) / 4f * toothRoundness
    val controlPoint2Y = outerMidY - innerStartY - (outerMidY - outerStartY) / 4f * toothRoundness

    relativeCubicTo(
        controlPoint1X,
        controlPoint1Y,
        controlPoint2X,
        controlPoint2Y,
        outerMidX - innerStartX,
        outerMidY - innerStartY
    )

    val controlPoint3X = (outerEndX - outerMidX) / 4f * toothRoundness
    val controlPoint3Y = (outerEndY - outerMidY) / 4f * toothRoundness

    val controlPoint4X = innerEndX - outerMidX - (innerEndX - innerMidX) / 6f * toothRoundness
    val controlPoint4Y = innerEndY - outerMidY - (innerEndY - innerMidY) / 6f * toothRoundness

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
    toothMiddleAngle: Float,
    toothStartAngle: Float,
    innerRadius: Float,
    outerRadius: Float
) {
    val toothRadius = outerRadius - innerRadius
    if (toothIndex == 0) {
        moveTo(
            gearCenterX + cos(startAngle) * innerRadius,
            gearCenterY + sin(startAngle) * innerRadius
        )
    } else {
        lineTo(
            gearCenterX + cos(startAngle) * innerRadius,
            gearCenterY + sin(startAngle) * innerRadius
        )
    }
    val toothBaseX = cos(startAngle + toothStartAngle) * innerRadius
    val toothBaseY = sin(startAngle + toothStartAngle) * innerRadius
    lineTo(
        gearCenterX + toothBaseX,
        gearCenterY + toothBaseY
    )
    val toothEndX = cos(startAngle + toothStartAngle + toothMiddleAngle) * toothRadius
    val toothEndY = sin(startAngle + toothStartAngle + toothMiddleAngle) * toothRadius
    lineTo(
        gearCenterX + toothBaseX + toothEndX,
        gearCenterY + toothBaseY + toothEndY
    )
    lineTo(
        gearCenterX + cos(endAngle) * innerRadius + toothEndX,
        gearCenterY + sin(endAngle) * innerRadius + toothEndY
    )
}
