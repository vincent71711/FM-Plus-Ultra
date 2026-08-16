# Start here

## What this project is

FM Plus Ultra is a GPLv3 Android file manager derived from
[Material Files](https://github.com/zhanghai/MaterialFiles). It preserves the
upstream Git history, license, attribution, and provider architecture while
adding faster SMB transfers, clearer transfer monitoring, more familiar file
navigation, structured visuals, and foldable-focused fixes.

The immutable Material Files baseline is commit
`fc1250038496ebf4d4c139f62d16f0071f2c995a`. The public derivative lives at
[vincent71711/FM-Plus-Ultra](https://github.com/vincent71711/FM-Plus-Ultra).

## Current release

The first public build is
[FM Plus Ultra 0.1.0 Beta 1](https://github.com/vincent71711/FM-Plus-Ultra/releases/tag/v0.1.0-beta.1):

- Version name: `0.1.0-beta.1`
- Android version code: `53`
- Release application ID: `com.froslabs.filemanagerplusultra`
- Debug application ID: `com.froslabs.filemanagerplusultra.debug`
- Minimum Android version: Android 6.0 / API 23
- Target Android version: API 34
- Release certificate SHA-256:
  `b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`

The signed release passes debug assembly, release assembly, and release lint.
It was installed alongside the debug build on a Galaxy Z Fold7 and passed a
cold-launch crash/ANR check. The release is minified, has debug logging compiled
out, and uses a permanent signing identity stored outside Git.

## What is already complete

- **Hybrid SMB security acceleration:** Android's optimized native security
  implementation is selected for supported SMB signing and encryption work,
  with the portable Java provider retained as an automatic per-operation
  compatibility fallback.
- **Bounded SMB transfer pipeline:** larger 256 KiB requests and controlled
  read/write windows improve throughput without unbounded memory or request use.
- **File-size-aware downloads:** the read pipeline avoids unnecessary requests
  past the expected end of smaller files.
- **Transfer monitoring:** an in-app panel shows progress, speed, remaining size,
  ETA, item count, and cancellation while retaining the Android notification.
- **Stable browsing during transfers:** provider events are coalesced so active
  SMB transfers do not repeatedly flash or redraw the entire file list.
- **Home and recent navigation:** the Home dashboard includes reorderable local
  categories, storage summaries, Remote and FTP entry points, and recent
  locations grouped by storage source.
- **Clear file operations:** copy, move, rename, delete, selection, and pending
  paste state remain visible and usable across navigation.
- **Structured interface:** pinned controls, clickable breadcrumbs, row dividers,
  neutral selection surfaces, dark-ruby accents, and clearer menu contrast.
- **Foldable behavior:** folded, unfolded, and rotated layouts no longer hide or
  misalign the primary navigation controls, and unfolded landscape uses the
  temporary drawer rather than a permanent sidebar.
- **Reliability fixes:** saved storage migration, Home transitions, stale-row
  flashes, selection behavior, theme startup, exit confirmation, and expected
  SMB watcher cancellation have been corrected.
- **Privacy:** Firebase Analytics, Crashlytics, Google Services integration, and
  automatic crash reporting were removed without adding replacement telemetry.

The complete human-readable list is in the
[FM Plus Ultra changelog](CHANGELOG.md).

## SMB performance summary

The inherited cross-provider upload path was initially observed at about
**1.1 MB/s**. After the hybrid Android-native security engine and bounded SMB
pipeline were completed, the repeatable 2 GiB checkpoint reached
**91.49 MB/s upload** and **119.15 MB/s download** in the development test
environment.

The earliest measurement was not the same controlled test as the later 2 GiB
runs, so it is presented as the real starting user experience rather than a
laboratory-perfect comparison. The controlled portable-Java checkpoint measured
28.81 MB/s upload and 34.22 MB/s download; the final hybrid result was about
3.2x and 3.5x faster respectively.

See the explanation and comparison graph in
[Why SMB transfers are much faster](README.md#why-smb-transfers-are-much-faster).

## Next phase

The original immediate implementation and validation work is complete. The next
phase is presentation and beta feedback:

## Project references

- [README](README.md)
- [Changelog](CHANGELOG.md)
- [Infrastructure and release conventions](INFRASTRUCTURE.md)
- [Upstream baseline and update procedure](docs/UPSTREAM_BASE.md)
- [Deferred decisions and revision queue](CONTEXT/REVISION_QUEUE.md)
- [GPLv3 license](LICENSE)
