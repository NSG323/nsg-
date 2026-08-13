package com.nsg.cybersentinel.diagnostic

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.webkit.WebView
import com.nsg.cybersentinel.runtime.SentinelRuntime
import com.nsg.cybersentinel.state.SentinelStateStore
import org.json.JSONArray
import org.json.JSONObject

class DiagnosticCollector(private val context: Context) {
    private val cm = context.getSystemService(ConnectivityManager::class.java)
    private val power = context.getSystemService(PowerManager::class.java)
    private val notifications = context.getSystemService(NotificationManager::class.java)
    private val state = SentinelStateStore(context)

    fun capture(reason: String): JSONObject {
        val active = cm.activeNetwork
        val caps = active?.let(cm::getNetworkCapabilities)
        val link = active?.let(cm::getLinkProperties)
        val config = context.resources.configuration
        val webViewPackage = runCatching { WebView.getCurrentWebViewPackage() }.getOrNull()
        val appVersion = runCatching {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull()
        val bootCount = runCatching {
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
        }.getOrDefault(-1)
        val defaultIme = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        }.getOrNull()
        val voiceRecognitionService = runCatching {
            Settings.Secure.getString(context.contentResolver, "voice_recognition_service")
        }.getOrNull()

        return JSONObject().apply {
            put("schema", "NSG_SENTINEL_DIAGNOSTIC_V2")
            put("reason", reason)
            put("timestampMs", System.currentTimeMillis())
            put("elapsedRealtimeMs", SystemClock.elapsedRealtime())
            put("bootCount", bootCount)
            put("installationId", state.installationId)

            put("device", JSONObject().apply {
                put("manufacturer", Build.MANUFACTURER)
                put("model", Build.MODEL)
                put("sdkInt", Build.VERSION.SDK_INT)
                put("androidRelease", Build.VERSION.RELEASE)
                put("securityPatch", Build.VERSION.SECURITY_PATCH ?: "")
            })

            put("display", JSONObject().apply {
                put("fontScale", config.fontScale.toDouble())
                put("densityDpi", config.densityDpi)
                put("orientation", config.orientation)
                put("uiMode", config.uiMode)
            })

            put("input", JSONObject().apply {
                put("defaultIme", defaultIme ?: JSONObject.NULL)
                put("voiceRecognitionService", voiceRecognitionService ?: JSONObject.NULL)
            })

            put("app", JSONObject().apply {
                put("version", appVersion ?: "unknown")
                put("protectionDesired", state.desiredProtection)
                put("serviceRunning", SentinelRuntime.snapshot.serviceRunning)
                put("serviceStartCount", state.serviceStartCount)
                put("lastServiceStartMs", state.lastServiceStartMs)
                put("lastServiceStopMs", state.lastServiceStopMs)
                put("lastStopReason", state.lastStopReason)
                put("notificationsEnabled", notifications.areNotificationsEnabled())
                put("ignoringBatteryOptimizations", power.isIgnoringBatteryOptimizations(context.packageName))
            })

            put("network", JSONObject().apply {
                put("transport", transportLabel(caps))
                put("validated", caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true)
                put("captivePortal", caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) == true)
                put("metered", caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) != true)
                put("vpnActive", caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true)
                put("privateDnsActive", link?.isPrivateDnsActive == true)
                put("privateDnsServerName", link?.privateDnsServerName ?: JSONObject.NULL)
                put("interfaceName", link?.interfaceName ?: JSONObject.NULL)
                put("mtu", link?.mtu ?: 0)
                put("dnsServers", JSONArray(link?.dnsServers?.map { it.hostAddress ?: it.toString() } ?: emptyList<String>()))
                put("httpProxy", link?.httpProxy?.toString() ?: JSONObject.NULL)
            })

            put("webView", JSONObject().apply {
                put("packageName", webViewPackage?.packageName ?: JSONObject.NULL)
                put("versionName", webViewPackage?.versionName ?: JSONObject.NULL)
            })

            put("sentinel", JSONObject().apply {
                put("auditHead", SentinelRuntime.snapshot.auditHead)
                put("lastError", SentinelRuntime.snapshot.lastError ?: JSONObject.NULL)
            })
        }
    }

    private fun transportLabel(caps: NetworkCapabilities?): String = when {
        caps == null -> "NONE"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "BLUETOOTH"
        else -> "OTHER"
    }
}
