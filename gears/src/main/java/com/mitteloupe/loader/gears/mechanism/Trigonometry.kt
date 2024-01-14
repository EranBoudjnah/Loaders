package com.mitteloupe.loader.gears.mechanism

import com.mitteloupe.loader.trigonometry.PI_FLOAT
import kotlin.math.asin

fun numberOfTeeth(toothWidth: Float, outerRadius: Float, toothDepth: Float): Int =
    (PI_FLOAT / asin(toothWidth / ((outerRadius - toothDepth) * 2f))).toInt()
