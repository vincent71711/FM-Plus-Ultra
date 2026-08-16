# Repository operating rules

These instructions apply throughout `/srv/file-manager-app`.

## Product and upstream baseline

- This is **File Manager Plus Ultra**, a private-use, history-preserving GPLv3 derivative of
  [Material Files](https://github.com/zhanghai/MaterialFiles).
- The immutable upstream baseline is commit
  `fc1250038496ebf4d4c139f62d16f0071f2c995a`, tagged locally as
  `mod-baseline/2026-08-16-material-files-fc12500`.
- Upstream uses `master`; this repository's integration branch is `main` and
  work must use `codex/<short-description>` branches.
- Preserve `LICENSE`, copyright headers, attribution, Git history, and
  third-party notices. Mark material modifications and relevant dates.
- The private working identity is `File Manager Plus Ultra`; release ID is
  `com.froslabs.filemanagerplusultra` and development builds use the `.debug`
  suffix. The upstream Kotlin namespace remains unchanged intentionally.
- The working name is not approved for public distribution because it is close
  to an existing commercial product name. Revisit public branding first.
- The primary physical target is a Samsung Galaxy Z Fold7 connected through
  wireless ADB. Select its explicit current serial; never assume the first ADB
  device.

## Product requirements

- Preserve local browsing and obvious copy, move, rename, delete, create, and
  cross-provider workflows.
- An SMB connection without a share path must enumerate accessible shares.
- SMB transfer correctness and competitive throughput are baseline concerns.
- File lists must remain visually stable during transfers; automatic provider
  events must not trigger visible full-list refresh loops.
- The default list presentation must have strong row structure and dividers,
  clear metadata columns, familiar folder/file visuals, and direct selection
  actions. Use original assets; do not copy proprietary application assets.
- Transfers require an in-app operations overview, an expandable detailed view,
  and a stable Android notification.

## Authorization and safety

- Never push, force-push, publish, release, upload an APK/AAB, or contact
  external parties unless Vincent explicitly requests it.
- Do not change the recorded private branding/application IDs, or choose public
  branding, signing, an update channel, or distribution without recording the
  decision in `CONTEXT/REVISION_QUEUE.md`.
- Do not add analytics, tracking, servers, cloud infrastructure, or automatic
  uploads. Firebase was removed from the derivative and must not be restored.
- Never clear app data, uninstall unrelated apps, change unrelated phone
  settings, access unrelated user files, root the phone, or unlock its
  bootloader.
- Use only synthetic or explicitly redistributable fixtures. Destructive remote
  tests must use an approved disposable share and a unique test path.
- Preserve unrelated user changes in a dirty worktree.

## Git practice

- `origin` is Vincent's Forgejo repository and `upstream` is Material Files.
- Keep commits focused and leave the project buildable.
- Never rewrite or force-push shared history.
- Integrate future upstream changes on a dedicated branch using
  `docs/UPSTREAM_BASE.md`.

## Secrets and generated data

Never commit private endpoints, credentials, device serials, signing keys,
passwords, local SDK configuration, IDE state, generated APK/AAB files, raw
diagnostics, benchmark fixtures, recordings, screenshots containing user data,
or user files. Sanitized benchmark summaries require review before commit.

## Validation

The upstream CI gate, validated locally with JDK 21 after expanding the pinned
`dav4jvm` coordinate to its full SHA, is:

```bash
export JAVA_HOME=/home/vincent/.cache/codex-jdks/temurin-21
export ANDROID_HOME=/home/vincent/Android/Sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
./gradlew assembleDebug lintVitalRelease --stacktrace --console=plain
```

The untouched upstream coordinate used an unavailable abbreviated JitPack SHA;
the full SHA identifies the same commit and is the minimal reproducibility fix.
No upstream test source sets are currently checked in. Run the complete gate for
source, manifest, resource, Gradle, dependency, or release changes. Run relevant
physical-device tests for UI, storage, permissions, providers, lifecycle,
folding, or transfer changes. Documentation-only changes require at least
`git diff --check`.

Before handoff, update `STATUS.md`, `START_HERE.md`, and
`CONTEXT/NEW_THREAD_HANDOFF.md`, verify `git status`, and state whether anything
was pushed, installed, or published.
