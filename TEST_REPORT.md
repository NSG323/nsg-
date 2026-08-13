# TEST REPORT

## Authored deterministic tests

- `Ipv4ParserTest.parsesMinimalIpv4UdpHeader`
- `Ipv4ParserTest.rejectsMalformedLength`
- `FlowTableTest.remainsBounded`
- `FlowTableTest.expiresIdleFlows`

## Execution status

Android/JUnit tests were not executed because Android Gradle tooling/SDK was absent. Separately, the pure Kotlin core sources compiled with `kotlinc`, and a direct smoke harness passed IPv4/UDP parsing plus bounded-flow checks (`CORE_SMOKE_PASS`). The included GitHub Actions workflow runs `gradle testDebugUnitTest` before APK assembly.

No test is marked PASS until it actually executes successfully.
