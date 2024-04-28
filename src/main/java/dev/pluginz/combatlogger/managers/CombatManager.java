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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class CombatManager {
    private List <Player> playersInCombat, playersLeftInCombat;
    private HashMap<Player, CombatTimer> playerCombatTimerHashMap;
    private CombatLoggerPlugin plugin;
    private final ConfigManager configManager;

    public CombatManager(CombatLoggerPlugin plugin) {
        this.playerCombatTimerHashMap = new HashMap<>();
        this.playersInCombat = new List<>();
        this.playersLeftInCombat = new List<>();
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
    public void addPlayerToPlayersLeftInCombat(Player player){
        playersLeftInCombat.append(player);
    }
    public void removePlayerFromPlayersLeftInCombat(Player player){
        String playerA = player.getUniqueId().toString();
        for(playersLeftInCombat.toFirst(); playersLeftInCombat.hasAccess(); playersLeftInCombat.next()) {
            String playerB = playersLeftInCombat.getContent().getUniqueId().toString();
            if (playerA.equals(playerB))
                playersLeftInCombat.remove();
        }
    }
    public List<Player> getPlayersLeftInCombat(){
        return playersLeftInCombat;
    }
    public boolean didPlayerLeaveInCombat(Player player){
        for (playersLeftInCombat.toFirst(); playersLeftInCombat.hasAccess(); playersLeftInCombat.next()) {
            String playerA = player.getUniqueId().toString();
            String playerB = playersLeftInCombat.getContent().getUniqueId().toString();
            if (playerA.equals(playerB))
                return true;
        }
        return false;
    }
    public void handlePlayerQuit(Player player){
        if(playerIsInCombat(player)){
            player.setHealth(0);
            removePlayerFromCombat(player);
            addPlayerToPlayersLeftInCombat(player);
        }
    }
    public void judgePlayerQuit(Player player) {
        if (didPlayerLeaveInCombat(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You were killed because you left while in combat"));
            removePlayerFromPlayersLeftInCombat(player);
        }
    }
}
