package com.mitteloupe.android

import android.graphics.PointF
import com.mitteloupe.android.FieldReflection.reflectSetValue
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

fun mockPointF(x: Float = 0f, y: Float = 0f) = mock<PointF>().apply {
    reflectSetValue(this, "x", x)
    reflectSetValue(this, "y", y)
    given(this.toString()).willReturn("PointF($x, $y)")
}
