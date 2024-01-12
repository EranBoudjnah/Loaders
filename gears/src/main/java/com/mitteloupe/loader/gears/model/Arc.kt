package com.mitteloupe.loader.gears.model

import android.graphics.PointF
import android.graphics.RectF
import com.mitteloupe.loader.trigonometry.PI_FLOAT_2
import com.mitteloupe.loader.trigonometry.PRECISION_CORRECTION
import com.mitteloupe.loader.trigonometry.atan2
import com.mitteloupe.loader.trigonometry.distance
import com.mitteloupe.loader.trigonometry.safeGreater
import com.mitteloupe.loader.trigonometry.safeGreaterOrEqual
import com.mitteloupe.loader.trigonometry.safeIn
import com.mitteloupe.loader.trigonometry.safeLesser
import com.mitteloupe.loader.trigonometry.safeLesserOrEqual
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Arc(
    override val center: PointF,
    override val radius: Float,
    val startAngle: Float,
    val sweepAngle: Float
) : Circular {
    private var _length: Float? = null
    val length: Float
        get() {
            val value = _length
            return if (value == null) {
                val result = radius * sweepAngle
                _length = result
                result
            } else {
                value
            }
        }

    fun intersectionWithRectangle(rectangle: RectF): List<Arc> {
        if (rectangle.contains(center) &&
            rectangle.distanceTo(center).safeGreaterOrEqual(radius)
        ) {
            return listOf(this)
        }

        val intersectionPoints = rectangle.edges.flatMap { edge ->
            edge.intersectWithCircle(this)
        }

        if (intersectionPoints.isEmpty()) return emptyList()

        val sweepAngles = intersectionPoints.map { point ->
            (point.y - center.y).atan2(point.x - center.x)
        }.map { angleToPoint ->
            var sweepAngleToPoint = angleToPoint - startAngle
            while (sweepAngleToPoint < 0f) {
                sweepAngleToPoint += PI_FLOAT_2
            }
            sweepAngleToPoint
        }.sorted() + sweepAngle

        return sweepAngles.mapIndexedNotNull { index, sweepAngle ->
            val previousSweepAngle = if (index > 0) sweepAngles[index - 1] else 0f
            val arc = Arc(
                center,
                radius,
                startAngle + previousSweepAngle,
                sweepAngle - previousSweepAngle
            )
            val floatSafeRectangle =
                RectF(rectangle).apply { inset(PRECISION_CORRECTION, PRECISION_CORRECTION) }
            val arcMidPoint = PointF(
                arc.center.x + arc.radius * cos(arc.startAngle + arc.sweepAngle / 2f),
                arc.center.y + arc.radius * sin(arc.startAngle + arc.sweepAngle / 2f)
            )
            if (floatSafeRectangle.contains(arcMidPoint)) {
                arc
            } else {
                null
            }
        }
    }

    fun subtractGear(gear: Gear): List<Arc> {
        if (!intersectsWithCircle(gear)) {
            if (containedInCircle(gear)) {
                return emptyList()
            }

            return listOf(this)
        }

        val intersections = intersectionSweepAngles(gear)
        if (intersections.isEmpty()) {
            return listOf(this)
        }

        val arcs = mutableListOf<Arc>()
        var currentStart = startAngle
        var lastIntersection = 0f

        (intersections + sweepAngle).sorted().forEach { intersection ->
            val normalizedIntersection = intersection - lastIntersection
            val nextArc = Arc(
                center,
                radius,
                currentStart,
                normalizedIntersection
            )
            if (!nextArc.intersectsWithCircle(gear) || !nextArc.containedInCircle(gear)) {
                arcs.add(nextArc)
            }
            currentStart += (intersection - lastIntersection)
            lastIntersection = intersection
        }
        return arcs
    }

    private fun containedInCircle(gear: Gear): Boolean {
        val arcEndPoint1 = center.pointAtAngle(startAngle)
        val endAngle = startAngle + sweepAngle
        val arcEndPoint2 = center.pointAtAngle(endAngle)
        val middleAngle = startAngle + sweepAngle / 2f
        val arcMiddlePoint = center.pointAtAngle(middleAngle)

        val gearCenter = gear.center

        return distance(gearCenter, arcEndPoint1).safeLesserOrEqual(gear.radius) &&
            distance(gearCenter, arcEndPoint2).safeLesserOrEqual(gear.radius) &&
            distance(gearCenter, arcMiddlePoint).safeLesserOrEqual(gear.radius)
    }

    private fun intersectionPoints(gear: Gear): List<PointF> {
        val centersDistance = distance(center, gear.center)

        if (centersDistance.safeGreater(radius + gear.radius)) {
            return emptyList()
        }

        val radiusSquared = radius * radius
        val circleRadiusSquared = gear.radius * gear.radius
        val centersDistanceSquared = centersDistance * centersDistance
        val center1ToIntersectionDistance =
            (radiusSquared - circleRadiusSquared + centersDistanceSquared) / (centersDistance * 2f)
        val intersectionToLineDistance = sqrt(radiusSquared - center1ToIntersectionDistance.pow(2))

        val intersectionLineCenter = center + PointF(
            center1ToIntersectionDistance * (gear.center.x - center.x) / centersDistance,
            center1ToIntersectionDistance * (gear.center.y - center.y) / centersDistance
        )

        val intersectionPoint1 = intersectionLineCenter + PointF(
            intersectionToLineDistance * (gear.center.y - center.y) / centersDistance,
            -intersectionToLineDistance * (gear.center.x - center.x) / centersDistance
        )

        val potentialIntersectionPoints = if (centersDistance == radius + gear.radius) {
            listOf(intersectionPoint1)
        } else {
            val intersectionPoint2 = intersectionLineCenter + PointF(
                -intersectionToLineDistance * (gear.center.y - center.y) / centersDistance,
                intersectionToLineDistance * (gear.center.x - center.x) / centersDistance
            )

            listOf(intersectionPoint1, intersectionPoint2)
        }

        return potentialIntersectionPoints.filter(::pointIsOnArc)
    }

    private fun pointIsOnArc(point: PointF): Boolean {
        val angleToPoint = atan2(point.y - center.y, point.x - center.x)
        return normalized().any { subArc ->
            var normalizedAngleToPoint = angleToPoint
            while (normalizedAngleToPoint.safeLesser(subArc.startAngle)) {
                normalizedAngleToPoint += PI_FLOAT_2
            }
            normalizedAngleToPoint.safeIn(subArc.startAngle, subArc.startAngle + subArc.sweepAngle)
        }
    }

    private fun intersectionSweepAngles(gear: Gear): List<Float> =
        intersectionPoints(gear).map { it.sweepAngle }

    private fun intersectionCenterAngles(gear: Gear): List<Float> = intersectionPoints(gear).map {
        atan2(it.y - center.y, it.x - center.x)
    }

    private val PointF.sweepAngle: Float
        get() {
            val theta = atan2(y - center.y, x - center.x)
            val normalizedStartAngle = (startAngle + PI_FLOAT_2) % PI_FLOAT_2
            var normalizedTheta = theta
            while (normalizedTheta.safeLesser(normalizedStartAngle)) {
                normalizedTheta += PI_FLOAT_2
            }
            return normalizedTheta - normalizedStartAngle
        }

    private fun intersectsWithCircle(gear: Gear): Boolean {
        val centersDistance = distance(center, gear.center)
        if (centersDistance.safeGreater(radius + gear.radius)) {
            return false
        }
        if (centersDistance.safeLesser(abs(gear.radius - radius))) {
            return false
        }

        val intersectionCenterAngles = intersectionCenterAngles(gear).map {
            (it + PI_FLOAT_2) % PI_FLOAT_2
        }

        return intersectionCenterAngles.any { intersectionAngle ->
            angleInArc(intersectionAngle)
        }
    }

    private fun angleInArc(angle: Float): Boolean {
        return normalized().any { arc ->
            var angleInRange = angle
            val endAngle = (arc.startAngle + arc.sweepAngle)
            while (angleInRange.safeLesser(arc.startAngle)) {
                angleInRange += PI_FLOAT_2
            }
            angleInRange.safeIn(arc.startAngle, endAngle)
        }
    }

    private fun Arc.normalized(): List<Arc> {
        var normalizedStartAngle = startAngle
        while (normalizedStartAngle < 0f) {
            normalizedStartAngle += PI_FLOAT_2
        }
        normalizedStartAngle %= PI_FLOAT_2
        var remainingSweepAngle = sweepAngle
        val arcs = mutableListOf<Arc>()
        while (remainingSweepAngle > 0f) {
            arcs.add(
                Arc(
                    center,
                    radius,
                    normalizedStartAngle,
                    min(remainingSweepAngle, PI_FLOAT_2)
                )
            )
            remainingSweepAngle -= PI_FLOAT_2
            normalizedStartAngle = 0f
        }
        return arcs
    }

    private fun PointF.pointAtAngle(angle: Float) = this +
        PointF(radius * cos(angle), radius * sin(angle))
}
