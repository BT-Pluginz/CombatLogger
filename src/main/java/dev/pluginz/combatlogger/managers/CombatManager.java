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

import dev.pluginz.combatlogger.timer.CombatTimer;
import dev.pluginz.combatlogger.utils.List;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {
    private List <Player> playersInCombat;
    private HashMap<Player, CombatTimer> playerCombatTimerHashMap;
    private int combatTimeoutInSeconds;
    private CombatLoggerPlugin plugin;
    private final ConfigManager configManager;

    public CombatManager(CombatLoggerPlugin plugin) {
        this.playerCombatTimerHashMap = new HashMap<>();
        this.playersInCombat = new List<>();
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    public List <Player> getPlayersInCombat(){
        return playersInCombat;
    }
    public void addPlayerToCombat(Player player){
        if(!playerIsInCombat(player)) {
            playersInCombat.append(player);
            playerCombatTimerHashMap.put(player, new CombatTimer(true, configManager.getCombatTimeout(), plugin, player));
        }else{
            playerCombatTimerHashMap.get(player).setTimeInSeconds(configManager.getCombatTimeout());
            playerCombatTimerHashMap.get(player).sendActionBar();
        }
    }
    public void removePlayerFromCombat(Player player){
        for(playersInCombat.toFirst(); playersInCombat.hasAccess(); playersInCombat.next())
            if(player == playersInCombat.getContent())
                playersInCombat.remove();
        playerCombatTimerHashMap.remove(player);
    }
    public boolean playerIsInCombat(Player player){
        for(playersInCombat.toFirst(); playersInCombat.hasAccess(); playersInCombat.next())
            if(player == playersInCombat.getContent())
                return true;
        return false;
    }
    public int getCombatTimeoutInSeconds(Player player){
        if(playerIsInCombat(player))
            return playerCombatTimerHashMap.get(player).getTimeInSeconds();
        return -1;
    }
}
