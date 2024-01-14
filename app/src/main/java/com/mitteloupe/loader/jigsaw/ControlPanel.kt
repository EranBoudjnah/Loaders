package com.mitteloupe.loader.jigsaw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.R
import com.mitteloupe.loader.jigsaw.BrushProvider.ImageResourceBrushProvider
import com.mitteloupe.loader.jigsaw.model.KnobConfiguration
import com.mitteloupe.loader.jigsaw.model.ProgressState
import com.mitteloupe.loader.settings.MultipleValueSelector
import com.mitteloupe.loader.settings.SliderWithTitle
import kotlin.reflect.KClass

private const val MAXIMUM_HORIZONTAL_PIECES = 32
private const val MINIMUM_HORIZONTAL_PIECES = 1
private const val MAXIMUM_VERTICAL_PIECES = 20
private const val MINIMUM_VERTICAL_PIECES = 1

@Composable
fun ControlPanel(
    progress: MutableState<Float>,
    progressMode: MutableState<KClass<out ProgressState>>,
    knobConfiguration: MutableState<KnobConfiguration>,
    horizontalPieces: MutableState<Int>,
    verticalPieces: MutableState<Int>,
    brushProvider: MutableState<BrushProvider>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        SliderWithTitle(
            text = "Progress",
            value = progress.value,
            onValueChange = { progress.value = it },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        MultipleValueSelector(
            selectedOption = progressMode,
            "Sweep" to ProgressState.DeterminateSweep::class,
            "Spiral" to ProgressState.DeterminateSpiral::class,
            "Indeterminate" to ProgressState.Indeterminate::class,
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        Divider()
        MultipleValueSelector(
            selectedOption = knobConfiguration,
            "Round knob" to JigsawLoaderDefaults.knobConfiguration,
            "Flat knob" to JigsawLoaderDefaults.flatKnobConfiguration,
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Horizontal pieces",
            value = (horizontalPieces.value - MINIMUM_HORIZONTAL_PIECES).toFloat() /
                (MAXIMUM_HORIZONTAL_PIECES - MINIMUM_HORIZONTAL_PIECES).toFloat(),
            onValueChange = {
                horizontalPieces.value =
                    (it * (MAXIMUM_HORIZONTAL_PIECES - MINIMUM_HORIZONTAL_PIECES).toFloat())
                        .toInt() + MINIMUM_HORIZONTAL_PIECES
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        SliderWithTitle(
            text = "Vertical pieces",
            value = (verticalPieces.value - MINIMUM_VERTICAL_PIECES).toFloat() /
                (MAXIMUM_VERTICAL_PIECES - MINIMUM_VERTICAL_PIECES).toFloat(),
            onValueChange = {
                verticalPieces.value =
                    (it * (MAXIMUM_VERTICAL_PIECES - MINIMUM_VERTICAL_PIECES).toFloat()).toInt() +
                    MINIMUM_VERTICAL_PIECES
            },
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        MultipleValueSelector(
            selectedOption = brushProvider,
            "Bitmap" to ImageResourceBrushProvider(R.drawable.jigsaw_image),
            "Color" to JigsawLoaderDefaults.brushProvider,
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}
