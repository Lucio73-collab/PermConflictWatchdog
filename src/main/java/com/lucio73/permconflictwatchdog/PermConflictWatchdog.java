package com.lucio73.permconflictwatchdog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Warns server owners when a second permissions plugin is installed next to LuckPerms.
 *
 * <p>Answers LuckPerms issue #3689, which asks for a louder signal than the single
 * console line the server prints today.
 */
public final class PermConflictWatchdog extends JavaPlugin {

    private static final String LINE = "=".repeat(64);

    /** Players already told about the conflict, so nobody gets nagged twice per restart. */
    private final Set<UUID> alerted = ConcurrentHashMap.newKeySet();

    private volatile WatchdogConfig watchdogConfig;
    private volatile ScanResult lastResult = ScanResult.empty();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.watchdogConfig = WatchdogConfig.load(getConfig(), getLogger());

        getServer().getPluginManager().registerEvents(new JoinAlertListener(this), this);

        PluginCommand command = getCommand("permconflictwatchdog");
        if (command != null) {
            WatchdogCommand handler = new WatchdogCommand(this);
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }

        // A task scheduled from onEnable() first runs on the server's opening tick,
        // which is after every other plugin has finished enabling. The extra delay
        // gives plugin loaders (PlugMan and friends) time to bring in their payloads.
        getServer().getScheduler().runTaskLater(this, this::rescan, watchdogConfig.scanDelayTicks());
    }

    /** Re-runs the sweep and reports the outcome to the console. Returns the fresh result. */
    public ScanResult rescan() {
        WatchdogConfig config = this.watchdogConfig;
        ScanResult result = ConflictScanner.scan(getServer().getPluginManager(), config);
        this.lastResult = result;
        this.alerted.clear();

        if (config.debug()) {
            getLogger().info("Scanned " + getServer().getPluginManager().getPlugins().length
                    + " plugins against " + config.knownPlugins().size() + " known permissions plugins.");
            for (DetectedPlugin detected : result.detected()) {
                getLogger().info("  matched " + detected.describe() + " by " + detected.matchedBy());
            }
        }

        if (result.conflict()) {
            logConflict(result);
        } else if (config.debug()) {
            getLogger().info("No permission plugin conflict detected.");
        } else {
            // Clean servers stay quiet: this only shows with a verbose logger.
            getLogger().fine("No permission plugin conflict detected.");
        }
        return result;
    }

    /** Reloads config.yml, then rescans. */
    public ScanResult reload() {
        reloadConfig();
        this.watchdogConfig = WatchdogConfig.load(getConfig(), getLogger());
        return rescan();
    }

    private void logConflict(ScanResult result) {
        getLogger().warning(LINE);
        getLogger().warning("PERMISSION PLUGIN CONFLICT DETECTED");
        getLogger().warning("");
        getLogger().warning("More than one permissions plugin is installed on this server:");
        for (DetectedPlugin detected : result.detected()) {
            getLogger().warning("  - " + detected.describe());
        }
        getLogger().warning("");
        getLogger().warning("Running several permissions plugins at once makes permission checks");
        getLogger().warning("unpredictable. Whichever plugin registers last usually wins, so groups,");
        getLogger().warning("prefixes and permissions can silently stop behaving the way you set them up.");
        getLogger().warning("");
        getLogger().warning("Fix: keep ONE permissions plugin, remove the others from your plugins");
        getLogger().warning("folder, and restart the server.");
        getLogger().warning(LINE);
    }

    /** Sends the in-game alert to a player, once per server session. */
    void alert(Player player) {
        ScanResult result = this.lastResult;
        WatchdogConfig config = this.watchdogConfig;
        if (!result.conflict() || !config.alertOpsOnJoin() || !alerted.add(player.getUniqueId())) {
            return;
        }
        for (Component line : alertMessage(config, result)) {
            player.sendMessage(line);
        }
    }

    List<Component> alertMessage(WatchdogConfig config, ScanResult result) {
        TagResolver placeholders = TagResolver.resolver(
                Placeholder.unparsed("plugins", result.joinedNames()),
                Placeholder.unparsed("count", String.valueOf(result.detected().size()))
        );
        MiniMessage miniMessage = MiniMessage.miniMessage();
        return List.of(
                miniMessage.deserialize(config.alertLine1(), placeholders),
                miniMessage.deserialize(config.alertLine2(), placeholders)
        );
    }

    public WatchdogConfig watchdogConfig() {
        return watchdogConfig;
    }

    public ScanResult lastResult() {
        return lastResult;
    }
}
