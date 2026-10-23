package com.example.instaunsave

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class UnsaveAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Instagram screen events will be handled here.
        // No automatic clicks are performed yet.
    }

    override fun onInterrupt() {
        // Stop work if the service is interrupted.
    }
}
