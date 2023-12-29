package com.mitteloupe.loader.gears

import android.graphics.PointF
import com.mitteloupe.loader.gears.model.Gear
import kotlin.math.atan2

class GearMesher {
    fun meshingAngle(
        firstGear: Gear,
        newGearCenter: PointF,
        newGearRadius: Float
    ): Float {
        val newGearTeethCount = numberOfTeeth(
            toothWidth = firstGear.toothWidth,
            outerRadius = newGearRadius,
            toothDepth = firstGear.toothDepth
        )
        val teethRatio = firstGear.teethCount.toFloat() / newGearTeethCount.toFloat()

        val angleBetweenCogs = atan2(
            newGearCenter.y - firstGear.center.y,
            newGearCenter.x - firstGear.center.x
        ) + PI_FLOAT_HALF

        val evenNewGearTeethCountOffset = if (newGearTeethCount % 2 == 0) {
            (PI_FLOAT_2 / newGearTeethCount.toFloat()) / 2f
        } else {
            0f
        }

        return angleBetweenCogs * (teethRatio + 1) - firstGear.rotation * teethRatio +
            evenNewGearTeethCountOffset
    }
}
