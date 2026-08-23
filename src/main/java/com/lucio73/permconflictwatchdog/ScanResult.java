package com.lucio73.permconflictwatchdog;

import java.util.List;

/**
 * Outcome of one sweep over the installed plugins.
 *
 * @param detected  every known permissions plugin that is installed, LuckPerms first
 * @param conflict  true when the detected set should be warned about
 */
public record ScanResult(List<DetectedPlugin> detected, boolean conflict) {

    public static ScanResult empty() {
        return new ScanResult(List.of(), false);
    }

    /** {@code "LuckPerms v5.5.21, PermissionsEx v1.23.4"} - used in the in-game alert. */
    public String joinedNames() {
        return String.join(", ", detected.stream().map(DetectedPlugin::describe).toList());
    }
}
