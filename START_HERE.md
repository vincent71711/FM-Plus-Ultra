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

## Immediate next work

Immediate next steps:

1. Have Vincent visually review the launcher identity and grant only the
   permissions needed for the intended private test.
2. Verify local browsing and basic file actions with a unique synthetic fixture
   without touching unrelated user data.
3. Create a synthetic, disposable SMB benchmark fixture and test path without
   storing endpoints or credentials in Git.
4. Measure local-to-SMB and SMB-to-local correctness and throughput before
   changing the transfer engine.

See `CONTEXT/REVISION_QUEUE.md` for decisions and `INFRASTRUCTURE.md` for exact
commands and repository conventions.
