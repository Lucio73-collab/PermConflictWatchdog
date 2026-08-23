package com.lucio73.permconflictwatchdog;

/**
 * A permissions plugin that is actually installed on this server.
 *
 * @param displayName name from the known-plugins list
 * @param pluginName  name the installed plugin reports for itself
 * @param version     installed version, or {@code "unknown"}
 * @param matchedBy   which rule recognised it, for the {@code debug} log
 */
public record DetectedPlugin(String displayName, String pluginName, String version, String matchedBy) {

    /** {@code "LuckPerms v5.5.21"}, or {@code "LuckPerms (installed as MyPerms) v1.0"} when the jar was renamed. */
    public String describe() {
        String label = displayName.equalsIgnoreCase(pluginName)
                ? displayName
                : displayName + " (installed as " + pluginName + ")";
        return label + " v" + version;
    }
}
