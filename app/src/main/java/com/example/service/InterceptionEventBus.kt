package com.example.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActiveInterception(
    val appName: String,
    val surface: String,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val motivationalQuote: String = "Notice the unconscious reflex. Breathe in... and choose what actually matters to you."
)

object InterceptionEventBus {
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val _currentInterception = MutableStateFlow<ActiveInterception?>(null)
    val currentInterception: StateFlow<ActiveInterception?> = _currentInterception.asStateFlow()

    private val _totalBlockedToday = MutableStateFlow(19)
    val totalBlockedToday: StateFlow<Int> = _totalBlockedToday.asStateFlow()

    private val _totalMinutesSaved = MutableStateFlow(32)
    val totalMinutesSaved: StateFlow<Int> = _totalMinutesSaved.asStateFlow()

    fun setServiceConnected(connected: Boolean) {
        _isServiceConnected.value = connected
    }

    fun triggerInterception(appName: String, surface: String, packageName: String) {
        _totalBlockedToday.value += 1
        _totalMinutesSaved.value += 2
        _currentInterception.value = ActiveInterception(
            appName = appName,
            surface = surface,
            packageName = packageName
        )
    }

    fun dismissInterception() {
        _currentInterception.value = null
    }
}
