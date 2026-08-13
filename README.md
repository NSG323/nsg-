# NSG Cyber Sentinel

Defensive Android network-anomaly monitor reconstructed from the NSG Cyber Sentinel v1.0 coding handoff packet.

## Current candidate

This source package intentionally separates **working telemetry/transport foundations** from the **frozen metric core**.

The original candidate ZIP referenced by the handoff packet was not available. Because the handoff packet does not include the exact CEDI/CCII/CPEI/COIE equations or coefficients, those values are not fabricated. The Android app exposes the metric boundary as locked until the authoritative source is restored.

### Implemented now

- foreground metadata telemetry
- aggregate RX/TX rates
- connectivity/transport state
- adaptive baseline sensor
- IPv4/TCP/UDP/DNS metadata parsers
- bounded flow table
- Oxygen/resource-share primitives
- SHA-256 audit chain
- VPN permission/lifecycle scaffold
- TUN connectivity safety gate
- GitHub Actions debug APK build

### Deliberately not enabled

- packet payload retention
- hidden persistence
- credential/message capture
- automated destructive containment
- TUN interception without a functioning protected-socket forwarding engine
- replacement metric equations

## Build

GitHub Actions workflow: **Android Debug APK**.

When the project is pushed to `main`, the workflow runs unit tests and then builds `app/build/outputs/apk/debug/app-debug.apk` as an artifact.
