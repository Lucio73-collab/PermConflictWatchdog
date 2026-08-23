I've built this as a standalone plugin, since it works just as well outside LuckPerms itself: **PermConflictWatchdog** — https://modrinth.com/plugin/permconflictwatchdog

On startup it checks every installed plugin against a list of known permissions plugins and, if it finds more than one, prints a console warning naming them and explaining why the combination causes unpredictable permission checks. Staff can optionally get the same warning in chat on join, once per restart. Servers with a single permissions plugin see nothing at all.

A few details that seemed worth getting right:

- Detection matches on **plugin name or main class**, so a renamed jar or a fork under a different name is still caught and reported as `PowerRanks (installed as MyRenamedPerms)`.
- The known-plugins list lives in `config.yml` rather than the jar, so admins can add one without waiting for a release.
- The scan runs a second after startup finishes, so plugins brought in by loaders like PlugMan are included.
- **Vault is deliberately not flagged** — it's a bridge that's meant to sit alongside LuckPerms, and flagging it would make the warning noise.
- It only reads and reports. It never disables or unregisters anything.

Currently ships knowing about LuckPerms, PermissionsEx, GroupManager, zPermissions, PowerRanks, UltraPermissions, bPermissions, PermissionsBukkit and Privileges. Built and load-tested on Paper 26.2 / Java 25.

Source: https://github.com/Lucio73-collab/PermConflictWatchdog

Happy to add more plugins to the default list if anyone knows of ones I've missed. And if the LuckPerms team would rather this behaviour lived in LuckPerms itself, the detection logic is small and MIT licensed — feel free to take any of it.
