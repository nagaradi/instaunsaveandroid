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
    private val totalPosts = 12000
    private val batchSize = 20

    private lateinit var countLabel: TextView
    private lateinit var statusLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(15, 15, 18)
        window.navigationBarColor = Color.rgb(15, 15, 18)

        render()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun render() {
        val bg = Color.rgb(15, 15, 18)
        val panel = Color.rgb(28, 28, 34)
        val white = Color.rgb(245, 245, 248)
        val muted = Color.rgb(180, 180, 190)
        val accent = Color.rgb(190, 50, 220)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(dp(22), dp(30), dp(22), dp(22))
            setBackgroundColor(bg)
        }

        fun label(
            text: String,
            size: Float,
            color: Int,
            bold: Boolean = false
        ) = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(5), 0, dp(5))
        }

        fun addButton(
            text: String,
            color: Int = accent,
            action: () -> Unit
        ) {
            val button = Button(this).apply {
                this.text = text
                setTextColor(Color.WHITE)
                setBackgroundColor(color)
                isAllCaps = false
                textSize = 16f
                setOnClickListener { action() }
            }

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
            params.topMargin = dp(9)
            root.addView(button, params)
        }

        root.addView(label("Insta Unsave", 29f, white, true))
        root.addView(label("Saved posts progress tracker", 15f, muted))

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setBackgroundColor(panel)
        }

        val cardParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardParams.topMargin = dp(20)
        root.addView(card, cardParams)

        card.addView(label("How to use", 19f, white, true))
        card.addView(
            label(
                "1. Open Instagram Saved posts.\n" +
                "2. Select and Unsave up to 20 posts manually.\n" +
                "3. Return here and tap Confirm 20 removed.\n" +
                "The counter tracks your progress only.",
                15f,
                muted
            )
        )

        countLabel = label("Progress: $count / $totalPosts", 22f, white, true)
        val countParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        countParams.topMargin = dp(18)
        root.addView(countLabel, countParams)

        val remaining = label("", 15f, muted)
        root.addView(remaining)

        fun updateProgress() {
            countLabel.text = "Progress: $count / $totalPosts"
            remaining.text = "Remaining: ${totalPosts - count} posts"
        }

        addButton("Open Instagram") {
            openInstagram()
        }

        addButton("Confirm 20 posts removed", Color.rgb(73, 73, 86)) {
            if (count + batchSize <= totalPosts) {
                count += batchSize
                updateProgress()
                statusLabel.text =
                    "Progress updated. Confirm posts were actually removed."
            } else {
                count = totalPosts
                updateProgress()
                statusLabel.text = "Target count reached."
            }
        }

        addButton("Reset counter", Color.rgb(73, 73, 86)) {
            count = 0
            updateProgress()
            statusLabel.text = "Counter reset."
        }

        statusLabel = label(
            "This app does not automatically select or Unsave Instagram posts.",
            13f,
            muted
        )
        val statusParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        statusParams.topMargin = dp(12)
        root.addView(statusLabel, statusParams)

        updateProgress()
        setContentView(root)
    }

    private fun openInstagram() {
        try {
            val launch =
                packageManager.getLaunchIntentForPackage("com.instagram.android")

            if (launch != null) {
                startActivity(launch)
            } else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/")
                    )
                )
            }
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Instagram could not be opened",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
