package com.mitteloupe.loader

import android.graphics.RectF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.GearMesher
import com.mitteloupe.loader.gears.PI_FLOAT_HALF
import com.mitteloupe.loader.gears.RectangleFiller
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.radians
import com.mitteloupe.loader.ui.theme.LoadersTheme
import java.time.Instant
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

private val startTime = Instant.now().toEpochMilli()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CogwheelsRectangle()
                }
            }
        }
    }
}

@Composable
fun CogwheelsRectangle(
    rectangle: RectF = RectF(40f, 40f, 800f.dp.value, 500f.dp.value),//RectF(20f, 20f, 400f, 250f),
    toothDepth: Float = 14f, // 6f,
    toothWidth: Float = 14f // 2.5f
) {
    val gears by remember {
        mutableStateOf(
            RectangleFiller(GearMesher()).fill(
                rectangle = rectangle,
                minimumRadius = 45f.dp.value,
                maximumRadius = 90f.dp.value,
                toothDepth = toothDepth.dp.value,
                toothWidth = toothWidth.dp.value
            )
        )
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            topLeft = Offset(rectangle.left, rectangle.top),
            size = Size(rectangle.width(), rectangle.height()),
            color = Color.Blue,
            style = Stroke(width = 2f)
        )
    }

    gears.forEachIndexed { index, gear ->
        Gear(
            gear = gear,
            toothDepth = toothDepth,
            rotation = rotation,
            brush = SolidColor(Color.Gray)
        )
    }
}

@Composable
fun Gear(
    gear: Gear,
    toothDepth: Float,
    rotation: Float,
    brush: Brush = SolidColor(Color.Red),
    roundness: Float = 1.5f
) {
    val numberOfTeeth = gear.teethCount
    val toothAngle = (360f / numberOfTeeth).radians
    val halfToothAngle = toothAngle / 2f
    val outerRadius = gear.radius - roundness * 2f
    val innerRadius = outerRadius - toothDepth
    val relativeRotation = gear.rotation - PI_FLOAT_HALF - rotation * gear.relativeSpeed

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val path = Path().apply {
            (0 until numberOfTeeth).forEach { toothIndex ->
                val startAngle =
                    (toothIndex.toFloat() * .999999f * toothAngle) + relativeRotation
                val endAngle = startAngle + toothAngle
                val midAngle = startAngle + halfToothAngle

                if (toothIndex == 0) {
                    moveTo(
                        gear.center.x + cos(startAngle) * outerRadius,
                        gear.center.y + sin(startAngle) * outerRadius
                    )
                } else {
                    lineTo(
                        gear.center.x + cos(startAngle) * outerRadius,
                        gear.center.y + sin(startAngle) * outerRadius
                    )
                }
                lineTo(
                    gear.center.x + cos(midAngle) * innerRadius,
                    gear.center.y + sin(midAngle) * innerRadius
                )
                lineTo(
                    gear.center.x + cos(endAngle) * outerRadius,
                    gear.center.y + sin(endAngle) * outerRadius
                )
            }

            close()
        }
        val circlePath = Path().apply {
            addOval(
                Rect(
                    gear.center.x - 6f.dp.toPx(),
                    gear.center.y - 6f.dp.toPx(),
                    gear.center.x + 6f.dp.toPx(),
                    gear.center.y + 6f.dp.toPx()
                )
            )
        }
        clipPath(circlePath, clipOp = ClipOp.Difference) {
            drawPath(path = path, brush = brush)
            drawPath(
                path = path,
                brush = brush,
                style = Stroke(
                    width = roundness.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(roundness.dp.toPx())
                )
            )
        }

    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme {
        CogwheelsRectangle()
    }
}
