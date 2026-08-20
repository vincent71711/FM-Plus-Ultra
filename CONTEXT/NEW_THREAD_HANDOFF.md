# New thread handoff

- Product: FM Plus Ultra, an Android file manager
  derivative for a Galaxy Z Fold7.
- Base: official Material Files, GPLv3, full history preserved.
- Upstream: `https://github.com/zhanghai/MaterialFiles.git`, branch `master`.
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a` with local tag
  `mod-baseline/2026-08-16-material-files-fc12500`.
- Work branch: `codex/beta-feedback-fixes`; validated checkpoint is on Forgejo
  `main` and the public GitHub fork `vincent71711/FM-Plus-Ultra`.
- Minimal bootstrap repair: `dav4jvm` dependency expanded from abbreviated SHA
  to the same commit's full SHA because JitPack no longer serves the short form.
- Validated release gate: JDK 21 plus
  `./gradlew assembleDebug lintVitalRelease assembleRelease --stacktrace --console=plain`.
- Upstream app version: 1.7.4 (39); current release: 0.1.0-beta.2 (54). The
  derivative Android version code must
  remain above inherited migration thresholds and increase monotonically; using
  1 caused legacy storage migration to discard saved SMB entries on restart.
- Debug identity: `com.froslabs.filemanagerplusultra.debug`; reserved release
  identity: `com.froslabs.filemanagerplusultra`.
- Firebase Analytics, Crashlytics, Google Services plugins/configuration, and
  runtime initialization are removed. Do not restore or replace telemetry.
- The selected version-2 librarian icon source is
  `docs/branding/file-manager-plus-ultra-icon-source.png`; use the checked-in
  generation script for launcher densities. Do not regenerate the mascot. The
  adaptive foreground is currently scaled to 64% for additional breathing room.
- Vincent approved FM Plus Ultra as the public source-fork name on 2026-08-16.
  The project must not imply affiliation with similarly named commercial apps.
  Public prerelease `v0.1.0-beta.1` and permanent signing are approved; store
  distribution and non-beta release channels remain undecided.
- The latest version 54 development debug is installed on the explicitly selected
  Fold7 and S23 FE. Vincent requested no scripted functional test after this
  install and will perform the physical interaction checks. The permanently
  signed version 53 release remains on the Fold7 and coexists with Play Store
  Material Files. Do not record either wireless-ADB serial.
- A live cover (1080x2520) to inner (1968x2184) to cover transition retained the
  same resumed process/activity and produced no fatal exception.
- Synthetic local rename and copy-then-delete actions are verified. The move
  action needs retesting because its source and destination both remained.
- Installed UI checkpoint: pinned black toolbar, gray clickable breadcrumbs,
  divided file rows, editable Home shortcut order, and one Remote entry. Recent
  keeps up to five unique storage sources, each labeled with its friendly source
  name and latest relative folder; raw navigation roots are excluded.
- Home shortcuts open child file activities, giving them the same native Back
  transition as Remote and preventing one shortcut's rows from flashing in the
  next. Shared paste state remains process-wide.
- The first transfer repair changes the generic copy buffer from 8 KiB to
  256 KiB; physical tests showed 6.7x to 9.8x faster upload and a byte-correct
  round trip.
- Full TrueNAS-side captures showed File Manager Plus using 256 KiB requests,
  at most two writes outstanding, and a shallow read window. Its 4 GiB upload
  held about 39 MB/s. Version 41's 512 KiB x12 upload declined from 36.8 to
  26.4 MB/s by quarter despite approximately 8 ms responses, abundant SMB
  credits, and no TCP window stalls. Its 4 MiB x2 download held about 32.8 MB/s
  after ramp-up with no active-transfer retransmissions.
- Version 42's direct 256 KiB test reached only 28.81 MB/s upload and 34.22 MB/s
  download. The common per-byte ceiling led to SMBJ's pure-Java AES-CMAC signing.
  Version 44 uses AndroidOpenSSL for available primitives with Bouncy Castle
  fallback and reached 52.39 MB/s upload and 89.42 MB/s download. Version 45
  retains that hybrid provider with bounded 256 KiB x8 reads and x4 writes;
  its physical 2 GiB test reached 84.40 MB/s upload and 110.21 MB/s download.
- Version 46 bounds the x8 speculative read pipeline to the opened file's size,
  with one EOF probe for files that grow. A 25-file, 313,522,904-byte audiobook
  download moved its post-conflict active data in about 4.87 seconds (about
  64.4 MB/s); the raw job timer included roughly seven idle seconds before reads.
  Its unchanged upload control reached 72.04 MB/s.
- Version 47's later 2 GiB run reached 91.49 MB/s phone-to-SMB and 119.15 MB/s
  SMB-to-phone without a transfer error, ANR, or crash.
- Temporary packet captures and diagnostics were deleted after analysis; none
  are tracked in Git.
- Selections across folders within one configured storage source accumulate;
  selecting from another source replaces the batch. Copy and Move batches clear
  when Paste is initiated. Physical validation showed two Internal storage folders
  accumulate as `Copying 2`, then a test-SMB selection replace them as `Copying 1`.
- The same test build draws dividers inside animated rows, uses clicked sidebar
  item identity to prevent same-path double highlights, and defaults long file
  names to end truncation. Version 41 migrates an existing saved Middle value
  to End; the installed preference was verified as `2`. Visual confirmation is pending.
- Provider event bursts now use trailing-edge coalescing; the installed build
  stayed visually stable during an SMB upload. Transfers have a compact in-app
  indicator and detailed progress dialog in addition to the notification.
- UX reference: structured divided rows, explicit selection/paste controls, a
  compact operations list, detailed progress (paths, bytes, speed, ETA, count,
  cancel), stable notification, conflict actions, and stable browsing.
- Rotated-cover header alignment is fixed. Unfolded landscape uses a temporary
  drawer; an incremental-resource `drawerLayout` crash was fixed and retested.
- Version 48 uses dark-ruby accents with neutral white/gray surfaces. The drawer
  is exact white after disabling its Material elevation color overlay. Home edit
  mode removes the inert grabber, jiggles tiles, and starts drag on tile touch.
- Version 48 fixes the crash loop from disabling Material 3 with a custom color;
  both that state and Material 3/default were physically cold-launched. Native
  Android SMB providers are explicitly preferred per primitive with portable
  fallback, and expected nested watch-cancellation noise is suppressed.
- Top-level Back now prompts once and requires a second gesture within 2.5 seconds
  to exit; the guard follows the actual Home screen even for restored/non-Main
  root activities, while nested navigation remains single-Back. Its toast is
  cancelled on confirmed exit or activity destruction and no longer lingers over
  the launcher; physically confirmed on the Fold7.
- Version 52 adds Home storage/category statistics, a Remote connection count,
  an Access from network shortcut to FTP Server, neutral progress/selection/
  clipboard surfaces, selected-row long-press deselection, and haptic feedback
  on both states of the Home edit button.
- Version `0.1.0-beta.1` (53) is the first public minified release APK. Its
  permanent certificate SHA-256 is
  `b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`;
  the private signing material remains outside Git and requires durable backup.
- Beta 2 revision implemented 2026-08-20: Android default-network
  changes invalidate cached SMB sessions. A transport failure during complete
  directory enumeration evicts the matching stale session, reconnects, and
  retries once; writes are not automatically replayed.
- Remote Home now starts the launcher Home activity with `CLEAR_TOP`, removing
  the intermediate Remote connections page while retaining process-wide pending
  paste state.
- List-view file rows now use a dedicated 64dp height instead of 72dp, retaining
  font sizes, metadata, icons, dividers, and 48dp action targets.
- Home statistics refresh whenever an already-visible Home screen resumes. The
  Home grid now has pull-to-refresh, with its indicator ending only after every
  dashboard subtitle has been recalculated.
- File and folder creation records the requested path until the refreshed
  adapter contains it, then scrolls to its actual position under the current
  list/grid and sort settings; navigating elsewhere cancels the pending reveal.
- Validation: `assembleDebug lintVitalRelease assembleRelease` passes with JDK
  21. The version 54 debug APK is installed on the explicitly selected Fold7 and
  passed an 824 ms cold launch without a fatal exception or ANR. The permanently
  signed release APK is in Downloads as
  `FM-Plus-Ultra-0.1.0-beta.2-54.apk` but was not installed.
  Targeted SMB reconnect, remote Home, density, and paste-state checks remain.
- Vincent approved the `0.1.0-beta.2` (54) source and permanently signed APK for
  GitHub publication on 2026-08-20.
- Current beta-feedback work adds common short-tap haptics for enabled
  app controls, suppresses them for swipes/cancels/long presses, and performs a
  gesture-end haptic only when pull-to-refresh crosses its activation threshold.
  It performs a distinct one-shot haptic when a file job reaches successful
  completion. It also recognizes SMBJ's exact
  `IllegalStateException: Transport is not connected` as
  a retryable stale-transport failure during directory enumeration, evicts only
  the matching cached session, reconnects, and retries once. The documented JDK
  21 `assembleDebug lintVitalRelease` gate passes. The resulting debug APK is on
  both explicitly selected phones; physical behavior remains for Vincent to test.
- The transfer dialog now renders a meaningful route from configured storage
  names plus relative paths, rather than duplicating the active filename and
  destination basename. Its active-file heading auto-sizes within two lines.
  Multi-file copy/move work tracks real bytes for the active file separately from
  aggregate job bytes, updates the heading as each file changes, and displays
  labeled Current file and Overall progress bars. Single-file work keeps one
  Overall bar. The JDK 21 debug/release-lint gate passes, and the debug update is
  installed on both explicitly selected phones without scripted interaction.
- A Fold7 SMB-to-local copy of two 2 GiB files stalled at 142,344,192 bytes for
  about 60 seconds while its progress activity was still foregrounded. It later
  resumed and completed all 4 GiB without an ANR or crash. The optimized SMB
  channel was waiting on a raw future without the common channel's intended
  15-second deadline. The current fix enforces that deadline, closes the stuck
  connection, removes the partial destination through the existing safe-copy
  cleanup, rolls back only that file's contributed progress, and automatically
  retries the current file once. A second timeout uses the existing error dialog.
  Debug logs now include every SMB request aged at least one second. The JDK 21
  `assembleDebug lintVitalRelease` gate passes; physical interaction testing is
  intentionally left to Vincent.
