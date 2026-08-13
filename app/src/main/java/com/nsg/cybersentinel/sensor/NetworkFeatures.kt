package com.nsg.cybersentinel.sensor

data class NetworkFeatures(
    val volumeRateNorm: Double = 0.0,
    val temporalVelocityNorm: Double = 0.0,
    val destinationNoveltyNorm: Double = 0.0,
    val destinationDiversityNorm: Double = 0.0,
    val resourcePressureNorm: Double = 0.0,
    val retryPersistenceNorm: Double = 0.0,
    val repeatedTargetingNorm: Double = 0.0,
    val sequenceStructureNorm: Double = 0.0,
    val dnsChurnNorm: Double = 0.0,
    val baselineMaturityNorm: Double = 0.0,
    val independentAgreementNorm: Double = 0.0,
    val contradictionNorm: Double = 0.0,
    val noiseNorm: Double = 0.0,
)
