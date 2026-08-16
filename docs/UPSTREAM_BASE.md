# Upstream baseline and update procedure

## Current baseline

- Project: Material Files
- Repository: `https://github.com/zhanghai/MaterialFiles.git`
- Upstream branch: `master`
- Commit: `fc1250038496ebf4d4c139f62d16f0071f2c995a`
- Local tag: `mod-baseline/2026-08-16-material-files-fc12500`
- Recorded: 2026-08-16
- License: GNU GPL version 3 or later

The upstream CI command failed at this commit because JitPack no longer serves
the abbreviated `dav4jvm` revision. The mod's first build-only change expands
that coordinate to the exact commit's full SHA.

## Fetching and integrating future changes

```bash
git switch main
git status --short
git fetch upstream --prune --tags
git switch -c codex/upstream-YYYYMMDD
git log --oneline --decorate main..upstream/master
git merge --no-ff upstream/master
```

Before resolving conflicts, review upstream release notes, dependency and SDK
changes, license/notice changes, privacy changes, provider behavior, and signing
configuration. Never discard mod changes mechanically.

After integration, run the exact gate in `AGENTS.md`, then exercise local file
operations and synthetic SMB operations on the Fold7. Record the new upstream
commit here and in continuity files. Do not push or publish without explicit
authorization.
