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

    private val totalPosts = 12000
    private val prefs by lazy {
        getSharedPreferences("unsave_progress", MODE_PRIVATE)
    }

    private var count = 0
    private lateinit var countLabel: TextView
    private lateinit var remainingLabel: TextView
    private lateinit var statusLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        count = prefs.getInt("count", 0).coerceIn(0, totalPosts)

        window.statusBarColor = Color.rgb(15, 15, 18)
        window.navigationBarColor = Color.rgb(15, 15, 18)

        render()
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

        fun label(
            text: String,
            size: Float,
            color: Int,
            bold: Boolean = false
        ) = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) {
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
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
        root.addView(label("Saved posts batch helper", 15f, muted))

        root.addView(
            label(
                "1. Instagram kholen aur Saved posts mein jaayen.\n" +
                "2. Instagram mein posts ko manually Unsave karein.\n" +
                "3. Jitni posts sach mein Unsave hui hain, utni confirm karein.\n\n" +
                "Batch buttons sirf confirmation ke liye hain; " +
                "ye Instagram mein automatic Unsave nahi karte.",
                15f,
                muted
            )
        )

        countLabel = label("", 22f, white, true)
        root.addView(countLabel)

        remainingLabel = label("", 15f, muted)
        root.addView(remainingLabel)

        fun updateProgress() {
            countLabel.text = "Confirmed progress: $count / $totalPosts"
            remainingLabel.text = "Target remaining: ${totalPosts - count}"
        }

        addButton("Open Instagram") {
            openInstagram()
        }

        addButton("Confirm 100 actually removed", gray) {
            confirmRemoved(100)
        }

        addButton("Confirm 200 actually removed", gray) {
            confirmRemoved(200)
        }

        addButton("Reset progress", gray) {
            count = 0
            prefs.edit().putInt("count", count).apply()
            updateProgress()
            statusLabel.text = "Progress reset."
        }

        statusLabel = label(
            "Progress tabhi confirm karein jab posts sach mein Unsave hui hon.",
            13f,
            muted
        )
        root.addView(statusLabel)

        updateProgress()
        setContentView(root)
    }

    private fun confirmRemoved(batchSize: Int) {
        if (count >= totalPosts) {
            statusLabel.text = "Target already reached."
            return
        }

        val remaining = totalPosts - count
        val actualRemoved = minOf(batchSize, remaining)

        // This confirmation does not independently verify Instagram.
        // Only use it after you have actually unsaved this many posts.
        count += actualRemoved
        prefs.edit().putInt("count", count).apply()

        countLabel.text = "Confirmed progress: $count / $totalPosts"
        remainingLabel.text = "Target remaining: ${totalPosts - count}"
        statusLabel.text =
            "You confirmed $actualRemoved posts. " +
            "The app cannot verify Instagram's actual state."
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
