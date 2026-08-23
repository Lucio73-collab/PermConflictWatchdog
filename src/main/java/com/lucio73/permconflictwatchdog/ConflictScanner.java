package com.lucio73.permconflictwatchdog;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Walks the installed plugins once and reports which of them are permissions plugins.
 */
public final class ConflictScanner {

    private ConflictScanner() {
    }

    public static ScanResult scan(PluginManager pluginManager, WatchdogConfig config) {
        List<DetectedPlugin> detected = new ArrayList<>();

        for (Plugin plugin : pluginManager.getPlugins()) {
            // Never flag ourselves, and honour the operator's ignore list.
            if (plugin.getName().equals("PermConflictWatchdog") || config.isIgnored(plugin.getName())) {
                continue;
            }
            for (KnownPermissionPlugin known : config.knownPlugins()) {
                String matchedBy = known.matchAgainst(plugin, declaredMainClass(plugin));
                if (matchedBy != null) {
                    detected.add(new DetectedPlugin(known.displayName(), plugin.getName(), version(plugin), matchedBy));
                    break;
                }
            }
        }

        // LuckPerms first (it is the plugin admins usually mean to keep), then alphabetical.
        String luckPerms = config.luckPermsName();
        detected.sort(Comparator
                .comparing((DetectedPlugin d) -> !d.displayName().equalsIgnoreCase(luckPerms))
                .thenComparing(DetectedPlugin::displayName, String.CASE_INSENSITIVE_ORDER));

        boolean conflict = detected.size() >= 2
                && (!config.requireLuckPerms() || containsLuckPerms(detected, luckPerms));

        return new ScanResult(List.copyOf(detected), conflict);
    }

    private static boolean containsLuckPerms(List<DetectedPlugin> detected, String luckPermsName) {
        return detected.stream().anyMatch(d -> d.displayName().equalsIgnoreCase(luckPermsName)
                || d.pluginName().equalsIgnoreCase(luckPermsName));
    }

    private static String declaredMainClass(Plugin plugin) {
        try {
            return plugin.getPluginMeta().getMainClass();
        } catch (Throwable ignored) {
            // Paper plugins (as opposed to Bukkit plugins) have no single main class entry.
            return null;
        }
    }

    private static String version(Plugin plugin) {
        try {
            String version = plugin.getPluginMeta().getVersion();
            return version == null || version.isBlank() ? "unknown" : version;
        } catch (Throwable ignored) {
            return "unknown";
        }
    }
}
