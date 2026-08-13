package com.nsg.cybersentinel.metrics

/**
 * NSG Cyber Sentinel coefficient set v0.1.
 *
 * Status: CANON CANDIDATE — engineered from the authoritative metric semantics.
 * These values are intentionally isolated so calibration can change coefficients
 * without changing telemetry, packet parsing, flow state, or metric architecture.
 */
object CyberMetricCoefficientsV01 {
    const val STATUS = "COEFF_V0_1_CANDIDATE"

    object Cedi {
        const val VOLUME_RATE = 0.16
        const val TEMPORAL_VELOCITY = 0.20
        const val DESTINATION_NOVELTY = 0.24
        const val DESTINATION_DIVERSITY = 0.18
        const val RESOURCE_PRESSURE = 0.22
    }

    object Ccii {
        const val RETRY_PERSISTENCE = 0.30
        const val REPEATED_TARGETING = 0.26
        const val SEQUENCE_STRUCTURE = 0.28
        const val DNS_CHURN = 0.10
        const val TEMPORAL_VELOCITY = 0.06
    }

    object CpeiBase {
        const val DESTINATION_NOVELTY = 0.12
        const val TEMPORAL_VELOCITY = 0.12
        const val RETRY_PERSISTENCE = 0.14
        const val REPEATED_TARGETING = 0.14
        const val SEQUENCE_STRUCTURE = 0.14
        const val DNS_CHURN = 0.10
        const val RESOURCE_PRESSURE = 0.14
        const val DESTINATION_DIVERSITY = 0.10
    }

    object CpeiCollision {
        const val RETRY_X_SEQUENCE = 0.24
        const val TARGETING_X_RETRY = 0.18
        const val NOVELTY_X_VELOCITY = 0.18
        const val DNS_X_NOVELTY = 0.14
        const val RESOURCE_X_VELOCITY = 0.14
        const val DIVERSITY_X_NOVELTY = 0.12
        const val BASE_MIX = 0.55
        const val COLLISION_MIX = 0.45
    }

    object Coie {
        const val BASELINE_MATURITY = 0.45
        const val INDEPENDENT_AGREEMENT = 0.40
        const val CONSISTENCY = 0.10
        const val SIGNAL_CLARITY = 0.05
        const val LINEAR_MIX = 0.55
        const val MATURITY_AGREEMENT_GATE_MIX = 0.45
    }

    object ThreatFusion {
        const val CEDI = 0.22
        const val CCII = 0.34
        const val CPEI = 0.44
        const val MIN_CONFIDENCE_FACTOR = 0.55
        const val COIE_CONFIDENCE_FACTOR = 0.45
    }

    object StateGate {
        const val LEARNING_BASELINE_MATURITY = 0.25
        const val LEARNING_COIE = 0.20

        const val BLUE_CEDI = 0.40
        const val BLUE_FUSED = 0.28

        const val YELLOW_FUSED = 0.42
        const val YELLOW_CCII = 0.38
        const val YELLOW_CPEI = 0.42
        const val YELLOW_COIE = 0.35

        const val ORANGE_FUSED = 0.56
        const val ORANGE_CCII = 0.52
        const val ORANGE_CPEI = 0.52
        const val ORANGE_COIE = 0.55

        const val RED_FUSED = 0.72
        const val RED_CCII = 0.68
        const val RED_CPEI = 0.68
        const val RED_COIE = 0.70
    }
}
