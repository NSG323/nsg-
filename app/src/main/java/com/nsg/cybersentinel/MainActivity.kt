package com.nsg.cybersentinel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.nsg.cybersentinel.runtime.SentinelRuntime
import com.nsg.cybersentinel.service.ProtectionService
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val refresh = object : Runnable {
        override fun run() {
            renderStatus()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        setContentView(buildUi())
    }

    override fun onResume() {
        super.onResume()
        handler.post(refresh)
    }

    override fun onPause() {
        handler.removeCallbacks(refresh)
        super.onPause()
    }

    private fun buildUi(): ScrollView {
        val black = Color.rgb(5, 7, 10)
        val panel = Color.rgb(13, 17, 23)
        val foreground = Color.rgb(232, 244, 242)
        val purple = Color.rgb(123, 44, 255)
        val seahawksGreen = Color.rgb(105, 190, 40)
        val muted = Color.rgb(156, 171, 181)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(40))
            setBackgroundColor(black)
        }

        val approvedBrand = resources.getIdentifier("nsg_sentinel_brand", "drawable", packageName)
        if (approvedBrand != 0) {
            root.addView(ImageView(this).apply {
                setImageResource(approvedBrand)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "NSG Sentinel approved artwork"
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210)).apply {
                bottomMargin = dp(18)
            })
        }

        root.addView(TextView(this).apply {
            text = "NSG CYBER SENTINEL"
            textSize = 25f
            setTextColor(foreground)
            setTypeface(typeface, 1)
            gravity = Gravity.CENTER_HORIZONTAL
        })
        root.addView(TextView(this).apply {
            text = "NETWORK  //  FORENSICS  //  DEFENSE  //  INCIDENT RESPONSE"
            textSize = 11f
            letterSpacing = 0.06f
            setTextColor(seahawksGreen)
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 6, 0, dp(22))
        })

        status = TextView(this).apply {
            textSize = 14f
            setTextColor(foreground)
            setLineSpacing(6f, 1f)
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(panel)
        }
        root.addView(status, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = dp(18)
        })

        root.addView(button("START PROTECTION", purple, Color.WHITE) {
            startForegroundService(Intent(this, ProtectionService::class.java))
        })
        root.addView(button("STOP PROTECTION", seahawksGreen, Color.BLACK) {
            stopService(Intent(this, ProtectionService::class.java))
        })
        root.addView(button("PURGE BASELINE + AUDIT", muted, Color.BLACK) {
            val intent = Intent(this, ProtectionService::class.java).setAction(ProtectionService.ACTION_PURGE)
            startForegroundService(intent)
        })

        root.addView(TextView(this).apply {
            text = "Defensive coefficient candidate v0.1 is active. Deep packet routing remains safety-locked until bidirectional forwarding is verified. Metadata-only protection stays available without enabling incomplete interception."
            textSize = 12f
            setTextColor(muted)
            setPadding(0, dp(28), 0, 0)
        })

        return ScrollView(this).apply { addView(root) }
    }

    private fun button(label: String, color: Int, textColor: Int, click: () -> Unit): Button = Button(this).apply {
        text = label
        setTextColor(textColor)
        setBackgroundColor(color)
        gravity = Gravity.CENTER
        setOnClickListener { click() }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = 14
        }
    }

    private fun renderStatus() {
        val s = SentinelRuntime.snapshot
        val t = s.telemetry
        status.text = buildString {
            appendLine("SERVICE       ${if (s.serviceRunning) "RUNNING" else "STOPPED"}")
            appendLine("TRANSPORT     ${t.transport}  validated=${t.validated}  metered=${t.metered}")
            appendLine("VPN OBSERVED  ${t.vpnActive}")
            appendLine("RX RATE       ${formatRate(t.rxBytesPerSec)}")
            appendLine("TX RATE       ${formatRate(t.txBytesPerSec)}")
            appendLine("TX/RX RATIO   ${String.format(Locale.US, "%.3f", t.txRxRatio)}")
            appendLine("BURST Z       ${String.format(Locale.US, "%.3f", t.burstZ)}")
            appendLine("BASELINE N    ${t.baselineSamples}")
            appendLine("CORE          ${s.metrics.coreStatus}")
            appendLine("CEDI          ${s.metrics.cedi ?: "LOCKED"}")
            appendLine("CCII          ${s.metrics.ccii ?: "LOCKED"}")
            appendLine("CPEI          ${s.metrics.cpei ?: "LOCKED"}")
            appendLine("COIE          ${s.metrics.coie ?: "LOCKED"}")
            appendLine("THREAT        ${s.metrics.threatState}")
            appendLine("RESPONSE      ${s.metrics.recommendation}")
            appendLine("AUDIT HEAD    ${s.auditHead.take(18)}")
            if (s.lastError != null) appendLine("ERROR         ${s.lastError}")
        }
    }

    private fun formatRate(v: Long): String = when {
        v >= 1_000_000 -> String.format(Locale.US, "%.2f MB/s", v / 1_000_000.0)
        v >= 1_000 -> String.format(Locale.US, "%.2f KB/s", v / 1_000.0)
        else -> "$v B/s"
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 99)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
