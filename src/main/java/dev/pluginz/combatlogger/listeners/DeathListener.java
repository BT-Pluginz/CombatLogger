package dev.pluginz.combatlogger.listeners;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import dev.pluginz.combatlogger.managers.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private final CombatLoggerPlugin plugin;
    private CombatManager combatManager;
    public DeathListener(CombatLoggerPlugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player murder = event.getEntity().getKiller();
        /*
        combatManager.handlePlayerDeath(victim);
        combatManager.handleFightWon(murder);
         */
    }
}
