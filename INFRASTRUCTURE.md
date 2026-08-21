# Infrastructure and repository conventions

## Repository

- Public repository: [vincent71711/FM-Plus-Ultra](https://github.com/vincent71711/FM-Plus-Ultra)
- Upstream repository: [zhanghai/MaterialFiles](https://github.com/zhanghai/MaterialFiles)
- Upstream branch: `master`
- Derivative integration/default branch: `main`
- Working branches: `codex/<short-description>`
- Baseline commit: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Baseline tag: `mod-baseline/2026-08-16-material-files-fc12500`

## Toolchain and baseline validation

- JDK: Temurin 21
- Gradle wrapper: 9.3.1
- Android Gradle Plugin: 9.1.0
- Kotlin: 2.3.20
- compile SDK: 36; target SDK: 34; minimum SDK: 23
- NDK: 28.1.13356709
- Upstream application version: 1.7.4 (39)

Validated release command, assuming JDK 21 and the Android SDK are already
installed:

```bash
export JAVA_HOME=/path/to/jdk-21
export ANDROID_HOME=/path/to/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
./gradlew assembleDebug lintVitalRelease assembleRelease \
  --stacktrace --console=plain
```

`assembleRelease` requires a local release signing configuration. Contributors
who do not possess the project's private release key should run
`assembleDebug lintVitalRelease` instead.

Upstream's abbreviated `dav4jvm` JitPack SHA no longer resolves. The bootstrap
expands it to the full SHA of the exact same commit. The first successful gate
completed 94 tasks. There are no checked-in upstream test source sets.

Build output belongs under Gradle `build/` directories and must not be committed.

## Application identity, signing, and installation

The derivative identity is:

- Visible and public source-fork name: `FM Plus Ultra`
- Debug ID: `com.froslabs.filemanagerplusultra.debug`
- Release ID: `com.froslabs.filemanagerplusultra`
- Mod version: `0.1.0-beta.3`; debug APK version: `0.1.0-beta.3-debug` (55). Android
  version codes begin at 40, one above the Material Files baseline (39), and
  must increase monotonically. Do not reset them independently of the inherited
  preference-migration thresholds.
- Upstream code namespace: `me.zhanghai.android.files` (retained intentionally)

The separate IDs allow release and development builds to coexist with each
other and with the Play/F-Droid Material Files package. Project materials must
not imply affiliation with similarly named commercial products.

The debug APK and permanently signed `0.1.0-beta.1` release APK were installed
and cold-launched successfully on a physical Galaxy Z Fold7. Device-specific
validation must select an explicit ADB serial at runtime; serials must never be
written into repository files.

Signing properties, keystores, passwords, and backups remain outside Git. Do
not use upstream signing configuration for derivative distribution.

The permanent release key is stored outside Git. Its public certificate SHA-256 is
`b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`.
Ignored local signing properties and the private keystore must never be
committed. Version `0.1.0-beta.1` (53) is the first public GitHub prerelease;
`0.1.0-beta.3` (55) is the current release version.

## Analytics and external services

Upstream compiled Firebase Analytics and Crashlytics and tracked an upstream
`google-services.json`. Commit `48a19f19` removed those SDKs, build plugins,
configuration, runtime initialization, and telemetry policy language from the
derivative. Dependency inspection reports no Firebase in
`debugRuntimeClasspath`. Do not replace them with another analytics, crash,
cloud, or server service without explicit approval.

## Assets and fixtures

Preserve upstream notices. New branding and icons must be original or properly
licensed. Proprietary reference-app screenshots, recordings, icons, user file
names, endpoints, and server information must not enter Git.

The selected launcher source is
[the FM Plus Ultra librarian artwork](docs/branding/file-manager-plus-ultra-icon-source.png).
Regenerate density-specific launcher PNGs with:

```bash
./scripts/generate_launcher_icons.py
```

The script requires Pillow and removes only the near-black canvas border before
resizing. Do not use rejected generative variants as launcher sources.

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
7. Update [the upstream baseline](docs/UPSTREAM_BASE.md),
   [the changelog](CHANGELOG.md), [project status](STATUS.md), and handoff files.
8. Do not push until explicitly authorized.
