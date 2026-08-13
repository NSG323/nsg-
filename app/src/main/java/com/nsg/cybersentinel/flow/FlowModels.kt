package com.nsg.cybersentinel.flow

data class FlowKey(
    val protocol: Int,
    val sourceIp: String,
    val sourcePort: Int,
    val destinationIp: String,
    val destinationPort: Int,
)

data class FlowState(
    val firstSeenMs: Long,
    var lastSeenMs: Long,
    var packetsOut: Long = 0,
    var packetsIn: Long = 0,
    var bytesOut: Long = 0,
    var bytesIn: Long = 0,
    var synCount: Long = 0,
    var resetCount: Long = 0,
    var retryCount: Long = 0,
    var domain: String? = null,
    var destinationNovel: Boolean = false,
    var burst: Boolean = false,
) {
    val durationMs: Long get() = (lastSeenMs - firstSeenMs).coerceAtLeast(0L)
}
