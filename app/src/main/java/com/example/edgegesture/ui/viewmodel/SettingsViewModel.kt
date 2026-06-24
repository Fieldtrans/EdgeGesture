package com.example.edgegesture.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edgegesture.data.model.SettingsState
import com.example.edgegesture.data.repository.ConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing gesture settings
 *
 * Handles all business logic related to gesture configuration,
 * including loading, saving, importing, and exporting settings.
 */
class SettingsViewModel(
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState.default())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _hookStatus = MutableStateFlow(HookStatus.default())
    val hookStatus: StateFlow<HookStatus> = _hookStatus.asStateFlow()

    init {
        loadSettings()
        loadHookStatus()
    }

    /**
     * Load settings from repository
     */
    private fun loadSettings() {
        viewModelScope.launch {
            val settings = configRepository.loadSettings()
            _settingsState.value = settings
        }
    }

    /**
     * Load hook status from repository
     */
    private fun loadHookStatus() {
        viewModelScope.launch {
            val status = configRepository.loadHookStatus()
            _hookStatus.value = status
        }
    }

    /**
     * Update settings and save to repository
     */
    fun updateSettings(settings: SettingsState) {
        viewModelScope.launch {
            _settingsState.value = settings
            configRepository.saveSettings(settings)
        }
    }

    /**
     * Export settings to JSON string
     */
    fun exportSettings(): String {
        return _settingsState.value.toJsonString()
    }

    /**
     * Import settings from JSON string
     */
    fun importSettings(jsonString: String): Result<Unit> {
        return try {
            val settings = SettingsState.fromJsonString(jsonString)
            viewModelScope.launch {
                _settingsState.value = settings
                configRepository.saveSettings(settings)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Refresh hook status
     */
    fun refreshHookStatus() {
        viewModelScope.launch {
            val status = configRepository.loadHookStatus()
            _hookStatus.value = status
        }
    }
}

/**
 * Hook status data class
 */
data class HookStatus(
    val text: String,
    val active: Boolean,
    val enhancedActive: Boolean,
) {
    companion object {
        fun default() =
            HookStatus(
                text = "Loading...",
                active = false,
                enhancedActive = false,
            )
    }
}
