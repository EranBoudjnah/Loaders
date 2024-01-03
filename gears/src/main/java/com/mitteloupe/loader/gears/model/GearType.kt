package com.mitteloupe.loader.gears.model

sealed interface GearType {
    data object Sharp : GearType

    data object Square : GearType
}
