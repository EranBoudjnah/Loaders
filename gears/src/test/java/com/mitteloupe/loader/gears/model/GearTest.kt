package com.mitteloupe.loader.gears.model

import android.graphics.PointF
import com.mitteloupe.android.mockPointF
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_2
import com.mitteloupe.loader.gears.mechanism.PI_FLOAT_HALF
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GearTest {
    private lateinit var center: PointF

    private val radius = 16f

    private val rotation = PI_FLOAT_HALF

    private val isClockwise = false

    private val toothWidth = 4f

    private val toothDepth = 4f

    private val canBeExtended = false

    private lateinit var classUnderTest: Gear

    @Before
    fun setUp() {
        center = mockPointF(4f, 4f)

        classUnderTest = Gear(
            center = center,
            radius = radius,
            rotation = rotation,
            isClockwise = isClockwise,
            toothWidth = toothWidth,
            toothDepth = toothDepth,
            canBeExtended = canBeExtended
        )
    }

    @Test
    fun `When getTeethCount then returns number of teeth`() {
        // Given
        val expectedTeethCount = 18

        // When
        val actualTeethCount = classUnderTest.teethCount

        // Then
        assertEquals(expectedTeethCount, actualTeethCount)
    }

    @Test
    fun `When getRelativeSpeed then returns speed relative to number of teeth, direction`() {
        // Given
        val expectedRelativeSpeed = -0.055555556f

        // When
        val actualRelativeSpeed = classUnderTest.relativeSpeed

        // Then
        assertEquals(expectedRelativeSpeed, actualRelativeSpeed)
    }

    @Test
    fun `Given radius delta when outerRadius then returns extendable gear with extended radius`() {
        // Given
        val givenRadiusDelta = 16f
        val expectedGear = Gear(
            center = center,
            radius = radius + givenRadiusDelta,
            rotation = rotation,
            isClockwise = isClockwise,
            toothWidth = toothWidth,
            toothDepth = toothDepth,
            canBeExtended = true
        )

        // When
        val actualGear = classUnderTest.outerRadius(givenRadiusDelta)

        // Then
        assertEquals(expectedGear, actualGear)
    }

    @Test
    @Suppress("ktlint:standard:max-line-length")
    fun `Given radius delta when outerArc then returns full arc at gear center with extended radius`() {
        // Given
        val givenRadiusDelta = 16f
        val expectedCenter = center
        val expectedSweepAngle = PI_FLOAT_2
        val expectedRadius = radius + givenRadiusDelta

        // When
        val actualArc = classUnderTest.outerArc(givenRadiusDelta)

        // Then
        assertEquals(expectedCenter, actualArc.center)
        assertEquals(expectedSweepAngle, actualArc.sweepAngle)
        assertEquals(expectedRadius, actualArc.radius)
    }

    @Test
    fun `Given point inside gear when contains then returns true`() {
        // Given
        val givenPoint = mockPointF(5f, 5f)

        // When
        val actualResult = classUnderTest.contains(givenPoint) { other ->
            mockPointF(x - other.x, y - other.y)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given point outside gear when contains then returns false`() {
        // Given
        val givenPoint = mockPointF(21f, 4f)

        // When
        val actualResult = classUnderTest.contains(givenPoint) { other ->
            mockPointF(x - other.x, y - other.y)
        }

        // Then
        assertFalse(actualResult)
    }
}
