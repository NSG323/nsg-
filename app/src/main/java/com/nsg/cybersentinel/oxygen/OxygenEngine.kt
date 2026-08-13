package com.nsg.cybersentinel.oxygen

data class ResourcePool(
    val bytes: Long,
    val packets: Long,
    val activeFlows: Long,
    val newDestinations: Long,
    val retries: Long,
    val dnsQueries: Long,
    val connectionAttempts: Long,
    val concurrentSockets: Long,
)

data class EntityResourceShare(
    val entityId: String,
    val byteShare: Double,
    val packetShare: Double,
    val flowShare: Double,
    val retryShare: Double,
)

object OxygenEngine {
    fun share(entityId: String, entity: ResourcePool, total: ResourcePool): EntityResourceShare = EntityResourceShare(
        entityId = entityId,
        byteShare = safe(entity.bytes, total.bytes),
        packetShare = safe(entity.packets, total.packets),
        flowShare = safe(entity.activeFlows, total.activeFlows),
        retryShare = safe(entity.retries, total.retries),
    )

    private fun safe(v: Long, total: Long): Double = if (total > 0) v.toDouble() / total.toDouble() else 0.0
}
