# Changelog

This file records File Manager App changes separately from inherited Material
Files functionality. Upstream history remains available in Git and upstream
release notes.

## Unreleased — 0.1.0-dev

### Mod changes

- Rebased the project foundation on the full official Material Files history at
  commit `fc1250038496ebf4d4c139f62d16f0071f2c995a`.
- Expanded the unavailable abbreviated `dav4jvm` JitPack revision to the exact
  same commit's full SHA, restoring reproducible dependency resolution.
- Added project governance, continuity, validation, infrastructure, licensing,
  upstream-integration, and decision documentation.

### Inherited upstream baseline

- Material Files 1.7.4 application version and current post-release `master`.
- Local files, archives, root access, FTP, SFTP, SMB, WebDAV, themes, and NIO2
  provider architecture are inherited upstream features, not mod additions.
