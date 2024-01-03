package com.mitteloupe.loader.gears.model

interface ProgressState {
    fun stateAtPosition(range: Int, value: Int): Float

    data object Indefinite : ProgressState {
        override fun stateAtPosition(range: Int, value: Int): Float = 1f
    }

    data class Progress(
        val value: Int,
        val minimum: Int = 0,
        val maximum: Int = 100,
        val tolerance: Int = 0
    ) : ProgressState {
        private val range = maximum - minimum
        private val normalizedValue = (value - minimum).toFloat() / range.toFloat()
        override fun stateAtPosition(range: Int, value: Int): Float {
            val floatRange = range.toFloat()
            val relativeValue = normalizedValue * floatRange
            val relativeTolerance = tolerance.toFloat() / this.range.toFloat() * floatRange
            return when {
                value <= relativeValue -> 1f
                value > relativeValue + relativeTolerance -> 0f
                else -> 1f - (value - relativeValue) / relativeTolerance
            }
        }
    }
}
