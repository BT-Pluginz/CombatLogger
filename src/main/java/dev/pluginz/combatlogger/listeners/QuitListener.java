package dev.pluginz.combatlogger.listeners;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import dev.pluginz.combatlogger.managers.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final CombatLoggerPlugin plugin;
    private CombatManager combatManager;

    public QuitListener(CombatLoggerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //combatManager.handlePlayerQuit(player);
    }
}
