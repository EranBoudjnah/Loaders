package com.mitteloupe.loader.gears

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.mechanism.sqrt
import com.mitteloupe.loader.gears.model.GearType
import com.mitteloupe.loader.settings.SliderWithTitle
import com.mitteloupe.loader.settings.TwoValueSelector
import kotlin.math.max
import kotlin.random.Random

private const val MINIMAL_RADIUS_VALUE = 6f
private const val MAXIMAL_RADIUS_VALUE = 60f
private const val MAXIMAL_TOOTH_ROUNDNESS_VALUE = 1f
private const val MINIMAL_TOOTH_WIDTH = 2f
private val maximalToothWidth = MAXIMAL_RADIUS_VALUE * 3f.sqrt()

@Composable
fun ControlPanel(
    minimumRadius: MutableState<Float>,
    maximumRadius: MutableState<Float>,
    progress: MutableState<Float>,
    color: MutableState<Color>,
    gearType: MutableState<GearType>,
    toothDepth: MutableState<Float>,
    toothWidth: MutableState<Float>,
    holeRadius: MutableState<Float>,
    toothRoundness: MutableState<Float>
) {
    fun maximalToothDepth() =
        minimumRadius.value - max(holeRadius.value, toothWidth.value / 3f.sqrt())

    fun maximalToothWidth(): Float = (minimumRadius.value - toothDepth.value) * 3f.sqrt()
    fun maximalHoleRadius() = minimumRadius.value - toothDepth.value - .01f

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SliderWithTitle(
            text = "Progress",
            value = progress.value,
            onValueChange = { progress.value = it },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Button(
            onClick = {
                color.value =
                    Color.hsl(Random.nextFloat() * 360f, Random.nextInt(3).toFloat() * .45f, .7f)
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Change Color",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            SliderWithTitle(
                text = "Minimum radius",
                value = (minimumRadius.value - MINIMAL_RADIUS_VALUE) / MAXIMAL_RADIUS_VALUE,
                onValueChange = {
                    minimumRadius.value = (MINIMAL_RADIUS_VALUE + it * MAXIMAL_RADIUS_VALUE)
                        .coerceIn(toothDepth.value + holeRadius.value, maximumRadius.value)
                },
                modifier = Modifier.fillMaxWidth(.5f)
            )
            SliderWithTitle(
                text = "Maximum radius",
                value = (maximumRadius.value - MINIMAL_RADIUS_VALUE) / MAXIMAL_RADIUS_VALUE,
                onValueChange = {
                    maximumRadius.value =
                        max(minimumRadius.value, it * MAXIMAL_RADIUS_VALUE + MINIMAL_RADIUS_VALUE)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        TwoValueSelector(
            selectedOption = gearType,
            "Square" to GearType.Square,
            "Sharp" to GearType.Sharp,
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Tooth depth",
            value = toothDepth.value / MAXIMAL_RADIUS_VALUE,
            onValueChange = { value ->
                toothDepth.value = (value * MAXIMAL_RADIUS_VALUE).coerceIn(0f, maximalToothDepth())
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Tooth width",
            value = toothWidth.value / maximalToothWidth,
            onValueChange = { value ->
                toothWidth.value =
                    (value * maximalToothWidth).coerceIn(MINIMAL_TOOTH_WIDTH, maximalToothWidth())
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Hole radius",
            value = holeRadius.value / MAXIMAL_RADIUS_VALUE,
            onValueChange = { value ->
                holeRadius.value = (value * MAXIMAL_RADIUS_VALUE).coerceIn(0f, maximalHoleRadius())
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Tooth roundness",
            value = toothRoundness.value / MAXIMAL_TOOTH_ROUNDNESS_VALUE,
            onValueChange = {
                toothRoundness.value = it * MAXIMAL_TOOTH_ROUNDNESS_VALUE
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}
