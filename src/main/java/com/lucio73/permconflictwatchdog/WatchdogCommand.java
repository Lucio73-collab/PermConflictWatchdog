package com.lucio73.permconflictwatchdog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * {@code /permconflictwatchdog [status|rescan|reload]} - inspect or re-run the check without a restart.
 */
public final class WatchdogCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("status", "rescan", "reload");
    private static final String PREFIX = "<gray>[</gray><gold>PermConflictWatchdog</gold><gray>]</gray> ";

    private final PermConflictWatchdog plugin;

    public WatchdogCommand(PermConflictWatchdog plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {
        String sub = args.length == 0 ? "status" : args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "status" -> report(sender, plugin.lastResult());
            case "rescan" -> {
                send(sender, "<gray>Rescanning installed plugins...</gray>");
                report(sender, plugin.rescan());
            }
            case "reload" -> {
                send(sender, "<gray>Reloading config.yml...</gray>");
                report(sender, plugin.reload());
            }
            default -> send(sender, "<red>Unknown subcommand.</red> <gray>Use /" + label + " status|rescan|reload</gray>");
        }
        return true;
    }

    private void report(CommandSender sender, ScanResult result) {
        if (result.detected().isEmpty()) {
            send(sender, "<green>No known permissions plugin is installed.</green>");
            return;
        }
        send(sender, "<gray>Permissions plugins found (" + result.detected().size() + "):</gray>");
        for (DetectedPlugin detected : result.detected()) {
            send(sender, "<gray> - </gray><white>" + escape(detected.describe()) + "</white>");
        }
        if (result.conflict()) {
            send(sender, "<red><bold>Conflict active.</bold></red> "
                    + "<gray>Keep one permissions plugin and remove the rest, then restart.</gray>");
            send(sender, "<gray>Alert staff see on join"
                    + (plugin.watchdogConfig().alertOpsOnJoin() ? "" : " (currently disabled)") + ":</gray>");
            for (Component line : plugin.alertMessage(plugin.watchdogConfig(), result)) {
                sender.sendMessage(line);
            }
        } else {
            send(sender, "<green>No conflict: only one permissions plugin is active.</green>");
        }
    }

    private void send(CommandSender sender, String miniMessage) {
        Component message = MiniMessage.miniMessage().deserialize(PREFIX + miniMessage);
        sender.sendMessage(message);
    }

    /** Plugin names and versions are third party text, so keep MiniMessage from parsing them. */
    private static String escape(String raw) {
        return raw.replace("<", "\\<");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, String @NotNull [] args) {
        if (args.length != 1) {
            return List.of();
        }
        String prefix = args[0].toLowerCase(Locale.ROOT);
        return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).toList();
    }
}
