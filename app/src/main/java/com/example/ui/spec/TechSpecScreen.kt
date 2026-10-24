package com.example.ui.spec

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark

@Composable
fun TechSpecScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val verticalScroll = rememberScrollState()

    val tabs = listOf("Architecture", "Database Schema", "OS Blocking Guide", "Lock Algorithm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Regain Technical Spec",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Production architecture • Schema DDL • OS Interception",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Regain Tech Spec", FULL_TECH_SPEC_MARKDOWN)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Full Spec copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = ObsidianDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("copy_spec_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Copy Spec", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = CyanPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanPrimary
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tech_spec_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
        ) {
            when (selectedTab) {
                0 -> ArchitectureTab()
                1 -> DatabaseSchemaTab()
                2 -> OsBlockingGuideTab()
                3 -> LockAlgorithmTab()
            }
        }
    }
}

@Composable
fun ArchitectureTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCard(
            title = "1. Cross-Platform & Native Architecture",
            tag = "Production Stack",
            content = """
### RECOMMENDED TECH STACK
• Client Layer:
  - Android: Native Kotlin + Jetpack Compose + AccessibilityService API + AudioTrack PCM Synthesis.
  - iOS: Native Swift + SwiftUI + Screen Time API (FamilyControls, DeviceActivity, ManagedSettings, DeviceActivityReport).
  
• Backend Layer:
  - Database: PostgreSQL (via Supabase) with Row-Level Security (RLS) & Realtime WebSockets for Multiplayer Focus Rooms.
  - Auth: Supabase Auth / Firebase Authentication with JWT & Google Sign-In.
  - Analytics & Telemetry: ClickHouse / BigQuery for anonymous aggregate doomscroll telemetry.

• State & Audio Engines:
  - State Management: Kotlin Coroutines StateFlow / MVVM.
  - Ambient Generator: On-device real-time synthetic waveform buffer (Pink noise for rain, carrier-offset stereo for 10Hz/40Hz Binaural Beats).
            """.trimIndent()
        )

        SpecCard(
            title = "Android vs iOS Interception Strategy",
            tag = "Platform Deep Dive",
            content = """
• Android Mechanism:
  - AccessibilityService with TYPE_WINDOW_STATE_CHANGED and TYPE_WINDOW_CONTENT_CHANGED.
  - Traverses AccessibilityNodeInfo trees to pinpoint view IDs (e.g., com.instagram.android:id/clips_viewer_container).
  - Preserves utility (Direct Messages, search, settings) by verifying current activity & tab state before issuing intervention.

• iOS Mechanism:
  - Screen Time API framework:
    1. FamilyControls: Requests parental/self-authorization.
    2. DeviceActivityMonitor: Listens for interval thresholds.
    3. ShieldConfigurationExtension: Renders custom mindful shield UI with breathing prompt.
    4. ShieldActionExtension: Handles 'Step Back' or 'Conscious Override' callbacks.
            """.trimIndent()
        )
    }
}

@Composable
fun DatabaseSchemaTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCard(
            title = "PostgreSQL Relational Schema (DDL)",
            tag = "Supabase / SQL",
            content = """
-- 1. User Profiles
CREATE TABLE profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username VARCHAR(50) UNIQUE NOT NULL,
    avatar_color VARCHAR(16) DEFAULT '#00E5FF',
    streak_days INT DEFAULT 0,
    total_focus_minutes INT DEFAULT 0,
    total_reels_blocked INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. Focus Sessions Log
CREATE TABLE focus_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    mode VARCHAR(32) NOT NULL, -- 'POMODORO_25_5', 'DEEP_WORK_50_10', 'CUSTOM'
    duration_minutes INT NOT NULL,
    completed BOOLEAN DEFAULT TRUE,
    strict_mode_active BOOLEAN DEFAULT FALSE,
    ambient_sound VARCHAR(32) DEFAULT 'NONE',
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ NOT NULL
);

-- 3. Multiplayer Focus Rooms
CREATE TABLE multiplayer_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    topic VARCHAR(200) NOT NULL,
    creator_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    session_minutes INT DEFAULT 50,
    is_private BOOLEAN DEFAULT FALSE,
    room_code VARCHAR(8),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Active Room Members (Real-time synced)
CREATE TABLE room_members (
    room_id UUID REFERENCES multiplayer_rooms(id) ON DELETE CASCADE,
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    status VARCHAR(32) DEFAULT 'Focusing', -- 'Focusing', 'Short Break', 'Locked In'
    minutes_left INT DEFAULT 25,
    last_ping TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (room_id, user_id)
);

-- 5. Doomscroll Interceptions
CREATE TABLE doomscroll_interceptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    app_package VARCHAR(100) NOT NULL,
    surface_name VARCHAR(64) NOT NULL, -- 'Reels', 'Shorts', 'For You'
    action_taken VARCHAR(64) NOT NULL, -- 'STEPPED_BACK', 'CONSCIOUS_PASS', 'DM_ONLY'
    time_saved_seconds INT DEFAULT 120,
    intercepted_at TIMESTAMPTZ DEFAULT NOW()
);
            """.trimIndent()
        )

        SpecCard(
            title = "Firestore / NoSQL Document Model",
            tag = "Firebase Format",
            content = """
/users/{userId}
  - username: string
  - streakDays: number
  - focusScore: number
  - strictConfig: { emergencyDelay: 60, pinRequired: true }

/rooms/{roomId}
  - name: "Deep Work Library"
  - timerMinutes: 50
  - currentStatus: "RUNNING"
  /participants/{userId}
    - name: "Alex R."
    - status: "Locked In"
    - minutesRemaining: 34
    - lastHeartbeat: timestamp
            """.trimIndent()
        )
    }
}

@Composable
fun LockAlgorithmTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCard(
            title = "Strict Mode & Lock Algorithm",
            tag = "Security & Anti-Bypass",
            content = """
### UN-BYPASSABLE LOCK SPECIFICATION
1. Active State Lock:
   - While timerState == RUNNING and isStrictModeEnabled == true:
     • Early cancel / pause requests are intercepted.
     • System Settings / App uninstallation screens are monitored via AccessibilityService.
     • If user opens Android Settings -> Regain -> Uninstall, service auto-executes GLOBAL_ACTION_HOME.

2. Emergency Fallback Cooldown (The Friction Penalty):
   - User must press "Emergency Unlock".
   - 60-Second Inviolable Countdown begins with forced breathing circle.
   - Dynamic Arithmetic Challenge presented: (e.g. 83 + 47 = ?).
   - Only upon cooldown expiration + correct answer is the timer unlocked.
   - Records penalty mark in daily streak analytics to deter impulsive overrides.
            """.trimIndent()
        )
    }
}

@Composable
fun OsBlockingGuideTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCard(
            title = "Android AccessibilityNodeInfo Traversal Guide",
            tag = "Android OS",
            content = """
### SURGICAL NODE INSPECTION ALGORITHM
1. Register AccessibilityService in Manifest:
   - Request android.permission.BIND_ACCESSIBILITY_SERVICE.
   - Set accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged".

2. On AccessibilityEvent:
   val root = event.source ?: return
   val pkg = event.packageName?.toString() ?: ""

3. Surgical Identifier Matching:
   • Instagram Reels:
     - Check: root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_container")
     - Verify: root.findAccessibilityNodeInfosByText("Reels").any { it.isSelected }
     - Whitelist: If viewId contains "direct_inbox", allow immediately without blocking!

   • YouTube Shorts:
     - Check: root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_watch_fragment_root")
     - Whitelist: Standard watch fragment (watch_while_layout) is allowed.

4. Interception Execution:
   - Call performGlobalAction(GLOBAL_ACTION_BACK) to kick back.
   - Launch MindfulOverlayActivity with Intent.FLAG_ACTIVITY_NEW_TASK.
            """.trimIndent()
        )
    }
}

@Composable
fun SpecCard(
    title: String,
    tag: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tag,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariantDark)
                    .padding(12.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = content,
                    color = Color(0xFFE5E7EB),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

const val FULL_TECH_SPEC_MARKDOWN = """
# Regain - Technical Specification & Architecture Spec
## Principal Mobile Application Engineer & UI/UX Spec

### 1. SYSTEM ARCHITECTURE & TECH STACK
• Client Layer:
  - Android: Native Kotlin + Jetpack Compose + AccessibilityService + AudioTrack PCM Synthesis
  - iOS: Native Swift + SwiftUI + Screen Time API (FamilyControls + DeviceActivity + ShieldConfiguration)
• Backend Layer:
  - Supabase PostgreSQL with Row Level Security (RLS) & WebSockets Realtime
  - Firebase Authentication / Google Sign-In with Credential Manager

### 2. DATABASE SCHEMA (POSTGRESQL)
CREATE TABLE profiles (
    id UUID PRIMARY KEY,
    username VARCHAR(50),
    streak_days INT DEFAULT 0,
    total_focus_minutes INT DEFAULT 0,
    total_reels_blocked INT DEFAULT 0
);

CREATE TABLE focus_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id),
    mode VARCHAR(32),
    duration_minutes INT,
    completed BOOLEAN DEFAULT TRUE,
    started_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE multiplayer_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100),
    topic VARCHAR(200),
    session_minutes INT DEFAULT 50
);

CREATE TABLE doomscroll_interceptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id),
    app_package VARCHAR(100),
    surface_name VARCHAR(64),
    action_taken VARCHAR(64),
    time_saved_seconds INT DEFAULT 120
);

### 3. SURGICAL BLOCKING IMPLEMENTATION
- Traverses AccessibilityNodeInfo for 'clips_viewer_container' (Instagram) & 'reel_watch_fragment_root' (YouTube).
- Preserves DMs, Search, and long-form lectures without false-positive blocks.
"""
