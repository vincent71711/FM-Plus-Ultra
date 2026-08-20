# Changelog

This file records FM Plus Ultra changes separately from inherited
Material Files functionality. Upstream history remains available in Git and
upstream release notes.

## 0.1.0-beta.2 candidate — 2026-08-20

- Recover SMB directory browsing after an idle, backgrounded, or network-switched
  connection by discarding stale sessions and retrying one safe enumeration on
  a fresh connection. Active SMB sessions are also invalidated when Android's
  default network changes; writes are not automatically replayed.
- Make the persistent Home button return from remote browsing to the actual Home
  dashboard instead of exposing the intermediate Remote connections page.
- Reduce list-view file rows from 72dp to 64dp while preserving text sizes,
  metadata, icons, dividers, and 48dp action targets.
- Refresh Home storage/category statistics whenever an already-visible Home
  screen resumes, and add pull-to-refresh to the Home dashboard.
- Reveal a newly created file or folder after the refreshed list places it under
  the active view and sort order, including items sorted above the viewport.

## 0.1.0-beta.1 — 2026-08-16

### What changed in FM Plus Ultra

#### Much faster SMB transfers

- Reworked the security layer into a hybrid engine: Android's optimized native
  security code is used whenever the phone supports the required SMB operation,
  with the portable Java implementation retained as an automatic compatibility
  fallback.
- Replaced the tiny inherited copy blocks and mostly wait-for-each-piece transfer
  flow with a bounded pipeline of larger requests. It stays fast without allowing
  memory use or the number of outstanding requests to grow without control.
- Made downloads aware of the actual file size so small files and the end of large
  files do not launch unnecessary reads beyond the expected end.
- Improved the observed inherited upload experience from about 1.1 MB/s to a
  final 2 GiB checkpoint of 91.49 MB/s upload and 119.15 MB/s download in the
  Fold7/TrueNAS test environment. See the README for comparison caveats.

#### Transfers that are easy to follow

- Added an in-app transfer panel showing progress, current speed, transferred and
  remaining size, ETA, item count, and cancellation, while retaining the Android
  notification.
- Stopped SMB folder-watch events from repeatedly refreshing and flashing the
  complete file list during an active transfer.
- Corrected progress tracks and pending copy/move surfaces so completed and
  remaining work are visually distinct.

#### Faster everyday navigation

- Added a real Home dashboard with reorderable shortcuts, storage/category size
  summaries, a Remote connection count, and an Access from network shortcut to
  the existing FTP server.
- Added friendly recent-location shortcuts that remember the latest folder for
  each local or remote storage without filling the list with duplicates.
- Added native Back transitions from Home shortcuts and prevented stale rows from
  one folder flashing while another folder opens.
- Preserved multi-item copy/move selections within one source while preventing a
  batch from accidentally mixing files from unrelated storage sources.

#### Clearer visuals and foldable behavior

- Added structured row dividers, a pinned black toolbar, clickable breadcrumbs,
  clearer menus, and neutral selection surfaces with dark-ruby accents.
- Added an original librarian launcher icon and adjusted its scale for Samsung's
  launcher mask.
- Fixed rotated-cover alignment, removed an unnecessary permanent unfolded
  landscape sidebar, and repaired the related unfolded-landscape crash.
- Made Home shortcut editing direct and visible with tile dragging, a subtle
  jiggle, and haptic feedback on the edit and confirmation controls.

#### Reliability and privacy

- Added a guarded double-Back exit flow and fixed selection, long-press, saved-
  connection migration, theme-toggle startup, and nested SMB watcher issues.
- Removed Firebase Analytics, Crashlytics, Google Services integration, and
  automatic crash reporting without replacing them with other telemetry.
- Preserved Material Files' full Git history, GPLv3 license, author attribution,
  copyright notices, and third-party disclosures.

### Technical implementation notes

- Increased the generic cross-provider copy block from 8 KiB to 256 KiB; the
  first physical tests were 6.7x to 9.8x faster and round-trip byte-correct.
- Selects AndroidOpenSSL/Conscrypt per cryptographic primitive, with SMBJ's
  portable Bouncy Castle provider used when the native operation is unavailable.
- Uses bounded 256 KiB SMB windows (up to eight reads and four writes) and a
  file-size-aware final read boundary.
- Coalesces provider change-event bursts and suppresses only expected watcher
  cancellation noise during navigation teardown.
- Uses official Material Files commit
  `fc1250038496ebf4d4c139f62d16f0071f2c995a` as the immutable mod baseline and
  expands the unavailable abbreviated `dav4jvm` revision to the same commit's
  full SHA for reproducible builds.

### Inherited upstream baseline

- Material Files 1.7.4 application version and current post-release `master`.
- Local files, archives, root access, FTP, SFTP, SMB, WebDAV, themes, and NIO2
  provider architecture are inherited upstream features, not mod additions.
