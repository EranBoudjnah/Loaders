package com.mitteloupe.loader.gears.model

import android.graphics.PointF
import android.graphics.RectF

fun RectF.distanceTo(point: PointF) = minOf(
    point.x - left,
    right - point.x,
    point.y - top,
    bottom - point.y
)

val RectF.edges: List<Edge>
    get() = listOf(
        Edge(PointF(left, top), PointF(right, top)),
        Edge(PointF(right, top), PointF(right, bottom)),
        Edge(PointF(right, bottom), PointF(left, bottom)),
        Edge(PointF(left, bottom), PointF(left, top))
    )

fun RectF.contains(point: PointF) = point.x in left..right && point.y in top..bottom
