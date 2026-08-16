# Start here

## Current status

The active repository is the full official Material Files history at upstream
commit `fc1250038496ebf4d4c139f62d16f0071f2c995a` on
`codex/project-bootstrap`. The local baseline tag is
`mod-baseline/2026-08-16-material-files-fc12500`.

Upstream's CI command initially failed because its abbreviated JitPack revision
for `dav4jvm` is unavailable. Expanding that coordinate to the exact full commit
SHA restored the build without changing dependency content. The official gate
then completed successfully: 94 tasks, no fatal lint findings.

No project changes have been pushed or published, and the derivative APK has
not been installed. The Play Store Material Files 1.7.4 app remains the user-test
reference on the Fold7.

## Immediate next work

Before SMB implementation or device installation:

1. Choose a development/release application ID that can coexist with upstream.
2. Remove Firebase Analytics, Crashlytics, Google Services configuration, and
   associated runtime/build plugins from the derivative.
3. Create a synthetic, disposable SMB benchmark fixture and test path without
   storing endpoints or credentials in Git.
4. Re-run the baseline gate and install the separated debug build.
5. Measure local-to-SMB and SMB-to-local correctness and throughput before
   changing the transfer engine.

See `CONTEXT/REVISION_QUEUE.md` for decisions and `INFRASTRUCTURE.md` for exact
commands and repository conventions.
