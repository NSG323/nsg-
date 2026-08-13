# ARCHITECTURE

## Canonical chain

Android Telemetry → Behavioral Sensors → Cyber Metric Adapter → CEDI / CCII / CPEI / COIE → Threat State → Response Recommendation → Audit Ledger.

## Reconstructed candidate boundary

The original `NSG_Cyber_Sentinel_v1_0_candidate.zip` referenced by the handoff packet was not available. This repository therefore reconstructs only components whose behavior is specified by the packet.

### Implemented

- user-visible foreground aggregate telemetry service
- RX/TX rate telemetry
- transport / validation / metered / VPN observation
- adaptive sensor baseline and burst z-score
- IPv4 defensive parser
- TCP/UDP metadata parsers
- conservative DNS question metadata parser
- bounded flow table
- Cyber Oxygen resource-share primitives
- SHA-256 chained local audit ledger
- VpnService permission and lifecycle scaffold
- TUN safety gate
- CyberMetricAdapter input contract

### Deliberately locked

CEDI, CCII, CPEI and COIE scoring coefficients/equations were not present in the handoff packet. `LockedMetricCore` refuses to invent them. Threat state therefore remains LEARNING until the authoritative equations/source are restored.

## VPN safety gate

`TunInterfaceManager.establishOnlyWhen()` will not establish a TUN unless a forwarding engine reports `ready=true`. The current forwarding engine intentionally reports false. This prevents the known failure mode where a VPN captures traffic and drops it.
