package com.mitteloupe.loader.gears.model

import android.graphics.PointF

operator fun PointF.minus(other: PointF) = PointF(this.x - other.x, this.y - other.y)

operator fun PointF.plus(other: PointF) = PointF(this.x + other.x, this.y + other.y)

operator fun PointF.times(multiplier: Float) = PointF(this.x * multiplier, this.y * multiplier)

operator fun PointF.div(divider: Float) = PointF(this.x / divider, this.y / divider)
