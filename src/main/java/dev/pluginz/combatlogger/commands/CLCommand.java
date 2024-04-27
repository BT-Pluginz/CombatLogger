/*
 * This file is part of BT's CombatLogger, licensed under the MIT License.
 *
 *  Copyright (c) BT Pluginz <github@tubyoub.de>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.pluginz.combatlogger.commands;

import dev.pluginz.combatlogger.managers.CombatManager;
import dev.pluginz.combatlogger.CombatLoggerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CLCommand implements CommandExecutor {

    private final CombatManager combatManager;
    private final CombatLoggerPlugin plugin;

    public CLCommand(CombatManager combatManager, CombatLoggerPlugin plugin) {
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
            sendUsageMessage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                //listPlayersInCombat(sender);
                return true;
            case "settimer":
                setCombatTimer(sender, args);
                return true;
            case "start":
            case "stop":
                handleCombat(sender, args, label);
                return true;
            case "ally":
            handleAlly(sender, args);
            return true;
            default:
                sendUsageMessage(sender, label);
                return true;
        }
    }

    private void handleCombat(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            sendUsageMessage(sender, label);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Player not found or not online.");
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            startCombat(sender, target);
        } else if (args[0].equalsIgnoreCase("stop")) {
            stopCombat(sender, target);
        }
    }

    private void startCombat(CommandSender sender, Player target) {
        if (sender.hasPermission("combatlogger.start")) {
            //combatManager.handlePlayerHit(target);
            sender.sendMessage("Combat started for " + target.getName() + ".");
        } else {
            sender.sendMessage("You don't have permission to start combat.");
        }
    }

    private void stopCombat(CommandSender sender, Player target) {
        if (sender.hasPermission("combatlogger.stop")) {
            //combatManager.stopCombatTimer(target);
            sender.sendMessage("Combat stopped for " + target.getName() + ".");
        } else {
            sender.sendMessage("You don't have permission to stop combat.");
        }
    }
/*
    private void listPlayersInCombat(CommandSender sender) {
        if (sender.hasPermission("combatlogger.list")) {
            //List<String> playersInCombat = combatManager.getPlayersInCombat();
            sender.sendMessage("Players currently in combat:");
            for (String playerName : playersInCombat) {
                sender.sendMessage(playerName);
            }
        } else {
            sender.sendMessage("You don't have permission to list players in combat.");
        }
    }
    */


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

    private void handleAlly(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage("Usage: /cl ally <add|remove|accept|deny> <player>");
            return;
        }
        Player player1 = (Player) sender;
        Player player2 = Bukkit.getPlayerExact(args[2]);
        if (player2 == null || !player2.isOnline()) {
            sender.sendMessage("Player not found or not online.");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "add":
                plugin.getAllyManager().sendAllyRequest(player1, player2);
                break;
            case "remove":
                plugin.getAllyManager().removeAlly(player1, player2);
                sender.sendMessage("Removed " + player2.getName() + " as an ally.");
                break;
            case "accept":
                plugin.getAllyManager().addAlly(player1, player2);
                player1.sendMessage("You have accepted the ally request from " + player2.getName() + ".");
                player2.sendMessage(player1.getName() + " has accepted your ally request.");
                break;
            case "deny":
                sender.sendMessage("You have denied the ally request from " + player2.getName() + ".");
                player2.sendMessage(player1.getName() + " has denied your ally request.");
                break;
            default:
                sender.sendMessage("Invalid command. Usage: /cl ally <add|remove|accept|deny> <player>");
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

    private void sendUsageMessage(CommandSender sender, String label) {
        sender.sendMessage("Invalid command. Usage: /" + label + " <start|stop|list|help|setTimer> [player]");
    }
}