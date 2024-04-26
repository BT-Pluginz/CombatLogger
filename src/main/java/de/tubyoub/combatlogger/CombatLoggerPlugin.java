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

        int combatTimeoutInSeconds = getConfig().getInt("combatTimeoutInSeconds", 30);

        this.combatManager = new CombatManager(this, combatTimeoutInSeconds);

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);

        getCommand("combatlogger").setExecutor(new CombatLoggerCommand(combatManager,this));
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    @Override
    public void onDisable() {
    }
}
