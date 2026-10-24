# Regain 🛡️⏱️

> **Combat short-form video addiction & supercharge deep focus with surgical content interception and multiplayer accountability.**

![Android](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20Architecture-3DDC84?style=flat&logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.2-7F52FF?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3)-4285F4?style=flat&logo=jetpackcompose)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

---

## 📖 Overview

**Regain** is an Android application designed for students, developers, and remote workers struggling with reflexive short-form video addiction (Instagram Reels, YouTube Shorts, TikTok).

Unlike traditional blockers that bluntly shut down entire applications—breaking urgent communication with clients, colleagues, and family—**Regain operates with surgical precision**:
- 🚫 **Blocks ONLY the addictive infinite feed**: Instagram Reels tab/viewer, YouTube Shorts pivot tab/player, and TikTok feeds.
- ✅ **Preserves essential utility**: Direct Messages, search, profiles, and educational long-form videos remain 100% accessible.

---

## ✨ Key Features

1. **Anti-Doomscrolling Surgical Engine**:
   - Background `AccessibilityService` that traverses the `AccessibilityNodeInfo` hierarchy in real time.
   - Detects specific view container IDs (`clips_viewer_container`, `reel_watch_fragment_root`) and browser URL routes (`/reels/`, `/shorts/`).
   - Interactive in-app live simulator to test and demo surgical intervention without leaving the app.

2. **Mindful Intercept Overlay Screen**:
   - Smooth 4-4-4 breathing cycle animation (*Inhale 4s, Hold 4s, Release 4s*).
   - Conscious 5-second friction pause to dissolve reflexive scrolling impulses.
   - Quick one-tap shortcut to open Direct Messages only.

3. **Focus & Pomodoro Engine**:
   - Customizable intervals: Pomodoro 25/5, Deep Work 50/10, Custom Timer (5–120m), Stopwatch, and Countdown.
   - **Real-Time Offline Synthetic Audio Engine**: Generates real PCM audio via Android `AudioTrack`:
     - 🌧️ Stochastic Pink Rainfall with randomized droplet impacts
     - 💨 Broadband Soft White Noise
     - 🧠 10Hz Binaural Alpha Waves (dual-ear carrier entrainment)
     - 🎵 Lo-Fi Pentatonic Chord Pulse

4. **Strict Mode (Un-Bypassable Guard)**:
   - Locks active focus sessions against impulsive pauses or early exits.
   - Emergency exit requires a mandatory 60-second breathing penalty cooldown + solving an arithmetic challenge.

5. **Multiplayer Focus Rooms & Leaderboards**:
   - Synchronized virtual study halls with live member statuses (*Focusing*, *Locked In*, *Short Break*).
   - One-tap cheers and celebrations (🔥, ⚡, 🧠, ☕, 🚀).
   - Global Leaderboards tracking streaks, focus points, and reels avoided.

6. **Deep Analytics Dashboard**:
   - Interactive Canvas bar chart with single-day tap inspection.
   - Platform distraction breakdown (Instagram vs. YouTube vs. TikTok).
   - Reclaimed life equivalence metrics (*e.g. 3.5 hrs saved = 14 podcast episodes or 1 reading sprint*).
   - Hourly urge window heatmap.

7. **In-App Technical Architecture & SQL Schema Hub**:
   - Complete architectural blueprints, PostgreSQL DDL schemas, Firestore models, and Android vs iOS comparison guides with one-tap clipboard export.

---

## 🏗️ Architecture & Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt               # Root Navigation Scaffold & Intent Receiver
├── audio/
│   └── AmbientSoundEngine.kt     # Real-time AudioTrack 16-bit PCM waveform synthesizer
├── model/
│   └── Models.kt                 # Data models (FocusMode, SurgicalTarget, Rooms, Analytics)
├── service/
│   ├── InterceptionEventBus.kt   # Reactive StateFlow event bus
│   └── RegainAccessibilityService.kt # AccessibilityNodeInfo inspector & surgical detector
├── ui/
│   ├── analytics/
│   │   └── AnalyticsScreen.kt    # Custom Canvas charts & distraction analytics
│   ├── focus/
│   │   └── FocusScreen.kt        # Radial pomodoro timer, ambient controls & strict mode
│   ├── overlay/
│   │   └── MindfulOverlayDialog.kt # Breathing circle & conscious friction overlay
│   ├── rooms/
│   │   └── RoomsScreen.kt        # Multiplayer study hubs & leaderboard
│   ├── shield/
│   │   └── ShieldScreen.kt       # Surgical target rules & live sandbox simulator
│   ├── spec/
│   │   └── TechSpecScreen.kt     # Complete production technical spec & SQL schemas
│   └── theme/
│       ├── Color.kt              # Midnight obsidian, neon cyan & emerald palette
│       ├── Theme.kt              # Material 3 theme configuration
│       └── Type.kt               # Typography system
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- JDK 17+ or JDK 21
- Android SDK 36 (minSdk 24)

### Building the Project
Clone the repository and build via Gradle:
```bash
git clone https://github.com/YOUR_USERNAME/regain.git
cd regain
gradle assembleDebug
```

### Running Tests
Execute unit and Robolectric test suites:
```bash
gradle :app:testDebugUnitTest
```

---

## 📄 License
This project is open-source under the Apache License 2.0.
