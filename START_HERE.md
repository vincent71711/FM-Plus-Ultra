# Start here

## Current status

The active repository is the full official Material Files history at upstream
commit `fc1250038496ebf4d4c139f62d16f0071f2c995a` on
`codex/private-derivative`. The local baseline tag is
`mod-baseline/2026-08-16-material-files-fc12500`.

Upstream's CI command initially failed because its abbreviated JitPack revision
for `dav4jvm` is unavailable. Expanding that coordinate to the exact full commit
SHA restored the build without changing dependency content. The official gate
then completed successfully: 94 tasks, no fatal lint findings.

Firebase Analytics, Crashlytics, their build plugins, runtime initializer, and
the upstream Google Services configuration have been removed. The private
working identity is File Manager Plus Ultra. Debug builds use
`com.froslabs.filemanagerplusultra.debug`; the reserved release ID is
`com.froslabs.filemanagerplusultra`. The selected version-2 librarian artwork is
now the reproducible launcher icon source.

The complete JDK 21 validation gate passes after these changes. The derivative
debug APK was installed on the explicitly selected Fold7 and cold-launched
successfully without a fatal exception. It coexists with the Play Store
Material Files 1.7.4 package. A live cover-to-inner-to-cover transition passed:
the same process and activity remained resumed at 1080x2520 and 1968x2184 with
no fatal exception. Synthetic rename and copy-then-delete actions were verified;
the move result still needs a controlled retest because both source and
destination remain. No changes have been pushed or published.

The installed UI checkpoint has a pinned black toolbar, gray clickable
breadcrumbs, divided file rows, an editable Home dashboard, a single Remote
entry, and up to five persistent recent storage sources in the drawer. Each
source appears once with its friendly name and latest relative folder;
top-level roots such as `/` and `/storage/emulated/0` are excluded. Home
shortcuts use child activities so Android provides the same Back transition as
Remote and each folder starts with a fresh list instead of stale rows.

The current uncommitted test build also moves dividers with row animations,
uses clicked-item identity to prevent two same-path drawer rows highlighting,
and truncates long filenames at the end. These need quick physical confirmation.

The first SMB transfer optimization is physically validated. Increasing the
generic copy block from 8 KiB to 256 KiB made upload 6.7x to 9.8x faster, and a
fresh round trip matched the source byte count and SHA-256.

The installed test build adds a bounded four-block asynchronous SMB upload
pipeline. A 256 MiB upload/download round trip matched the source byte count and
SHA-256. An app-timed upload completed in 12.074 seconds: 21.20 MiB/s
(22.23 MB/s). The debug build logs exact transfer start, finish, and elapsed time
as `FMPU.TransferTiming`.

The derivative Android version code is 41. Version 41 migrates an inherited
saved Middle filename ellipsis to End without clearing other preferences.
Do not reset it: version code 1 caused inherited legacy migrations to rebuild
the storage list and discard saved SMB entries after restart.

## Immediate next work

Immediate next steps:

1. Confirm the row-divider animation, end ellipsis, and single drawer highlight.
2. Plan and measure the next SMB optimization against the 21.20 MiB/s baseline.

See `CONTEXT/REVISION_QUEUE.md` for decisions and `INFRASTRUCTURE.md` for exact
commands and repository conventions.
