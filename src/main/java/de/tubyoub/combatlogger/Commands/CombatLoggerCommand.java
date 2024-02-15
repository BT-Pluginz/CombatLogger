package de.tubyoub.combatlogger.Commands;

import de.tubyoub.combatlogger.CombatManager;
import de.tubyoub.combatlogger.CombatLoggerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CombatLoggerCommand implements CommandExecutor {

    private CombatManager combatManager;
    private CombatLoggerPlugin plugin;

    public CombatLoggerCommand(CombatManager combatManager, CombatLoggerPlugin plugin) {
        this.combatManager = combatManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /" + label + " <start|stop|list|help|setTimer> [player]");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            listPlayersInCombat(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("setTimer")) {
            setCombatTimer(sender, args);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /" + label + " <start|stop> <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Player not found or not online.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                startCombat(sender, target);
                break;
            case "stop":
                stopCombat(sender, target);
                break;
            default:
                sender.sendMessage("Invalid command. Usage: /" + label + " <start|stop|list|help|setTimer> [player]");
                break;
        }
        return true;
    }

    private void startCombat(CommandSender sender, Player target) {
        if (sender.hasPermission("combatlogger.start")) {
            combatManager.handlePlayerHit(target);
            sender.sendMessage("Combat started for " + target.getName() + ".");
        } else {
            sender.sendMessage("You don't have permission to start combat.");
        }
    }

    private void stopCombat(CommandSender sender, Player target) {
        if (sender.hasPermission("combatlogger.stop")) {
            combatManager.stopCombatTimer(target);
            sender.sendMessage("Combat stopped for " + target.getName() + ".");
        } else {
            sender.sendMessage("You don't have permission to stop combat.");
        }
    }

    private void listPlayersInCombat(CommandSender sender) {
        if (sender.hasPermission("combatlogger.list")) {
            List<String> playersInCombat = combatManager.getPlayersInCombat();
            sender.sendMessage("Players currently in combat:");
            for (String playerName : playersInCombat) {
                sender.sendMessage(playerName);
            }
        } else {
            sender.sendMessage("You don't have permission to list players in combat.");
        }
    }

    private void setCombatTimer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("combatlogger.settimer")) {
            sender.sendMessage("You don't have permission to set the combat timer.");
            return;
        }

        if (args.length == 1) {
            int currentTimer = plugin.getConfig().getInt("combatTimeoutInSeconds");
            sender.sendMessage("Current combat timer: " + currentTimer + " seconds.");
        } else if (args.length == 2) {
            try {
                int newTimer = Integer.parseInt(args[1]);
                if (newTimer <= 0) {
                    sender.sendMessage("Invalid timer value. Timer must be a positive integer.");
                    return;
                }
                plugin.getConfig().set("combatTimeoutInSeconds", newTimer);
                plugin.saveConfig();
                sender.sendMessage("Combat timer set to " + newTimer + " seconds.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid timer value. Timer must be a positive integer.");
            }
        } else {
            sender.sendMessage("Usage: /cl setTimer [seconds]");
        }
    }

    private void sendHelp(CommandSender sender) {
        if (sender.hasPermission("combatlogger.start") || sender.hasPermission("combatlogger.stop") || sender.hasPermission("combatlogger.list") || sender.hasPermission("combatlogger.settimer")) {
            sender.sendMessage("CombatLogger Commands:");
            sender.sendMessage("/cl start <player> - Start combat for the specified player.");
            sender.sendMessage("/cl stop <player> - Stop combat for the specified player.");
            sender.sendMessage("/cl list - List all players currently in combat.");
            sender.sendMessage("/cl setTimer [seconds] - Set the combat timer.");
            sender.sendMessage("/cl help - Show this help message.");
        } else {
            sender.sendMessage("You don't have permission to use any CombatLogger commands.");
        }
    }
}
