package com.mitteloupe.loader.jigsaw

import android.graphics.Matrix
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize

sealed interface BrushProvider {
    @Composable
    operator fun invoke(targetComposableSize: IntSize): Brush

    data class ColorBrushProvider(private val color: Color) : BrushProvider {
        @Composable
        override fun invoke(targetComposableSize: IntSize) =
            SolidColor(color)
    }

    data class ImageResourceBrushProvider(
        @DrawableRes private val resourceId: Int,
    ) : BrushProvider {
        @Composable
        override fun invoke(targetComposableSize: IntSize): Brush {
            val imageBitmap = ImageBitmap.imageResource(resourceId)

            val imageShader by remember {
                mutableStateOf(ImageShader(imageBitmap, TileMode.Clamp, TileMode.Clamp))
            }

            val imageMatrix by remember(targetComposableSize) {
                val matrix = Matrix()
                if (targetComposableSize == IntSize.Zero) {
                    return@remember derivedStateOf { matrix }
                }
                val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height
                val composableAspectRatio = targetComposableSize.width / targetComposableSize.height
                val scaleFactor: Float
                val translationOffset: Float
                if (imageAspectRatio > composableAspectRatio) {
                    scaleFactor =
                        targetComposableSize.height.toFloat() / imageBitmap.height.toFloat()
                    translationOffset =
                        (targetComposableSize.width - imageBitmap.width * scaleFactor) / 2f
                    matrix.setScale(scaleFactor, scaleFactor)
                    matrix.postTranslate(translationOffset, 0f)
                } else {
                    scaleFactor = targetComposableSize.width.toFloat() / imageBitmap.width
                    translationOffset =
                        (targetComposableSize.height - imageBitmap.height * scaleFactor) / 2
                    matrix.setScale(scaleFactor, scaleFactor)
                    matrix.setTranslate(0f, translationOffset)
                }
                derivedStateOf {
                    matrix
                }
            }

            imageShader.setLocalMatrix(imageMatrix)
            return ShaderBrush(imageShader)
        }
    }
}
