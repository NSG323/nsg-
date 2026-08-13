# NSG CYBER SENTINEL — CODING HANDOFF PACKET
Version: 1.0
Status: IMPLEMENTATION PACKET
Target: Android / Kotlin
Primary objective: Continue NSG Cyber Sentinel from the current v1.0 candidate into a working deep network telemetry build without altering the metric core.

---

## 1. SYSTEM PURPOSE

NSG Cyber Sentinel is an adaptive, defensive Android cybersecurity system.

Primary design goal:

Android Telemetry
→ Behavioral Sensors
→ Cyber Metrics
→ Threat State
→ Response Recommendation
→ Audit Ledger

The system is designed to detect abnormal behavior through interaction density, sequence structure, pressure emergence, confidence gating, and resource-share distortion rather than relying only on malware signatures.

This is a defensive system. It must not introduce stealth, persistence abuse, credential access, surveillance of unrelated users, or covert collection.

---

## 2. FROZEN ARCHITECTURE BOUNDARY

DO NOT silently alter the existing metric architecture.

The sports systems remain separate and untouched.

Cyber descendants:

- CEDI = Cyber Interaction Density Index
- CCII = Cyber Intent Index
- CPEI = Cyber Pressure Emergence Index
- COIE = Cyber Observable Intelligence Efficiency / confidence layer
- Threat State Engine
- Response Policy
- Adaptive Baseline
- Sequence Engine
- Entity / Resource Pressure Graph
- SHA-256 hash-chained audit ledger

Rule:
Sports EDI/CII/PEI/OIE != Cyber CEDI/CCII/CPEI/COIE.

The cyber metrics may preserve conceptual mechanisms but are separate implementations.

If a failure is discovered:
1. Locate the failing layer.
2. Patch the minimum necessary layer.
3. Do not recalibrate or rewrite frozen metric math unless explicitly authorized.

---

## 3. CURRENT SYSTEM STATE

Current completed components:

- Android foreground protection service
- Aggregate RX/TX telemetry
- Transport type observation
- Network validation state
- Metered state
- VPN state awareness
- Moving adaptive baseline
- Burst / deviation detection
- TX/RX asymmetry detection
- Transport volatility detection
- Persistence signals
- Sequence memory
- Entity pressure graph
- CEDI
- CCII
- CPEI
- COIE
- Threat state:
  - LEARNING
  - GREEN
  - BLUE
  - YELLOW
  - ORANGE
  - RED
- Response policy:
  - OBSERVE
  - INFORM
  - WARN
  - INVESTIGATE
  - CONTAIN
- Controlled anomaly test
- Controlled test excluded from baseline learning
- Persistent baseline
- SHA-256 chained local audit ledger
- Android dashboard
- VpnService permission / architecture scaffold

Current deliberate limitation:

The VPN service MUST NOT establish a TUN interface until a correct packet-forwarding data plane exists.

Do not create a VPN that captures packets and drops them.

---

## 4. NEXT ENGINEERING TARGET

Build the deep network telemetry plane.

Required pipeline:

TUN
→ IPv4 / IPv6 parser
→ TCP / UDP parser
→ Flow table
→ DNS metadata extraction
→ Flow metadata sensors
→ Upstream protected socket forwarding
→ Response packet handling
→ TUN return path
→ Cyber Metric Adapter
→ CEDI / CCII / CPEI / COIE
→ Threat State

The first objective is metadata visibility and stable forwarding.

Do NOT start with payload inspection.

---

## 5. PHASE 2A — VPN DATA PLANE

Implement Android VpnService data plane.

Requirements:

- Request user permission with VpnService.prepare()
- Run as explicit user-visible foreground service
- Create TUN interface only after service activation
- Add IPv4 support first
- Add IPv6 support after IPv4 path is stable
- Protect all upstream sockets using VpnService.protect()
- Prevent routing loops
- Preserve phone connectivity
- Cleanly close file descriptors and sockets
- Restore normal network behavior on service stop
- No hidden service restart behavior
- No unauthorized persistent activation

Suggested components:

vpn/
- CyberVpnService.kt
- TunInterfaceManager.kt
- PacketReader.kt
- PacketWriter.kt
- ForwardingEngine.kt
- SocketProtector.kt

---

## 6. PHASE 2B — PACKET PARSERS

Implement defensive metadata parsers.

packet/
- Packet.kt
- IPv4Packet.kt
- IPv6Packet.kt
- TcpSegment.kt
- UdpDatagram.kt
- DnsMessage.kt

Minimum extracted metadata:

IP:
- source address
- destination address
- protocol
- packet length
- timestamp

TCP:
- source port
- destination port
- SYN
- ACK
- FIN
- RST
- payload length

UDP:
- source port
- destination port
- datagram length

DNS:
- query name
- query type
- response code
- answer count
- TTL summary

Do not store full packet payloads by default.

---

## 7. PHASE 2C — FLOW TABLE

Create a finite flow-state engine.

flow/
- FlowKey.kt
- FlowState.kt
- FlowTable.kt
- FlowMetrics.kt

FlowKey:

protocol
+ source IP
+ source port
+ destination IP
+ destination port

Per-flow state should include:

- firstSeen
- lastSeen
- packetsOut
- packetsIn
- bytesOut
- bytesIn
- SYN count
- reset count
- retransmission proxy if observable
- duration
- DNS-associated domain if known
- connection state
- activity velocity
- retry count
- destination novelty
- burst state

Flow expiration must be bounded.

No unbounded in-memory maps.

---

## 8. PHASE 2D — CYBER SENSOR FEATURES

Create metric-ready features.

sensor/
- NetworkFeatureExtractor.kt
- DestinationNoveltySensor.kt
- DnsChurnSensor.kt
- BurstSensor.kt
- RetryPersistenceSensor.kt
- FlowDiversitySensor.kt
- TemporalVelocitySensor.kt
- ResourceShareSensor.kt
- SequenceSensor.kt

Required features:

1. Destination novelty
   - new IPs relative to baseline
   - new domains relative to baseline

2. DNS churn
   - unique domains per window
   - NXDOMAIN ratio
   - rapid domain turnover
   - low-repeat destination behavior

3. Connection density
   - flows per second
   - flows per minute
   - concurrent flows

4. Retry persistence
   - repeated failed connection attempts
   - repeated resets
   - rapid reconnect patterns

5. Destination diversity
   - unique /24 or equivalent subnet counts
   - unique ASN / geography only if future threat-intel layer provides it

6. Temporal burst
   - deviation from rolling baseline

7. TX/RX asymmetry
   - outbound-dominant sessions
   - abrupt ratio changes

8. Resource-share distortion
   - fraction of total observable traffic captured by one entity

9. Sequence structure
   Example:
   NEW_DESTINATION
   → HIGH_RATE
   → FAILED_CONNECTION
   → RETRY
   → NEW_DESTINATION
   → REPEAT

---

## 9. CYBER METRIC ADAPTER

Create one translation boundary:

metrics/
- CyberMetricInput.kt
- CyberMetricAdapter.kt
- CediEngine.kt
- CciiEngine.kt
- CpeiEngine.kt
- CoieEngine.kt
- ThreatStateEngine.kt

The network engine must NEVER directly manipulate final threat state.

Network engine emits observations.

CyberMetricAdapter converts observations into metric inputs.

Metric engines remain isolated.

Conceptual chain:

Raw Flow Data
→ Derived Features
→ Metric Inputs
→ CEDI
→ CCII
→ CPEI
→ COIE
→ Threat State

---

## 10. METRIC SEMANTICS

### CEDI
Measures interaction density / deviation.

It can rise from:
- volume
- velocity
- novelty
- diversity
- pressure on resources

High CEDI alone != malicious.

### CCII
Measures purposeful / structured behavior.

It should depend on combinations such as:
- persistence
- repeated targeting
- retry structure
- sequential behavior
- escalation
- evasion-like transitions if observable

Do NOT infer intent from bandwidth alone.

### CPEI
Measures pressure emerging from interaction.

It should increase when multiple otherwise weak signals collide.

Example:
new destination
+ burst
+ retry persistence
+ DNS churn
+ resource capture

### COIE
Confidence / evidence quality.

It should increase with:
- repeated observations
- independent signal agreement
- stable evidence
- baseline maturity
- low contradiction
- low noise

Low COIE should suppress aggressive response.

---

## 11. OXYGEN / RESOURCE-SHARE PORT

Implement Cyber Oxygen as a separate module.

oxygen/
- ResourcePool.kt
- EntityResourceShare.kt
- OxygenEngine.kt

Initial definition:

Oxygen_i =
entity_observable_resource_usage
/
total_observable_resource_pool

Possible resource dimensions:

- bytes
- packets
- active flows
- new destinations
- retries
- DNS queries
- connection attempts
- concurrent sockets

Do not collapse all dimensions into one number without preserving raw components.

Output:
- per-entity shares
- concentration score
- share deviation vs baseline
- dominant entity flag

---

## 12. ENTITY MODEL

Represent observable cyber entities without overclaiming attribution.

entity/
- EntityId.kt
- EntityType.kt
- EntityState.kt
- EntityGraph.kt

Initial entity types:

- DEVICE
- NETWORK
- DESTINATION_IP
- DOMAIN
- FLOW
- TRANSPORT
- APP_UID if reliably attributable

If Android UID attribution is unavailable for a flow:
mark UNKNOWN.

Never fabricate app attribution.

---

## 13. RESPONSE POLICY

No automatic destructive actions in the first production candidate.

Allowed:

GREEN:
- OBSERVE

BLUE:
- OBSERVE
- optional informational UI

YELLOW:
- INFORM
- surface contributing signals

ORANGE:
- WARN
- recommend investigation

RED:
- INVESTIGATE
- recommend containment

CONTAIN remains a recommendation until explicitly authorized for automated enforcement.

Any future automated response must:
- be reversible
- be user-visible
- have an audit record
- require confidence gating
- avoid bricking connectivity

---

## 14. AUDIT REQUIREMENTS

Every real scoring cycle should log:

- timestamp
- source mode
- major sensor values
- CEDI
- CCII
- CPEI
- COIE
- threat state
- response recommendation
- previous hash
- current hash

Controlled tests:
- mark as CONTROLLED_TEST
- do not update adaptive baseline
- do not trigger enforcement
- may be written to a separate test ledger

---

## 15. PRIVACY BOUNDARY

Default system behavior:

- metadata first
- no full content capture
- no credential extraction
- no message inspection
- no microphone/camera collection
- no stealth
- no spyware-style persistence
- no collection from other devices/users without authorization

Retention should be minimized.

Provide a purge/reset function for:
- baseline
- flow history
- audit ledger
- learned domain state

---

## 16. FAILURE MODES TO TEST

Must explicitly test:

1. VPN loop
2. no internet after VPN activation
3. socket leak
4. TUN file descriptor leak
5. flow-table memory growth
6. malformed IPv4 packet
7. malformed IPv6 packet
8. malformed TCP/UDP header
9. fragmented packets
10. DNS compression edge cases
11. high-bandwidth legitimate download
12. video streaming
13. app-store update
14. speed test
15. network handoff Wi-Fi → cellular
16. airplane-mode interruption
17. device sleep / wake
18. service restart
19. VPN permission revoked
20. phone reboot
21. controlled anomaly contamination
22. false RED from bandwidth alone
23. COIE confidence inflation
24. stale entity pressure
25. hash-chain verification failure

---

## 17. ACCEPTANCE TESTS

### Test A — Normal browsing
Expected:
- internet remains functional
- CEDI low/moderate
- CCII low
- threat GREEN/BLUE
- no containment recommendation

### Test B — Large legitimate download
Expected:
- CEDI may rise
- CCII remains low unless sequence evidence exists
- COIE prevents malicious classification
- no RED solely because of volume

### Test C — Rapid destination churn in controlled lab traffic
Expected:
- novelty rises
- diversity rises
- CEDI rises
- CPEI rises if multiple signals interact

### Test D — Repeated failed/retry pattern in controlled lab
Expected:
- CCII increases
- sequence engine records persistence
- CPEI increases with collision

### Test E — Controlled multi-signal anomaly
Expected:
- CEDI high
- CCII elevated
- CPEI high
- COIE depends on evidence maturity
- threat progresses toward YELLOW/ORANGE
- no destructive action

### Test F — Baseline protection
Expected:
controlled tests never alter learned normal baseline.

---

## 18. PERFORMANCE TARGETS

Initial targets:

- no obvious impact on normal browsing
- bounded flow table
- bounded sequence memory
- no main-thread packet processing
- no ANR
- no unbounded coroutine creation
- no blocking disk writes in packet loop
- batch audit writes
- graceful degradation under high traffic

Use profiling before optimization.

Do not fake benchmark values.

---

## 19. IMPLEMENTATION ORDER

Execute in this order:

1. Inspect existing v1.0 candidate
2. Lock metric interfaces
3. Implement TUN lifecycle
4. Implement IPv4 parser
5. Implement UDP forwarding
6. Implement DNS metadata
7. Implement TCP forwarding
8. Stabilize connectivity
9. Add flow table
10. Add feature extraction
11. Wire CyberMetricAdapter
12. Add Oxygen/resource-share
13. Add entity graph enrichment
14. Add dashboard diagnostics
15. Add audit fields
16. Add deterministic tests
17. Add instrumentation tests
18. Run failure-mode suite
19. Produce build report
20. Package APK/project candidate

Do not move to metric calibration merely because a transport bug occurs.

---

## 20. CODING STANDARD

- Kotlin
- coroutines for asynchronous work
- clear interfaces between transport, sensors, metrics, response, audit
- immutable data objects where practical
- explicit units in variable names where useful
- bounded collections
- no silent exception swallowing
- structured logging
- deterministic tests for metric logic
- instrumentation tests for Android service/VPN lifecycle
- comments explain mechanism, not obvious syntax

---

## 21. REQUIRED DELIVERABLES

Produce:

1. Updated Android Studio project
2. BUILD_REPORT.md
3. ARCHITECTURE.md
4. METRIC_INTERFACE.md
5. TEST_REPORT.md
6. FAILURE_MODE_REPORT.md
7. PRIVACY_BOUNDARY.md
8. CHANGELOG.md
9. APK if build environment supports it

---

## 22. CODER START COMMAND

Start from the current NSG Cyber Sentinel v1.0 candidate.

Preserve all existing cyber metric semantics and architecture.

Implement the deep Android VPN telemetry plane beginning with safe TUN lifecycle and stable packet forwarding. Do not modify CEDI/CCII/CPEI/COIE mathematics unless a compile/runtime incompatibility requires an interface patch; if so, document the incompatibility and make the minimum patch only.

Priority order:
connectivity correctness
→ packet parsing
→ flow state
→ metadata features
→ metric adapter
→ validation
→ hardening.

Never claim a phase is complete unless its acceptance test passes.

---

## 23. CURRENT SOURCE PACKAGE

Expected source input:
NSG_Cyber_Sentinel_v1_0_candidate.zip

This coding packet is the authoritative implementation handoff for the next engineering pass.
