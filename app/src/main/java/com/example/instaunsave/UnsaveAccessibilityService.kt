package com.example.instaunsave

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class UnsaveAccessibilityService : AccessibilityService() {

    private var lastMessage = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.packageName?.toString() != "com.instagram.android") {
            return
        }

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return
        }

        val root = rootInActiveWindow ?: return
        val texts = mutableListOf<String>()
        collectVisibleText(root, texts)

        val allText = texts.joinToString(" ")
        val hasUnsave = texts.any {
            it.trim().equals("Unsave", ignoreCase = true)
        }

        val message = when {
            hasUnsave ->
                "Instagram Unsave button detected. Posts selected: check Instagram."
            allText.contains("Manage", ignoreCase = true) &&
                allText.contains("Saved", ignoreCase = true) ->
                "Instagram Saved/Manage screen detected."
            else -> ""
        }

        if (message.isNotEmpty() && message != lastMessage) {
            lastMessage = message
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectVisibleText(
        node: AccessibilityNodeInfo,
        result: MutableList<String>
    ) {
        node.text?.toString()?.let { result.add(it) }
        node.contentDescription?.toString()?.let { result.add(it) }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectVisibleText(child, result)
        }
    }

    override fun onInterrupt() {
        // No automatic actions are running.
    }
}
