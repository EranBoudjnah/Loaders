package com.mitteloupe.loader.gears.model

interface ProgressState {
    fun stateAtPosition(range: Int, value: Int): Float

    data object Indeterminate : ProgressState {
        override fun stateAtPosition(range: Int, value: Int): Float = 1f
    }

    data class Determinate(
        val progress: Float,
        val tolerance: Float = 0f
    ) : ProgressState {
        override fun stateAtPosition(range: Int, value: Int): Float {
            val floatRange = range.toFloat()
            val relativeValue = progress * floatRange
            val relativeTolerance = tolerance * floatRange
            return when {
                value <= relativeValue -> 1f
                value > relativeValue + relativeTolerance -> 0f
                else -> 1f - (value - relativeValue) / relativeTolerance
            }
        }
    }
}
