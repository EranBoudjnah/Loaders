package com.mitteloupe.loader.gears.model

import kotlin.math.abs

const val PRECISION_CORRECTION = 0.0001f

fun Float.safeLesser(value: Float) = !safeEqual(value) && this < value - PRECISION_CORRECTION

fun Float.safeLesserOrEqual(value: Float) = this < value + PRECISION_CORRECTION

fun Float.safeGreater(value: Float) = !safeEqual(this) && this > value - PRECISION_CORRECTION

fun Float.safeGreaterOrEqual(value: Float) = this > value - PRECISION_CORRECTION

fun Float.safeIn(lesserValue: Float, greaterValue: Float) =
    this in lesserValue - PRECISION_CORRECTION..greaterValue + PRECISION_CORRECTION

fun Float.safeEqual(value: Float) = abs(this - value) < PRECISION_CORRECTION
