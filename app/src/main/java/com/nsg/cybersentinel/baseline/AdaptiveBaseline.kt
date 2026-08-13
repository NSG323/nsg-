package com.nsg.cybersentinel.baseline

import kotlin.math.sqrt

/**
 * Online baseline for aggregate byte-rate telemetry.
 * This is a sensor baseline only; it does not implement or recalibrate CEDI/CCII/CPEI/COIE.
 */
class AdaptiveBaseline {
    private var n = 0L
    private var mean = 0.0
    private var m2 = 0.0

    val samples: Long get() = n
    val average: Double get() = mean
    val stdDev: Double get() = if (n > 1) sqrt(m2 / (n - 1)) else 0.0

    fun observe(value: Double, learn: Boolean = true): Double {
        val z = if (n > 10 && stdDev > 1e-9) (value - mean) / stdDev else 0.0
        if (learn) {
            n += 1
            val delta = value - mean
            mean += delta / n
            val delta2 = value - mean
            m2 += delta * delta2
        }
        return z
    }

    fun reset() {
        n = 0L
        mean = 0.0
        m2 = 0.0
    }
}
