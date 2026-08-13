# METRIC INTERFACE

## Frozen boundary

Network code emits observations/features. It must never directly set final threat state.

`CyberMetricInput` carries separate normalized components for:

- volume rate
- temporal velocity
- destination novelty
- destination diversity
- resource pressure
- retry persistence
- repeated targeting
- sequence structure
- DNS churn
- baseline maturity
- independent signal agreement
- contradiction
- noise

`CyberMetricCore` is the only scoring interface. The reconstructed candidate ships `LockedMetricCore`, which returns no fabricated CEDI/CCII/CPEI/COIE values.

## Restoration rule

When the authoritative v1.0 metric implementation is recovered, replace only `LockedMetricCore` with an implementation that preserves the frozen equations and thresholds. Do not change transport, parser, or flow code to compensate for metric calibration issues.
