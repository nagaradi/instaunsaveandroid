package com.example.instaunsave

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class UnsaveAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Only inspect Instagram while it is in the foreground.
        if (packageName != "com.instagram.android") return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // Inspect visible UI only; do not click or remove posts.
                val root: AccessibilityNodeInfo =
                    rootInActiveWindow ?: return

                val screenText = root.textContentForInspection()

                // Diagnostic signal only, not proof that a post was removed.
                if (screenText.contains("saved", ignoreCase = true)) {
                    // Saved-related screen detected.
                }
            }
        }
    }

    private fun AccessibilityNodeInfo.textContentForInspection(): String {
        val result = StringBuilder()
        val stack = ArrayDeque<AccessibilityNodeInfo>()
        stack.add(this)

        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            node.text?.let { result.append(it).append(' ') }
            node.contentDescription?.let {
                result.append(it).append(' ')
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { stack.add(it) }
            }
        }

        return result.toString()
    }

    override fun onInterrupt() {
        // No ongoing automated actions to interrupt.
    }
}
