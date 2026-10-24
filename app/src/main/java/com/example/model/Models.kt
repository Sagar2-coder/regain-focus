package com.example.model

enum class FocusMode(val title: String, val focusMinutes: Int, val breakMinutes: Int, val description: String) {
    POMODORO_25_5("Classic 25/5", 25, 5, "Standard Pomodoro rhythm for steady output"),
    DEEP_WORK_50_10("Deep Work 50/10", 50, 10, "Extended immersion for high-cognition tasks"),
    CUSTOM("Custom Timer", 40, 10, "Custom focus and rest intervals"),
    STOPWATCH("Open Stopwatch", 0, 0, "Unbounded flow state tracking"),
    COUNTDOWN("Quick Sprint", 15, 3, "Short burst to crush procrastination")
}

enum class FocusTimerState {
    IDLE,
    RUNNING,
    PAUSED,
    ON_BREAK,
    EMERGENCY_UNLOCKING
}

enum class AmbientSound(val displayName: String, val subtitle: String) {
    NONE("Silent Focus", "No background sound"),
    RAINFALL("Rainfall & Drops", "Soothing stochastic pink water droplets"),
    WHITE_NOISE("Soft Noise", "Broadband sound masking external noise"),
    BINAURAL_ALPHA("10Hz Binaural Alpha", "Synchronized carrier waves for flow state"),
    LOFI_BEATS("Lo-Fi Chord Pulse", "Gentle acoustic chillwave pulse")
}

data class StrictLockSettings(
    val isStrictModeEnabled: Boolean = false,
    val emergencyDelaySeconds: Int = 60,
    val mathChallengeRequired: Boolean = true,
    val lockAppSwitcher: Boolean = true,
    val emergencyPin: String = "1234"
)

data class SurgicalTarget(
    val id: String,
    val packageName: String,
    val appName: String,
    val surfaceName: String,
    val isEnabled: Boolean = true,
    val viewsBlockedToday: Int = 0,
    val deepLinkSafeZone: String = "Direct Messages & Search allowed"
)

data class InterceptionLog(
    val id: String,
    val appName: String,
    val surface: String,
    val timestamp: Long,
    val actionTaken: String,
    val secondsSaved: Int
)

data class RoomParticipant(
    val id: String,
    val name: String,
    val avatarInitial: String,
    val status: String,
    val minutesRemaining: Int,
    val streakDays: Int,
    val isSelf: Boolean = false
)

data class MultiplayerRoom(
    val id: String,
    val name: String,
    val topic: String,
    val iconEmoji: String,
    val currentTimerMinutes: Int,
    val isJoined: Boolean = false,
    val activeCount: Int,
    val participants: List<RoomParticipant>
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val avatarInitial: String,
    val focusPoints: Int,
    val streakDays: Int,
    val reelsBlocked: Int,
    val badge: String,
    val isCurrentUser: Boolean = false
)

data class DailyAnalytics(
    val dayLabel: String,
    val focusMinutes: Int,
    val reelsIntercepted: Int,
    val minutesSaved: Int
)
