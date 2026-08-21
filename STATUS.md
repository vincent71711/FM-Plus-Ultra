# Status

- Phase: competitive SMB baseline achieved; continue product validation
- Branch: `codex/beta-feedback-fixes`
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Identity: FM Plus Ultra; debug package
  `com.froslabs.filemanagerplusultra.debug`
- Telemetry: Firebase/Crashlytics/Google Services removed
- Branding: approved version-2 librarian source integrated into launcher assets;
  adaptive foreground scaled to 64% so more of the folder is visible
- Build: version 55 passes the documented JDK 21
  `assembleDebug lintVitalRelease assembleRelease` gate. The minified release
  APK reports `0.1.0-beta.3` (55), verifies under v1/v2 signing, and matches the
  permanent release certificate.
- Current release: version `0.1.0-beta.3` (55) adds broad haptic feedback,
  clearer multi-file transfer progress, stale SMB browsing recovery, and a
  defensive stalled-read timeout/retry. Vincent approved its GitHub source and
  signed APK publication on 2026-08-20; prerelease `v0.1.0-beta.3` now contains
  the signed APK and checksum. The underlying random SMB stale state that
  self-resumed after about 60 seconds in one foreground Fold7 run remains under
  diagnosis; the new timeout/retry is mitigation rather than a confirmed
  root-cause fix.
- Tests: no upstream test source sets found; lint vital, debug assembly, and
  signed release assembly pass
- Device install: the latest version 54 development debug is installed on the
  explicitly selected Fold7 and S23 FE. Vincent requested no scripted physical
  testing after this install and will perform the interaction checks. The
  permanently signed version 53 release remains installed on the Fold7.
- Fold lifecycle: cover (1080x2520) to inner (1968x2184) to cover passed while
  retaining the same resumed process/activity with no fatal exception
- Synthetic local actions: rename and copy-then-delete results verified; move
  remains unverified because the synthetic source and destination both remain
- Navigation/UI: installed Home dashboard has reorderable shortcuts, native
  Back transitions, one Remote entry, pinned controls, readable sort options,
  and divided file rows. Recent now keeps at most five unique storage sources,
  labels them with friendly source-relative paths, and suppresses raw roots.
- Pending UI validation: dividers now animate with their rows; sidebar selection
  follows the clicked item rather than duplicate paths; long names truncate at
  the end by default.
- SMB transfer: increasing the generic copy buffer from 8 KiB to 256 KiB was
  physically validated at 6.7x to 9.8x faster upload with a byte-correct round
  trip.
- Persistence repair: derivative Android version code corrected from 1 and is now 53;
  version 1 retriggered inherited migrations that discarded saved SMB entries.
  A saved SMB entry now survives restart.
- Filename preference migration: version 41 converts the inherited saved Middle
  ellipsis default to End; the installed preference was verified as value `2`.
- Coexistence: Play Store Material Files remains installed under its upstream ID
- Push/publication: Beta 3 source branch and tag are on private Forgejo and the
  public GitHub fork `vincent71711/FM-Plus-Ultra`. GitHub prerelease
  `v0.1.0-beta.3` contains the signed APK and checksum; draft PR #1 tracks the
  source changes against `main`.
- Refresh stability: provider event bursts are coalesced; the installed build
  remained visually stable during an SMB upload.
- SMB packet findings: File Manager Plus held its captured 4 GiB upload near
  39 MB/s with 256 KiB requests and at most two writes outstanding. Version 41's
  512 KiB x12 upload fell from 36.8 to 26.4 MB/s by quarter despite approximately
  8 ms SMB response latency, plentiful credits, and no TCP window stalls.
- Transfer diagnostics: the installed debug build logs exact job start, samples,
  finish, elapsed time, direction, and SMB request age/credits.
- Fold UI: rotated-cover header alignment fixed; unfolded landscape now uses a
  temporary drawer and the `drawerLayout` crash is repaired.
- SMB baseline: version 45 uses AndroidOpenSSL for native AES-CMAC/HMAC/AES-GCM
  with automatic Bouncy Castle fallback, bounded async transport, 256 KiB x8
  reads, and 256 KiB x4 writes. A 2 GiB physical round trip measured 84.40 MB/s
  upload and 110.21 MB/s download, with late-run samples near 90-98 and
  125-129 MB/s respectively.
- SMB multi-file test: version 46 keeps the 256 KiB x8 pipeline for large files
  but bounds speculative reads to each opened file's size. The 25-file,
  313,522,904-byte audiobook download completed its post-conflict active window
  in about 4.87 seconds (about 64.4 MB/s); the raw 26.26 MB/s job average includes
  roughly seven seconds waiting before file data began. The unchanged upload
  control measured 72.04 MB/s, consistent with version 45's 71.34 MB/s result.
- Capture cleanup: temporary packet captures and diagnostics were removed after
  analysis; no capture artifacts are tracked in Git.
- Clipboard: selections within one configured storage source may accumulate;
  selecting from another source replaces the batch. Copy and Move batches clear
  when Paste is initiated. Physical validation accumulated two files from separate
  Internal storage folders as `Copying 2`, then replaced them with one test-SMB
  file as `Copying 1`.
- Latest 2 GiB round trip: version 47 reached 91.49 MB/s phone-to-SMB and
  119.15 MB/s SMB-to-phone with no transfer error, ANR, or crash.
- Version 48 UI: dark-ruby accents, neutral white/gray surfaces, ruby generic
  folder icons and controls, and an exact-white drawer whose Material elevation
  color overlay is disabled. Sort/view popups use a fixed neutral gray instead
  of a dynamic pink surface. Home edit mode removes the inert grabber, subtly
  jiggles tiles, and starts drag from a tile touch.
- Version 48 compatibility: Material 3 off plus a custom color no longer causes
  a persistent startup crash; both that state and restored Material 3/default
  were cold-launched without a fatal exception.
- Top-level Back is guarded: the first swipe shows a short exit prompt and only
  a second Back within 2.5 seconds exits; the guard now follows the displayed
  Home screen even when Android restores or launches it through a non-Main intent.
  The prompt toast is cancelled on confirmed exit or activity destruction so it
  no longer lingers over the launcher; physically confirmed on the Fold7.
- SMB robustness: Android-native Conscrypt/OpenSSL providers are tried per
  primitive before portable Bouncy Castle fallback. Expected nested watcher
  interruptions during remote-folder exit no longer print misleading stacks.
- Release signing: `0.1.0-beta.1` is minified, `BuildConfig.DEBUG=false`, and
  signed with the permanent 4096-bit RSA release certificate whose SHA-256 is
  `b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`.
  The private key and credentials remain outside Git.
- Version 52 UI: Home displays storage used/total plus local category size and
  file count, Remote displays its configured-connection count, and Access from
  network opens the existing FTP server. Progress tracks, selection fills, and
  the clipboard bar use neutral grays. Long-pressing a selected row now deselects
  it instead of opening it. The Home pencil/check control provides standard
  haptic feedback in both states.
- Beta feedback implementation: cached SMB sessions are invalidated on Android
  default-network changes and after transport failures; a failed complete
  directory enumeration reconnects and retries once, while writes are not
  replayed. Remote Home now returns to the actual Home dashboard, and list-view
  file rows are 64dp instead of 72dp without smaller text or lost metadata. Home
  statistics now refresh on resume and through pull-to-refresh. Newly created
  files and folders are revealed at their resulting sorted list/grid position.
- Latest beta-feedback fixes: all short taps on enabled app controls now provide
  standard system-respecting haptic feedback while swipes, canceled gestures,
  and long presses are excluded. Pull-to-refresh provides a gesture-end haptic
  only after crossing the refresh threshold. A successful file operation provides
  one distinct completion haptic when its progress dialog changes to Complete.
  The separate file-conflict dialog window now also provides tap feedback for
  its Apply-to-all checkbox and Cancel, Skip, and Replace/Merge/Rename actions.
  The transfer dialog's shared action button likewise provides feedback when it
  acts as Cancel or OK, independently of the automatic completion haptic. SMBJ's
  exact `IllegalStateException: Transport is not connected` stale-socket signal
  is now classified as a retryable transport failure, so directory browsing
  evicts the matching session, reconnects, and retries once instead of requiring
  a manual pull-to-refresh.
- Transfer details now use configured storage names and relative paths for the
  gray source-to-destination route instead of repeating the current filename and
  target basename. The current-file heading auto-sizes rather than ellipsizing.
  Multi-file jobs update that heading for each active file and show separate
  current-file and overall byte-progress bars; single-file jobs retain one
  overall bar.
- Known issue / Fold7 stall diagnosis: a 4 GiB two-file SMB download stopped at 142,344,192
  bytes for about 60 seconds while the app was still foregrounded, then resumed
  and completed without an ANR or crash. The optimized SMB channel had bypassed
  the common 15-second read deadline with an unbounded wait. Reads now enforce
  that deadline, close a stalled SMB connection, roll back the partial current
  file's progress, and restart that file once. A repeated stall reaches the
  existing error flow instead of leaving the transfer apparently frozen. Debug
  logging now records every SMB request taking at least one second.
- Validation: Beta 3 passes
  `assembleDebug lintVitalRelease assembleRelease` with JDK 21. Its permanent
  release signature, package identity, and version 55 metadata are verified;
  SHA-256 is `a5baec22e77b787fd47705aecb7003c2714ada5944f1fa78dd29e4f0b853e954`.
  Per Vincent's request, Beta 3 was not installed or interaction-tested as part
  of publication.
- Physical checks for SMB reconnect, remote Home navigation, row density, and
  pending paste retention remain with Vincent. Haptic feel and the refined SMB
  disconnected-transport recovery also remain for Vincent's physical check. The
  new stalled-read recovery is build-validated but intentionally not exercised
  by scripted phone interaction.
