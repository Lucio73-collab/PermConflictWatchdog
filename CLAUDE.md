# PermConflictWatchdog

Paper 26.2 plugin (Java 25) that warns when a second permissions plugin runs next to LuckPerms. Report-only: it never changes, disables or unregisters anything.

## Commands
- Build: `./gradlew build` (jar in `build/libs/`)
- Version lives only in `build.gradle.kts`; it is expanded into `plugin.yml`

## Layout
- `src/main/java/com/lucio73/permconflictwatchdog/`: `PermConflictWatchdog.java` entry, `ConflictScanner.java` detection, `WatchdogCommand.java` (`/pcw`), `JoinAlertListener.java`, `WatchdogConfig.java`
- `src/main/resources/config.yml` known plugins list and messages (MiniMessage)
- `publish/` Modrinth description, changelog and gallery images

## Conventions
- Detection matches on plugin name OR main class; Vault is deliberately not a permissions plugin
- Update `CHANGELOG.md` and `publish/modrinth-changelog.md` with each release
- No em dashes in docs; Conventional Commits

## Never
- Commit `run/` or `test-server/` (local test server)
- Upload to Modrinth, push tags or create releases without explicit confirmation
