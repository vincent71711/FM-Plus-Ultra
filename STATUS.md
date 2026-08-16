# Status

- Phase: validated first SMB transfer optimization
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
- Known UX defect: inherited `scroll|enterAlways` flags collapse the entire
  file-list toolbar during upward scrolling; top controls must be pinned
- Required navigation workflow: persistent Home plus an expandable list of
  previously opened local/remote folders, without losing pending copy/move state
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
- Next work: reduce notification churn, then build the in-app transfer model.
