package com.example.ui.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AmbientSound
import com.example.model.FocusMode
import com.example.model.FocusTimerState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CrimsonStrict
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.viewmodel.FocusViewModel
import java.util.Locale

@Composable
fun FocusScreen(viewModel: FocusViewModel) {
    val focusMode by viewModel.focusMode.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val totalSeconds by viewModel.totalSeconds.collectAsState()
    val completedCycles by viewModel.completedCycles.collectAsState()
    val selectedAmbient by viewModel.selectedAmbient.collectAsState()
    val ambientVolume by viewModel.ambientVolume.collectAsState()
    val strictSettings by viewModel.strictSettings.collectAsState()
    val showMathPrompt by viewModel.showMathPrompt.collectAsState()
    val emergencyCountdown by viewModel.emergencyCountdown.collectAsState()
    val mathQuestion by viewModel.mathChallengeQuestion.collectAsState()
    val customMinutes by viewModel.customMinutes.collectAsState()

    var userMathInput by remember { mutableStateOf("") }
    var mathError by remember { mutableStateOf(false) }
    var showStrictExplainer by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Strict Mode Explainer Dialog
    if (showStrictExplainer) {
        AlertDialog(
            onDismissRequest = { showStrictExplainer = false },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CrimsonStrict,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "How Strict Mode Works",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "When an urge to doomscroll hits, it takes less than 3 seconds to impulsively close a timer. Strict Mode adds mindful friction so you regain control.",
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Un-bypassable: Timer cannot be paused or stopped casually.\n• Emergency Delay: If you truly must quit, a 60-second breathing penalty ensures it is deliberate.\n• Math Challenge: A quick arithmetic challenge wakes up your prefrontal cortex.",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStrictExplainer = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = ObsidianDark)
                ) {
                    Text("Got it")
                }
            }
        )
    }

    // Emergency unlock dialog
    if (showMathPrompt) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CrimsonStrict,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Strict Mode Emergency Guard",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Take 5 deep breaths while the penalty cooldown elapses. Is this pause truly intentional?",
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CrimsonStrict.copy(alpha = 0.15f))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Emergency Cooldown",
                                color = CrimsonStrict,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${emergencyCountdown}s remaining",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Solve arithmetic to verify intentional break:",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$mathQuestion = ?",
                        color = CyanPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userMathInput,
                        onValueChange = {
                            userMathInput = it
                            mathError = false
                        },
                        placeholder = { Text("Enter answer", color = Color.Gray) },
                        isError = mathError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_math_input")
                    )
                    if (mathError) {
                        Text(
                            text = "Incorrect answer or cooldown still active.",
                            color = CrimsonStrict,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitEmergencyUnlock(userMathInput) { success ->
                            if (!success) {
                                mathError = true
                            } else {
                                userMathInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonStrict),
                    enabled = emergencyCountdown == 0,
                    modifier = Modifier.testTag("confirm_emergency_unlock_button")
                ) {
                    Text(if (emergencyCountdown > 0) "Wait (${emergencyCountdown}s)" else "Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.cancelEmergencyUnlock()
                    userMathInput = ""
                }) {
                    Text("Stay Focused", color = EmeraldSecondary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Regain Focus",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Defend cognition • Eliminate mindless scroll",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }

            // Streak & cycle tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🍅", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$completedCycles Cycles",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Mode Selector Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FocusMode.values()) { mode ->
                val isSelected = focusMode == mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) CyanPrimary.copy(alpha = 0.18f) else SurfaceDark)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) CyanPrimary else CardBorderDark,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(enabled = timerState == FocusTimerState.IDLE) {
                            viewModel.setFocusMode(mode)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("mode_${mode.name.lowercase()}")
                ) {
                    Text(
                        text = mode.title,
                        color = if (isSelected) CyanPrimary else Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        if (focusMode == FocusMode.CUSTOM && timerState == FocusTimerState.IDLE) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Custom Duration: ${customMinutes}m", color = CyanPrimary, fontSize = 12.sp)
                Slider(
                    value = customMinutes.toFloat(),
                    onValueChange = { viewModel.setCustomMinutes(it.toInt()) },
                    valueRange = 5f..120f,
                    steps = 22,
                    colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary),
                    modifier = Modifier.width(180.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Central Radial Timer Dial
        Box(
            modifier = Modifier.size(270.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = if (totalSeconds > 0) {
                (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
            } else 1f

            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAura by infiniteTransition.animateFloat(
                initialValue = 0.96f,
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "aura"
            )

            // Neon ambient halo when active
            if (timerState == FocusTimerState.RUNNING) {
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .scale(pulseAura)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    CyanPrimary.copy(alpha = 0.14f),
                                    EmeraldSecondary.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Canvas Radial Arc
            Canvas(modifier = Modifier.size(240.dp)) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(radius * 2, radius * 2)

                // Track Background
                drawArc(
                    color = SurfaceVariantDark,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Gradient Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            CyanPrimary,
                            EmeraldSecondary,
                            CyanPrimary
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Inside Timer Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val mins = secondsRemaining / 60
                val secs = secondsRemaining % 60
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                Text(
                    text = timeString,
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val stateLabel = when (timerState) {
                    FocusTimerState.IDLE -> "Ready to Focus"
                    FocusTimerState.RUNNING -> if (strictSettings.isStrictModeEnabled) "Strict Locked In" else "Deep Flow"
                    FocusTimerState.PAUSED -> "Paused"
                    FocusTimerState.ON_BREAK -> "Rest & Breathe"
                    FocusTimerState.EMERGENCY_UNLOCKING -> "Penalty Cooldown"
                }

                val stateColor = when (timerState) {
                    FocusTimerState.RUNNING -> if (strictSettings.isStrictModeEnabled) CrimsonStrict else EmeraldSecondary
                    FocusTimerState.PAUSED -> AmberWarning
                    FocusTimerState.ON_BREAK -> CyanPrimary
                    else -> Color(0xFF9CA3AF)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(stateColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stateLabel,
                        color = stateColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 4-step Pomodoro cycle dots
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    val currentStep = completedCycles % 4
                    for (i in 0..3) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i < currentStep) EmeraldSecondary
                                    else if (i == currentStep && timerState == FocusTimerState.RUNNING) CyanPrimary
                                    else CardBorderDark
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Play / Pause / Reset Primary Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset / Stop button
            IconButton(
                onClick = { viewModel.stopTimer() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, CardBorderDark, CircleShape)
                    .testTag("reset_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset timer",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Main Play/Pause Button
            Button(
                onClick = {
                    if (timerState == FocusTimerState.RUNNING) {
                        viewModel.pauseTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier
                    .height(64.dp)
                    .width(160.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .testTag("toggle_timer_button"),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerState == FocusTimerState.RUNNING) AmberWarning else CyanPrimary,
                    contentColor = ObsidianDark
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (timerState == FocusTimerState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = ObsidianDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (timerState == FocusTimerState.RUNNING) "Pause" else if (timerState == FocusTimerState.PAUSED) "Resume" else "Start Focus",
                        color = ObsidianDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Strict Mode Icon indicator
            IconButton(
                onClick = { viewModel.toggleStrictMode(!strictSettings.isStrictModeEnabled) },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (strictSettings.isStrictModeEnabled) CrimsonStrict.copy(alpha = 0.2f) else SurfaceDark)
                    .border(
                        width = 1.dp,
                        color = if (strictSettings.isStrictModeEnabled) CrimsonStrict else CardBorderDark,
                        shape = CircleShape
                    )
                    .testTag("quick_strict_toggle")
            ) {
                Icon(
                    imageVector = if (strictSettings.isStrictModeEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Strict Mode",
                    tint = if (strictSettings.isStrictModeEnabled) CrimsonStrict else Color(0xFF9CA3AF),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ambient Background Sound Generator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Synthetic Ambient Sound",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (selectedAmbient != AmbientSound.NONE) {
                        // Live animated equalizer wave
                        AudioEqualizerAnimation()
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sound Selector Chips with friendly icons
                val soundIcons = mapOf(
                    AmbientSound.NONE to "🔇",
                    AmbientSound.RAINFALL to "🌧️",
                    AmbientSound.WHITE_NOISE to "💨",
                    AmbientSound.BINAURAL_ALPHA to "🧠",
                    AmbientSound.LOFI_BEATS to "🎵"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AmbientSound.values()) { sound ->
                        val isPicked = selectedAmbient == sound
                        val iconEmoji = soundIcons[sound] ?: "🎵"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isPicked) EmeraldSecondary.copy(alpha = 0.2f) else SurfaceVariantDark)
                                .border(
                                    width = 1.dp,
                                    color = if (isPicked) EmeraldSecondary else CardBorderDark,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.selectAmbientSound(sound) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = iconEmoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sound.displayName,
                                    color = if (isPicked) EmeraldSecondary else Color(0xFFD1D5DB),
                                    fontSize = 12.sp,
                                    fontWeight = if (isPicked) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                if (selectedAmbient != AmbientSound.NONE) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (ambientVolume == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = ambientVolume,
                            onValueChange = { viewModel.setAmbientVolume(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldSecondary,
                                activeTrackColor = EmeraldSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(ambientVolume * 100).toInt()}%",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Strict Mode Guard Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (strictSettings.isStrictModeEnabled) CrimsonStrict else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Strict Mode (Un-bypassable)",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "What is Strict Mode?",
                                    tint = CyanPrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { showStrictExplainer = true }
                                )
                            }
                            Text(
                                text = if (strictSettings.isStrictModeEnabled) "Active: 60s cooldown penalty on quit" else "Casual mode: timer can be paused freely",
                                color = Color(0xFF9CA3AF),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = strictSettings.isStrictModeEnabled,
                        onCheckedChange = { viewModel.toggleStrictMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CrimsonStrict,
                            checkedTrackColor = CrimsonStrict.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("strict_mode_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AudioEqualizerAnimation() {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val h1 by transition.animateFloat(
        initialValue = 4f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 14f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 6f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(18.dp)
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldSecondary))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldSecondary))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldSecondary))
    }
}
