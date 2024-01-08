package com.mitteloupe.loader.gears.model

interface ProgressState {
    fun stateAtPosition(range: Float, value: Float): Float

    data object Indeterminate : ProgressState {
        override fun stateAtPosition(range: Float, value: Float): Float = 1f
    }

    data class Determinate(
        val progress: Float
    ) : ProgressState {
        override fun stateAtPosition(range: Float, value: Float): Float {
            val relativeValue = progress * range
            return if (progress >= 1f || value.safeLesserOrEqual(relativeValue)) 1f else 0f
        }
    }
}
