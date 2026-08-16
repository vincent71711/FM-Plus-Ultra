# Revision queue

## Decisions completed for private development

- **Application ID:** release ID `com.froslabs.filemanagerplusultra`; debug ID
  `com.froslabs.filemanagerplusultra.debug`.
- **Visible name:** File Manager Plus Ultra for private use.
- **Launcher art:** selected version-2 librarian mascot with glasses, high red
  ponytail, gold folder, and no headphones. The canonical source is
  `docs/branding/file-manager-plus-ultra-icon-source.png`.
- **Attribution:** About screen prominently identifies the Material Files base,
  Hai Zhang, Vincent's modification, GPLv3, and the modification start date.
- **Telemetry:** Firebase Analytics, Crashlytics, and Google Services are
  removed and are not to be replaced.

## Decisions still required

- **Public branding:** the private working name is too close to an existing
  commercial application name to assume for public distribution. Choose a
  distinct name before sharing or publishing.
- **Signing:** debug keystore for development; private release key and backup
  process must be decided before any release build is distributed.
- **Updates:** no automatic update channel. Decide later whether updates are
  manual APK installs or a private repository.
- **Distribution:** private use only at present. Any future recipient triggers a
  GPLv3 source-delivery review.

## Remaining bootstrap work

- Complete user visual review and the remaining synthetic local move test for
  the installed separated debug build.
- Decide whether WebDAV remains in initial scope. It currently causes the
  `dav4jvm` build dependency but is not involved in SMB.
- Add synthetic automated tests; upstream currently has no checked-in test
  source sets.

## Proposed first implementation phase

- Completed pending commit: benchmark the inherited engine and replace its
  generic 8 KiB copy block with a physically validated 256 KiB block.
- Coalesce or suppress self-generated SMB change events during active jobs and
  refresh once at completion.
- Add a transfer state model supporting operations list, details, speed, ETA,
  remaining bytes, item count, cancel, completion, and failure.
- Keep the Android notification stable and substantially less frequent.
- Pin the file-list toolbar and breadcrumbs by removing the inherited
  `scroll|enterAlways` collapse behavior; list scrolling must not hide controls.
- Add a persistent Home control and expandable recent-location/history selector
  spanning local and remote folders, while preserving pending paste operations.
- Add structured row dividers and direct Copy/Move/Rename/Delete actions after
  transfer correctness is established.

## Deferred

- Pause/resume support, if the underlying provider can make it reliable.
- Multi-pane foldable navigation beyond a stable responsive single-pane layout.
- Release automation, store listings, telemetry, servers, or cloud services.
