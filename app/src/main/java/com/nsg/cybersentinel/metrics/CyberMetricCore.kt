package com.nsg.cybersentinel.metrics

import com.nsg.cybersentinel.model.MetricSnapshot
import com.nsg.cybersentinel.model.ResponseRecommendation
import com.nsg.cybersentinel.model.ThreatState
import kotlin.math.sqrt

interface CyberMetricCore {
    fun score(input: CyberMetricInput): MetricSnapshot
}

/**
 * Preserved fallback for builds that intentionally disable candidate math.
 */
class LockedMetricCore : CyberMetricCore {
    override fun score(input: CyberMetricInput): MetricSnapshot = MetricSnapshot(
        coreStatus = "FROZEN_MATH_NOT_RESTORED"
    )
}

/**
 * NSG Cyber Sentinel coefficient set v0.1 — Canon Candidate.
 *
 * Design invariants:
 * 1. High traffic volume alone cannot produce ORANGE/RED.
 * 2. CCII is dominated by structured/repeated behavior, not bandwidth.
 * 3. CPEI contains explicit interaction terms so pressure emerges from collision.
 * 4. COIE gates aggressive state transitions using evidence maturity/agreement.
 * 5. All outputs are bounded to [0, 1].
 */
class CandidateMetricCoreV01 : CyberMetricCore {
    override fun score(input: CyberMetricInput): MetricSnapshot {
        val i = input.clamped()
        val k = CyberMetricCoefficientsV01

        val cedi = clamp01(
            k.Cedi.VOLUME_RATE * i.volumeRateNorm +
                k.Cedi.TEMPORAL_VELOCITY * i.temporalVelocityNorm +
                k.Cedi.DESTINATION_NOVELTY * i.destinationNoveltyNorm +
                k.Cedi.DESTINATION_DIVERSITY * i.destinationDiversityNorm +
                k.Cedi.RESOURCE_PRESSURE * i.resourcePressureNorm
        )

        val ccii = clamp01(
            k.Ccii.RETRY_PERSISTENCE * i.retryPersistenceNorm +
                k.Ccii.REPEATED_TARGETING * i.repeatedTargetingNorm +
                k.Ccii.SEQUENCE_STRUCTURE * i.sequenceStructureNorm +
                k.Ccii.DNS_CHURN * i.dnsChurnNorm +
                k.Ccii.TEMPORAL_VELOCITY * i.temporalVelocityNorm
        )

        val cpeiBase = clamp01(
            k.CpeiBase.DESTINATION_NOVELTY * i.destinationNoveltyNorm +
                k.CpeiBase.TEMPORAL_VELOCITY * i.temporalVelocityNorm +
                k.CpeiBase.RETRY_PERSISTENCE * i.retryPersistenceNorm +
                k.CpeiBase.REPEATED_TARGETING * i.repeatedTargetingNorm +
                k.CpeiBase.SEQUENCE_STRUCTURE * i.sequenceStructureNorm +
                k.CpeiBase.DNS_CHURN * i.dnsChurnNorm +
                k.CpeiBase.RESOURCE_PRESSURE * i.resourcePressureNorm +
                k.CpeiBase.DESTINATION_DIVERSITY * i.destinationDiversityNorm
        )

        val cpeiCollision = clamp01(
            k.CpeiCollision.RETRY_X_SEQUENCE * geometricPair(i.retryPersistenceNorm, i.sequenceStructureNorm) +
                k.CpeiCollision.TARGETING_X_RETRY * geometricPair(i.repeatedTargetingNorm, i.retryPersistenceNorm) +
                k.CpeiCollision.NOVELTY_X_VELOCITY * geometricPair(i.destinationNoveltyNorm, i.temporalVelocityNorm) +
                k.CpeiCollision.DNS_X_NOVELTY * geometricPair(i.dnsChurnNorm, i.destinationNoveltyNorm) +
                k.CpeiCollision.RESOURCE_X_VELOCITY * geometricPair(i.resourcePressureNorm, i.temporalVelocityNorm) +
                k.CpeiCollision.DIVERSITY_X_NOVELTY * geometricPair(i.destinationDiversityNorm, i.destinationNoveltyNorm)
        )

        val cpei = clamp01(
            k.CpeiCollision.BASE_MIX * cpeiBase +
                k.CpeiCollision.COLLISION_MIX * cpeiCollision
        )

        val consistency = 1.0 - i.contradictionNorm
        val signalClarity = 1.0 - i.noiseNorm
        val coieLinear = clamp01(
            k.Coie.BASELINE_MATURITY * i.baselineMaturityNorm +
                k.Coie.INDEPENDENT_AGREEMENT * i.independentAgreementNorm +
                k.Coie.CONSISTENCY * consistency +
                k.Coie.SIGNAL_CLARITY * signalClarity
        )
        val maturityAgreementGate = geometricPair(
            i.baselineMaturityNorm,
            i.independentAgreementNorm
        )
        val coie = clamp01(
            k.Coie.LINEAR_MIX * coieLinear +
                k.Coie.MATURITY_AGREEMENT_GATE_MIX * maturityAgreementGate
        )

        val threatBase = clamp01(
            k.ThreatFusion.CEDI * cedi +
                k.ThreatFusion.CCII * ccii +
                k.ThreatFusion.CPEI * cpei
        )
        val fusedThreat = clamp01(
            threatBase * (
                k.ThreatFusion.MIN_CONFIDENCE_FACTOR +
                    k.ThreatFusion.COIE_CONFIDENCE_FACTOR * coie
                )
        )

        val state = stateFor(i.baselineMaturityNorm, cedi, ccii, cpei, coie, fusedThreat)
        val recommendation = when (state) {
            ThreatState.LEARNING, ThreatState.GREEN, ThreatState.BLUE -> ResponseRecommendation.OBSERVE
            ThreatState.YELLOW -> ResponseRecommendation.INFORM
            ThreatState.ORANGE -> ResponseRecommendation.WARN
            ThreatState.RED -> ResponseRecommendation.INVESTIGATE
        }

        return MetricSnapshot(
            cedi = cedi,
            ccii = ccii,
            cpei = cpei,
            coie = coie,
            threatState = state,
            recommendation = recommendation,
            coreStatus = k.STATUS,
        )
    }

    private fun stateFor(
        baselineMaturity: Double,
        cedi: Double,
        ccii: Double,
        cpei: Double,
        coie: Double,
        fusedThreat: Double,
    ): ThreatState {
        val g = CyberMetricCoefficientsV01.StateGate
        return when {
            baselineMaturity < g.LEARNING_BASELINE_MATURITY || coie < g.LEARNING_COIE -> ThreatState.LEARNING

            fusedThreat >= g.RED_FUSED &&
                ccii >= g.RED_CCII &&
                cpei >= g.RED_CPEI &&
                coie >= g.RED_COIE -> ThreatState.RED

            fusedThreat >= g.ORANGE_FUSED &&
                ccii >= g.ORANGE_CCII &&
                cpei >= g.ORANGE_CPEI &&
                coie >= g.ORANGE_COIE -> ThreatState.ORANGE

            fusedThreat >= g.YELLOW_FUSED &&
                (ccii >= g.YELLOW_CCII || cpei >= g.YELLOW_CPEI) &&
                coie >= g.YELLOW_COIE -> ThreatState.YELLOW

            cedi >= g.BLUE_CEDI || fusedThreat >= g.BLUE_FUSED -> ThreatState.BLUE
            else -> ThreatState.GREEN
        }
    }

    private fun geometricPair(a: Double, b: Double): Double = sqrt(clamp01(a) * clamp01(b))
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
