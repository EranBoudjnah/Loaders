package com.mitteloupe.loader.jigsaw.model

data class KnobConfiguration(
    val edgeInsetRatio: Float = .15f,
    val knobBaseWeight: Float = .01f,
    val knobBaseStart: Float = .35f,
    val knobBaseControlStrength1: Float = .7f,
    val knobBaseControlStrength2: Float = .3f,
    val knobTopPinchWeight: Float = .4f,
    val knobMidDistanceRatio: Float = .17f,
    val knobEndDistanceRatio: Float = .32f
)
