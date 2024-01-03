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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_HALF
import com.mitteloupe.loader.gears.mechanism.radians
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.model.GearType
import kotlin.math.cos
import kotlin.math.sin

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
        mutableStateOf(Path())
    }
    val numberOfTeeth = gear.teethCount
    val toothAngle = (360f / numberOfTeeth).radians
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
        val radiusPx = (gear.radius.dp - toothRoundness / 2f).toPx()
        val toothDepthPx = toothDepth.toPx()
        val innerRadius = radiusPx - toothDepthPx
        with(path) {
            reset()
            when (gearType) {
                GearType.Sharp -> {
                    (0 until numberOfTeeth).forEach { toothIndex ->
                        val startAngle =
                            (toothIndex.toFloat() * .999999f * toothAngle) + relativeRotation

                        drawSharpTooth(
                            toothIndex,
                            radiusPx,
                            radiusPx,
                            startAngle,
                            halfToothAngle,
                            innerRadius,
                            radiusPx
                        )
                    }
                }

                GearType.Square -> {
                    relativeRotation += quarterToothAngle
                    (0 until numberOfTeeth).forEach { toothIndex ->
                        val startAngle =
                            (toothIndex.toFloat() * .999999f * toothAngle) + relativeRotation
                        val endAngle = startAngle + toothAngle

                        drawSquareTooth(
                            toothIndex,
                            radiusPx,
                            radiusPx,
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
                    radiusPx - holeRadiusPx,
                    radiusPx - holeRadiusPx,
                    radiusPx + holeRadiusPx,
                    radiusPx + holeRadiusPx
                )
            )

            fillType = PathFillType.EvenOdd
        }
        drawPath(path = path, brush = brush)
        drawPath(
            path = path,
            brush = brush,
            style = Stroke(
                width = toothRoundness.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(toothRoundness.toPx())
            )
        )
    }
}

private fun Path.drawSharpTooth(
    toothIndex: Int,
    gearCenterX: Float,
    gearCenterY: Float,
    startAngle: Float,
    halfToothAngle: Float,
    innerRadius: Float,
    outerRadius: Float
) {
    val midAngle = startAngle + halfToothAngle

    if (toothIndex == 0) {
        moveTo(
            gearCenterX + cos(startAngle) * outerRadius,
            gearCenterY + sin(startAngle) * outerRadius
        )
    } else {
        lineTo(
            gearCenterX + cos(startAngle) * outerRadius,
            gearCenterY + sin(startAngle) * outerRadius
        )
    }
    lineTo(
        gearCenterX + cos(midAngle) * innerRadius,
        gearCenterY + sin(midAngle) * innerRadius
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
