# PermConflictWatchdog

**Tells you when a second permissions plugin is installed next to LuckPerms - before your permissions start misbehaving.**

Running two permissions plugins at once does not throw an error. Both load, both attach permissions, and whichever one wins any given check is largely down to load order. The symptoms turn up much later and never point at the real cause: a group's prefix goes missing, an op-only command becomes public, an inheritance change has no effect.

PermConflictWatchdog turns that into one loud console warning at startup, plus an optional in-game message to your staff.

It answers [LuckPerms issue #3689](https://github.com/LuckPerms/LuckPerms/issues/3689), where the LuckPerms team tagged the idea `good first issue` and `help wanted`.

> **This plugin only looks and reports.** It never changes, disables or unregisters anything.

## What you get

**A console warning you cannot miss**

```
[WARN]: [PermConflictWatchdog] ================================================================
[WARN]: [PermConflictWatchdog] PERMISSION PLUGIN CONFLICT DETECTED
[WARN]: [PermConflictWatchdog]
[WARN]: [PermConflictWatchdog] More than one permissions plugin is installed on this server:
[WARN]: [PermConflictWatchdog]   - LuckPerms v5.5.71
[WARN]: [PermConflictWatchdog]   - GroupManager v2.9
[WARN]: [PermConflictWatchdog]   - PowerRanks (installed as MyRenamedPerms) v1.4
[WARN]: [PermConflictWatchdog]
[WARN]: [PermConflictWatchdog] Running several permissions plugins at once makes permission checks
[WARN]: [PermConflictWatchdog] unpredictable. Whichever plugin registers last usually wins, so groups,
[WARN]: [PermConflictWatchdog] prefixes and permissions can silently stop behaving the way you set them up.
[WARN]: [PermConflictWatchdog]
[WARN]: [PermConflictWatchdog] Fix: keep ONE permissions plugin, remove the others from your plugins
[WARN]: [PermConflictWatchdog] folder, and restart the server.
[WARN]: [PermConflictWatchdog] ================================================================
```

**An in-game heads-up for staff** - once per player per restart, for anyone with `permconflictwatchdog.alert` (default op). Fully customisable in MiniMessage, or switch it off entirely.

**Silence when everything is fine.** A server with one permissions plugin logs *nothing*. No startup banner, no ads, no "thanks for installing".

**Renamed jars and forks are still caught.** Detection matches on the plugin name *or* the main class, so a repackaged jar shows up as `PowerRanks (installed as MyRenamedPerms)`.

**An editable detection list.** The known-plugins list lives in `config.yml`, not in the jar. Add a plugin, run `/pcw reload`, done - no waiting on an update.

## Detects out of the box

LuckPerms · PermissionsEx · GroupManager · zPermissions · PowerRanks · UltraPermissions · bPermissions · PermissionsBukkit · Privileges

**Vault is deliberately excluded** - it is a permissions *bridge* meant to sit alongside LuckPerms, not a competing permissions plugin. Same for rank and GUI helpers that store their data in LuckPerms.

## Commands

`/pcw` (or `/permconflictwatchdog`), permission `permconflictwatchdog.admin`, default op.

| Command | Does |
|---|---|
| `/pcw status` | What was found, whether a conflict is active, plus a preview of the staff alert |
| `/pcw rescan` | Re-run the check now, e.g. after loading a plugin at runtime |
| `/pcw reload` | Re-read `config.yml` and rescan, no restart needed |

## Configuration

| Option | Default | Meaning |
|---|---|---|
| `alert-ops-on-join` | `true` | In-game alert for staff, once per player per restart |
| `require-luckperms` | `true` | Only warn when LuckPerms is one of the plugins found |
| `luckperms-plugin-name` | `LuckPerms` | Which plugin counts as LuckPerms, for forks |
| `debug` | `false` | Log the scan detail and confirm the all-clear |
| `scan-delay-ticks` | `20` | Wait after startup, so plugin loaders like PlugMan finish first |
| `ignored-plugins` | `[]` | Never report these |
| `messages.*` | see file | The alert text, MiniMessage, with `<plugins>` and `<count>` |

Every option is commented in `config.yml` itself.

## Requirements

- **Paper 26.2** or a Paper fork (Purpur, Pufferfish)
- **Java 25**
- No dependencies. LuckPerms is not required to install it.

## Install

Drop the jar in `plugins/`, restart. That's it - if your setup is clean you will never hear from it again.

---

Made by **Lucio73** · MIT licensed · Bug reports and plugins to add to the detection list are welcome.
