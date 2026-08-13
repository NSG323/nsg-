package com.nsg.cybersentinel.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import com.nsg.cybersentinel.MainActivity

class CyberVpnService : VpnService() {
    private val forwarding: ForwardingEngine = ForwardingEngineUnavailable()
    private lateinit var tunManager: TunInterfaceManager

    override fun onCreate() {
        super.onCreate()
        tunManager = TunInterfaceManager(this)
        ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification())
        // Safety gate from the authoritative packet: never establish a TUN that captures and drops traffic.
        tunManager.establishOnlyWhen(forwarding)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        forwarding.stop()
        tunManager.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = super.onBind(intent)

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL, "Cyber Sentinel VPN", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun notification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this, CHANNEL)
            .setContentTitle("NSG Cyber Sentinel")
            .setContentText("VPN scaffold active; TUN forwarding remains safety-locked")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL = "nsg_sentinel_vpn"
        private const val NOTIFICATION_ID = 1202
    }
}
