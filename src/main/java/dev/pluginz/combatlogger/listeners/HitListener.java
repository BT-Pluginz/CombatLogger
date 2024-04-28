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