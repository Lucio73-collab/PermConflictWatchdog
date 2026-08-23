**First release.**

Detects when more than one permissions plugin is installed on your server and warns you about it, instead of leaving you to work it out later from permissions that quietly stopped behaving.

- Startup scan against nine known permissions plugins - LuckPerms, PermissionsEx, GroupManager, zPermissions, PowerRanks, UltraPermissions, bPermissions, PermissionsBukkit and Privileges
- Clear console warning naming every plugin found, why it matters, and how to fix it
- Optional in-game alert to staff on join, once per player per restart, customisable in MiniMessage
- Completely silent on servers with a single permissions plugin
- Matches on plugin name *or* main class, so renamed jars and forks are still caught
- `/pcw status`, `/pcw rescan` and `/pcw reload` - no restart needed after editing the config
- Detection list lives in `config.yml`, so you can add a plugin without waiting for an update
- Vault is deliberately not detected: it is a bridge, not a competing permissions plugin

Built and load-tested against Paper 26.2 (build 116) on Java 25.
