package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundEngine
import com.example.model.AmbientSound
import com.example.model.DailyAnalytics
import com.example.model.FocusMode
import com.example.model.FocusTimerState
import com.example.model.InterceptionLog
import com.example.model.LeaderboardUser
import com.example.model.MultiplayerRoom
import com.example.model.RoomParticipant
import com.example.model.StrictLockSettings
import com.example.model.SurgicalTarget
import com.example.service.InterceptionEventBus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

class FocusViewModel : ViewModel() {

    private val soundEngine = AmbientSoundEngine()
    private var timerJob: Job? = null
    private var emergencyJob: Job? = null

    private val _focusMode = MutableStateFlow(FocusMode.POMODORO_25_5)
    val focusMode: StateFlow<FocusMode> = _focusMode.asStateFlow()

    private val _timerState = MutableStateFlow(FocusTimerState.IDLE)
    val timerState: StateFlow<FocusTimerState> = _timerState.asStateFlow()

    private val _totalSeconds = MutableStateFlow(25 * 60)
    val totalSeconds: StateFlow<Int> = _totalSeconds.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(25 * 60)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _completedCycles = MutableStateFlow(3)
    val completedCycles: StateFlow<Int> = _completedCycles.asStateFlow()

    private val _customMinutes = MutableStateFlow(45)
    val customMinutes: StateFlow<Int> = _customMinutes.asStateFlow()

    private val _strictSettings = MutableStateFlow(StrictLockSettings())
    val strictSettings: StateFlow<StrictLockSettings> = _strictSettings.asStateFlow()

    private val _emergencyCountdown = MutableStateFlow(60)
    val emergencyCountdown: StateFlow<Int> = _emergencyCountdown.asStateFlow()

    private val _mathChallengeQuestion = MutableStateFlow("47 + 29")
    val mathChallengeQuestion: StateFlow<String> = _mathChallengeQuestion.asStateFlow()

    private val _mathChallengeAnswer = MutableStateFlow(76)

    private val _showMathPrompt = MutableStateFlow(false)
    val showMathPrompt: StateFlow<Boolean> = _showMathPrompt.asStateFlow()

    private val _selectedAmbient = MutableStateFlow(AmbientSound.NONE)
    val selectedAmbient: StateFlow<AmbientSound> = _selectedAmbient.asStateFlow()

    private val _ambientVolume = MutableStateFlow(0.7f)
    val ambientVolume: StateFlow<Float> = _ambientVolume.asStateFlow()

    private val _surgicalTargets = MutableStateFlow(
        listOf(
            SurgicalTarget(
                id = "ig_reels",
                packageName = "com.instagram.android",
                appName = "Instagram",
                surfaceName = "Reels Tab & Viewer",
                isEnabled = true,
                viewsBlockedToday = 8,
                deepLinkSafeZone = "Direct Messages, Profile & Explore Search remain active"
            ),
            SurgicalTarget(
                id = "yt_shorts",
                packageName = "com.google.android.youtube",
                appName = "YouTube",
                surfaceName = "Shorts Pivot Tab & Player",
                isEnabled = true,
                viewsBlockedToday = 6,
                deepLinkSafeZone = "Long-form study videos & subscriptions stay accessible"
            ),
            SurgicalTarget(
                id = "tiktok_feed",
                packageName = "com.zhiliaoapp.musically",
                appName = "TikTok",
                surfaceName = "For You Endless Video Feed",
                isEnabled = true,
                viewsBlockedToday = 5,
                deepLinkSafeZone = "Full app blocked during strict focus sessions"
            ),
            SurgicalTarget(
                id = "web_shorts",
                packageName = "com.android.chrome",
                appName = "Chrome & Browsers",
                surfaceName = "m.youtube.com/shorts & reels URLs",
                isEnabled = true,
                viewsBlockedToday = 2,
                deepLinkSafeZone = "Documentation, articles, and research pages allowed"
            )
        )
    )
    val surgicalTargets: StateFlow<List<SurgicalTarget>> = _surgicalTargets.asStateFlow()

    private val _rooms = MutableStateFlow(
        listOf(
            MultiplayerRoom(
                id = "room_deepwork",
                name = "Deep Work Library",
                topic = "Coding, Writing & Thesis Prep",
                iconEmoji = "🏛️",
                currentTimerMinutes = 50,
                isJoined = true,
                activeCount = 14,
                participants = listOf(
                    RoomParticipant("1", "Alex R. (You)", "A", "Locked In", 21, 5, isSelf = true),
                    RoomParticipant("2", "Elena S.", "E", "Focusing", 24, 12),
                    RoomParticipant("3", "Kenji T.", "K", "Focusing", 18, 8),
                    RoomParticipant("4", "Sarah M.", "S", "Short Break", 3, 19),
                    RoomParticipant("5", "David K.", "D", "Locked In", 35, 3)
                )
            ),
            MultiplayerRoom(
                id = "room_noscm",
                name = "Zero-Shorts Challenge",
                topic = "Breaking 3-Hour Daily Doomscroll",
                iconEmoji = "🛡️",
                currentTimerMinutes = 25,
                isJoined = false,
                activeCount = 28,
                participants = listOf(
                    RoomParticipant("6", "Marcus V.", "M", "Focusing", 15, 4),
                    RoomParticipant("7", "Priya N.", "P", "Focusing", 19, 7)
                )
            ),
            MultiplayerRoom(
                id = "room_morning",
                name = "Dawn Sprint Cohort",
                topic = "Morning Deep Flow Without Notifications",
                iconEmoji = "🌅",
                currentTimerMinutes = 40,
                isJoined = false,
                activeCount = 9,
                participants = listOf(
                    RoomParticipant("8", "Jordan L.", "J", "Locked In", 32, 21),
                    RoomParticipant("9", "Chloe W.", "C", "Focusing", 28, 14)
                )
            )
        )
    )
    val rooms: StateFlow<List<MultiplayerRoom>> = _rooms.asStateFlow()

    private val _leaderboard = MutableStateFlow(
        listOf(
            LeaderboardUser(1, "Sophia Chen", "S", 1240, 24, 184, "🔥 Zenith Master"),
            LeaderboardUser(2, "Liam O'Connor", "L", 1120, 19, 142, "⚡ Flow Guardian"),
            LeaderboardUser(3, "Alex Rivera (You)", "A", 980, 14, 96, "🛡️ Shield Master", isCurrentUser = true),
            LeaderboardUser(4, "Mei Zhang", "M", 910, 11, 88, "🎯 Focused Archer"),
            LeaderboardUser(5, "Ethan Becker", "E", 845, 9, 64, "🧠 Neuro Titan"),
            LeaderboardUser(6, "Fatima Al-Mansoor", "F", 790, 7, 52, "🌟 Steady Beam")
        )
    )
    val leaderboard: StateFlow<List<LeaderboardUser>> = _leaderboard.asStateFlow()

    private val _dailyAnalytics = MutableStateFlow(
        listOf(
            DailyAnalytics("Mon", 145, 12, 18),
            DailyAnalytics("Tue", 180, 15, 24),
            DailyAnalytics("Wed", 210, 21, 35),
            DailyAnalytics("Thu", 165, 14, 22),
            DailyAnalytics("Fri", 195, 18, 30),
            DailyAnalytics("Sat", 120, 26, 42),
            DailyAnalytics("Sun (Today)", 160, 21, 35)
        )
    )
    val dailyAnalytics: StateFlow<List<DailyAnalytics>> = _dailyAnalytics.asStateFlow()

    private val _recentLogs = MutableStateFlow(
        listOf(
            InterceptionLog("1", "Instagram", "Reels Viewer", System.currentTimeMillis() - 1000 * 60 * 25, "Breathed & Stepped Back", 120),
            InterceptionLog("2", "YouTube", "Shorts Pivot Tab", System.currentTimeMillis() - 1000 * 60 * 95, "Redirected to Search", 180),
            InterceptionLog("3", "Instagram", "Reels Feed", System.currentTimeMillis() - 1000 * 60 * 180, "Opened Direct Messages Only", 90),
            InterceptionLog("4", "TikTok", "For You", System.currentTimeMillis() - 1000 * 60 * 320, "Shield Activated", 240)
        )
    )
    val recentLogs: StateFlow<List<InterceptionLog>> = _recentLogs.asStateFlow()

    private val _latestRoomKudos = MutableStateFlow<String?>(null)
    val latestRoomKudos: StateFlow<String?> = _latestRoomKudos.asStateFlow()

    fun setFocusMode(mode: FocusMode) {
        if (_timerState.value != FocusTimerState.IDLE) return
        _focusMode.value = mode
        val sec = when (mode) {
            FocusMode.POMODORO_25_5 -> 25 * 60
            FocusMode.DEEP_WORK_50_10 -> 50 * 60
            FocusMode.CUSTOM -> _customMinutes.value * 60
            FocusMode.STOPWATCH -> 0
            FocusMode.COUNTDOWN -> 15 * 60
        }
        _totalSeconds.value = sec
        _secondsRemaining.value = sec
    }

    fun setCustomMinutes(mins: Int) {
        val clamped = mins.coerceIn(5, 180)
        _customMinutes.value = clamped
        if (_focusMode.value == FocusMode.CUSTOM && _timerState.value == FocusTimerState.IDLE) {
            _totalSeconds.value = clamped * 60
            _secondsRemaining.value = clamped * 60
        }
    }

    fun startTimer() {
        if (_timerState.value == FocusTimerState.RUNNING) return
        _timerState.value = FocusTimerState.RUNNING

        if (_selectedAmbient.value != AmbientSound.NONE) {
            soundEngine.play(_selectedAmbient.value)
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _timerState.value == FocusTimerState.RUNNING) {
                delay(1000L)
                if (_focusMode.value == FocusMode.STOPWATCH) {
                    _secondsRemaining.value += 1
                } else {
                    if (_secondsRemaining.value > 0) {
                        _secondsRemaining.value -= 1
                    } else {
                        onTimerFinished()
                        break
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        if (_strictSettings.value.isStrictModeEnabled) {
            triggerEmergencyUnlock()
            return
        }
        _timerState.value = FocusTimerState.PAUSED
        timerJob?.cancel()
        soundEngine.stop()
    }

    fun stopTimer() {
        if (_strictSettings.value.isStrictModeEnabled && _secondsRemaining.value > 0 && _timerState.value != FocusTimerState.IDLE) {
            triggerEmergencyUnlock()
            return
        }
        forceResetTimer()
    }

    private fun forceResetTimer() {
        _timerState.value = FocusTimerState.IDLE
        timerJob?.cancel()
        emergencyJob?.cancel()
        _showMathPrompt.value = false
        val sec = if (_focusMode.value == FocusMode.CUSTOM) {
            _customMinutes.value * 60
        } else {
            _focusMode.value.focusMinutes * 60
        }
        _totalSeconds.value = sec
        _secondsRemaining.value = sec
        soundEngine.stop()
    }

    private fun onTimerFinished() {
        _completedCycles.value += 1
        soundEngine.stop()
        if (_focusMode.value == FocusMode.POMODORO_25_5 || _focusMode.value == FocusMode.DEEP_WORK_50_10) {
            _timerState.value = FocusTimerState.ON_BREAK
            val breakSec = _focusMode.value.breakMinutes * 60
            _totalSeconds.value = breakSec
            _secondsRemaining.value = breakSec
        } else {
            _timerState.value = FocusTimerState.IDLE
        }
    }

    fun selectAmbientSound(sound: AmbientSound) {
        _selectedAmbient.value = sound
        if (_timerState.value == FocusTimerState.RUNNING) {
            soundEngine.play(sound)
        } else if (sound == AmbientSound.NONE) {
            soundEngine.stop()
        }
    }

    fun setAmbientVolume(vol: Float) {
        _ambientVolume.value = vol
        soundEngine.setVolume(vol)
    }

    fun toggleStrictMode(enabled: Boolean) {
        _strictSettings.value = _strictSettings.value.copy(isStrictModeEnabled = enabled)
    }

    fun triggerEmergencyUnlock() {
        _timerState.value = FocusTimerState.EMERGENCY_UNLOCKING
        val rng = Random()
        val num1 = rng.nextInt(50) + 20
        val num2 = rng.nextInt(50) + 15
        _mathChallengeQuestion.value = "$num1 + $num2"
        _mathChallengeAnswer.value = num1 + num2
        _emergencyCountdown.value = _strictSettings.value.emergencyDelaySeconds
        _showMathPrompt.value = true

        emergencyJob?.cancel()
        emergencyJob = viewModelScope.launch {
            while (isActive && _emergencyCountdown.value > 0) {
                delay(1000L)
                _emergencyCountdown.value -= 1
            }
        }
    }

    fun submitEmergencyUnlock(userAnswer: String, onResult: (Boolean) -> Unit) {
        val parsed = userAnswer.trim().toIntOrNull()
        if (parsed == _mathChallengeAnswer.value && _emergencyCountdown.value <= 0) {
            forceResetTimer()
            onResult(true)
        } else {
            onResult(false)
        }
    }

    fun cancelEmergencyUnlock() {
        emergencyJob?.cancel()
        _showMathPrompt.value = false
        _timerState.value = FocusTimerState.RUNNING
    }

    fun toggleSurgicalTarget(id: String) {
        _surgicalTargets.value = _surgicalTargets.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun simulateInterception(appName: String, surface: String) {
        val target = _surgicalTargets.value.find { it.appName.equals(appName, ignoreCase = true) }
        val pkg = target?.packageName ?: "com.example.app"
        InterceptionEventBus.triggerInterception(appName, surface, pkg)

        _recentLogs.value = listOf(
            InterceptionLog(
                id = System.currentTimeMillis().toString(),
                appName = appName,
                surface = surface,
                timestamp = System.currentTimeMillis(),
                actionTaken = "Mindful Intervention Screen Shown",
                secondsSaved = 120
            )
        ) + _recentLogs.value

        _surgicalTargets.value = _surgicalTargets.value.map {
            if (it.appName.equals(appName, ignoreCase = true)) {
                it.copy(viewsBlockedToday = it.viewsBlockedToday + 1)
            } else it
        }
    }

    fun joinRoom(roomId: String) {
        _rooms.value = _rooms.value.map {
            it.copy(isJoined = it.id == roomId)
        }
    }

    fun sendKudos(emoji: String) {
        _latestRoomKudos.value = emoji
        viewModelScope.launch {
            delay(3000L)
            if (_latestRoomKudos.value == emoji) {
                _latestRoomKudos.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.stop()
        timerJob?.cancel()
        emergencyJob?.cancel()
    }
}
