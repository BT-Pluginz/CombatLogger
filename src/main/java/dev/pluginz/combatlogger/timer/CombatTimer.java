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

package dev.pluginz.combatlogger.timer;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import dev.pluginz.combatlogger.managers.CombatManager;
import dev.pluginz.combatlogger.managers.ConfigManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTimer extends Timer{
    private final ConfigManager configManager;
    private final CombatManager combatManager;
    private final Player player;
    public CombatTimer(boolean running, int timeInSeconds, CombatLoggerPlugin plugin, Player player) {
        super(running, timeInSeconds, plugin);
        this.configManager = plugin.getConfigManager();
        this.combatManager = plugin.getCombatManager();
        this.player = player;
        this.sendActionBar();
        this.run();
    }
    @Override
    public void run(){
        new BukkitRunnable() {
            @Override
            public void run() {

                if(!isRunning())
                    return;
                sendActionBar();
                setTimeInSeconds(getTimeInSeconds() - 1);
            }
        }.runTaskTimer(plugin, 20, 20);
    }
    public void sendActionBar(){
        if(isRunning() && 0 == getTimeInSeconds()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You are no longer in combat"));
            combatManager.removePlayerFromCombat(player);

        }
        if(isRunning() && 0 < getTimeInSeconds())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You are in combat for " + getTimeInSeconds() + "s"));
    }
}
