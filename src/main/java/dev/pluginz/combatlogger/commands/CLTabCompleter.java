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
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "list", "settimer", "ally");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            } else if (args[0].equalsIgnoreCase("ally")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[1].equalsIgnoreCase("add")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> !plugin.getAllyManager().areAllies(player, Bukkit.getPlayer(name)))
                                .collect(Collectors.toList());
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        return plugin.getAllyManager().getAllies(player).stream()
                                .map(Player::getName)
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }
}