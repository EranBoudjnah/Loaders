package com.mitteloupe.loader.jigsaw.model

interface ProgressState {
    fun stateAtPosition(x: Int, y: Int, horizontalPieces: Int, verticalPieces: Int): Boolean

    data object Indeterminate : ProgressState {
        override fun stateAtPosition(x: Int, y: Int, horizontalPieces: Int, verticalPieces: Int) =
            true
    }

    data class DeterminateSpiral(
        val progress: Float
    ) : ProgressState {
        override fun stateAtPosition(
            x: Int,
            y: Int,
            horizontalPieces: Int,
            verticalPieces: Int
        ): Boolean {
            if (progress <= 0f) return false
            if (progress >= 1f) return true
            val progress = ((horizontalPieces * verticalPieces).toFloat() * progress).toInt()

            var remaining = progress
            var currentX = x
            var currentY = y
            var currentHorizontalPieces = horizontalPieces
            var currentVerticalPieces = verticalPieces - 2
            var cellsInLayer: Int
            do {
                if (isOnEdge(currentX, currentY, currentHorizontalPieces, currentVerticalPieces)) {
                    return isActive(
                        currentX,
                        currentY,
                        remaining,
                        currentHorizontalPieces,
                        currentVerticalPieces
                    )
                }
                cellsInLayer = (currentHorizontalPieces + currentVerticalPieces) * 2
                currentX--
                currentY--
                currentHorizontalPieces -= 2
                currentVerticalPieces -= 2
                remaining -= cellsInLayer
            } while (remaining > 0 && cellsInLayer > 0)

            return false
        }

        private fun isOnEdge(
            x: Int,
            y: Int,
            horizontalPieces: Int,
            verticalPieces: Int
        ): Boolean = x == 0 || y == 0 ||
            x == horizontalPieces - 1 || y == verticalPieces + 1

        private fun isActive(
            x: Int,
            y: Int,
            progress: Int,
            horizontalPieces: Int,
            verticalPieces: Int
        ): Boolean {
            val isOnTop = x <= progress && y == 0
            if (isOnTop) return true
            val isOnRight =
                x == horizontalPieces - 1 &&
                    y <= progress - horizontalPieces
            if (isOnRight) return true
            val isOnBottom =
                y == verticalPieces + 1 &&
                    horizontalPieces - x <= progress - horizontalPieces - verticalPieces
            if (isOnBottom) return true
            return x == 0 &&
                verticalPieces - y <= progress - horizontalPieces * 2 - verticalPieces
        }
    }

    data class DeterminateSweep(
        val progress: Float
    ) : ProgressState {
        override fun stateAtPosition(
            x: Int,
            y: Int,
            horizontalPieces: Int,
            verticalPieces: Int
        ): Boolean {
            if (progress <= 0f) return false
            if (progress >= 1f) return true
            val progress = ((horizontalPieces * verticalPieces).toFloat() * progress).toInt()

            val progressX = progress / verticalPieces
            if (x < progressX) return true
            if (x > progressX) return false

            val isTopToBottom = progressX % 2 == 0
            val progressY = progress % verticalPieces
            if (isTopToBottom && y <= progressY) return true
            return !isTopToBottom && y >= verticalPieces - progressY - 1
        }
    }
}
