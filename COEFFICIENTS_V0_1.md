# NSG Cyber Sentinel — Cyber Metric Coefficient Set v0.1

Status: **CANDIDATE / NOT YET VALIDATED CANON**

This coefficient set is newly authored from the Cyber Sentinel metric semantics. It does not claim to recover a missing historical formula. It remains isolated and versioned so calibration can change weights without changing telemetry, parsers, flow state, privacy boundaries, or response architecture.

All metric inputs and outputs are normalized to `[0,1]`.

## CEDI — Cyber Interaction Density Index

`CEDI = 0.25V + 0.20T + 0.20N + 0.15D + 0.20P`

- `V` volume rate
- `T` temporal velocity / burst behavior
- `N` destination novelty
- `D` destination diversity
- `P` observable resource pressure

High CEDI alone is not malicious.

## CCII — Cyber Intent / Sequence Persistence

`C = min(S,R,G)`

`CCII = 0.30S + 0.25R + 0.20G + 0.10Q + 0.05N + 0.10C`

- `S` sequence structure
- `R` retry persistence
- `G` repeated targeting
- `Q` DNS churn
- `N` destination novelty
- `C` three-way persistence collision

Bandwidth is deliberately excluded from CCII.

## CPEI — Cyber Pressure Emergence Index

`K = sqrt((NR + NQ + RP + RS + PS) / 5)`

`CPEI = 0.20N + 0.15Q + 0.20R + 0.15P + 0.15S + 0.15K`

The collision term makes interacting weak signals matter more than isolated extremes.

## COIE — Cyber Observable Intelligence Efficiency / confidence

Evidence stability is derived as:

`E = (R + G + S) / 3`

Then:

`COIE = 0.30A + 0.25M + 0.20E + 0.15(1-X) + 0.10(1-Z)`

- `A` independent signal agreement
- `M` baseline maturity
- `E` evidence stability/repetition
- `X` contradiction
- `Z` noise

COIE gates confidence. It cannot create a threat by itself.

## Threat fusion

`ThreatRaw = 0.30*CEDI + 0.35*CCII + 0.35*CPEI`

`ThreatScore = ThreatRaw * (0.40 + 0.60*COIE)`

State gates:

- `< 0.25` GREEN
- `0.25–0.39` BLUE
- `0.40–0.54` YELLOW
- `0.55–0.74` ORANGE
- `>= 0.75` RED only when `COIE >= 0.70`
- baseline maturity `< 0.25` remains LEARNING

## Response recommendation

`ResponseUrgency = 0.70*ThreatScore + 0.30*COIE`

- OBSERVE below `0.35`
- INFORM at `>=0.35` with `COIE >=0.40`
- WARN at `>=0.50` with `COIE >=0.50`
- INVESTIGATE at `>=0.65` with `COIE >=0.60`
- CONTAIN recommendation at `>=0.80` with `COIE >=0.75` and RED state

`CONTAIN` is a recommendation only. Automatic destructive containment remains disabled.

## Adaptive baseline learning constants

Candidate learning rates for a future EMA baseline implementation:

- initial learning `alpha = 0.08`
- mature normal operation `alpha = 0.02`
- elevated anomaly `alpha = 0.005`
- controlled test `alpha = 0.00`
- RED event `alpha = 0.00`

The current Welford sensor baseline remains intact; these alpha values are documented but are not silently substituted into that different estimator.
