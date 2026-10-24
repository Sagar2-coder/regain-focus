package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.MainActivity

class RegainAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "RegainAccessibility"
        private const val IG_PACKAGE = "com.instagram.android"
        private const val YT_PACKAGE = "com.google.android.youtube"
        private const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
        private const val TIKTOK_LITE_PACKAGE = "com.zhiliaoapp.musically.go"
    }

    private var lastInterceptionTime = 0L
    private val debounceDelayMs = 2500L

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Regain Anti-Doomscroll Service Connected")
        InterceptionEventBus.setServiceConnected(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val pkgName = event.packageName?.toString() ?: return
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInterceptionTime < debounceDelayMs) return

        val rootNode = rootInActiveWindow ?: return

        try {
            when {
                pkgName.contains(IG_PACKAGE) -> {
                    if (isInstagramReelsActive(rootNode)) {
                        intercept("Instagram", "Reels Feed", pkgName)
                    }
                }
                pkgName.contains(YT_PACKAGE) -> {
                    if (isYouTubeShortsActive(rootNode)) {
                        intercept("YouTube", "YouTube Shorts", pkgName)
                    }
                }
                pkgName.contains(TIKTOK_PACKAGE) || pkgName.contains(TIKTOK_LITE_PACKAGE) -> {
                    intercept("TikTok", "For You Feed", pkgName)
                }
                else -> {
                    checkBrowserForShortsUrl(rootNode, pkgName)
                }
            }
        } finally {
            rootNode.recycle()
        }
    }

    private fun isInstagramReelsActive(root: AccessibilityNodeInfo): Boolean {
        val reelViewNodes = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_container")
        if (reelViewNodes.isNotEmpty()) {
            reelViewNodes.forEach { it.recycle() }
            return true
        }

        val clipsVideoContainer = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_video_container")
        if (clipsVideoContainer.isNotEmpty()) {
            clipsVideoContainer.forEach { it.recycle() }
            return true
        }

        val reelsTexts = root.findAccessibilityNodeInfosByText("Reels")
        for (node in reelsTexts) {
            val isSelected = node.isSelected || (node.parent?.isSelected == true)
            node.recycle()
            if (isSelected) return true
        }

        return false
    }

    private fun isYouTubeShortsActive(root: AccessibilityNodeInfo): Boolean {
        val shortsContainers = root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_watch_fragment_root")
        if (shortsContainers.isNotEmpty()) {
            shortsContainers.forEach { it.recycle() }
            return true
        }

        val shortsViewer = root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_container")
        if (shortsViewer.isNotEmpty()) {
            shortsViewer.forEach { it.recycle() }
            return true
        }

        val shortsTabs = root.findAccessibilityNodeInfosByText("Shorts")
        for (node in shortsTabs) {
            val isSelected = node.isSelected || (node.parent?.isSelected == true)
            node.recycle()
            if (isSelected) return true
        }

        return false
    }

    private fun checkBrowserForShortsUrl(root: AccessibilityNodeInfo, pkgName: String) {
        val urlBars = root.findAccessibilityNodeInfosByViewId("$pkgName:id/url_bar")
        if (urlBars.isEmpty()) return

        for (node in urlBars) {
            val text = node.text?.toString()?.lowercase() ?: ""
            node.recycle()
            if (text.contains("/reels/") || text.contains("/shorts/") || text.contains("tiktok.com")) {
                intercept("Browser", "Shorts URL Intercepted", pkgName)
                break
            }
        }
    }

    private fun intercept(appName: String, surface: String, packageName: String) {
        lastInterceptionTime = System.currentTimeMillis()
        Log.i(TAG, "Surgically Intercepted $appName -> $surface")

        InterceptionEventBus.triggerInterception(appName, surface, packageName)
        performGlobalAction(GLOBAL_ACTION_BACK)

        try {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("SHOW_OVERLAY", true)
                putExtra("INTERCEPTED_APP", appName)
                putExtra("INTERCEPTED_SURFACE", surface)
            }
            startActivity(launchIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch overlay: ${e.message}")
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Regain Anti-Doomscroll Service Interrupted")
        InterceptionEventBus.setServiceConnected(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        InterceptionEventBus.setServiceConnected(false)
    }
}
