package com.nsg.cybersentinel.metrics

/**
 * Translation boundary from telemetry/flow features into the frozen cyber metric core.
 * Fields preserve raw components; they are not silently collapsed into a replacement formula.
 */
data class CyberMetricInput(
    val volumeRateNorm: Double,
    val temporalVelocityNorm: Double,
    val destinationNoveltyNorm: Double,
    val destinationDiversityNorm: Double,
    val resourcePressureNorm: Double,
    val retryPersistenceNorm: Double,
    val repeatedTargetingNorm: Double,
    val sequenceStructureNorm: Double,
    val dnsChurnNorm: Double,
    val baselineMaturityNorm: Double,
    val independentAgreementNorm: Double,
    val contradictionNorm: Double,
    val noiseNorm: Double,
)
