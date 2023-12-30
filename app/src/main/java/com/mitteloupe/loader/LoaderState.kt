package com.mitteloupe.loader

sealed interface LoaderState {
    data object Indefinite : LoaderState

    data class Progress(
        val value: Int,
        val minimum: Int = 0,
        val maximum: Int = 100
    ) : LoaderState
}
