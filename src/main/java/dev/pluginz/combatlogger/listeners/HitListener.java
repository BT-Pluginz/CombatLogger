package dev.pluginz.combatlogger.listeners;

import dev.pluginz.combatlogger.CombatLoggerPlugin;
import dev.pluginz.combatlogger.managers.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitListener implements Listener {
    private final CombatLoggerPlugin plugin;
    private CombatManager combatManager;
    public HitListener(CombatLoggerPlugin plugin){
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        Player damager = null, damaged = null;
        if (event.getEntity() instanceof Player) {
            damaged = (Player) event.getEntity();
            if (event.getDamager() instanceof Projectile){
                if(((Projectile) event.getDamager()).getShooter() instanceof Player)
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
            } else if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            if (!plugin.getAllyManager().areAllies(damager, damaged) && null != damager && null != damaged && damager != damaged) {

                combatManager.addPlayerToCombat(damager);
                combatManager.addPlayerToCombat(damaged);
            }


        }
    }
}