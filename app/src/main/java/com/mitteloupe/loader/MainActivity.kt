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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.gears.GearsLoader
import com.mitteloupe.loader.gears.GearsLoaderDefaults
import com.mitteloupe.loader.gears.mechanism.sqrt
import com.mitteloupe.loader.gears.model.GearConfiguration
import com.mitteloupe.loader.gears.model.GearType
import com.mitteloupe.loader.gears.model.ProgressState
import com.mitteloupe.loader.ui.theme.LoadersTheme
import kotlin.math.max
import kotlin.random.Random

private const val MINIMAL_RADIUS_VALUE = 6f
private const val MAXIMAL_RADIUS_VALUE = 60f
private const val MAXIMAL_TOOTH_ROUNDNESS_VALUE = 1f
private const val MINIMAL_TOOTH_WIDTH = 2f
private val maximalToothWidth = MAXIMAL_RADIUS_VALUE * 3f.sqrt()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                val progress = remember { mutableFloatStateOf(.75f) }
                val defaultColor = GearsLoaderDefaults.color
                val color = remember { mutableStateOf(defaultColor) }
                val minimumRadius = remember {
                    mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.minimumRadius.value)
                }
                val maximumRadius = remember {
                    mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.maximumRadius.value)
                }
                val gearType = remember { mutableStateOf(GearsLoaderDefaults.gearType) }
                val toothDepth = remember {
                    mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.toothDepth.value)
                }
                val toothWidth = remember {
                    mutableFloatStateOf(GearsLoaderDefaults.gearConfiguration.toothWidth.value)
                }
                val holeRadius =
                    remember { mutableFloatStateOf(GearsLoaderDefaults.holeRadius.value) }
                val toothRoundness =
                    remember { mutableFloatStateOf(GearsLoaderDefaults.TOOTH_ROUNDNESS) }
                val scrollState = rememberScrollState()
                Surface(color = MaterialTheme.colorScheme.background) {
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
                            holeRadius = holeRadius.floatValue.dp,
                            color = color.value,
                            gearType = gearType.value,
                            progressState = ProgressState.Determinate(
                                progress.floatValue
                            ),
                            modifier = Modifier
                                .width(352.dp)
                                .height(200.dp)
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        ControlPanel(
                            minimumRadius = minimumRadius,
                            maximumRadius = maximumRadius,
                            progress = progress,
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
}

@Composable
private fun ControlPanel(
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

@Composable
private fun SliderWithTitle(
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier) {
        Text(
            text = text,
            color = color,
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
    selectedOption: MutableState<T>,
    vararg options: Pair<String, T>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            Row {
                RadioButton(
                    selected = selectedOption.value == option.second,
                    onClick = { selectedOption.value = option.second }
                )
                Text(
                    text = option.first,
                    color = color,
                    modifier = Modifier.padding(0.dp, 12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme(dynamicColor = false) {
        val progress by remember { mutableFloatStateOf(0.75f) }
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
                color = GearsLoaderDefaults.color,
                gearType = GearType.Square,
                progressState = ProgressState.Determinate(progress),
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
