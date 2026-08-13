# FAILURE MODE REPORT

Status legend: DESIGNED = defensive behavior implemented in code; PENDING = requires device/instrumentation validation.

| Failure mode | Status | Control |
|---|---|---|
| VPN loop | DESIGNED / PENDING validation | upstream sockets must use `VpnService.protect`; forwarding not enabled yet |
| no internet after VPN activation | DESIGNED / PENDING | TUN refuses to establish without ready forwarding engine |
| socket leak | PENDING | forwarding layer not yet active |
| TUN fd leak | DESIGNED / PENDING | `TunInterfaceManager.close()` |
| flow-table memory growth | DESIGNED | bounded access-order map + expiration |
| malformed IPv4 | DESIGNED | parser returns null on invalid lengths/header |
| malformed TCP/UDP | DESIGNED | header/length bounds checks |
| fragmented packets | DESIGNED | transport metadata parser fails closed |
| DNS compression edge cases | DESIGNED / PENDING | conservative fail-closed behavior |
| bandwidth-only false RED | DESIGNED | metric core unavailable/locked; transport never sets threat state |
| controlled-test baseline contamination | PENDING | test harness not yet restored |
| hash-chain failure | PENDING | local SHA-256 chain implemented; verification pass still needed |

Remaining handoff failure modes require real-device instrumentation and the restored frozen metric core.
