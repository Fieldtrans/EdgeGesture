package com.example.edgegesture.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {
    @Test
    fun `uses dynamic color by default on android 12 and newer`() {
        assertEquals(
            AppColorScheme.Dynamic,
            selectAppColorScheme(darkTheme = false, dynamicColor = true, sdkInt = 31),
        )
    }

    @Test
    fun `uses fixed color below android 12`() {
        assertEquals(
            AppColorScheme.FixedLight,
            selectAppColorScheme(darkTheme = false, dynamicColor = true, sdkInt = 30),
        )
    }
}
