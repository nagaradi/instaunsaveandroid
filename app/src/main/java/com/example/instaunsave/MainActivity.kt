package com.example.instaunsave

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private var count = 0
    private lateinit var countLabel: TextView
    private lateinit var statusLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(15, 15, 18)
        window.navigationBarColor = Color.rgb(15, 15, 18)
        render()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun render() {
        val bg = Color.rgb(15, 15, 18)
        val panel = Color.rgb(28, 28, 34)
        val white = Color.rgb(245, 245, 248)
        val muted = Color.rgb(180, 180, 190)
        val accent = Color.rgb(217, 70, 239)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(dp(22), dp(30), dp(22), dp(22))
            setBackgroundColor(bg)
        }
        fun label(text: String, size: Float, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(5), 0, dp(5))
        }
        fun addText(text: String, size: Float, color: Int, bold: Boolean = false) {
            root.addView(label(text, size, color, bold), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
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
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52))
            params.topMargin = dp(9)
            root.addView(button, params)
        }

        addText("Insta Unsave", 29f, white, true)
        addText("Saved posts helper", 15f, muted)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setBackgroundColor(panel)
        }
        val cardParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        cardParams.topMargin = dp(20)
        root.addView(card, cardParams)
        card.addView(label("How to use", 19f, white, true))
        card.addView(label("1. Open Instagram and go to your Saved posts.\n2. Open a post and tap its bookmark icon to Unsave it.\n3. Return here and tap +1 to update your progress.", 15f, muted))

        countLabel = label("Unsave progress: $count", 21f, white, true)
        val countParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        countParams.topMargin = dp(18)
        root.addView(countLabel, countParams)
        addButton("Open Instagram") { openInstagram() }
        addButton("+1 post unsaved", Color.rgb(73, 73, 86)) {
            count++
            countLabel.text = "Unsave progress: $count"
            statusLabel.text = "Progress updated. Confirm each post in Instagram."
        }
        addButton("Reset counter", Color.rgb(73, 73, 86)) {
            count = 0
            countLabel.text = "Unsave progress: $count"
            statusLabel.text = "Counter reset."
        }
        statusLabel = label("This app does not access your Instagram account or automatically change Saved posts.", 13f, muted)
        val statusParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        statusParams.topMargin = dp(12)
        root.addView(statusLabel, statusParams)
        setContentView(root)
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
            Toast.makeText(this, "Instagram could not be opened", Toast.LENGTH_SHORT).show()
        }
    }
}
