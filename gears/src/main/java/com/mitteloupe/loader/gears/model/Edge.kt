package com.mitteloupe.loader.gears.model

import android.graphics.PointF
import com.mitteloupe.loader.gears.mechanism.distance
import com.mitteloupe.loader.gears.mechanism.sqrt

data class Edge(
    val point1: PointF,
    val point2: PointF
) {
    fun intersectWithCircle(circle: Circular): List<PointF> {
        val vector = point2 - point1
        val edgeLength = distance(point1.x, point1.y, point2.x, point2.y)

        val normalized = vector / edgeLength

        val centerToEdgeVectorX = point1.x - circle.center.x
        val centerToEdgeVectorY = point1.y - circle.center.y

        val dotProduct = normalized.x * centerToEdgeVectorX + normalized.y * centerToEdgeVectorY

        val closestPoint = point1 - (normalized * dotProduct)

        val distance = distance(closestPoint, circle.center)

        return if (distance == circle.radius) {
            if (isPointInEdge(closestPoint, edgeLength)) {
                listOf(closestPoint)
            } else {
                emptyList()
            }
        } else if (distance < circle.radius) {
            val offset = (circle.radius * circle.radius - distance * distance).sqrt()

            val pointA = closestPoint + normalized * offset
            val pointB = closestPoint - normalized * offset

            val pointsOnEdge = mutableListOf<PointF>()
            if (isPointInEdge(pointA, edgeLength)) {
                pointsOnEdge.add(pointA)
            }
            if (isPointInEdge(pointB, edgeLength)) {
                pointsOnEdge.add(pointB)
            }

            pointsOnEdge
        } else {
            emptyList()
        }
    }

    private fun isPointInEdge(point: PointF, edgeLength: Float): Boolean {
        val dot = (point.x - point1.x) * (point2.x - point1.x) +
            (point.y - point1.y) * (point2.y - point1.y)
        return dot >= 0 && dot <= edgeLength * edgeLength
    }
}
