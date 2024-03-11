package com.mitteloupe.loader.jigsaw.model

import android.graphics.PointF

sealed interface Orientation {
    fun oriented(point: PointF): PointF

    data object Top : Orientation {
        override fun oriented(point: PointF) = point
    }

    data object Right : Orientation {
        override fun oriented(point: PointF) = PointF(-point.y, point.x)
    }

    data object Bottom : Orientation {
        override fun oriented(point: PointF) = PointF(-point.x, -point.y)
    }

    data object Left : Orientation {
        override fun oriented(point: PointF) = PointF(point.y, -point.x)
    }
}
