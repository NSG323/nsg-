package com.nsg.cybersentinel.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.nsg.cybersentinel.MainActivity
import com.nsg.cybersentinel.audit.HashChainLedger
import com.nsg.cybersentinel.metrics.CandidateMetricCoreV01
import com.nsg.cybersentinel.model.SentinelSnapshot
import com.nsg.cybersentinel.runtime.SentinelRuntime
import com.nsg.cybersentinel.telemetry.TelemetryCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ProtectionService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var telemetry: TelemetryCollector
    private lateinit var ledger: HashChainLedger
    private val metricCore = CandidateMetricCoreV01()
    private var sampler: Job? = null

    override fun onCreate() {
        super.onCreate()
        telemetry = TelemetryCollector(this)
        ledger = HashChainLedger(this)
        ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_PURGE) {
            ledger.purge()
            telemetry.resetBaseline()
        }
        startForeground(NOTIFICATION_ID, notification())
        if (sampler == null) sampler = scope.launch { loop() }
        return START_NOT_STICKY
    }

    private suspend fun loop() {
        var auditCounter = 0
        while (scope.isActive) {
            try {
                val t = telemetry.sample(learnBaseline = true)
                val metricInput = com.nsg.cybersentinel.metrics.CyberMetricInput(
                    volumeRateNorm = 0.0,
                    temporalVelocityNorm = 0.0,
                    destinationNoveltyNorm = 0.0,
                    destinationDiversityNorm = 0.0,
                    resourcePressureNorm = 0.0,
                    retryPersistenceNorm = 0.0,
                    repeatedTargetingNorm = 0.0,
                    sequenceStructureNorm = 0.0,
                    dnsChurnNorm = 0.0,
                    baselineMaturityNorm = (t.baselineSamples / 60.0).coerceIn(0.0, 1.0),
                    independentAgreementNorm = 0.0,
                    contradictionNorm = 0.0,
                    noiseNorm = 0.0,
                )
                val metricSnapshot = metricCore.score(metricInput)

                var auditHead = ledger.head()
                if (++auditCounter >= 10) {
                    auditCounter = 0
                    val payload = "rxBps=${t.rxBytesPerSec},txBps=${t.txBytesPerSec},transport=${t.transport},validated=${t.validated},burstZ=${"%.3f".format(t.burstZ)},metricCore=${metricSnapshot.coreStatus},cedi=${metricSnapshot.cedi},ccii=${metricSnapshot.ccii},cpei=${metricSnapshot.cpei},coie=${metricSnapshot.coie},state=${metricSnapshot.threatState}"
                    auditHead = ledger.append(t.timestampMs, "REAL", payload)
                }
                SentinelRuntime.update(
                    SentinelSnapshot(
                        serviceRunning = true,
                        telemetry = t,
                        metrics = metricSnapshot,
                        auditHead = auditHead,
                    )
                )
            } catch (t: Throwable) {
                SentinelRuntime.update(SentinelRuntime.snapshot.copy(lastError = t.javaClass.simpleName + ": " + (t.message ?: "unknown")))
            }
            delay(1_000)
        }
    }

    override fun onDestroy() {
        sampler?.cancel()
        scope.coroutineContext[Job]?.cancel()
        SentinelRuntime.update(SentinelRuntime.snapshot.copy(serviceRunning = false))
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL, "Cyber Sentinel Protection", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun notification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this, CHANNEL)
            .setContentTitle("NSG Cyber Sentinel")
            .setContentText("Metadata-only protection telemetry active")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_PURGE = "com.nsg.cybersentinel.PURGE"
        private const val CHANNEL = "nsg_sentinel_protection"
        private const val NOTIFICATION_ID = 1201
    }
}
