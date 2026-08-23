package com.lucio73.permconflictwatchdog;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Typed view over config.yml. Everything the watchdog knows about which plugins
 * count as permissions plugins lives here, so the list can be edited without a rebuild.
 */
public final class WatchdogConfig {

    private final boolean alertOpsOnJoin;
    private final boolean requireLuckPerms;
    private final boolean debug;
    private final long scanDelayTicks;
    private final String luckPermsName;
    private final Set<String> ignoredPlugins;
    private final List<KnownPermissionPlugin> knownPlugins;
    private final String alertLine1;
    private final String alertLine2;

    private WatchdogConfig(boolean alertOpsOnJoin, boolean requireLuckPerms, boolean debug, long scanDelayTicks,
                           String luckPermsName, Set<String> ignoredPlugins,
                           List<KnownPermissionPlugin> knownPlugins, String alertLine1, String alertLine2) {
        this.alertOpsOnJoin = alertOpsOnJoin;
        this.requireLuckPerms = requireLuckPerms;
        this.debug = debug;
        this.scanDelayTicks = scanDelayTicks;
        this.luckPermsName = luckPermsName;
        this.ignoredPlugins = ignoredPlugins;
        this.knownPlugins = knownPlugins;
        this.alertLine1 = alertLine1;
        this.alertLine2 = alertLine2;
    }

    public static WatchdogConfig load(FileConfiguration config, Logger logger) {
        List<KnownPermissionPlugin> known = new ArrayList<>();
        for (Map<?, ?> raw : config.getMapList("known-permission-plugins")) {
            String displayName = string(raw.get("display-name"));
            Set<String> names = lowercaseSet(raw.get("names"));
            Set<String> mainClasses = stringSet(raw.get("main-classes"));

            if (displayName == null && !names.isEmpty()) {
                displayName = names.iterator().next();
            }
            if (displayName == null || (names.isEmpty() && mainClasses.isEmpty())) {
                logger.warning("Skipping a known-permission-plugins entry: it needs at least "
                        + "'names' or 'main-classes' (and a 'display-name').");
                continue;
            }
            known.add(new KnownPermissionPlugin(displayName, names, mainClasses));
        }
        if (known.isEmpty()) {
            logger.warning("No usable entries in 'known-permission-plugins' - the watchdog has nothing to look for. "
                    + "Delete config.yml and restart to restore the defaults.");
        }

        return new WatchdogConfig(
                config.getBoolean("alert-ops-on-join", true),
                config.getBoolean("require-luckperms", true),
                config.getBoolean("debug", false),
                Math.max(1L, config.getLong("scan-delay-ticks", 20L)),
                config.getString("luckperms-plugin-name", "LuckPerms"),
                lowercaseSet(config.getStringList("ignored-plugins")),
                List.copyOf(known),
                config.getString("messages.alert-line-1",
                        "<red><bold>[!]</bold> Permission plugin conflict:</red> <white><plugins></white>"),
                config.getString("messages.alert-line-2",
                        "<gray>Keep only one permissions plugin installed. See the server console for details.</gray>")
        );
    }

    private static String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Set<String> stringSet(Object value) {
        Set<String> out = new HashSet<>();
        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (item != null) {
                    out.add(String.valueOf(item).trim());
                }
            }
        } else if (value != null) {
            out.add(String.valueOf(value).trim());
        }
        out.remove("");
        return Set.copyOf(out);
    }

    private static Set<String> lowercaseSet(Object value) {
        Set<String> out = new HashSet<>();
        for (String item : stringSet(value)) {
            out.add(item.toLowerCase(Locale.ROOT));
        }
        return Set.copyOf(out);
    }

    public boolean alertOpsOnJoin() {
        return alertOpsOnJoin;
    }

    public boolean requireLuckPerms() {
        return requireLuckPerms;
    }

    public boolean debug() {
        return debug;
    }

    public long scanDelayTicks() {
        return scanDelayTicks;
    }

    public String luckPermsName() {
        return luckPermsName;
    }

    public boolean isIgnored(String pluginName) {
        return ignoredPlugins.contains(pluginName.toLowerCase(Locale.ROOT));
    }

    public List<KnownPermissionPlugin> knownPlugins() {
        return knownPlugins;
    }

    public String alertLine1() {
        return alertLine1;
    }

    public String alertLine2() {
        return alertLine2;
    }
}
