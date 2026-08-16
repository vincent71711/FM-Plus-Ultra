# Status

- Phase: Home/navigation UI checkpoint complete; returning to SMB optimization
- Branch: `codex/private-derivative`
- Baseline: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Identity: File Manager Plus Ultra; debug package
  `com.froslabs.filemanagerplusultra.debug`
- Telemetry: Firebase/Crashlytics/Google Services removed
- Branding: approved version-2 librarian source integrated into launcher assets
- Build: successful with the documented JDK 21 CI gate after identity changes
- Tests: no upstream test source sets found; lint vital and debug assembly pass
- Device install: debug APK installed on the explicitly selected Fold7;
  successful cold launch, live process, and no fatal exception
- Fold lifecycle: cover (1080x2520) to inner (1968x2184) to cover passed while
  retaining the same resumed process/activity with no fatal exception
- Synthetic local actions: rename and copy-then-delete results verified; move
  remains unverified because the synthetic source and destination both remain
- Navigation/UI: installed Home dashboard has reorderable shortcuts, native
  Back transitions, one Remote entry, drawer recent subfolders (maximum five),
  pinned controls, readable sort options, and divided file rows.
- SMB transfer: increasing the generic copy buffer from 8 KiB to 256 KiB was
  physically validated at 6.7x to 9.8x faster upload with a byte-correct round
  trip.
- Persistence repair: derivative Android version code corrected from 1 to 40;
  version 1 retriggered inherited migrations that discarded saved SMB entries.
  A saved SMB entry now survives restart.
- Coexistence: Play Store Material Files remains installed under its upstream ID
- Push/publication: none
- Refresh stability: provider event bursts are coalesced; the installed build
  remained visually stable during an SMB upload.
- Next work: benchmark and optimize SMB beyond the validated 256 KiB buffer win.
