package com.example.myedgegesture.ui.viewmodel

import com.example.myedgegesture.data.model.SettingsState
import com.example.myedgegesture.data.repository.ConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SettingsViewModel
 *
 * Tests the business logic of settings management without UI dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var configRepository: ConfigRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        configRepository = mockk(relaxed = true)

        // Mock repository to return default settings
        coEvery { configRepository.loadSettings() } returns SettingsState.default()
        coEvery { configRepository.loadHookStatus() } returns HookStatus.default()

        viewModel = SettingsViewModel(configRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load from repository`() = runTest {
        // Given - setup is done in @Before

        // When - ViewModel is initialized
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - repository should be called
        coVerify { configRepository.loadSettings() }
        coVerify { configRepository.loadHookStatus() }
    }

    @Test
    fun `updateSettings should save to repository`() = runTest {
        // Given
        val newSettings = SettingsState.default().copy(enabled = false)

        // When
        viewModel.updateSettings(newSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { configRepository.saveSettings(newSettings) }
        assertEquals(false, viewModel.settingsState.value.enabled)
    }

    @Test
    fun `resetToRecommended should apply recommended values`() = runTest {
        // Given
        val customSettings = SettingsState.default().copy(
            edgeWidthDp = 50,
            swipeDistanceDp = 200
        )
        coEvery { configRepository.loadSettings() } returns customSettings

        // When
        viewModel.resetToRecommended()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            configRepository.saveSettings(match {
                it.edgeWidthDp != 50 && it.swipeDistanceDp != 200
            })
        }
    }

    @Test
    fun `exportSettings should return JSON string`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val json = viewModel.exportSettings()

        // Then
        assertTrue(json.contains("EdgeGesture"))
        assertTrue(json.contains("schemaVersion"))
    }

    @Test
    fun `importSettings should update state on success`() = runTest {
        // Given
        val validJson = """
            {
                "schemaVersion": 1,
                "app": "EdgeGesture",
                "enabled": false,
                "edgeWidthDp": 25
            }
        """.trimIndent()

        // When
        val result = viewModel.importSettings(validJson)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(result.isSuccess)
        coVerify { configRepository.saveSettings(any()) }
    }

    @Test
    fun `importSettings should fail on invalid JSON`() = runTest {
        // Given
        val invalidJson = "not a valid json"

        // When
        val result = viewModel.importSettings(invalidJson)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `refreshHookStatus should reload status from repository`() = runTest {
        // Given
        val newStatus = HookStatus(
            text = "Test status",
            active = true,
            enhancedActive = true
        )
        coEvery { configRepository.loadHookStatus() } returns newStatus

        // When
        viewModel.refreshHookStatus()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("Test status", viewModel.hookStatus.value.text)
        assertEquals(true, viewModel.hookStatus.value.active)
    }
}
