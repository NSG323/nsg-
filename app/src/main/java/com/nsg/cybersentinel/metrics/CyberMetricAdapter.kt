package com.nsg.cybersentinel.metrics

import com.nsg.cybersentinel.sensor.NetworkFeatures

class CyberMetricAdapter {
    fun adapt(features: NetworkFeatures): CyberMetricInput = CyberMetricInput(
        volumeRateNorm = features.volumeRateNorm,
        temporalVelocityNorm = features.temporalVelocityNorm,
        destinationNoveltyNorm = features.destinationNoveltyNorm,
        destinationDiversityNorm = features.destinationDiversityNorm,
        resourcePressureNorm = features.resourcePressureNorm,
        retryPersistenceNorm = features.retryPersistenceNorm,
        repeatedTargetingNorm = features.repeatedTargetingNorm,
        sequenceStructureNorm = features.sequenceStructureNorm,
        dnsChurnNorm = features.dnsChurnNorm,
        baselineMaturityNorm = features.baselineMaturityNorm,
        independentAgreementNorm = features.independentAgreementNorm,
        contradictionNorm = features.contradictionNorm,
        noiseNorm = features.noiseNorm,
    )
}
