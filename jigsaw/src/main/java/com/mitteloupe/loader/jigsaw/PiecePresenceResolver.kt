package com.mitteloupe.loader.jigsaw

import com.mitteloupe.loader.jigsaw.model.ProgressState
import kotlin.random.Random

interface PiecePresenceResolver {
    fun iterate()

    fun piecePresent(x: Int, y: Int): Boolean

    data class ProgressPiecePresenceResolver(
        private val progressState: ProgressState,
        private val horizontalPieces: Int,
        private val verticalPieces: Int
    ) : PiecePresenceResolver {
        override fun iterate() = Unit

        override fun piecePresent(x: Int, y: Int): Boolean =
            progressState.stateAtPosition(x, y, horizontalPieces, verticalPieces)
    }

    data class IndeterminatePiecePresenceResolver(
        private val threshold: Float
    ) : PiecePresenceResolver {
        private var seed = 0

        override fun iterate() {
            seed = Random.nextInt()
        }

        override fun piecePresent(x: Int, y: Int): Boolean =
            Random(seed + x + y * 46340).nextFloat() >= threshold
    }
}
