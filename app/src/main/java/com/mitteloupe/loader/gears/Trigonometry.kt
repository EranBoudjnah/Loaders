package com.mitteloupe.loader.gears

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

const val PI_FLOAT = PI.toFloat()
const val PI_FLOAT_HALF = PI.toFloat() / 2f
const val PI_FLOAT_2 = PI_FLOAT * 2f

val Float.degrees: Float
    get() = this * 180f / PI_FLOAT
val Float.radians: Float
    get() = this / 180f * PI_FLOAT

fun Float.atan2(secondValue: Float): Float = atan2(this, secondValue)

fun Float.sqrt(): Float = sqrt(this)

fun distance(point1: PointF, point2: PointF): Float =
    distance(point1.x, point1.y, point2.x, point2.y)

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    hypot(x2 - x1, y2 - y1)

fun numberOfTeeth(toothWidth: Float, outerRadius: Float, toothDepth: Float): Int =
    (PI_FLOAT / asin(toothWidth / (2f * (outerRadius - toothDepth)))).toInt()
