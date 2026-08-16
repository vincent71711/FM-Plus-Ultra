# Changelog

This file records FM Plus Ultra changes separately from inherited
Material Files functionality. Upstream history remains available in Git and
upstream release notes.

## Unreleased — 0.1.0-dev

### Mod changes

- Added Home storage and category summaries, a configured Remote count, and an
  Access from network shortcut to the existing FTP server.
- Made transfer tracks, selected rows, and the pending copy/move bar use clear
  neutral fills instead of ambiguous pink or fully filled progress coloring.
- Fixed long-press on an already selected row so it deselects rather than
  opening the item, and made the Home exit guard follow the displayed screen
  across alternate Android launch and restore paths.
- Added standard haptic feedback to both the pencil and confirmation states of
  Home shortcut editing.
- Replaced inherited blue accents with dark ruby for generic folders and active
  controls while retaining neutral white/gray surfaces, including an exact-white
  drawer and neutral popup menus unaffected by Material elevation tint.
- Reworked Home shortcut editing: removed the nonfunctional drag-handle icon,
  added a subtle edit-mode jiggle, and starts drag directly from the tile.
- Added a top-level double-Back exit guard with a short prompt while preserving
  normal single-Back behavior for nested navigation and transient UI.
- Fixed the startup crash loop triggered by disabling Material 3 with a custom
  color by removing a Material 3-only attribute from the shared breadcrumb layout.
- Made SMB crypto provider selection explicitly Android-native-first per
  primitive with portable fallback, and suppressed expected nested SMB watcher
  interruption noise during navigation teardown.
- Shortened the visible private-use name to FM Plus Ultra and reduced the
  adaptive launcher foreground to 64% so more artwork survives Samsung's mask.
- Accelerated SMB transfers with Android-native cryptography where available,
  bounded asynchronous request pipelines, and a file-size-aware read boundary
  that avoids launching a full speculative pipeline beyond each file's EOF.
- Added a persistent Home dashboard with reorderable shortcuts, a single Remote
  connection entry, saved recent subfolders in the drawer, and activity-backed
  folder navigation with native Android Back transitions.
- Added structured file-row dividers, a pinned black toolbar/gray breadcrumb
  treatment, and corrected sort-menu contrast.
- Prevented stale rows from the previous Home shortcut from flashing while a
  different folder opens.
- Corrected the derivative Android version code to preserve saved storage
  connections across app updates and restarts.
- Coalesced provider change-event bursts to keep file lists stable during
  transfers.
- Rebased the project foundation on the full official Material Files history at
  commit `fc1250038496ebf4d4c139f62d16f0071f2c995a`.
- Expanded the unavailable abbreviated `dav4jvm` JitPack revision to the exact
  same commit's full SHA, restoring reproducible dependency resolution.
- Added project governance, continuity, validation, infrastructure, licensing,
  upstream-integration, and decision documentation.
- Removed Firebase Analytics, Crashlytics, Google Services build integration,
  the upstream service configuration, and automatic crash initialization.
- Established the private-use visible name FM Plus Ultra, debug application
  ID `com.froslabs.filemanagerplusultra.debug`, reserved release ID
  `com.froslabs.filemanagerplusultra`, and derivative version 0.1.0-dev.
- Added prominent modified-version attribution and retained upstream author and
  license access in the About screen.
- Integrated the selected original librarian mascot artwork as reproducible
  legacy, adaptive, and themed launcher resources.

### Inherited upstream baseline

- Material Files 1.7.4 application version and current post-release `master`.
- Local files, archives, root access, FTP, SFTP, SMB, WebDAV, themes, and NIO2
  provider architecture are inherited upstream features, not mod additions.
