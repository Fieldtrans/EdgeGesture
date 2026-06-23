package com.example.edgegesture.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SettingsStateTest {
    @Test
    fun `default state has correct values`() {
        val state = SettingsState.default()
        assertEquals(false, state.enabled)
        assertEquals(72, state.pointerRadiusDp)
        assertEquals(130, state.pointerCurve)
        assertEquals(true, state.hapticFeedbackEnabled)
    }

    @Test
    fun `toJsonString contains all fields`() {
        val json = SettingsState.default().toJsonString()
        assertTrue(json.contains("schemaVersion"))
        assertTrue(json.contains("EdgeGesture"))
        assertTrue(json.contains("pointerRadiusDp"))
        assertTrue(json.contains("hapticFeedbackEnabled"))
    }

    @Test
    fun `fromJsonString roundtrip preserves values`() {
        val original =
            SettingsState.default().copy(
                enabled = true,
                pointerSensitivity = 150,
                hapticFeedbackEnabled = false,
            )
        val json = original.toJsonString()
        val restored = SettingsState.fromJsonString(json)
        assertEquals(original.enabled, restored.enabled)
        assertEquals(original.pointerSensitivity, restored.pointerSensitivity)
        assertEquals(original.hapticFeedbackEnabled, restored.hapticFeedbackEnabled)
    }

    @Test
    fun `withRecommendedValues resets to defaults`() {
        val custom = SettingsState.default().copy(pointerSensitivity = 180)
        val reset = custom.withRecommendedValues()
        assertEquals(100, reset.pointerSensitivity)
    }

    @Test
    fun `fromJsonString rejects invalid schema version`() {
        val invalidJson = """{"schemaVersion": 99, "app": "EdgeGesture"}"""
        try {
            SettingsState.fromJsonString(invalidJson)
            fail("Should throw on unsupported schema version")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("schema") == true || e.message?.contains("Schema") == true)
        }
    }
}
