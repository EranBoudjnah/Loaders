package com.mitteloupe.loader.gears.model

import android.graphics.PointF
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_2
import com.mitteloupe.loader.gears.mechanism.numberOfTeeth

data class Gear(
    override val center: PointF,
    override val radius: Float,
    val rotation: Float,
    val isClockwise: Boolean,
    val toothWidth: Float,
    val toothDepth: Float,
    val canBeExtended: Boolean = true
) : Circular {
    val teethCount = numberOfTeeth(toothWidth, radius, toothDepth)
    val relativeSpeed = if (isClockwise) {
        1f / teethCount
    } else {
        -1f / teethCount
    }

    fun outerRadius(outerRadius: Float) =
        Gear(
            center = center,
            radius = radius + outerRadius,
            rotation = rotation,
            toothWidth = toothWidth,
            toothDepth = toothDepth,
            isClockwise = isClockwise
        )

    fun outerArc(outerRadius: Float): Arc =
        Arc(center, radius + outerRadius, -PI_FLOAT / 2f, PI_FLOAT_2)

    fun contains(point: PointF): Boolean {
        val distanceFromCenter = center - point
        return distanceFromCenter.x * distanceFromCenter.x +
            distanceFromCenter.y * distanceFromCenter.y <= radius * radius
    }
}
