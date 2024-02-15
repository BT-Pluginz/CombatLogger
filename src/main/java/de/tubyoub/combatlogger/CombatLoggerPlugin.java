package de.tubyoub.combatlogger;

import de.tubyoub.combatlogger.Commands.CombatLoggerCommand;
import de.tubyoub.combatlogger.Listeners.CombatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatLoggerPlugin extends JavaPlugin {

    private CombatManager combatManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        int combatTimeoutInSeconds = getConfig().getInt("combatTimeoutInSeconds");

        CombatManager combatManager = new CombatManager(this, combatTimeoutInSeconds);

        getServer().getPluginManager().registerEvents(new CombatListener(this, combatTimeoutInSeconds), this);

        getCommand("combatlogger").setExecutor(new CombatLoggerCommand(combatManager,this));
    }
    @Override
    public void onDisable() {
    }
}
