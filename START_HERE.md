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

The file-list app bar currently uses inherited `scroll|enterAlways` flags, which
allow upward list scrolling to hide all top controls. The required derivative
behavior is a pinned toolbar and breadcrumb area.

The first SMB transfer optimization is physically validated. Increasing the
generic copy block from 8 KiB to 256 KiB made upload 6.7x to 9.8x faster, and a
fresh round trip matched the source byte count and SHA-256.

The derivative Android version code is 40, one above the upstream baseline.
Do not reset it: version code 1 caused inherited legacy migrations to rebuild
the storage list and discard saved SMB entries after restart.

## Immediate next work

Immediate next steps:

1. Commit the SMB buffer optimization and Android version-code repair.
2. Coalesce SMB size/last-write event bursts so active uploads do not trigger a
   visible complete directory reload every second; validate separately.
3. Reduce and stabilize notification updates without coupling notification
   cadence to the future in-app transfer-state model.
4. Retest the remaining synthetic local move case, then pin the toolbar and
   begin the reviewed transfer-panel work.

See `CONTEXT/REVISION_QUEUE.md` for decisions and `INFRASTRUCTURE.md` for exact
commands and repository conventions.
