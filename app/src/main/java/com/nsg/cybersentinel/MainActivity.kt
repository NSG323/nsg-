package com.nsg.cybersentinel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.nsg.cybersentinel.runtime.SentinelRuntime
import com.nsg.cybersentinel.service.ProtectionService
import com.nsg.cybersentinel.vpn.CyberVpnService
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
        val bg = Color.rgb(7, 17, 20)
        val fg = Color.rgb(232, 244, 242)
        val accent = Color.rgb(79, 209, 197)
        val muted = Color.rgb(143, 163, 173)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 44, 32, 44)
            setBackgroundColor(bg)
        }
        root.addView(TextView(this).apply {
            text = "NSG CYBER SENTINEL"
            textSize = 24f
            setTextColor(fg)
            setTypeface(typeface, 1)
        })
        root.addView(TextView(this).apply {
            text = "Defensive on-device metadata telemetry"
            textSize = 13f
            setTextColor(accent)
            setPadding(0, 6, 0, 26)
        })
        status = TextView(this).apply {
            textSize = 14f
            setTextColor(fg)
            setLineSpacing(6f, 1f)
            setPadding(0, 0, 0, 24)
        }
        root.addView(status)

        root.addView(button("START PROTECTION", accent) {
            startForegroundService(Intent(this, ProtectionService::class.java))
        })
        root.addView(button("STOP PROTECTION", muted) {
            stopService(Intent(this, ProtectionService::class.java))
        })
        root.addView(button("REQUEST VPN PERMISSION", accent) {
            val prepare = VpnService.prepare(this)
            if (prepare != null) startActivityForResult(prepare, VPN_REQ)
            else startVpnScaffold()
        })
        root.addView(button("PURGE BASELINE + AUDIT", muted) {
            val intent = Intent(this, ProtectionService::class.java).setAction(ProtectionService.ACTION_PURGE)
            startForegroundService(intent)
        })

        root.addView(TextView(this).apply {
            text = "Metric-core rule: CEDI / CCII / CPEI / COIE remain locked until the original frozen equations are restored. No replacement coefficients are being invented."
            textSize = 12f
            setTextColor(muted)
            setPadding(0, 28, 0, 0)
        })

        return ScrollView(this).apply { addView(root) }
    }

    private fun button(label: String, color: Int, click: () -> Unit): Button = Button(this).apply {
        text = label
        setTextColor(Color.BLACK)
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

    private fun startVpnScaffold() {
        startForegroundService(Intent(this, CyberVpnService::class.java))
    }

    @Deprecated("Deprecated in Android API; retained for minSdk-compatible VPN permission flow")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQ && resultCode == RESULT_OK) startVpnScaffold()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 99)
        }
    }

    companion object { private const val VPN_REQ = 701 }
}
