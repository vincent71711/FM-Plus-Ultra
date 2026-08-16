# New thread handoff

- Product: private-use Android file manager derivative for a Galaxy Z Fold7.
- Base: official Material Files, GPLv3, full history preserved.
- Upstream: `https://github.com/zhanghai/MaterialFiles.git`, branch `master`.
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a` with local tag
  `mod-baseline/2026-08-16-material-files-fc12500`.
- Work branch: `codex/project-bootstrap`; no push or publication.
- Minimal bootstrap repair: `dav4jvm` dependency expanded from abbreviated SHA
  to the same commit's full SHA because JitPack no longer serves the short form.
- Validated gate: JDK 21 plus
  `./gradlew assembleDebug lintVitalRelease --stacktrace --console=plain`;
  successful with 94 tasks.
- Upstream app version: 1.7.4 (39); mod continuity version: 0.1.0-dev.
- Do not install the derivative yet: upstream uses production application ID
  `me.zhanghai.android.files` and includes Firebase Analytics/Crashlytics.
- Observed upstream defects on the Fold7: cross-provider local-to-SMB copy about
  1.1 MB/s; 8 KiB generic copy buffer; SMB modification events cause a complete
  visible directory reload every second; progress notification updates every
  500 ms; no useful in-app transfer center.
- UX reference: structured divided rows, explicit selection/paste controls, a
  compact operations list, detailed progress (paths, bytes, speed, ETA, count,
  cancel), stable notification, conflict actions, and stable browsing.
- Next: resolve application ID, remove Firebase, establish synthetic SMB
  benchmark baseline, then propose—not broadly implement—the transfer phase.
