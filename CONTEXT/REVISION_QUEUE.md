# Revision queue

## Decisions required before derivative installation

- **Application ID:** choose a unique ID that coexists with
  `me.zhanghai.android.files`. Recommended working direction:
  `org.froslabs.filemanager` with a `.debug` suffix for debug builds.
- **Visible name and branding:** working name is File Manager App. Final name,
  icon, colors, and attribution-screen wording remain undecided.
- **Signing:** debug keystore for development; private release key and backup
  process must be decided before any release build is distributed.
- **Updates:** no automatic update channel. Decide later whether updates are
  manual APK installs or a private repository.
- **Distribution:** private use only at present. Any future recipient triggers a
  GPLv3 source-delivery review.

## Required bootstrap work

- Remove Firebase Analytics, Crashlytics, Google Services build plugins, and the
  tracked upstream service configuration before installing our derivative.
- Decide whether WebDAV remains in initial scope. It currently causes the
  `dav4jvm` build dependency but is not involved in SMB.
- Add synthetic automated tests; upstream currently has no checked-in test
  source sets.

## Proposed first implementation phase

- Measure SMB upload/download correctness and speed on the unmodified engine.
- Replace the generic 8 KiB cross-provider copy path with negotiated, buffered,
  benchmarked transfer I/O.
- Coalesce or suppress self-generated SMB change events during active jobs and
  refresh once at completion.
- Add a transfer state model supporting operations list, details, speed, ETA,
  remaining bytes, item count, cancel, completion, and failure.
- Keep the Android notification stable and substantially less frequent.
- Add structured row dividers and direct Copy/Move/Rename/Delete actions after
  transfer correctness is established.

## Deferred

- Pause/resume support, if the underlying provider can make it reliable.
- Multi-pane foldable navigation beyond a stable responsive single-pane layout.
- Release automation, store listings, telemetry, servers, or cloud services.
