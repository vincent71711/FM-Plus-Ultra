# Infrastructure and repository conventions

## Repository

- Workspace: `/srv/file-manager-app`
- `origin`: `https://git.computersmarts.org/vincent/file-manager-app.git`
  (the local display may use an SSH rewrite)
- `upstream`: `https://github.com/zhanghai/MaterialFiles.git`
- Upstream branch: `master`
- Mod integration branch: `main`
- Working branches: `codex/<short-description>`
- Baseline commit: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Baseline tag: `mod-baseline/2026-08-16-material-files-fc12500`

No pushing, publishing, release tagging, or artifact upload is authorized.

## Toolchain and baseline validation

- JDK: Temurin 21
- Gradle wrapper: 9.3.1
- Android Gradle Plugin: 9.1.0
- Kotlin: 2.3.20
- compile SDK: 36; target SDK: 34; minimum SDK: 23
- NDK: 28.1.13356709
- Upstream application version: 1.7.4 (39)

Exact validated command:

```bash
export JAVA_HOME=/home/vincent/.cache/codex-jdks/temurin-21
export ANDROID_HOME=/home/vincent/Android/Sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
./gradlew assembleDebug lintVitalRelease --stacktrace --console=plain
```

Upstream's abbreviated `dav4jvm` JitPack SHA no longer resolves. The bootstrap
expands it to the full SHA of the exact same commit. The first successful gate
completed 94 tasks. There are no checked-in upstream test source sets.

Build output belongs under Gradle `build/` directories and must not be committed.

## Application identity, signing, and installation

Upstream currently builds `me.zhanghai.android.files` for debug and release.
That conflicts with the Play/F-Droid app and cannot be installed over a store
build signed by another key. Do not install until a unique derivative ID and
debug suffix are selected.

Signing properties, keystores, passwords, and backups remain outside Git. Do
not use upstream signing configuration for derivative distribution.

## Analytics and external services

Current upstream source compiles Firebase Analytics and Crashlytics and tracks
an upstream `google-services.json`. These are upstream baseline facts, not
approved derivative infrastructure. Remove them before derivative device tests.
Do not replace them with another analytics, crash, cloud, or server service
without explicit approval.

## Assets and fixtures

Preserve upstream notices. New branding and icons must be original or properly
licensed. Proprietary reference-app screenshots, recordings, icons, user file
names, endpoints, and server information must not enter Git.

SMB tests must use synthetic or redistributable files in an approved disposable
share and unique directory. Keep credentials and endpoints in ignored local
configuration or runtime entry only.

## Upstream integration

1. Start from a clean `main` and create `codex/upstream-<date>`.
2. Run `git fetch upstream --prune --tags`.
3. Review upstream commits, release notes, license changes, build changes, and
   security notes before integration.
4. Merge upstream `master` without rewriting history.
5. Resolve conflicts by preserving documented mod behavior and attribution.
6. Run the complete validation gate and relevant physical-device regression
   tests.
7. Update `docs/UPSTREAM_BASE.md`, `CHANGELOG.md`, `STATUS.md`, and handoff files.
8. Do not push until explicitly authorized.
