# Status

- Phase: SMB pipeline optimization and small UI fixes installed for physical validation
- Branch: `codex/private-derivative`
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Identity: File Manager Plus Ultra; debug package
  `com.froslabs.filemanagerplusultra.debug`
- Telemetry: Firebase/Crashlytics/Google Services removed
- Branding: approved version-2 librarian source integrated into launcher assets;
  adaptive foreground scaled to 82% so more of the folder is visible
- Build: successful with the documented JDK 21 CI gate after identity changes
- Tests: no upstream test source sets found; lint vital and debug assembly pass
- Device install: debug APK installed on the explicitly selected Fold7;
  successful cold launch, live process, and no fatal exception
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
- Persistence repair: derivative Android version code corrected from 1 and is now 41;
  version 1 retriggered inherited migrations that discarded saved SMB entries.
  A saved SMB entry now survives restart.
- Filename preference migration: version 41 converts the inherited saved Middle
  ellipsis default to End; the installed preference was verified as value `2`.
- Coexistence: Play Store Material Files remains installed under its upstream ID
- Push/publication: none
- Refresh stability: provider event bursts are coalesced; the installed build
  remained visually stable during an SMB upload.
- SMB pipeline validation: a 256 MiB upload/download round trip matched byte
  count and SHA-256. An app-timed upload completed in 12.074 seconds: 21.20 MiB/s
  (22.23 MB/s).
- Transfer diagnostics: the installed debug build now logs exact job start,
  finish, and elapsed time under `FMPU.TransferTiming`.
- Next work: validate the installed UI fixes and plan the next SMB optimization.
