# PermConflictWatchdog

A small Paper plugin that tells you when a second permissions plugin is installed
next to LuckPerms, instead of leaving you to work it out from permissions that
quietly stop behaving the way you set them up.

Running two permissions plugins at once does not throw an error. Both load, both
attach permissions, and whichever one wins a given check is largely down to load
order. The symptoms show up much later: a group's prefix goes missing, an op-only
command becomes public, an inheritance change has no effect. This plugin turns
that into one loud console warning at startup, and an optional in-game message to
your staff.

It answers [LuckPerms issue #3689](https://github.com/LuckPerms/LuckPerms/issues/3689),
where the LuckPerms team tagged the idea `good first issue` and `help wanted`.

PermConflictWatchdog only looks and reports. It never changes, disables or
unregisters anything.

## Requirements

| | |
|---|---|
| Server | Paper 26.2 (or a Paper fork - Purpur, Pufferfish) |
| Java | 25 |
| Dependencies | none - LuckPerms is not required to install it |

## Install

1. Download `PermConflictWatchdog-1.0.0.jar`.
2. Drop it in your server's `plugins/` folder.
3. Restart the server.

On a server with one permissions plugin you will see nothing at all - that is the
intended behaviour. On a server with two or more, the warning appears in the
console a second after startup finishes.

## What it looks like

```
[14:42:48 WARN]: [PermConflictWatchdog] ================================================================
[14:42:48 WARN]: [PermConflictWatchdog] PERMISSION PLUGIN CONFLICT DETECTED
[14:42:48 WARN]: [PermConflictWatchdog]
[14:42:48 WARN]: [PermConflictWatchdog] More than one permissions plugin is installed on this server:
[14:42:48 WARN]: [PermConflictWatchdog]   - LuckPerms v5.5.71
[14:42:48 WARN]: [PermConflictWatchdog]   - GroupManager v2.9
[14:42:48 WARN]: [PermConflictWatchdog]   - PowerRanks (installed as MyRenamedPerms) v1.4
[14:42:48 WARN]: [PermConflictWatchdog]
[14:42:48 WARN]: [PermConflictWatchdog] Running several permissions plugins at once makes permission checks
[14:42:48 WARN]: [PermConflictWatchdog] unpredictable. Whichever plugin registers last usually wins, so groups,
[14:42:48 WARN]: [PermConflictWatchdog] prefixes and permissions can silently stop behaving the way you set them up.
[14:42:48 WARN]: [PermConflictWatchdog]
[14:42:48 WARN]: [PermConflictWatchdog] Fix: keep ONE permissions plugin, remove the others from your plugins
[14:42:48 WARN]: [PermConflictWatchdog] folder, and restart the server.
[14:42:48 WARN]: [PermConflictWatchdog] ================================================================
```

Staff who join while a conflict is active also get a two-line chat message, once
per player per restart:

> **[!] Permission plugin conflict:** LuckPerms v5.5.71, GroupManager v2.9
> Keep only one permissions plugin installed. See the server console for details.

## Command

`/permconflictwatchdog` (alias `/pcw`), permission `permconflictwatchdog.admin`, default op.

| Subcommand | Does |
|---|---|
| `/pcw status` | Lists the permissions plugins found and whether a conflict is active. Also previews the exact message staff see on join. Default when no subcommand is given. |
| `/pcw rescan` | Re-runs the sweep now - useful after loading or unloading a plugin at runtime. |
| `/pcw reload` | Re-reads `config.yml`, then rescans. No restart needed after editing the config. |

## Permissions

| Node | Default | Grants |
|---|---|---|
| `permconflictwatchdog.admin` | op | Use of `/permconflictwatchdog`. |
| `permconflictwatchdog.alert` | op | Receives the in-game conflict alert on join. |

## Configuration

`plugins/PermConflictWatchdog/config.yml`. Every option is commented in the file
itself; run `/pcw reload` after editing.

| Option | Default | Meaning |
|---|---|---|
| `alert-ops-on-join` | `true` | Show the warning in-game to staff, once per player per restart. Set `false` to keep it console-only. |
| `require-luckperms` | `true` | Only warn when LuckPerms is one of the plugins found. Set `false` to warn about *any* two permissions plugins. |
| `luckperms-plugin-name` | `LuckPerms` | Which installed plugin counts as LuckPerms for the option above. Change only for a fork under another name. |
| `debug` | `false` | Log how many plugins were scanned, which rule matched each one, and confirm the all-clear on clean servers. |
| `scan-delay-ticks` | `20` | Ticks to wait after startup before scanning, so plugin loaders such as PlugMan finish first. |
| `ignored-plugins` | `[]` | Plugins that are never reported, for false positives or a knowingly mixed setup. |
| `messages.alert-line-1` / `alert-line-2` | see file | The in-game alert, in [MiniMessage](https://docs.advntr.dev/minimessage/) format. Placeholders: `<plugins>`, `<count>`. |

## Plugins it detects

The list lives in `config.yml` under `known-permission-plugins`, so you can add to
it without waiting for a new jar. Ships knowing about:

| Plugin | Recognised by |
|---|---|
| LuckPerms | name, `me.lucko.luckperms.bukkit.LPBukkitBootstrap` |
| PermissionsEx | names `PermissionsEx` / `PEX`, both the TehKode and Stellardrift main classes |
| GroupManager | names `GroupManager` / `EssentialsGroupManager`, `org.anjocaido.groupmanager.GroupManager` |
| zPermissions | name, `org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin` |
| PowerRanks | name, `nl.svenar.PowerRanks.PowerRanks` |
| UltraPermissions | name |
| bPermissions | names `bPermissions` / `bPermissions2`, `de.bananaco.bpermissions.imp.Permissions` |
| PermissionsBukkit | name, `com.platymuus.bukkit.permissions.PermissionsPlugin` |
| Privileges | name, `net.krinsoft.privileges.Privileges` |

An entry matches on **either** the plugin name **or** the main class, so a renamed
jar or a fork running under a different name is still caught - the warning then
reads `PowerRanks (installed as MyRenamedPerms)`.

**Vault is deliberately not on this list.** Vault is a permissions *bridge*, not a
permissions plugin, and belongs next to LuckPerms. The same goes for rank and GUI
helpers that store their data in LuckPerms rather than managing permissions
themselves.

## Adding a plugin to the list

```yaml
known-permission-plugins:
  - display-name: SomePermsPlugin
    names: [SomePermsPlugin, SPP]
    main-classes:
      - com.example.someperms.SomePermsPlugin
```

`main-classes` may be empty. Then `/pcw reload`.

## Building from source

```
./gradlew build
```

Needs JDK 25. The jar lands in `build/libs/`.

## License

MIT.
