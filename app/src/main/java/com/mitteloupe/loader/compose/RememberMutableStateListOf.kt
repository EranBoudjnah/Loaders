package com.mitteloupe.loader.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

@Composable
fun <T : Any> rememberMutableStateListOf(): SnapshotStateList<T> = rememberSaveable(
    saver = listSaver(
        save = { stateList ->
            if (stateList.isNotEmpty()) {
                val first = stateList.first()
                require(canBeSaved(first)) {
                    "${first::class} cannot be stored in the Bundle class."
                }
            }
            stateList.toList()
        },
        restore = { it.toMutableStateList() }
    )
) {
    mutableStateListOf()
}
