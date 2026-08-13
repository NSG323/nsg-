# PRIVACY BOUNDARY

Default behavior:

- metadata first
- no full packet payload retention
- no credential extraction
- no message inspection
- no microphone or camera collection
- no stealth
- no spyware-style persistence
- no collection from unrelated devices/users
- user-visible foreground operation
- local audit ledger

The DNS parser extracts only minimal query metadata and does not retain complete packet payloads. Raw packet processing is not enabled because TUN forwarding remains safety-locked.
