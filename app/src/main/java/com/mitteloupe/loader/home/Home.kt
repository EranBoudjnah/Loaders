package com.mitteloupe.loader.home

import com.mitteloupe.loader.gears.model.ProgressState as GearsProgressState
import com.mitteloupe.loader.jigsaw.model.ProgressState as JigsawProgressState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.R
import com.mitteloupe.loader.gears.GearsLoader
import com.mitteloupe.loader.gears.model.GearConfiguration
import com.mitteloupe.loader.gears.model.GearType
import com.mitteloupe.loader.jigsaw.BrushProvider
import com.mitteloupe.loader.jigsaw.BrushProvider.ImageResourceBrushProvider
import com.mitteloupe.loader.jigsaw.JigsawLoader
import com.mitteloupe.loader.jigsaw.JigsawLoaderDefaults

@Composable
fun Home(
    onNavigateToGears: () -> Unit,
    onNavigateToJigsaw: () -> Unit
) {
    var animatedProgress by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(null) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        ) { value, _ ->
            animatedProgress = value
        }
    }

    val scrollState = rememberScrollState()
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(4.dp)
        ) {
            Text(
                text = "Gears",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(324.dp)
                    .padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(horizontal = 4.dp)
            ) {
                GearsLoader(
                    gearConfiguration = GearConfiguration(
                        minimumRadius = 8.dp,
                        maximumRadius = 24.dp,
                        toothWidth = 4.dp,
                        toothDepth = 2.dp
                    ),
                    progressState = GearsProgressState.Determinate(animatedProgress),
                    holeRadius = 2.5.dp,
                    modifier = Modifier
                        .width(150.dp)
                        .height(100.dp)
                        .clickable { onNavigateToGears() }
                        .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp)
                )
                GearsLoader(
                    gearConfiguration = GearConfiguration(
                        minimumRadius = 8.dp,
                        maximumRadius = 24.dp,
                        toothWidth = 4.dp,
                        toothDepth = 3.dp,
                        overflow = true
                    ),
                    progressState = GearsProgressState.Determinate(animatedProgress),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    gearType = GearType.Sharp,
                    holeRadius = 0.dp,
                    toothRoundness = .5f,
                    modifier = Modifier
                        .width(150.dp)
                        .height(100.dp)
                        .clickable { onNavigateToGears() }
                        .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp)
                )
            }

            GearsLoader(
                gearConfiguration = GearConfiguration(
                    minimumRadius = 10.2.dp,
                    maximumRadius = 10.2.dp,
                    toothWidth = 6.2.dp,
                    toothDepth = 2.2.dp
                ),
                progressState = GearsProgressState.Determinate(animatedProgress),
                holeRadius = 2.5.dp,
                toothRoundness = .5f,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(300.dp)
                    .height(41.dp)
                    .clickable { onNavigateToGears() }
                    .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp)
            )

            Divider(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(324.dp)
                    .padding(16.dp)
            )
            Text(
                text = "Jigsaw",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(324.dp)
                    .padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(horizontal = 4.dp)
            ) {
                JigsawLoader(
                    modifier = Modifier
                        .width(150.dp)
                        .height(100.dp)
                        .clickable { onNavigateToJigsaw() }
                        .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp),
                    progressState = JigsawProgressState.DeterminateSweep(animatedProgress),
                    horizontalPieces = 5,
                    verticalPieces = 3,
                    puzzleBrushProvider = ImageResourceBrushProvider(R.drawable.jigsaw_image),
                    knobConfiguration = JigsawLoaderDefaults.flatKnobConfiguration
                )
                JigsawLoader(
                    modifier = Modifier
                        .width(150.dp)
                        .height(100.dp)
                        .clickable { onNavigateToJigsaw() }
                        .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp),
                    progressState = JigsawProgressState.DeterminateSpiral(animatedProgress),
                    horizontalPieces = 7,
                    verticalPieces = 4,
                    overflow = true
                )
            }
            JigsawLoader(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .width(300.dp)
                    .height(30.dp)
                    .clickable { onNavigateToJigsaw() }
                    .border(color = MaterialTheme.colorScheme.primary, padding = 4.dp),
puzzleBrushProvider = BrushProvider.ColorBrushProvider(MaterialTheme.colorScheme.onSurfaceVariant),
                progressState = JigsawProgressState.DeterminateSpiral(animatedProgress),
                horizontalPieces = 10,
                verticalPieces = 1,
                knobConfiguration = JigsawLoaderDefaults.flatKnobConfiguration,
                knobInversionEvaluator = { _, _ -> false }
            )
        }
    }
}

private fun Modifier.border(color: Color, padding: Dp): Modifier = drawBehind {
    drawRect(
        color = color,
        size = Size(
            size.width - (2.dp + padding).toPx(),
            size.height - (2.dp + padding).toPx()
        ),
        topLeft = Offset((1.dp + padding / 2).toPx(), (1.dp + padding / 2).toPx()),
        style = Stroke(width = 2.dp.toPx())
    )
}.padding(4.dp)

@Preview
@Composable
fun Preview() {
    Home(onNavigateToGears = {}, onNavigateToJigsaw = {})
}
