package dev.pluginz.combatlogger.listeners;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import dev.pluginz.combatlogger.managers.CombatManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final CombatLoggerPlugin plugin;
    private final CombatManager combatManager;

    public JoinListener(CombatLoggerPlugin plugin) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.GREEN.toString() + player.getName() + " joined the server.");
        combatManager.judgePlayerQuit(player);
    }

}
