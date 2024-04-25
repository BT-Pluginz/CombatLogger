//TODO: Cleanup
package de.tubyoub.combatlogger;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private Map<UUID, Long> combatTimers = new HashMap<>();
    private Map<UUID, Long> lastHitTimes = new HashMap<>();
    private Map<UUID, Integer> combatStatus = new HashMap<>();
    private Map<UUID, BossBar> bossBars = new HashMap<>();

    private int combatTimeoutInSeconds;
    private CombatLoggerPlugin plugin;

    public CombatManager(CombatLoggerPlugin plugin, int combatTimeoutInSeconds) {
        this.plugin = plugin;
        this.combatTimeoutInSeconds = combatTimeoutInSeconds;

        new BukkitRunnable() {
            @Override
            public void run() {
                checkTimers();
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void handlePlayerQuit(Player player) {
        UUID playerId = player.getUniqueId();

        if (combatTimers.containsKey(playerId)) {
            long logoutTime = System.currentTimeMillis();
            long lastCombatTime = combatTimers.get(playerId);
            if (logoutTime - lastCombatTime <= combatTimeoutInSeconds * 1000) {
                long remainingTime = (lastCombatTime + combatTimeoutInSeconds * 1000) - logoutTime;
                if (remainingTime > 0) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Player target = Bukkit.getPlayer(playerId);
                        if (target != null && target.isOnline()) {
                            target.setHealth(0);
                            target.sendMessage("You died because you logged out during combat.");
                        }
                    }, 0);
                }
            }

            combatTimers.remove(playerId);
            combatStatus.remove(playerId);
            removeBossBar(player);
        }
}



    public void handlePlayerDeath(Player player) {
        combatTimers.remove(player.getUniqueId());
        combatStatus.remove(player.getUniqueId());
        this.removeBossBar(player);
        player.sendMessage("You are no longer in combat because you lost");
    }
    public void handleFightWon(Player player){
        combatTimers.remove(player.getUniqueId());
        combatStatus.remove(player.getUniqueId());
        this.removeBossBar(player);
        player.sendMessage("You are no longer in combat because you won");
    }
    public void startCombatTimer(Player player) {
        UUID playerId = player.getUniqueId();
        combatTimers.put(playerId, System.currentTimeMillis());
        plugin.getLogger().info(String.valueOf(System.currentTimeMillis()));
        createBossBar(player);
}

    public void stopCombatTimer(Player player) {
        UUID playerId = player.getUniqueId();
        combatTimers.remove(playerId);
        removeBossBar(player);
    }
    public void resetCombatTimer(Player player) {
        combatTimers.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void handlePlayerHit(Player player) {
        UUID playerId = player.getUniqueId();

        if (!combatTimers.containsKey(playerId)) {
            startCombatTimer(player);
            player.sendMessage("You entered combat!");
        } else {
            resetCombatTimer(player);
        }
    }

    public List<String> getPlayersInCombat() {
        List<String> playersInCombat = new ArrayList<>();
        for (UUID playerId : combatStatus.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && combatStatus.get(playerId) == 1) {
                playersInCombat.add(player.getName());
            }
        }
        return playersInCombat;
    }

    private void checkTimers() {
        long currentTime = System.currentTimeMillis();
        for (UUID playerId : combatTimers.keySet()) {
            long lastCombatTime = combatTimers.get(playerId);
            if (combatTimers.get(playerId) == null){
                Player player = plugin.getServer().getPlayer(playerId);
                player.sendMessage("You are no longer in combat.");
                removeBossBar(player);
                plugin.getLogger().warning("First");
            }
            if (currentTime - lastCombatTime >= combatTimeoutInSeconds * 1000) {
                combatTimers.remove(playerId);
                combatStatus.remove(playerId);
                Player player = plugin.getServer().getPlayer(playerId);
                if (player != null) {
                    player.sendMessage("You are no longer in combat.");
                    removeBossBar(player);
                }

            } else {
                Player player = plugin.getServer().getPlayer(playerId);
                if (player != null) {
                    BossBar bossBar = bossBars.get(playerId);
                    if (bossBar != null) {
                        long timeLeft = lastCombatTime + (combatTimeoutInSeconds * 1000) - currentTime;
                        double progress = (double) timeLeft / (combatTimeoutInSeconds * 1000);
                        bossBar.setProgress(progress);
                    }
                }
            }
        }
    }


    private void createBossBar(Player player) {
        UUID playerId = player.getUniqueId();
        BossBar bossBar = Bukkit.createBossBar("Combat Timer", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        bossBars.put(playerId, bossBar);
    }

    private void removeBossBar(Player player) {
        UUID playerId = player.getUniqueId();
        BossBar bossBar = bossBars.get(playerId);
        if (bossBar != null) {
            bossBar.removeAll();
            bossBars.remove(playerId);
        }
    }

}
