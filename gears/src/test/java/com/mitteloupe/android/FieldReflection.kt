package com.mitteloupe.android

import java.lang.reflect.Field
import org.junit.Assert.fail

object FieldReflection {
    fun reflectValue(classToReflect: Class<Any>, fieldNameValueToFetch: String): Any? = try {
        val reflectedField = reflectField(classToReflect, fieldNameValueToFetch)
        reflectedField.isAccessible = true
        reflectedField[classToReflect]
    } catch (exception: Exception) {
        fail("Failed to reflect $fieldNameValueToFetch")
    }

    fun reflectValue(objectToReflect: Any, fieldNameValueToFetch: String): Any? = try {
        val reflectedField = reflectField(objectToReflect.javaClass, fieldNameValueToFetch)
        reflectedField[objectToReflect]
    } catch (exception: Exception) {
        fail("Failed to reflect $fieldNameValueToFetch")
    }

    private fun reflectField(classToReflect: Class<Any>, fieldNameValueToFetch: String): Field =
        try {
            var reflectedField: Field? = null
            var classForReflection: Class<Any>? = classToReflect
            do {
                try {
                    reflectedField = classForReflection?.getDeclaredField(fieldNameValueToFetch)
                } catch (exception: NoSuchFieldException) {
                    classForReflection = classForReflection?.superclass
                }
            } while (reflectedField == null || classForReflection == null)
            reflectedField.apply {
                isAccessible = true
            }
        } catch (exception: Exception) {
            fail("Failed to reflect $fieldNameValueToFetch from $classToReflect")
            throw exception
        }

    fun reflectSetValue(objectToReflect: Any, fieldNameToSet: String, valueToSet: Any?) {
        try {
            val reflectedField = reflectField(objectToReflect.javaClass, fieldNameToSet)
            reflectedField[objectToReflect] = valueToSet
        } catch (exception: Exception) {
            fail("Failed to reflectively set $fieldNameToSet=$valueToSet")
        }
    }
}
