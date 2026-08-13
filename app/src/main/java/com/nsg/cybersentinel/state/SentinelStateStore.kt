package com.nsg.cybersentinel.state

import android.content.Context
import java.util.UUID

class SentinelStateStore(context: Context) {
    private val prefs = context.getSharedPreferences("sentinel_state_v2", Context.MODE_PRIVATE)

    val installationId: String
        get() {
            val existing = prefs.getString(KEY_INSTALLATION_ID, null)
            if (existing != null) return existing
            val created = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_INSTALLATION_ID, created).apply()
            return created
        }

    var desiredProtection: Boolean
        get() = prefs.getBoolean(KEY_DESIRED_PROTECTION, false)
        set(value) { prefs.edit().putBoolean(KEY_DESIRED_PROTECTION, value).apply() }

    val lastServiceStartMs: Long get() = prefs.getLong(KEY_LAST_START, 0L)
    val lastServiceStopMs: Long get() = prefs.getLong(KEY_LAST_STOP, 0L)
    val serviceStartCount: Long get() = prefs.getLong(KEY_START_COUNT, 0L)
    val lastStopReason: String get() = prefs.getString(KEY_LAST_STOP_REASON, "NONE") ?: "NONE"

    fun recordStart(timestampMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_START, timestampMs)
            .putLong(KEY_START_COUNT, serviceStartCount + 1L)
            .putString(KEY_LAST_STOP_REASON, "RUNNING")
            .apply()
    }

    fun recordStop(reason: String, timestampMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_STOP, timestampMs)
            .putString(KEY_LAST_STOP_REASON, reason)
            .apply()
    }

    companion object {
        private const val KEY_INSTALLATION_ID = "installation_id"
        private const val KEY_DESIRED_PROTECTION = "desired_protection"
        private const val KEY_LAST_START = "last_start_ms"
        private const val KEY_LAST_STOP = "last_stop_ms"
        private const val KEY_START_COUNT = "start_count"
        private const val KEY_LAST_STOP_REASON = "last_stop_reason"
    }
}
