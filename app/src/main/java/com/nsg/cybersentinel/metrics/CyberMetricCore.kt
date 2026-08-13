package com.nsg.cybersentinel.metrics

import com.nsg.cybersentinel.model.MetricSnapshot

interface CyberMetricCore {
    fun score(input: CyberMetricInput): MetricSnapshot
}

/**
 * Safe reconstruction behavior when the authoritative candidate math is absent.
 * It intentionally does NOT invent coefficients or thresholds.
 */
class LockedMetricCore : CyberMetricCore {
    override fun score(input: CyberMetricInput): MetricSnapshot = MetricSnapshot(
        coreStatus = "FROZEN_MATH_NOT_RESTORED"
    )
}
