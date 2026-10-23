package com.example.instaunsave

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("unsave_helper", MODE_PRIVATE)
    }
    private lateinit var statusLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(15, 15, 18)
        window.navigationBarColor = Color.rgb(15, 15, 18)
        render()
    }

    override fun onResume() {
        super.onResume()
        if (::statusLabel.isInitialized) updateStatus()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun render() {
        val bg = Color.rgb(15, 15, 18)
        val white = Color.rgb(245, 245, 248)
        val muted = Color.rgb(180, 180, 190)
        val accent = Color.rgb(190, 50, 220)
        val gray = Color.rgb(73, 73, 86)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(dp(22), dp(25), dp(22), dp(22))
            setBackgroundColor(bg)
        }

        fun label(text: String, size: Float, color: Int, bold: Boolean = false) =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, dp(5), 0, dp(5))
            }

        fun addButton(text: String, color: Int = accent, action: () -> Unit) {
            val button = Button(this).apply {
                this.text = text
                setTextColor(Color.WHITE)
                setBackgroundColor(color)
                isAllCaps = false
                textSize = 16f
                setOnClickListener { action() }
            }
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52)
            )
            params.topMargin = dp(9)
            root.addView(button, params)
        }

        root.addView(label("Insta Unsave", 29f, white, true))
        root.addView(label("One-tap Unsave helper", 16f, muted))
        root.addView(label(
            "How to use:\n" +
                "1. Turn on Insta Unsave Helper in Android Accessibility settings.\n" +
                "2. Tap Arm one Unsave tap below.\n" +
                "3. Open Instagram and go to Saved > Manage.\n" +
                "4. Select the posts you want removed. The helper will try to tap the bottom Unsave button once.\n\n" +
                "It does not select posts automatically. Check Instagram afterward to confirm the posts were removed.",
            15f, muted
        ))

        statusLabel = label("", 14f, white)
        root.addView(statusLabel)

        addButton("Open Accessibility Settings", gray) {
            try {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(this, "Accessibility settings could not be opened.", Toast.LENGTH_SHORT).show()
            }
        }

        addButton("Arm one Unsave tap") {
            prefs.edit().putBoolean("armed", true).apply()
            updateStatus()
            Toast.makeText(this, "Armed for one bottom Unsave button. Now open Instagram.", Toast.LENGTH_LONG).show()
        }

        addButton("Open Instagram", gray) {
            openInstagram()
        }

        addButton("Disarm helper", gray) {
            prefs.edit().putBoolean("armed", false).apply()
            updateStatus()
        }

        root.addView(label(
            "Safety: the helper acts only after you arm it, and attempts one tap on an exact “Unsave” control near the bottom of Instagram. It cannot guarantee Instagram accepted the action.",
            13f, muted
        ))

        setContentView(root)
        updateStatus()
    }

    private fun updateStatus() {
        val armed = prefs.getBoolean("armed", false)
        statusLabel.text = if (armed) {
            "Status: ARMED — one Unsave tap pending."
        } else {
            "Status: Disarmed."
        }
    }

    private fun openInstagram() {
        try {
            val launch = packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (launch != null) {
                startActivity(launch)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/")))
            }
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Instagram could not be opened.", Toast.LENGTH_SHORT).show()
        }
    }
}
