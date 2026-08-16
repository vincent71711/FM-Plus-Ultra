# New thread handoff

- Product: File Manager Plus Ultra, a private-use Android file manager
  derivative for a Galaxy Z Fold7.
- Base: official Material Files, GPLv3, full history preserved.
- Upstream: `https://github.com/zhanghai/MaterialFiles.git`, branch `master`.
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a` with local tag
  `mod-baseline/2026-08-16-material-files-fc12500`.
- Work branch: `codex/private-derivative`; no push or publication.
- Minimal bootstrap repair: `dav4jvm` dependency expanded from abbreviated SHA
  to the same commit's full SHA because JitPack no longer serves the short form.
- Validated gate: JDK 21 plus
  `./gradlew assembleDebug lintVitalRelease --stacktrace --console=plain`;
  successful with 94 tasks.
- Upstream app version: 1.7.4 (39); mod version: 0.1.0-dev (1).
- Debug identity: `com.froslabs.filemanagerplusultra.debug`; reserved release
  identity: `com.froslabs.filemanagerplusultra`.
- Firebase Analytics, Crashlytics, Google Services plugins/configuration, and
  runtime initialization are removed. Do not restore or replace telemetry.
- The selected version-2 librarian icon source is
  `docs/branding/file-manager-plus-ultra-icon-source.png`; use the checked-in
  generation script for launcher densities. Do not regenerate the mascot.
- The private name is not approved public branding due similarity to an
  existing commercial app. No distribution is authorized.
- The debug APK is installed on the explicitly selected Fold7. It cold-launched
  successfully, remained alive without a fatal exception, and coexists with the
  Play Store Material Files package. Do not record the wireless-ADB serial.
- A live cover (1080x2520) to inner (1968x2184) to cover transition retained the
  same resumed process/activity and produced no fatal exception.
- Synthetic local rename and copy-then-delete actions are verified. The move
  action needs retesting because its source and destination both remained.
- The inherited file-list app bar uses `scroll|enterAlways`, allowing upward
  scrolling to hide all top controls. Pin the toolbar/breadcrumb area in the
  reviewed UI phase.
- Require a persistent Home control and expandable recent-location/history list
  for jumping among local and remote folders without retracing directory trees;
  pending copy/move state must survive those jumps.
- Observed upstream defects on the Fold7: cross-provider local-to-SMB copy about
  1.1 MB/s; 8 KiB generic copy buffer; SMB modification events cause a complete
  visible directory reload every second; progress notification updates every
  500 ms; no useful in-app transfer center.
- UX reference: structured divided rows, explicit selection/paste controls, a
  compact operations list, detailed progress (paths, bytes, speed, ETA, count,
  cancel), stable notification, conflict actions, and stable browsing.
- Next: obtain the user's visual review, retest synthetic local move, establish
  the SMB benchmark baseline, then implement the reviewed transfer phase.
