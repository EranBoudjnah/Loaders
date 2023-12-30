package com.mitteloupe.loader.gears.mechanism

import android.graphics.PointF
import android.graphics.RectF
import com.mitteloupe.loader.gears.model.Arc
import com.mitteloupe.loader.gears.model.Gear
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.random.Random

class RectangleFiller(
    private val gearMesher: GearMesher
) {
    private val intersectionRect = RectF()

    fun fill(
        rectangle: RectF,
        minimumRadius: Float,
        maximumRadius: Float,
        toothDepth: Float,
        toothWidth: Float
    ): List<Gear> {
        val gears = mutableListOf(
            initialGear(
                rectangle = rectangle,
                minimumGearRadius = minimumRadius,
                maximumGearRadius = maximumRadius,
                toothWidth = toothWidth,
                toothDepth = toothDepth
            )
        )

        do {
            var canAddConnectedGears = true
            val originGearId = gears.indexOfFirst { gear ->
                gear.canBeExtended
            }
            val originGear = gears[originGearId]
            while (canAddConnectedGears) {
                canAddConnectedGears =
                    gears.addGear(
                        originGear = originGear,
                        rectangle = rectangle,
                        minimumRadius = minimumRadius,
                        maximumRadius = maximumRadius,
                        toothDepth = toothDepth,
                        toothWidth = toothWidth
                    )
            }

            gears[originGearId] = originGear.copy(canBeExtended = false)
        } while (gears.any { it.canBeExtended })

        return gears
    }

    private fun MutableList<Gear>.addGear(
        originGear: Gear,
        rectangle: RectF,
        minimumRadius: Float,
        maximumRadius: Float,
        toothWidth: Float,
        toothDepth: Float,
        relativePosition: Float = Random.nextFloat(),
        nextRadius: Float = randomGearSize(minimumRadius, maximumRadius, toothWidth)
    ): Boolean {
        updateIntersectionRectForRadius(rectangle, nextRadius)
        if (intersectionRect.isEmpty) return false

        val distanceToNewGear = nextRadius
        val arcsInRect = originGear.outerArc(distanceToNewGear)
            .intersectionWithRectangle(intersectionRect)
        val validNextPoints = validArcs(originGear, arcsInRect, distanceToNewGear)

        if (validNextPoints.isEmpty()) {
            if (nextRadius > minimumRadius) {
                return addGear(
                    originGear = originGear,
                    rectangle = rectangle,
                    minimumRadius = minimumRadius,
                    maximumRadius = maximumRadius,
                    toothWidth = toothWidth,
                    toothDepth = toothDepth,
                    relativePosition = relativePosition,
                    nextRadius = minimumRadius
                )
            }
            return false
        }

        pointOnArcs(validNextPoints, relativePosition)?.let { newCenter ->
            add(
                Gear(
                    center = newCenter,
                    radius = nextRadius,
                    rotation = gearMesher.meshingAngle(
                        firstGear = originGear,
                        newGearCenter = newCenter,
                        newGearRadius = nextRadius
                    ),
                    toothWidth = toothWidth,
                    toothDepth = toothDepth,
                    isClockwise = !originGear.isClockwise
                )
            )
        } ?: run {
            if (nextRadius > minimumRadius) {
                return addGear(
                    originGear = originGear,
                    rectangle = rectangle,
                    minimumRadius = minimumRadius,
                    maximumRadius = maximumRadius,
                    toothWidth = toothWidth,
                    toothDepth = toothDepth,
                    relativePosition = relativePosition,
                    nextRadius = minimumRadius
                )
            }

            return false
        }

        return originHasMoreSpace(
            originGear,
            rectangle,
            minimumRadius,
            arcsInRect,
            distanceToNewGear
        )
    }

    private fun List<Gear>.originHasMoreSpace(
        originGear: Gear,
        rectangle: RectF,
        minimumRadius: Float,
        arcsInRect: List<Arc>,
        distanceToNewGear: Float
    ): Boolean {
        updateIntersectionRectForRadius(rectangle, minimumRadius)
        if (intersectionRect.isEmpty) return false
        val remainingNextPoints = if (size == 1) {
            arcsInRect
        } else {
            validArcs(originGear, arcsInRect, distanceToNewGear)
        }
        return remainingNextPoints.any { it.length >= .1f }
    }

    private fun updateIntersectionRectForRadius(rectangle: RectF, minimumRadius: Float) {
        intersectionRect.set(rectangle)
        intersectionRect.inset(minimumRadius, minimumRadius)
    }

    private fun List<Gear>.validArcs(
        originGear: Gear,
        arcsInRect: List<Arc>,
        distanceToNewGear: Float
    ) = filter { currentGear ->
        currentGear != originGear
    }.fold(arcsInRect) { arcs, currentGear ->
        val validGear = currentGear.outerRadius(distanceToNewGear)
        arcs.flatMap { currentArc ->
            currentArc.subtractGear(validGear)
        }
    }

    private fun initialGear(
        rectangle: RectF,
        minimumGearRadius: Float,
        maximumGearRadius: Float,
        toothWidth: Float,
        toothDepth: Float
    ): Gear = randomGearSize(
        minimumRadius = minimumGearRadius,
        maximumRadius = maximumGearRadius,
        toothWidth = toothWidth
    ).let { radius ->
        Gear(
            center = PointF(
                rectangle.centerX(),
                rectangle.centerY()
            ),
            radius = radius,
            rotation = 0f,
            toothWidth = toothWidth,
            toothDepth = toothDepth,
            isClockwise = true
        )
    }

    private fun pointOnArcs(
        arcs: List<Arc>,
        relativePosition: Float = Random.nextFloat()
    ): PointF? {
        val totalArcsLength = arcs.sumOf { arc ->
            arc.length.toDouble()
        }.toFloat()

        if (totalArcsLength <= 0f) {
            return null
        }

        val randomPointOnLength = relativePosition * totalArcsLength

        var remaining = randomPointOnLength
        val selectedArc = arcs.firstOrNull { arc ->
            remaining -= arc.length
            remaining <= 0
        }

        requireNotNull(selectedArc) {
            "Somehow the point is outside of all arcs ($totalArcsLength)."
        }

        val length = selectedArc.length
        val ratio = (length + remaining) / length
        val pointAngle = selectedArc.startAngle + ratio * selectedArc.sweepAngle
        val x = selectedArc.center.x + selectedArc.radius * cos(pointAngle)
        val y = selectedArc.center.y + selectedArc.radius * sin(pointAngle)
        return PointF(x, y)
    }

    private fun randomGearSize(
        minimumRadius: Float,
        maximumRadius: Float,
        toothWidth: Float
    ): Float {
        val seedRadius = Random.nextFloat() * (maximumRadius - minimumRadius) + minimumRadius
        val seedDiameter = seedRadius * 2f
        var numberOfTeeth = (seedDiameter / toothWidth).toInt()
        if (seedDiameter / toothWidth != numberOfTeeth.toFloat()) {
            numberOfTeeth = round(seedDiameter / toothWidth).toInt()
        }
        var resultRadius = (numberOfTeeth * toothWidth) / 2f
        if (resultRadius < minimumRadius) {
            resultRadius += toothWidth
        } else if (resultRadius > maximumRadius) {
            resultRadius -= toothWidth
        }
        return resultRadius
    }
}
