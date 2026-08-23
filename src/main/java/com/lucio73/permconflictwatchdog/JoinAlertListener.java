package com.lucio73.permconflictwatchdog;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Tells staff about an active conflict the first time they join after a restart.
 */
public final class JoinAlertListener implements Listener {

    /** Permission that decides who sees the alert; declared in plugin.yml as default: op. */
    public static final String ALERT_PERMISSION = "permconflictwatchdog.alert";

    private final PermConflictWatchdog plugin;

    public JoinAlertListener(PermConflictWatchdog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.lastResult().conflict() || !plugin.watchdogConfig().alertOpsOnJoin()) {
            return;
        }
        // A short delay puts the warning after the join / MOTD spam instead of under it,
        // and re-checks the permission once other plugins have finished attaching theirs.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline() && event.getPlayer().hasPermission(ALERT_PERMISSION)) {
                plugin.alert(event.getPlayer());
            }
        }, 40L);
    }
}
