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

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CLTabCompleter implements TabCompleter {

    private final CombatLoggerPlugin plugin;

    public CLTabCompleter(CombatLoggerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = Arrays.asList("start", "stop", "list", "settimer", "ally", "reload", "help", "info");

        if (args.length == 1) {
            for (String cmd : commands) {
                if (sender.hasPermission("combatlogger." + cmd) && cmd.startsWith(args[0])) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop")) {
                if (sender.hasPermission("combatlogger.start") || sender.hasPermission("combatlogger.stop")) {
                    completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.startsWith(args[1]))
                            .collect(Collectors.toList()));
                }
            } else if (args[0].equalsIgnoreCase("ally")) {
                if (sender.hasPermission("combatlogger.ally")) {
                    List<String> allyCommands = Arrays.asList("add", "remove");
                    for (String allyCmd : allyCommands) {
                        if (allyCmd.startsWith(args[1])) {
                            completions.add(allyCmd);
                        }
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("accept") || args[1].equalsIgnoreCase("deny")) {
                if (sender.hasPermission("combatlogger.ally")) {
                    Player player = (Player) sender;
                    completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !plugin.getAllyManager().areAllies(player, Bukkit.getPlayer(name)))
                            .filter(name -> Bukkit.getPlayer(name) != player)
                            .filter(name -> name.startsWith(args[2]))
                            .collect(Collectors.toList()));
                }
            }
        }
        return completions;
    }
}