package com.mitteloupe.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.GearsLoader
import com.mitteloupe.loader.gears.mechanism.sqrt
import com.mitteloupe.loader.gears.model.GearConfiguration
import com.mitteloupe.loader.gears.model.GearType
import com.mitteloupe.loader.gears.model.ProgressState
import com.mitteloupe.loader.ui.theme.LoadersTheme
import kotlin.math.max
import kotlin.random.Random

private const val minimalRadiusValue = 12f
private const val maximalRadiusValue = 60f
private const val maximalToothRoundnessValue = 1f
private const val minimalToothWidth = 2f
private val maximalToothWidth = maximalRadiusValue * 3f.sqrt()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                val progress = remember { mutableIntStateOf(75) }
                val tolerance = remember { mutableIntStateOf(10) }
                val color = remember { mutableStateOf(Color(94, 194, 194, 255)) }
                val minimumRadius = remember { mutableFloatStateOf(13f) }
                val maximumRadius = remember { mutableFloatStateOf(32f) }
                val gearType = remember { mutableStateOf<GearType>(GearType.Square) }
                val toothDepth = remember { mutableFloatStateOf(4f) }
                val toothWidth = remember { mutableFloatStateOf(6f) }
                val holeRadius = remember { mutableFloatStateOf(4f) }
                val toothRoundness = remember { mutableFloatStateOf(1f) }
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                ) {
                    GearsLoader(
                        gearConfiguration = GearConfiguration(
                            overflow = false,
                            minimumRadius = minimumRadius.floatValue.dp,
                            maximumRadius = maximumRadius.floatValue.dp,
                            toothDepth = toothDepth.floatValue.dp,
                            toothWidth = toothWidth.floatValue.dp
                        ),
                        toothRoundness = toothRoundness.floatValue,
                        holeRadius = holeRadius.value.dp,
                        gearColor = SolidColor(color.value),
                        gearType = gearType.value,
                        progressState = ProgressState.Progress(
                            progress.intValue,
                            tolerance = tolerance.intValue
                        ),
                        modifier = Modifier
                            .width(350.dp)
                            .height(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    ControlPanel(
                        minimumRadius = minimumRadius,
                        maximumRadius = maximumRadius,
                        progress = progress,
                        tolerance = tolerance,
                        color = color,
                        gearType = gearType,
                        toothDepth = toothDepth,
                        toothWidth = toothWidth,
                        holeRadius = holeRadius,
                        toothRoundness = toothRoundness
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlPanel(
    minimumRadius: MutableState<Float>,
    maximumRadius: MutableState<Float>,
    progress: MutableState<Int>,
    tolerance: MutableState<Int>,
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
            value = progress.value.toFloat() / 100f,
            onValueChange = { progress.value = (it * 100f).toInt() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Progress tolerance",
            value = tolerance.value.toFloat() / 50f,
            onValueChange = { tolerance.value = (it * 50f).toInt() },
            modifier = Modifier
                .width(350.dp)
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
                value = (minimumRadius.value - minimalRadiusValue) / maximalRadiusValue,
                onValueChange = {
                    minimumRadius.value = (minimalRadiusValue + it * maximalRadiusValue)
                        .coerceIn(toothDepth.value + holeRadius.value, maximumRadius.value)
                },
                modifier = Modifier.fillMaxWidth(.5f)
            )
            SliderWithTitle(
                text = "Maximum radius",
                value = (maximumRadius.value - minimalRadiusValue) / maximalRadiusValue,
                onValueChange = {
                    maximumRadius.value =
                        max(minimumRadius.value, it * maximalRadiusValue + minimalRadiusValue)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        TwoValueSelector("Square" to GearType.Square, "Sharp" to GearType.Sharp, gearType)
        SliderWithTitle(
            text = "Tooth depth",
            value = toothDepth.value / maximalRadiusValue,
            onValueChange = { value ->
                toothDepth.value = (value * maximalRadiusValue).coerceIn(0f, maximalToothDepth())
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
                    (value * maximalToothWidth).coerceIn(minimalToothWidth, maximalToothWidth())
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Hole radius",
            value = holeRadius.value / maximalRadiusValue,
            onValueChange = { value ->
                holeRadius.value = (value * maximalRadiusValue).coerceIn(0f, maximalHoleRadius())
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Tooth roundness",
            value = toothRoundness.value / maximalToothRoundnessValue,
            onValueChange = {
                toothRoundness.value = it * maximalToothRoundnessValue
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SliderWithTitle(
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun <T> TwoValueSelector(
    option1: Pair<String, T>,
    option2: Pair<String, T>,
    selectedOption: MutableState<T>
) {
    Column {
        Row {
            RadioButton(
                selected = selectedOption.value == option1.second,
                onClick = { selectedOption.value = option1.second }
            )
            Text(
                text = option1.first,
                modifier = Modifier.padding(0.dp, 12.dp)
            )
        }
        Row {
            RadioButton(
                selected = selectedOption.value == option2.second,
                onClick = { selectedOption.value = option2.second }
            )
            Text(
                text = option2.first,
                modifier = Modifier.padding(0.dp, 12.dp)
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme {
        val progress by remember { mutableFloatStateOf(0.75f) }
        val tolerance by remember { mutableIntStateOf(10) }
        val minimumRadius by remember { mutableFloatStateOf(12f) }
        val maximumRadius by remember { mutableFloatStateOf(20f) }
        Column(
            modifier = Modifier
        ) {
            GearsLoader(
                gearConfiguration = GearConfiguration(
                    overflow = false,
                    minimumRadius = minimumRadius.dp,
                    maximumRadius = maximumRadius.dp,
                    toothDepth = 4f.dp,
                    toothWidth = 6f.dp
                ),
                toothRoundness = 1f,
                holeRadius = 4f.dp,
                gearColor = SolidColor(Color(94, 194, 194, 255)),
                gearType = GearType.Square,
                progressState = ProgressState.Progress(
                    (progress * 100f).toInt(),
                    tolerance = tolerance
                ),
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
