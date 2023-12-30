package com.mitteloupe.loader.gears.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_HALF
import com.mitteloupe.loader.gears.mechanism.radians
import com.mitteloupe.loader.gears.model.Gear
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun Gear(
    gear: Gear,
    toothDepth: Dp,
    rotation: Float,
    toothRoundness: Dp,
    holeRadius: Dp,
    brush: Brush = SolidColor(Color.Red)
) {
    val numberOfTeeth = gear.teethCount
    val toothAngle = (360f / numberOfTeeth).radians
    val halfToothAngle = toothAngle / 2f
    val relativeRotation = gear.rotation - PI_FLOAT_HALF - rotation * gear.relativeSpeed

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val gearCenterX = gear.center.x.dp.toPx()
        val gearCenterY = gear.center.y.dp.toPx()
        val outerRadius = gear.radius.dp.toPx()
        val innerRadius = outerRadius - toothDepth.toPx()
        val path = Path().apply {
            (0 until numberOfTeeth).forEach { toothIndex ->
                val startAngle =
                    (toothIndex.toFloat() * .999999f * toothAngle) + relativeRotation
                val endAngle = startAngle + toothAngle
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
                lineTo(
                    gearCenterX + cos(endAngle) * outerRadius,
                    gearCenterY + sin(endAngle) * outerRadius
                )
            }

            close()
        }
        val holeRadiusPx = holeRadius.toPx()
        val circlePath = Path().apply {
            addOval(
                Rect(
                    gearCenterX - holeRadiusPx,
                    gearCenterY - holeRadiusPx,
                    gearCenterX + holeRadiusPx,
                    gearCenterY + holeRadiusPx
                )
            )
        }
        clipPath(circlePath, clipOp = ClipOp.Difference) {
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
}
