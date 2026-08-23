# Changelog

All notable changes to PermConflictWatchdog are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-23

First release. Answers [LuckPerms issue #3689](https://github.com/LuckPerms/LuckPerms/issues/3689).

### Added

- Startup scan of every installed plugin against a list of known permissions
  plugins, run one second after the server finishes starting so plugin loaders
  such as PlugMan are included.
- Clear multi-line console warning naming every permissions plugin found, why
  the combination is a problem, and how to fix it.
- Optional in-game alert to staff on join, once per player per restart, shown to
  holders of `permconflictwatchdog.alert` (default op) and configurable in
  MiniMessage format.
- Silence on clean servers: a server with one permissions plugin logs nothing at
  all unless `debug` is on.
- `/permconflictwatchdog` (alias `/pcw`) with `status`, `rescan` and `reload`.
  `status` also previews the exact alert staff receive.
- Detection by plugin name **or** main class, so renamed jars and forks are still
  caught and reported as `PowerRanks (installed as MyRenamedPerms)`.
- Editable `known-permission-plugins` list in `config.yml`, shipping with
  LuckPerms, PermissionsEx, GroupManager, zPermissions, PowerRanks,
  UltraPermissions, bPermissions, PermissionsBukkit and Privileges.
- `ignored-plugins` list for false positives and knowingly mixed setups.
- `require-luckperms` toggle to warn about any two permissions plugins rather
  than only combinations involving LuckPerms.

[1.0.0]: https://github.com/Lucio73-collab/PermConflictWatchdog/releases/tag/v1.0.0
