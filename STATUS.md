# Status

- Phase: private derivative identity and device baseline
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
- Coexistence: Play Store Material Files remains installed under its upstream ID
- Push/publication: none
- Blockers before SMB baseline: user permission/visual review and an approved
  disposable synthetic share path
- Next work: user visual review and synthetic local file-action smoke tests,
  then a private synthetic SMB correctness and performance baseline
