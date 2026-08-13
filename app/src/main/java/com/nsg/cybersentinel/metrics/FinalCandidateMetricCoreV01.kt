package com.nsg.cybersentinel.metrics

import com.nsg.cybersentinel.model.MetricSnapshot
import com.nsg.cybersentinel.model.ResponseRecommendation
import com.nsg.cybersentinel.model.ThreatState
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Defensive metadata-only scoring candidate v0.1.
 * Pure calculation: no packet handling, blocking, persistence, or network mutation.
 */
class FinalCandidateMetricCoreV01 : CyberMetricCore {
    override fun score(input: CyberMetricInput): MetricSnapshot {
        val i = input.clamped()

        val cedi = clamp01(
            0.25 * i.volumeRateNorm +
                0.20 * i.temporalVelocityNorm +
                0.20 * i.destinationNoveltyNorm +
                0.15 * i.destinationDiversityNorm +
                0.20 * i.resourcePressureNorm
        )

        val sequenceCollision = min(
            i.sequenceStructureNorm,
            min(i.retryPersistenceNorm, i.repeatedTargetingNorm)
        )
        val ccii = clamp01(
            0.30 * i.sequenceStructureNorm +
                0.25 * i.retryPersistenceNorm +
                0.20 * i.repeatedTargetingNorm +
                0.10 * i.dnsChurnNorm +
                0.05 * i.destinationNoveltyNorm +
                0.10 * sequenceCollision
        )

        val collisionMean = (
            i.destinationNoveltyNorm * i.retryPersistenceNorm +
                i.destinationNoveltyNorm * i.dnsChurnNorm +
                i.retryPersistenceNorm * i.resourcePressureNorm +
                i.retryPersistenceNorm * i.sequenceStructureNorm +
                i.resourcePressureNorm * i.sequenceStructureNorm
            ) / 5.0
        val pressureCollision = sqrt(collisionMean.coerceIn(0.0, 1.0))
        val cpei = clamp01(
            0.20 * i.destinationNoveltyNorm +
                0.15 * i.dnsChurnNorm +
                0.20 * i.retryPersistenceNorm +
                0.15 * i.resourcePressureNorm +
                0.15 * i.sequenceStructureNorm +
                0.15 * pressureCollision
        )

        val evidenceStability = (
            i.retryPersistenceNorm +
                i.repeatedTargetingNorm +
                i.sequenceStructureNorm
            ) / 3.0
        val coie = clamp01(
            0.30 * i.independentAgreementNorm +
                0.25 * i.baselineMaturityNorm +
                0.20 * evidenceStability +
                0.15 * (1.0 - i.contradictionNorm) +
                0.10 * (1.0 - i.noiseNorm)
        )

        val threatRaw = clamp01(0.30 * cedi + 0.35 * ccii + 0.35 * cpei)
        val threatScore = clamp01(threatRaw * (0.40 + 0.60 * coie))

        val state = when {
            i.baselineMaturityNorm < 0.25 -> ThreatState.LEARNING
            threatScore >= 0.75 && coie >= 0.70 -> ThreatState.RED
            threatScore >= 0.55 -> ThreatState.ORANGE
            threatScore >= 0.40 -> ThreatState.YELLOW
            threatScore >= 0.25 -> ThreatState.BLUE
            else -> ThreatState.GREEN
        }

        val urgency = clamp01(0.70 * threatScore + 0.30 * coie)
        val recommendation = when {
            state == ThreatState.LEARNING -> ResponseRecommendation.OBSERVE
            urgency >= 0.80 && coie >= 0.75 && state == ThreatState.RED -> ResponseRecommendation.CONTAIN
            urgency >= 0.65 && coie >= 0.60 -> ResponseRecommendation.INVESTIGATE
            urgency >= 0.50 && coie >= 0.50 -> ResponseRecommendation.WARN
            urgency >= 0.35 && coie >= 0.40 -> ResponseRecommendation.INFORM
            else -> ResponseRecommendation.OBSERVE
        }

        return MetricSnapshot(
            cedi = cedi,
            ccii = ccii,
            cpei = cpei,
            coie = coie,
            threatState = state,
            recommendation = recommendation,
            coreStatus = "COEFF_V0_1_CANDIDATE_FINAL",
        )
    }

    private fun clamp01(value: Double): Double = value.coerceIn(0.0, 1.0)

    private fun CyberMetricInput.clamped() = copy(
        volumeRateNorm = clamp01(volumeRateNorm),
        temporalVelocityNorm = clamp01(temporalVelocityNorm),
        destinationNoveltyNorm = clamp01(destinationNoveltyNorm),
        destinationDiversityNorm = clamp01(destinationDiversityNorm),
        resourcePressureNorm = clamp01(resourcePressureNorm),
        retryPersistenceNorm = clamp01(retryPersistenceNorm),
        repeatedTargetingNorm = clamp01(repeatedTargetingNorm),
        sequenceStructureNorm = clamp01(sequenceStructureNorm),
        dnsChurnNorm = clamp01(dnsChurnNorm),
        baselineMaturityNorm = clamp01(baselineMaturityNorm),
        independentAgreementNorm = clamp01(independentAgreementNorm),
        contradictionNorm = clamp01(contradictionNorm),
        noiseNorm = clamp01(noiseNorm),
    )
}
