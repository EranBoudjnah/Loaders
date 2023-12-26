package com.mitteloupe.loader.gears

import android.graphics.PointF
import android.graphics.RectF
import com.mitteloupe.loader.gears.model.Arc
import com.mitteloupe.loader.gears.model.Gear
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.random.Random

class RectangleFiller {
    private val intersectionRect = RectF()

    fun fill(
        rectangle: RectF,
        minimumRadius: Float,
        maximumRadius: Float,
        toothDepth: Float,
        toothWidth: Float
    ): List<Gear> {
        val gears = mutableListOf(
            initialCircle(rectangle, minimumRadius, maximumRadius, toothWidth)
        )

        var index = 0
        do {
            var canAddConnectedGears = true
            val targetGearId = gears.indexOfFirst { gear ->
                gear.canBeExtended
            }
            val originGear = gears[targetGearId]
            while (canAddConnectedGears) {
                canAddConnectedGears =
                    gears.addGear(
                        originGear = originGear,
                        rectangle = rectangle,
                        minimumRadius = minimumRadius,
                        maximumRadius = maximumRadius,
                        toothDepth = toothDepth,
                        toothWidth = toothWidth,
                        relativePosition = 0f
                    )
            }

            gears[targetGearId] = originGear.copy(canBeExtended = false)
            index++
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
        relativePosition: Float = Random.nextFloat()
    ): Boolean {
        val nextRadius = randomGearSize(minimumRadius, maximumRadius, toothWidth)
        intersectionRect.set(rectangle)
        intersectionRect.inset(nextRadius, nextRadius)
        val arcsInRect =
            originGear.outerArc(nextRadius - toothDepth)
                .intersectionWithRectangle(intersectionRect)
        val validNextPoints =
            filter { currentGear ->
                currentGear != originGear
            }.fold(arcsInRect) { arcs, currentGear ->
                val validGear = currentGear.outerRadius(
                    if (currentGear.isClockwise != originGear.isClockwise) {
                        nextRadius + toothDepth
                    } else {
                        nextRadius - toothDepth
                    }
                )
                arcs.flatMap { currentArc ->
                    currentArc.subtractGear(validGear)
                }
            }

        if (validNextPoints.isEmpty()) {
            return false
        }

        pointOnArcs(validNextPoints, relativePosition)?.let { newCenter ->
            add(
                Gear(
                    center = newCenter,
                    radius = nextRadius,
                    rotation = meshingAngle(originGear, newCenter, nextRadius, toothWidth),
                    toothWidth = toothWidth,
                    isClockwise = !originGear.isClockwise
                )
            )
        } ?: return false

        intersectionRect.set(rectangle)
        intersectionRect.inset(minimumRadius, minimumRadius)
        val remainingNextPoints = if (size == 1) {
            arcsInRect
        } else {
            filter { currentCircle ->
                currentCircle != originGear
            }.fold(arcsInRect) { arcs, currentCircle ->
                val outerCircle = currentCircle.outerRadius(
                    if (currentCircle.isClockwise != originGear.isClockwise) {
                        nextRadius + toothDepth
                    } else {
                        nextRadius - toothDepth
                    }
                )
                arcs.flatMap { currentArc ->
                    currentArc.subtractGear(outerCircle)
                }
            }
        }
        return remainingNextPoints.any { it.length >= 1f }
    }

    private fun initialCircle(
        rectangle: RectF,
        minimumGearRadius: Float,
        maximumGearRadius: Float,
        toothWidth: Float
    ) = Gear(
        center = PointF(
            rectangle.centerX(),
            rectangle.centerY()
        ),
        radius = randomGearSize(
            minimumRadius = minimumGearRadius,
            maximumRadius = maximumGearRadius,
            toothWidth = toothWidth
        ),
        rotation = 0f,
        toothWidth = toothWidth,
        isClockwise = true
    )

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
        val randomRadius = Random.nextFloat() * (maximumRadius - minimumRadius) + minimumRadius
        var numberOfTeeth = (randomRadius * 2f / toothWidth).toInt()
        if (randomRadius * 2f / toothWidth != numberOfTeeth.toFloat()) {
            numberOfTeeth = round(randomRadius * 2f / toothWidth).toInt()
        }
        var radius = (numberOfTeeth * toothWidth) / 2f
        if (radius < minimumRadius) {
            radius += toothWidth
        } else if (radius > maximumRadius) {
            radius -= toothWidth
        }
        return radius
    }

    private fun meshingAngle(
        firstGear: Gear,
        newGearCenter: PointF,
        newGearRadius: Float,
        toothWidth: Float
    ): Float {
        val newGearToothCount = (newGearRadius * 2f / toothWidth).toInt()
        val teethRatio = firstGear.teethCount.toFloat() / newGearToothCount.toFloat()
        val newToothSweep = (360f / newGearToothCount.toFloat()).radians
        var newRotation = if (newGearToothCount % 2f == 0f) {
            newToothSweep / 2f
        } else {
            0f
        }

        newRotation += firstGear.rotation * teethRatio

        val angleBetweenCogs = atan2(
            newGearCenter.y - firstGear.center.y,
            newGearCenter.x - firstGear.center.x
        ) + PI_FLOAT

        return newRotation + angleBetweenCogs * teethRatio
    }
}
