package com.nsg.cybersentinel.model

data class TelemetrySnapshot(
    val timestampMs: Long = 0L,
    val rxBytesPerSec: Long = 0L,
    val txBytesPerSec: Long = 0L,
    val totalRxBytes: Long = 0L,
    val totalTxBytes: Long = 0L,
    val transport: String = "UNKNOWN",
    val validated: Boolean = false,
    val metered: Boolean = false,
    val vpnActive: Boolean = false,
    val burstZ: Double = 0.0,
    val txRxRatio: Double = 0.0,
    val baselineSamples: Long = 0L,
)

enum class ThreatState { LEARNING, GREEN, BLUE, YELLOW, ORANGE, RED }
enum class ResponseRecommendation { OBSERVE, INFORM, WARN, INVESTIGATE, CONTAIN }

data class MetricSnapshot(
    val cedi: Double? = null,
    val ccii: Double? = null,
    val cpei: Double? = null,
    val coie: Double? = null,
    val threatState: ThreatState = ThreatState.LEARNING,
    val recommendation: ResponseRecommendation = ResponseRecommendation.OBSERVE,
    val coreStatus: String = "FROZEN_MATH_NOT_RESTORED",
)

data class SentinelSnapshot(
    val serviceRunning: Boolean = false,
    val telemetry: TelemetrySnapshot = TelemetrySnapshot(),
    val metrics: MetricSnapshot = MetricSnapshot(),
    val auditHead: String = "GENESIS",
    val lastError: String? = null,
)
