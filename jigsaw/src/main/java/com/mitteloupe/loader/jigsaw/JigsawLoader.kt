package com.mitteloupe.loader.jigsaw

import Orientation
import android.graphics.PointF
import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mitteloupe.loader.jigsaw.BrushProvider.ColorBrushProvider
import com.mitteloupe.loader.jigsaw.PiecePresenceResolver.IndeterminatePiecePresenceResolver
import com.mitteloupe.loader.jigsaw.PiecePresenceResolver.ProgressPiecePresenceResolver
import com.mitteloupe.loader.jigsaw.model.KnobConfiguration
import com.mitteloupe.loader.jigsaw.model.ProgressState
import com.mitteloupe.loader.jigsaw.model.ProgressState.Indeterminate
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun JigsawLoader(
    modifier: Modifier = Modifier,
    progressState: ProgressState = Indeterminate(),
    horizontalPieces: Int = JigsawLoaderDefaults.horizontalPieces,
    verticalPieces: Int = JigsawLoaderDefaults.verticalPieces,
    puzzleBrushProvider: BrushProvider =
        ColorBrushProvider(JigsawLoaderDefaults.color),
    lightBrush: Brush = SolidColor(Color.White.copy(alpha = .4f)),
    darkBrush: Brush = SolidColor(Color.Black.copy(alpha = .6f)),
    trackColor: Color = JigsawLoaderDefaults.trackColor,
    piecePresenceResolver: PiecePresenceResolver = JigsawLoaderDefaults.piecePresenceResolver(
        progressState = progressState,
        horizontalPieces = horizontalPieces,
        verticalPieces = verticalPieces
    ),
    transitionTimeMilliseconds: Int = AnimationConstants.DefaultDurationMillis,
    @FloatRange(from = 0.0, to = 1.0) trackSaturation: Float = 1f,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean =
        JigsawLoaderDefaults.knobInversionEvaluator,
    knobConfiguration: KnobConfiguration = JigsawLoaderDefaults.knobConfiguration
) {
    var activePiecePresenceResolver by remember {
        mutableStateOf(piecePresenceResolver)
    }
    if (piecePresenceResolver != activePiecePresenceResolver) {
        activePiecePresenceResolver = piecePresenceResolver
    }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val pieceWidth by remember(horizontalPieces) {
        derivedStateOf {
            size.width.toFloat() / horizontalPieces.toFloat()
        }
    }
    val pieceHeight by remember(verticalPieces) {
        derivedStateOf {
            size.height.toFloat() / verticalPieces.toFloat()
        }
    }

    var presenceState by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(activePiecePresenceResolver, progressState) {
        if (progressState is Indeterminate) {
            while (true) {
                activePiecePresenceResolver.iterate()
                presenceState = !presenceState
                delay(progressState.intervalMilliseconds)
            }
        }
    }

    val lightPath by remember(
        horizontalPieces,
        verticalPieces,
        progressState,
        knobConfiguration,
        presenceState
    ) {
        derivedStateOf {
            Path().apply {
                addAllPiecesTopLeft(
                    verticalPieces = verticalPieces,
                    horizontalPieces = horizontalPieces,
                    pieceWidth = pieceWidth,
                    pieceHeight = pieceHeight,
                    piecePresenceResolver = activePiecePresenceResolver,
                    knobInversionEvaluator = knobInversionEvaluator,
                    knobConfiguration = knobConfiguration
                )
            }
        }
    }
    val darkPath by remember(lightPath) {
        derivedStateOf {
            Path().apply {
                addAllPiecesBottomRight(
                    horizontalPieces = horizontalPieces,
                    verticalPieces = verticalPieces,
                    pieceWidth = pieceWidth,
                    pieceHeight = pieceHeight,
                    piecePresenceResolver = activePiecePresenceResolver,
                    knobInversionEvaluator = knobInversionEvaluator,
                    knobConfiguration = knobConfiguration
                )
            }
        }
    }

    val imageBrush = puzzleBrushProvider(size)

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = imageBrush,
                topLeft = Offset.Zero,
                size = Size(size.width.toFloat() - 1f, size.height.toFloat())
            )
            drawRect(
                brush = SolidColor(trackColor),
                topLeft = Offset.Zero,
                size = Size(size.width.toFloat() - 1f, size.height.toFloat())
            )
            drawRect(
                brush = SolidColor(Color.Gray),
                topLeft = Offset.Zero,
                size = Size(size.width.toFloat() - 1f, size.height.toFloat()),
                alpha = 1f - trackSaturation,
                blendMode = BlendMode.Saturation
            )
        }

        repeat(verticalPieces) { y ->
            repeat(horizontalPieces) { x ->
                val isActive = activePiecePresenceResolver.piecePresent(x, y)
                var activeState by remember {
                    mutableStateOf(isActive)
                }
                var alphaState by remember {
                    mutableFloatStateOf(if (isActive) 1f else 0f)
                }

                LaunchedEffect(isActive) {
                    if (activeState == isActive) {
                        return@LaunchedEffect
                    }
                    activeState = isActive
                    alphaState = if (isActive) 0f else 1f
                    animate(
                        initialValue = alphaState,
                        targetValue = 1f - alphaState,
                        animationSpec = tween(
                            durationMillis = transitionTimeMilliseconds,
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        alphaState = value
                    }
                }

                if (alphaState != 0f) {
                    val path by remember(lightPath) {
                        mutableStateOf(
                            Path().apply {
                                piece(
                                    left = pieceWidth * x,
                                    top = pieceHeight * y,
                                    width = pieceWidth,
                                    height = pieceHeight,
                                    placeX = x,
                                    placeY = y,
                                    placesHorizontal = horizontalPieces,
                                    placesVertical = verticalPieces,
                                    knobInversionEvaluator = knobInversionEvaluator,
                                    knobConfiguration = knobConfiguration
                                )
                            }
                        )
                    }
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawPath(
                            path = path,
                            brush = imageBrush,
                            alpha = alphaState,
                            style = Fill
                        )
                    }
                }
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            drawPath(
                path = lightPath,
                brush = lightBrush,
                style = Stroke(),
                blendMode = BlendMode.Screen
            )
            repeat(2) {
                drawPath(
                    path = darkPath,
                    brush = darkBrush,
                    style = Stroke(),
                    blendMode = BlendMode.Overlay
                )
            }
        }
    }
}

private fun Path.addAllPiecesTopLeft(
    horizontalPieces: Int,
    verticalPieces: Int,
    pieceWidth: Float,
    pieceHeight: Float,
    piecePresenceResolver: PiecePresenceResolver,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean,
    knobConfiguration: KnobConfiguration
) {
    repeat(verticalPieces) { y ->
        repeat(horizontalPieces) { x ->
            if (piecePresenceResolver.piecePresent(x, y)) {
                pieceTopLeft(
                    left = pieceWidth * x + 1f,
                    top = pieceHeight * y + 1f,
                    width = pieceWidth,
                    height = pieceHeight,
                    placeX = x,
                    placeY = y,
                    knobInversionEvaluator = knobInversionEvaluator,
                    knobConfiguration = knobConfiguration
                )
            }
        }
    }
}

private fun Path.pieceTopLeft(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    placeX: Int,
    placeY: Int,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean,
    knobConfiguration: KnobConfiguration
) {
    moveTo(left, top)
    if (placeY == 0) {
        lineTo(left + width, top)
    } else {
        pieceEdge(
            offsetX = left,
            offsetY = top,
            length = width,
            pieceHeight = height,
            orientation = Orientation.Top,
            knobInverted = knobInversionEvaluator(placeX, placeY),
            knobConfiguration = knobConfiguration
        )
    }
    moveTo(left, top + height)
    if (placeX == 0) {
        lineTo(left, top)
    } else {
        pieceEdge(
            offsetX = left,
            offsetY = top + height,
            length = height,
            pieceHeight = width,
            orientation = Orientation.Left,
            knobInverted = !knobInversionEvaluator(placeX, placeY),
            knobConfiguration = knobConfiguration
        )
    }
}

private fun Path.addAllPiecesBottomRight(
    horizontalPieces: Int,
    verticalPieces: Int,
    pieceWidth: Float,
    pieceHeight: Float,
    piecePresenceResolver: PiecePresenceResolver,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean,
    knobConfiguration: KnobConfiguration
) {
    repeat(verticalPieces) { y ->
        repeat(horizontalPieces) { x ->
            if (piecePresenceResolver.piecePresent(x, y)) {
                pieceBottomRight(
                    left = pieceWidth * x - 1f,
                    top = pieceHeight * y - 1f,
                    width = pieceWidth,
                    height = pieceHeight,
                    placeX = x,
                    placeY = y,
                    placesHorizontal = horizontalPieces,
                    placesVertical = verticalPieces,
                    knobInversionEvaluator = knobInversionEvaluator,
                    knobConfiguration = knobConfiguration
                )
            }
        }
    }
}

private fun Path.pieceBottomRight(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    placeX: Int,
    placeY: Int,
    placesHorizontal: Int,
    placesVertical: Int,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean,
    knobConfiguration: KnobConfiguration
) {
    moveTo(left + width, top)
    if (placeX == placesHorizontal - 1) {
        lineTo(left + width, top + height)
    } else {
        pieceEdge(
            offsetX = left + width,
            offsetY = top,
            length = height,
            pieceHeight = width,
            orientation = Orientation.Right,
            knobInverted = knobInversionEvaluator(placeX + 1, placeY),
            knobConfiguration = knobConfiguration
        )
    }
    if (placeY == placesVertical - 1) {
        lineTo(left, top + height)
    } else {
        pieceEdge(
            offsetX = left + width,
            offsetY = top + height,
            length = width,
            pieceHeight = height,
            orientation = Orientation.Bottom,
            knobInverted = !knobInversionEvaluator(placeX, placeY + 1),
            knobConfiguration = knobConfiguration
        )
    }
}

private fun Path.piece(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    placeX: Int,
    placeY: Int,
    placesHorizontal: Int,
    placesVertical: Int,
    knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean,
    knobConfiguration: KnobConfiguration
) {
    moveTo(left, top)
    if (placeY == 0) {
        lineTo(left + width, top)
    } else {
        pieceEdge(
            offsetX = left,
            offsetY = top,
            length = width,
            pieceHeight = height,
            orientation = Orientation.Top,
            knobInverted = knobInversionEvaluator(placeX, placeY),
            knobConfiguration = knobConfiguration
        )
    }
    if (placeX == placesHorizontal - 1) {
        lineTo(left + width, top + height)
    } else {
        pieceEdge(
            offsetX = left + width,
            offsetY = top,
            length = height,
            pieceHeight = width,
            orientation = Orientation.Right,
            knobInverted = knobInversionEvaluator(placeX + 1, placeY),
            knobConfiguration = knobConfiguration
        )
    }
    if (placeY == placesVertical - 1) {
        lineTo(left, top + height)
    } else {
        pieceEdge(
            offsetX = left + width,
            offsetY = top + height,
            length = width,
            pieceHeight = height,
            orientation = Orientation.Bottom,
            knobInverted = !knobInversionEvaluator(placeX, placeY + 1),
            knobConfiguration = knobConfiguration
        )
    }
    if (placeX == 0) {
        lineTo(left, top)
    } else {
        pieceEdge(
            offsetX = left,
            offsetY = top + height,
            length = height,
            pieceHeight = width,
            orientation = Orientation.Left,
            knobInverted = !knobInversionEvaluator(placeX, placeY),
            knobConfiguration = knobConfiguration
        )
    }
    close()
}

private fun Path.pieceEdge(
    offsetX: Float,
    offsetY: Float,
    length: Float,
    pieceHeight: Float,
    orientation: Orientation,
    knobConfiguration: KnobConfiguration,
    knobInverted: Boolean = false
) {
    val edgeInset = pieceHeight * knobConfiguration.edgeInsetRatio
    val knobBaseDistance = -pieceHeight * knobConfiguration.knobBaseWeight
    val knobMidDistance = -pieceHeight * knobConfiguration.knobMidDistanceRatio
    val invertedMultiplier = if (knobInverted) -1f else 1f

    val startControlPoint1 = orientation.oriented(
        PointF(
            length * knobConfiguration.knobBaseControlStrength1,
            edgeInset * invertedMultiplier
        )
    )
    val startControlPoint2 = orientation.oriented(
        PointF(
            length * knobConfiguration.knobBaseControlStrength2,
            knobBaseDistance * invertedMultiplier
        )
    )
    val knobBaseStartPoint = orientation.oriented(
        PointF(length * knobConfiguration.knobBaseStart, knobMidDistance * invertedMultiplier)
    )
    cubicTo(
        offsetX + startControlPoint1.x,
        offsetY + startControlPoint1.y,
        offsetX + startControlPoint2.x,
        offsetY + startControlPoint2.y,
        offsetX + knobBaseStartPoint.x,
        offsetY + knobBaseStartPoint.y
    )

    val knobTopDistance = -pieceHeight * knobConfiguration.knobEndDistanceRatio
    val knobControlPoint1 = orientation.oriented(
        PointF(length * knobConfiguration.knobTopPinchWeight, knobTopDistance * invertedMultiplier)
    )
    val knobControlPoint2 = orientation.oriented(
        PointF(
            length * (1f - knobConfiguration.knobTopPinchWeight),
            knobTopDistance * invertedMultiplier
        )
    )
    val knobBaseEndPoint = orientation.oriented(
        PointF(
            length * (1f - knobConfiguration.knobBaseStart),
            knobMidDistance * invertedMultiplier
        )
    )
    cubicTo(
        offsetX + knobControlPoint1.x,
        offsetY + knobControlPoint1.y,
        offsetX + knobControlPoint2.x,
        offsetY + knobControlPoint2.y,
        offsetX + knobBaseEndPoint.x,
        offsetY + knobBaseEndPoint.y
    )

    val endControlPoint1 = orientation.oriented(
        PointF(
            length * (1f - knobConfiguration.knobBaseControlStrength2),
            knobBaseDistance * invertedMultiplier
        )
    )
    val endControlPoint2 = orientation.oriented(
        PointF(
            length * (1f - knobConfiguration.knobBaseControlStrength1),
            edgeInset * invertedMultiplier
        )
    )
    val endPoint = orientation.oriented(PointF(length, 0f))
    cubicTo(
        offsetX + endControlPoint1.x,
        offsetY + endControlPoint1.y,
        offsetX + endControlPoint2.x,
        offsetY + endControlPoint2.y,
        offsetX + endPoint.x,
        offsetY + endPoint.y
    )
}

object JigsawLoaderDefaults {
    val color: Color
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.colorScheme.primary

    val trackColor: Color
        @ReadOnlyComposable
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant

    val brushProvider
        @ReadOnlyComposable
        @Composable
        get() = ColorBrushProvider(color)

    val horizontalPieces: Int = 12
    val verticalPieces: Int = 4

    val knobInversionEvaluator: (placeX: Int, placeY: Int) -> Boolean = { x, y ->
        Random(x + y * 46340).nextBoolean()
    }

    val knobConfiguration = KnobConfiguration()

    val flatKnobConfiguration = KnobConfiguration(
        edgeInsetRatio = 0f,
        knobBaseWeight = -.02f,
        knobBaseStart = .3f,
        knobBaseControlStrength1 = .5f,
        knobBaseControlStrength2 = .5f,
        knobTopPinchWeight = .49f,
        knobMidDistanceRatio = .155f,
        knobEndDistanceRatio = .16f
    )

    val indeterminatePiecePresenceThreshold = .15f

    fun piecePresenceResolver(
        progressState: ProgressState,
        horizontalPieces: Int,
        verticalPieces: Int
    ): PiecePresenceResolver = if (progressState is Indeterminate) {
        IndeterminatePiecePresenceResolver(indeterminatePiecePresenceThreshold)
    } else {
        ProgressPiecePresenceResolver(
            progressState = progressState,
            horizontalPieces = horizontalPieces,
            verticalPieces = verticalPieces
        )
    }
}

@Preview
@Composable
fun Preview() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(360.dp)
            .height(120.dp)
            .padding(8.dp)
    ) {
        JigsawLoader(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
