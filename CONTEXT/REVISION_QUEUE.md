# Revision queue

## Decisions completed

- **Application ID:** release ID `com.froslabs.filemanagerplusultra`; debug ID
  `com.froslabs.filemanagerplusultra.debug`.
- **Visible name and public source branding:** FM Plus Ultra. Vincent approved
  this name for the public GitHub source fork on 2026-08-16; do not imply
  affiliation with similarly named commercial applications.
- **Launcher art:** selected version-2 librarian mascot with glasses, high red
  ponytail, gold folder, and no headphones. The canonical source is
  `docs/branding/file-manager-plus-ultra-icon-source.png`. Its adaptive launcher
  foreground is scaled to 64% to expose more of the folder under Samsung's mask.
- **Attribution:** About screen prominently identifies the Material Files base,
  Hai Zhang, Vincent's modification, GPLv3, and the modification start date.
- **Telemetry:** Firebase Analytics, Crashlytics, and Google Services are
  removed and are not to be replaced.
- **Clipboard source isolation:** folders within one configured storage source
  may accumulate, while a different source replaces the existing batch. Copy and
  Move batches clear when Paste is initiated.
- **Private palette:** dark-ruby actions and generic file/folder accents with
  neutral white/gray broad surfaces; no pink tint across the navigation drawer.
- **Temporary release test artifact:** Vincent requested a non-debug APK in the
  Fold7 Downloads folder. Version 52 was built as the minified release variant and
  signed with the workstation's standard Android debug certificate solely so the
  private test artifact is installable. It is not the permanent release-signing
  decision and cannot be upgraded in place to an APK signed by a future release key.
- **Permanent release signing:** Vincent generated the permanent release key on
  2026-08-16. The public certificate SHA-256 is
  `b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`.
  Private material is ignored and outside Git; durable backup is still required.
- **First public binary:** GitHub prerelease `v0.1.0-beta.1`, version code 53,
  is approved with the signed APK, checksum, tagged corresponding source, GPLv3
  license, notices, and attribution.

## Decisions still required

- **Updates:** no automatic update channel. Decide later whether updates remain
  manual GitHub APK installs or gain an in-app update workflow.
- **Distribution:** public source and the first GitHub beta APK are approved.
  Store distribution, AAB publication, and non-beta release channels remain
  undecided.

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

- Add current folded, unfolded, Home, SMB, and transfer-progress screenshots to
  the public GitHub README during the next presentation/documentation phase.
- Optional longer transfer to characterize thermal behavior beyond the successful
  version 45 2 GiB baseline; not required for the next product work.
- Pause/resume support, if the underlying provider can make it reliable.
- Multi-pane foldable navigation beyond a stable responsive single-pane layout.
- Release automation, store listings, telemetry, servers, or cloud services.
