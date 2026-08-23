package com.lucio73.permconflictwatchdog;

import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.Set;

/**
 * One entry from the {@code known-permission-plugins} list in config.yml.
 *
 * @param displayName human readable name used in warnings
 * @param names       plugin names (as they appear in plugin.yml) that identify this plugin
 * @param mainClasses fully qualified main classes, used to catch renamed jars and forks
 */
public record KnownPermissionPlugin(String displayName, Set<String> names, Set<String> mainClasses) {

    /** How a loaded plugin was recognised, or {@code null} when it is not this plugin. */
    public String matchAgainst(Plugin plugin, String mainClass) {
        if (names.contains(plugin.getName().toLowerCase(Locale.ROOT))) {
            return "plugin name";
        }
        if (mainClasses.contains(plugin.getClass().getName())) {
            return "main class " + plugin.getClass().getName();
        }
        if (mainClass != null && mainClasses.contains(mainClass)) {
            return "main class " + mainClass;
        }
        return null;
    }
}
