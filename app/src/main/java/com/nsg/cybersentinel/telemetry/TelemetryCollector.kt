package com.nsg.cybersentinel.telemetry

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.SystemClock
import com.nsg.cybersentinel.baseline.AdaptiveBaseline
import com.nsg.cybersentinel.model.TelemetrySnapshot

class TelemetryCollector(context: Context) {
    private val cm = context.getSystemService(ConnectivityManager::class.java)
    private val baseline = AdaptiveBaseline()
    private var lastRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
    private var lastTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
    private var lastElapsed = SystemClock.elapsedRealtime()

    fun sample(learnBaseline: Boolean = true): TelemetrySnapshot {
        val nowElapsed = SystemClock.elapsedRealtime()
        val elapsedMs = (nowElapsed - lastElapsed).coerceAtLeast(1L)
        val totalRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
        val totalTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
        val rxDelta = (totalRx - lastRx).coerceAtLeast(0L)
        val txDelta = (totalTx - lastTx).coerceAtLeast(0L)
        val rxBps = rxDelta * 1000L / elapsedMs
        val txBps = txDelta * 1000L / elapsedMs
        val combined = (rxBps + txBps).toDouble()
        val burstZ = baseline.observe(combined, learnBaseline)
        val ratio = if (rxBps > 0) txBps.toDouble() / rxBps else if (txBps > 0) Double.POSITIVE_INFINITY else 0.0

        val active = cm.activeNetwork
        val caps = active?.let(cm::getNetworkCapabilities)
        val transport = when {
            caps == null -> "NONE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            else -> "OTHER"
        }
        val validated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        val metered = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) != true
        val vpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        lastRx = totalRx
        lastTx = totalTx
        lastElapsed = nowElapsed

        return TelemetrySnapshot(
            timestampMs = System.currentTimeMillis(),
            rxBytesPerSec = rxBps,
            txBytesPerSec = txBps,
            totalRxBytes = totalRx,
            totalTxBytes = totalTx,
            transport = transport,
            validated = validated,
            metered = metered,
            vpnActive = vpn,
            burstZ = if (burstZ.isFinite()) burstZ else 0.0,
            txRxRatio = if (ratio.isFinite()) ratio else 999.0,
            baselineSamples = baseline.samples,
        )
    }

    fun resetBaseline() = baseline.reset()
}
