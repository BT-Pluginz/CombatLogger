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

package dev.pluginz.combatlogger.managers;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AllyManager {
    private final CombatLoggerPlugin plugin;
    private final File allyFile;
    private final Map<UUID, Set<UUID>> allyMap = new HashMap<>();

    public AllyManager(CombatLoggerPlugin plugin, File allyFile) {
        this.plugin = plugin;
        this.allyFile = allyFile;
        this.load();
    }

    public void sendAllyRequest(Player sender, Player target) {
        TextComponent message = new TextComponent("Player " + sender.getName() + " wants to add you as an ally. ");
        TextComponent accept = new TextComponent("[Accept]");
        accept.setColor(ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cl ally accept " + sender.getName()));
        TextComponent deny = new TextComponent("[Deny]");
        deny.setColor(ChatColor.RED);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cl ally deny " + sender.getName()));
        message.addExtra(accept);
        message.addExtra(new TextComponent(" "));
        message.addExtra(deny);
        target.spigot().sendMessage(message);
    }

    public void addAlly(Player player1, Player player2) {
        allyMap.computeIfAbsent(player1.getUniqueId(), k -> new HashSet<>()).add(player2.getUniqueId());
        allyMap.computeIfAbsent(player2.getUniqueId(), k -> new HashSet<>()).add(player1.getUniqueId());
        this.save();
    }

    public void removeAlly(Player player1, Player player2) {
        Set<UUID> allies1 = allyMap.get(player1.getUniqueId());
        Set<UUID> allies2 = allyMap.get(player2.getUniqueId());
        if (allies1 != null) {
            allies1.remove(player2.getUniqueId());
        }
        if (allies2 != null) {
            allies2.remove(player1.getUniqueId());
        }
        this.save();
    }
    public List<Player> getAllies(Player player) {
        Set<UUID> allyUUIDs = allyMap.get(player.getUniqueId());
        if (allyUUIDs == null) {
            return Collections.emptyList();
        }
        return allyUUIDs.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public boolean areAllies(Player player1, Player player2) {
        Set<UUID> allies = allyMap.get(player1.getUniqueId());
        return allies != null && allies.contains(player2.getUniqueId());
    }

    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(allyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                UUID player1 = UUID.fromString(parts[0]);
                UUID player2 = UUID.fromString(parts[1]);
                allyMap.computeIfAbsent(player1, k -> new HashSet<>()).add(player2);
                allyMap.computeIfAbsent(player2, k -> new HashSet<>()).add(player1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(allyFile))) {
            for (Map.Entry<UUID, Set<UUID>> entry : allyMap.entrySet()) {
                UUID player1 = entry.getKey();
                for (UUID player2 : entry.getValue()) {
                    writer.println(player1 + "," + player2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
