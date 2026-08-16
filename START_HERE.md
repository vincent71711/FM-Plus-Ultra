# Start here

## Current status

The active repository is the full official Material Files history at upstream
commit `fc1250038496ebf4d4c139f62d16f0071f2c995a` on
`codex/private-derivative`. The local baseline tag is
`mod-baseline/2026-08-16-material-files-fc12500`.

Upstream's CI command initially failed because its abbreviated JitPack revision
for `dav4jvm` is unavailable. Expanding that coordinate to the exact full commit
SHA restored the build without changing dependency content. The official gate
then completed successfully: 94 tasks, no fatal lint findings.

Firebase Analytics, Crashlytics, their build plugins, runtime initializer, and
the upstream Google Services configuration have been removed. The private
working identity is FM Plus Ultra. Debug builds use
`com.froslabs.filemanagerplusultra.debug`; the reserved release ID is
`com.froslabs.filemanagerplusultra`. The selected version-2 librarian artwork is
now the reproducible launcher icon source.

The complete JDK 21 validation gate passes after these changes. Version 52
debug and the permanently signed `0.1.0-beta.1` release (version code 53) are
installed on the explicitly selected Fold7. The release cold-launched without
a fatal exception or ANR and coexists with the Play
Store Material Files 1.7.4 package. A live cover-to-inner-to-cover transition passed:
the same process and activity remained resumed at 1080x2520 and 1968x2184 with
no fatal exception. Synthetic rename and copy-then-delete actions were verified;
the move result still needs a controlled retest because both source and
destination remain. The validated source checkpoint is pushed to private
Forgejo and the public GitHub fork `vincent71711/FM-Plus-Ultra`. The first public
APK is published as GitHub prerelease `v0.1.0-beta.1`.

The installed UI checkpoint has a pinned black toolbar, gray clickable
breadcrumbs, divided file rows, an editable Home dashboard, a single Remote
entry, and up to five persistent recent storage sources in the drawer. Each
source appears once with its friendly name and latest relative folder;
top-level roots such as `/` and `/storage/emulated/0` are excluded. Home
shortcuts use child activities so Android provides the same Back transition as
Remote and each folder starts with a fresh list instead of stale rows.

The installed build also moves dividers with row animations,
uses clicked-item identity to prevent two same-path drawer rows highlighting,
and truncates long filenames at the end. These need quick physical confirmation.

The first SMB transfer optimization is physically validated. Increasing the
generic copy block from 8 KiB to 256 KiB made upload 6.7x to 9.8x faster, and a
fresh round trip matched the source byte count and SHA-256.

Full TrueNAS-side captures found that File Manager Plus used 256 KiB SMB
requests with a shallow pipeline: at most two outstanding writes and normally
one or two reads, briefly reaching five. Its captured 4 GiB upload held about
39 MB/s. Our version 41 upload used 512 KiB x12 and fell from 36.8 to 26.4 MB/s
by quarter even though SMB replies remained near 8 ms, credits were plentiful,
and TCP showed no window stalls. Its 4 MiB x2 download remained near 32.8 MB/s
after ramp-up with no retransmissions during the active transfer.

Version 42's direct 256 KiB profile confirmed that request depth was not the
primary limit: a 2 GiB run measured 28.81 MB/s upload and 34.22 MB/s download.
Both small and multi-megabyte reads converging near 33 MB/s identified SMB 3.1.1
packet signing as the per-byte CPU ceiling. The Fold7 exposes native AES-CMAC,
HMAC, RC4, and AES-GCM through AndroidOpenSSL, while SMBJ defaults to a pure-Java
Bouncy Castle implementation.

Version 44 established that AndroidOpenSSL removes the crypto ceiling. It uses
AndroidOpenSSL when a primitive is available and
automatically falls back to SMBJ's provider for MD4 and other legacy cases. It
combines this with bounded async transport, 256 KiB x5 reads, and 256 KiB x2
writes. A physical 2 GiB round trip completed at 52.39 MB/s upload and
89.42 MB/s download.

Version 45 raised the still-bounded windows to 256 KiB x8 reads
(2 MiB) and 256 KiB x4 writes (1 MiB), and resolves the native provider once.
Its 2 GiB physical run completed at 84.40 MB/s upload and 110.21 MB/s download;
late-run samples held roughly 90-98 and 125-129 MB/s respectively. The complete
JDK 21 gate passes and authentication remained compatible.

Installed version 46 makes that read pipeline file-size-aware. Large files still
receive the full eight-request window, while tiny files and final blocks no longer
launch a full set of speculative requests beyond EOF; one EOF probe preserves
correct behavior if a file grows after opening. A 25-file, 313,522,904-byte
audiobook download moved the post-conflict data in about 4.87 seconds (about
64.4 MB/s). Its raw 26.26 MB/s job average is not representative because the
timer included roughly seven seconds before actual file reads began. The upload
control was 72.04 MB/s versus version 45's 71.34 MB/s. Version 47 retains this
path. Version 47 shortened the visible app name to FM Plus Ultra and its latest
2 GiB physical round trip reached 91.49 MB/s upload and 119.15 MB/s download.

Installed version 48 applies dark-ruby accents to generic folders and controls
while keeping broad surfaces neutral white/gray. The drawer is explicitly white;
its Material elevation color overlay was removed after physical pixels proved it
was tinting white to pink. Home edit mode now uses a subtle tile jiggle, removes
the misleading inert grabber, and begins drag from the touched tile. The adaptive
launcher foreground is scaled to 64%.

Version 48 also fixes a persistent crash caused by a Material 3-only breadcrumb
attribute when Material 3 was disabled. The formerly failing custom-color state
and the restored Material 3/default state both launch without a fatal exception.
SMB cryptography now explicitly tries recognized Android native providers per
primitive before portable fallback, and expected nested directory-watch
interruptions are suppressed without hiding real watcher failures.
The launcher activity also requires a second Back within 2.5 seconds to exit;
the first gesture shows a short prompt while nested navigation remains unchanged.

The first public APK is `0.1.0-beta.1` (53), a minified release with diagnostic
logging compiled out and a permanent release signature. Its certificate SHA-256
is `b67ccd0f0e90510cc631058644dcb653fb47eaf0636d484fb2db8e2ff87cc5d7`.
The private signing material remains outside Git and needs durable offline backup.

All known packet-capture artifacts were removed after analysis: 17.87 GB from
TrueNAS and 6.60 GB from KohlerRunner1. The reusable TrueNAS capture wrapper was
retained; no fixture or unrelated file was touched.

Multiple selections from one configured storage source accumulate across folders,
while selecting from a different source replaces the old batch. Copy and Move
batches clear when Paste is initiated. Source isolation was physically validated
across two synthetic Internal storage folders and then against the test SMB server.

Version 52 adds File Manager Plus-style Home summaries (storage used/total and
local category size/file count), a Remote connection count, and an Access from
network shortcut to the existing FTP server. It also makes selection and
clipboard surfaces neutral gray, fixes selected-row long-press to deselect,
ties the double-Back exit guard to the actual Home screen, and adds haptic
feedback to the Home edit pencil/check control. The exit-confirmation toast now
cancels on confirmed exit or activity destruction instead of lingering over the
launcher; this was physically confirmed on the Fold7.

The derivative Android version code is 53. Version 41 migrated an inherited
saved Middle filename ellipsis to End without clearing other preferences.
Do not reset it: version code 1 caused inherited legacy migrations to rebuild
the storage list and discard saved SMB entries after restart.

## Immediate next work

Immediate next steps:

1. User-check the Home summaries, edit haptics, and deselection.
2. Continue file-operation correctness and UI review; use a longer transfer only
   if sustained thermal behavior needs another check.

See `CONTEXT/REVISION_QUEUE.md` for decisions and `INFRASTRUCTURE.md` for exact
commands and repository conventions.
