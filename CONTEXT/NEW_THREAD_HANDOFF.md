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
- Upstream app version: 1.7.4 (39); mod version: 0.1.0-dev (40). The derivative
  Android version code must remain above inherited migration thresholds and
  increase monotonically; using 1 caused legacy storage migration to discard
  saved SMB entries on restart.
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
- Installed UI checkpoint: pinned black toolbar, gray clickable breadcrumbs,
  divided file rows, editable Home shortcut order, one Remote entry, and up to
  five persistent recent subfolders embedded in the drawer. Navigation roots
  are excluded from Recent.
- Home shortcuts open child file activities, giving them the same native Back
  transition as Remote and preventing one shortcut's rows from flashing in the
  next. Shared paste state remains process-wide.
- The first transfer repair changes the generic copy buffer from 8 KiB to
  256 KiB; physical tests showed 6.7x to 9.8x faster upload and a byte-correct
  round trip.
- Provider event bursts now use trailing-edge coalescing; the installed build
  stayed visually stable during an SMB upload. Progress notifications still
  update about every 0.6 seconds, and there is no useful in-app transfer center.
- UX reference: structured divided rows, explicit selection/paste controls, a
  compact operations list, detailed progress (paths, bytes, speed, ETA, count,
  cancel), stable notification, conflict actions, and stable browsing.
- Next: run comparable SMB benchmarks, profile the remaining bottleneck, and
  make the next focused throughput optimization.
