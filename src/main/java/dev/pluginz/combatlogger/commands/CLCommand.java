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
import dev.pluginz.combatlogger.utils.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;


public class CLCommand implements CommandExecutor {

    private final CombatManager combatManager;
    private final CombatLoggerPlugin plugin;

    public CLCommand(CombatManager combatManager, CombatLoggerPlugin plugin) {
        this.combatManager = combatManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("reload")) {
            reloadPlugin(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                listPlayersInCombat(sender);
                return true;
            case "settimer":
                setCombatTimer(sender, args);
                return true;
            case "start":
                startCombat(sender, args);
                return true;
            case "stop":
                stopCombat(sender, args);
                return true;
            case "ally":
                handleAlly(sender, args);
                return true;
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void reloadPlugin(CommandSender sender) {
        if (sender.hasPermission("combatlogger.relaod")) {
            plugin.getConfigManager().reloadConfig();
            plugin.getAllyManager().reloadAllys();
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.GREEN + "Reloaded config and Allys successfully");
        }
    }

    private void startCombat(CommandSender sender, String[] args) {
        if (sender.hasPermission("combatlogger.start")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            combatManager.addPlayerToCombat(target);
            sender.sendMessage(plugin.getPluginPrefix() + "Combat started for " + target.getName() + ".");
        } else {
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.RED + "You don't have permission to start combat.");
        }
    }

    private void stopCombat(CommandSender sender, String[] args) {
        if (sender.hasPermission("combatlogger.stop")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            combatManager.removePlayerFromCombat(target);
            sender.sendMessage(plugin.getPluginPrefix() + "Combat stopped for " + target.getName() + ".");
        } else {
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.RED +  "You don't have permission to stop combat.");
        }
    }

    private void listPlayersInCombat(CommandSender sender) {
        if (sender.hasPermission("combatlogger.list")) {
            List<Player> playersInCombat = combatManager.getPlayersInCombat();
            if (playersInCombat.isEmpty()){
                sender.sendMessage(plugin.getPluginPrefix() + "There are currently no players in Combat");
                return;
            }
            sender.sendMessage(plugin.getPluginPrefix() + "Players currently in combat:");
            for (playersInCombat.toFirst(); playersInCombat.hasAccess(); playersInCombat.next()) {
                sender.sendMessage(String.valueOf(playersInCombat.getContent().getName()));
            }
        } else {
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.RED + "You don't have permission to list players in combat.");
        }
    }

    private void setCombatTimer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("combatlogger.settimer")) {
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.RED + "You don't have permission to set the combat timer.");
            return;
        }

        if (args.length == 1) {
            int currentTimer = plugin.getConfigManager().getCombatTimeout();
            sender.sendMessage(plugin.getPluginPrefix() + "Current combat timer: " + currentTimer + " seconds.");
        } else if (args.length == 2) {
            try {
                int newTimer = Integer.parseInt(args[1]);
                if (newTimer <= 0) {
                    sender.sendMessage(plugin.getPluginPrefix() + "Invalid timer value. Timer must be a positive integer.");
                    return;
                }
                plugin.getConfigManager().setCombatTimeout(newTimer);
                sender.sendMessage(plugin.getPluginPrefix() + "Combat timer set to " + newTimer + " seconds.");
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPluginPrefix() + "Invalid timer value. Timer must be a positive integer.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sender.sendMessage(plugin.getPluginPrefix() + "Usage: /cl setTimer [seconds]");
        }
    }

    private void handleAlly(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPluginPrefix() + "Only players can use this command.");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.getPluginPrefix() +  "Usage: /cl ally <add|remove <player>");
            return;
        }
        Player player1 = (Player) sender;
        Player player2 = Bukkit.getPlayerExact(args[2]);
        if (player2 == null || !player2.isOnline()) {
            sender.sendMessage(plugin.getPluginPrefix() + "Player not found or not online.");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "add":
                if (!plugin.getAllyManager().areAllies(player1, player2)) {
                    plugin.getAllyManager().sendAllyRequest(player1, player2);
                    player2.sendMessage(plugin.getPluginPrefix() + "You have 30 seconds to accept the request");
                    sender.sendMessage(plugin.getPluginPrefix() + "Send ally request to " + player2.getName());
                    break;
                } else {
                    sender.sendMessage(plugin.getPluginPrefix() + "You are already allies with " + player2.getName() + ".");
                    break;
                }
            case "remove":
                plugin.getAllyManager().removeAlly(player1, player2);
                sender.sendMessage(plugin.getPluginPrefix() + "Removed " + player2.getName() + " as an ally.");
                player2.sendMessage(plugin.getPluginPrefix() + player1.getName() + " has removed you as an ally.");
                break;
            case "accept":
                if (plugin.getAllyManager().hasAllyRequest(player2, player1)) {
                    plugin.getAllyManager().addAlly(player1, player2);
                    player1.sendMessage(plugin.getPluginPrefix() + "You have accepted the ally request from " + player2.getName() + ".");
                    player2.sendMessage(plugin.getPluginPrefix() + player1.getName() + " has accepted your ally request.");
                } else {
                    sender.sendMessage(plugin.getPluginPrefix() + "There is no active ally request from " + player2.getName() + ".");
                }
                break;
            case "deny":
                sender.sendMessage(plugin.getPluginPrefix() + "You have denied the ally request from " + player2.getName() + ".");
                player2.sendMessage(plugin.getPluginPrefix() + player1.getName() + " has denied your ally request.");
                break;
            default:
                sender.sendMessage(plugin.getPluginPrefix() + "Invalid command. Usage: /cl ally <add|remove <player>");
        }
    }
    private void sendHelp(CommandSender sender) {
        plugin.sendPluginMessages(sender,"title");
        if (sender.hasPermission("combatlogger.start") || sender.hasPermission("combatlogger.stop") || sender.hasPermission("combatlogger.list") || sender.hasPermission("combatlogger.settimer") || sender.hasPermission("combatlogger.ally")) {
            sender.sendMessage("CombatLogger Commands:");
            if (sender.hasPermission("combatlogger.start")) {
                sender.sendMessage("/cl start <player> - Start combat for the specified player.");
            }
            if (sender.hasPermission("combatlogger.stop")) {
                sender.sendMessage("/cl stop <player> - Stop combat for the specified player.");
            }
            if (sender.hasPermission("combatlogger.list")){
                sender.sendMessage("/cl list - List all players currently in combat.");
            }
            if (sender.hasPermission("combatlogger.settimer")) {
                sender.sendMessage("/cl setTimer [seconds] - Set the combat timer.");
            }
            if (sender.hasPermission("combatlogger.ally")){
                sender.sendMessage("/cl ally <add|remove <player>");
            }
            sender.sendMessage("/cl help - Show this message.");
            plugin.sendPluginMessages(sender, "line");
        } else {
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.RED +  "You don't have permission to use any CombatLogger commands.");
        }
    }
}