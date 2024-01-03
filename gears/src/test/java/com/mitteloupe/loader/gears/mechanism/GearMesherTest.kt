package com.mitteloupe.loader.gears.mechanism

import android.graphics.PointF
import com.mitteloupe.android.mockPointF
import com.mitteloupe.loader.gears.model.Gear
import com.mitteloupe.loader.gears.model.PRECISION_CORRECTION
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class GearMesherTest(
    @Suppress("unused") private val testCaseTitle: String,
    private val givenFirstGear: Gear,
    private val givenNewGearCenter: PointF,
    private val givenNewGearRadius: Float,
    private val expectedAngle: Float
) {
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data(): Collection<Array<*>> = listOf(
            testCase(
                testCaseTitle = "Given same size gear with odd tooth number above returns 0 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 0f,
                firstGearRadius = 55f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 0f,
                newGearCenterY = -110f,
                newGearRadius = 55f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = 0f
            ),
            testCase(
                testCaseTitle = "Given same size gear with odd tooth number on right returns 180 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 0f,
                firstGearRadius = 55f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 110f,
                newGearCenterY = 0f,
                newGearRadius = 55f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = PI_FLOAT
            ),
            testCase(
                testCaseTitle = "Given same size gear with odd tooth number below returns 0 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 0f,
                firstGearRadius = 55f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 0f,
                newGearCenterY = 110f,
                newGearRadius = 55f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = 0f
            ),
            testCase(
                testCaseTitle = "Given half size gear with odd tooth number below returns 180 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 0f,
                firstGearRadius = 100f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 0f,
                newGearCenterY = 155f,
                newGearRadius = 55f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = PI_FLOAT
            ),
            testCase(
                testCaseTitle = "Given same size gear with even tooth number above returns 18 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 0f,
                firstGearRadius = 100f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 0f,
                newGearCenterY = -200f,
                newGearRadius = 100f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = PI_FLOAT / 10f
            ),
            testCase(
                testCaseTitle = "Given same size gear with even tooth number below returns 18 degrees",
                firstGearCenterX = 0f,
                firstGearCenterY = 10f,
                firstGearRadius = 100f,
                firstGearRotation = 0f,
                firstGearIsClockwise = false,
                newGearCenterX = 0f,
                newGearCenterY = 210f,
                newGearRadius = 100f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = PI_FLOAT / 10f
            ),
            testCase(
                testCaseTitle = "Given rotated 90, half size gear with odd tooth number below returns 0 degrees",
                firstGearCenterX = 10f,
                firstGearCenterY = 0f,
                firstGearRadius = 100f,
                firstGearRotation = PI_FLOAT_HALF,
                firstGearIsClockwise = false,
                newGearCenterX = 10f,
                newGearCenterY = 155f,
                newGearRadius = 55f,
                toothWidth = 58.71322f,
                toothDepth = 5f,
                expectedMeshingAngle = 0f
            )
        )

        private fun testCase(
            testCaseTitle: String,
            firstGearCenterX: Float,
            firstGearCenterY: Float,
            firstGearRadius: Float,
            firstGearRotation: Float,
            firstGearIsClockwise: Boolean,
            newGearCenterX: Float,
            newGearCenterY: Float,
            newGearRadius: Float,
            toothWidth: Float,
            toothDepth: Float,
            expectedMeshingAngle: Float
        ) = arrayOf(
            testCaseTitle,
            Gear(
                center = com.mitteloupe.android.mockPointF(firstGearCenterX, firstGearCenterY),
                radius = firstGearRadius,
                rotation = firstGearRotation,
                isClockwise = firstGearIsClockwise,
                toothWidth = toothWidth,
                toothDepth = toothDepth,
                canBeExtended = false
            ),
            com.mitteloupe.android.mockPointF(newGearCenterX, newGearCenterY),
            newGearRadius,
            expectedMeshingAngle
        )
    }

    private lateinit var classUnderTest: GearMesher

    @Before
    fun setUp() {
        classUnderTest = GearMesher()
    }

    @Test
    fun `When meshingAngle`() {
        // Given

        // When
        val actualValue = classUnderTest.meshingAngle(
            givenFirstGear,
            givenNewGearCenter,
            givenNewGearRadius
        )
        var actualValueInRange = actualValue
        while (actualValueInRange >= PI_FLOAT_2 - PRECISION_CORRECTION) {
            actualValueInRange -= PI_FLOAT_2
        }

        // Then
        assertEquals(expectedAngle, actualValueInRange, PRECISION_CORRECTION)
    }
}
