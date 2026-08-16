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

The derivative identity is:

- Visible private-use name: `FM Plus Ultra`
- Debug ID: `com.froslabs.filemanagerplusultra.debug`
- Reserved release ID: `com.froslabs.filemanagerplusultra`
- Mod version: `0.1.0-dev`; debug APK version: `0.1.0-dev-debug` (52). Android
  version codes begin at 40, one above the Material Files baseline (39), and
  must increase monotonically. Do not reset them independently of the inherited
  preference-migration thresholds.
- Upstream code namespace: `me.zhanghai.android.files` (retained intentionally)

The separate IDs coexist with the Play/F-Droid Material Files package. The
private working name must be reconsidered before public distribution because it
is close to an existing commercial product name.

The debug APK was installed and cold-launched successfully on the explicitly
selected Fold7 on 2026-08-16. Always resolve and pass the current serial with
`adb -s`; never write the wireless-ADB serial into repository files.

Signing properties, keystores, passwords, and backups remain outside Git. Do
not use upstream signing configuration for derivative distribution.

For private inspection on 2026-08-16, a version-52 minified release variant was
temporarily signed with the workstation's standard Android debug certificate and
copied to the Fold7 Downloads folder without installation. This is not release
signing: a future permanent key will require uninstalling that test-signed release
package before installation. No signing property or keystore was added to Git.

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
`docs/branding/file-manager-plus-ultra-icon-source.png` (the approved version-2
librarian concept). Regenerate density-specific launcher PNGs with:

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
7. Update `docs/UPSTREAM_BASE.md`, `CHANGELOG.md`, `STATUS.md`, and handoff files.
8. Do not push until explicitly authorized.
