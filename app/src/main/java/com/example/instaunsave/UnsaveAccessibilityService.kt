package com.example.instaunsave

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class UnsaveAccessibilityService : AccessibilityService() {

    private val prefs by lazy {
        getSharedPreferences("unsave_helper", MODE_PRIVATE)
    }

    private var lastToast = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.packageName?.toString() != "com.instagram.android") return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        if (!prefs.getBoolean("armed", false)) return

        val root = rootInActiveWindow ?: return
        val rootBounds = Rect()
        root.getBoundsInScreen(rootBounds)
        if (rootBounds.isEmpty) return

        val candidates = mutableListOf<AccessibilityNodeInfo>()
        collectUnsaveCandidates(root, rootBounds, candidates)

        val target = candidates
            .filter { node ->
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                bounds.centerY() > rootBounds.top + (rootBounds.height() * 0.55f)
            }
            .maxByOrNull { node ->
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                bounds.centerY()
            } ?: return

        val clickable = findClickableAncestor(target) ?: target
        val clicked = clickable.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        // Disarm immediately so repeated content events cannot cause repeated taps.
        prefs.edit().putBoolean("armed", false).apply()

        val message = if (clicked) {
            "Unsave button tapped once. Please check Instagram to confirm."
        } else {
            "Unsave was found, but tap failed. Try selecting posts and arm again."
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun collectUnsaveCandidates(
        node: AccessibilityNodeInfo,
        rootBounds: Rect,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        val text = node.text?.toString()?.trim().orEmpty()
        val description = node.contentDescription?.toString()?.trim().orEmpty()
        val exactUnsave = text.equals("Unsave", ignoreCase = true) ||
            description.equals("Unsave", ignoreCase = true)

        if (exactUnsave && node.isVisibleToUser) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            if (!bounds.isEmpty && Rect.intersects(rootBounds, bounds)) {
                result.add(AccessibilityNodeInfo.obtain(node))
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectUnsaveCandidates(child, rootBounds, result)
        }
    }

    private fun findClickableAncestor(
        original: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {
        var current: AccessibilityNodeInfo? = AccessibilityNodeInfo.obtain(original)
        repeat(6) {
            val node = current ?: return null
            if (node.isClickable && node.isEnabled) return node
            val parent = node.parent
            node.recycle()
            current = parent
        }
        current?.recycle()
        return null
    }

    override fun onInterrupt() {
        // No repeated actions are scheduled.
    }
}
