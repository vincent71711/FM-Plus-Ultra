# Status

- Phase: competitive SMB baseline achieved; continue product validation
- Branch: `codex/private-derivative`
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Identity: FM Plus Ultra; debug package
  `com.froslabs.filemanagerplusultra.debug`
- Telemetry: Firebase/Crashlytics/Google Services removed
- Branding: approved version-2 librarian source integrated into launcher assets;
  adaptive foreground scaled to 64% so more of the folder is visible
- Build: version 52 passes the documented JDK 21 `assembleDebug lintVitalRelease` gate
- Tests: no upstream test source sets found; lint vital and debug assembly pass
- Device install: version 52 is installed and running on the explicitly selected Fold7
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
- Persistence repair: derivative Android version code corrected from 1 and is now 52;
  version 1 retriggered inherited migrations that discarded saved SMB entries.
  A saved SMB entry now survives restart.
- Filename preference migration: version 41 converts the inherited saved Middle
  ellipsis default to End; the installed preference was verified as value `2`.
- Coexistence: Play Store Material Files remains installed under its upstream ID
- Push/publication: validated source checkpoint pushed to private Forgejo and
  the public GitHub fork `vincent71711/FM-Plus-Ultra`. No APK release was published.
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
- Capture cleanup: all known diagnostics were removed after analysis (17.87 GB
  from TrueNAS and 6.60 GB from KohlerRunner1); the reusable capture wrapper remains.
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
- SMB robustness: Android-native Conscrypt/OpenSSL providers are tried per
  primitive before portable Bouncy Castle fallback. Expected nested watcher
  interruptions during remote-folder exit no longer print misleading stacks.
- APK handoff: the debug APK and a minified `BuildConfig.DEBUG=false` release
  test build were copied to the Fold7 Downloads folder. The release test build
  is signed with the workstation's Android debug certificate only; it is not a
  permanent or publishable release artifact and was not installed.
- Version 52 UI: Home displays storage used/total plus local category size and
  file count, Remote displays its configured-connection count, and Access from
  network opens the existing FTP server. Progress tracks, selection fills, and
  the clipboard bar use neutral grays. Long-pressing a selected row now deselects
  it instead of opening it. The Home pencil/check control provides standard
  haptic feedback in both states.
- Next work: user final inspection and continued file-operation/UI review.
