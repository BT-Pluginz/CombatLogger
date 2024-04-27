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

package dev.pluginz.combatlogger;

import dev.pluginz.combatlogger.commands.CLCommand;
import dev.pluginz.combatlogger.commands.CLTabCompleter;
import dev.pluginz.combatlogger.listeners.DeathListener;
import dev.pluginz.combatlogger.listeners.HitListener;
import dev.pluginz.combatlogger.listeners.QuitListener;
import dev.pluginz.combatlogger.managers.AllyManager;
import dev.pluginz.combatlogger.managers.CombatManager;
import dev.pluginz.combatlogger.managers.ConfigManager;
import dev.pluginz.combatlogger.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CombatLoggerPlugin extends JavaPlugin {
    private final String version = "1.0";

    private CombatManager combatManager;
    private ConfigManager configManager;
    private AllyManager allyManager;
    private boolean newVersion;
    @Override
    public void onEnable() {
        this.getLogger().info("______________________________ .");
        this.getLogger().info("\\______   \\__    ___/\\_   ___ \\|    |");
        this.getLogger().info(" |    |  _/ |    |   /    \\  \\/|    |");
        this.getLogger().info(" |    |   \\ |    |   \\     \\___|    |");
        this.getLogger().info(" |______  / |____|    \\______  /_______ \\" + "     BT's CombatLogger v" + version);
        this.getLogger().info("        \\/                   \\/        \\/" + "     Running on " + Bukkit.getServer().getName() + " using Blackmagic");


        configManager = new ConfigManager(this);
        configManager.loadConfig();

        this.combatManager = new CombatManager(this);
        if (configManager.isCheckVersion()) {
            newVersion = VersionChecker.isNewVersionAvailable(version);
            this.getLogger().warning("There is a new Version available for BT's CombatLogger");
        }

        File allyFile = new File("plugins/CombatLogger/allies.yml");
        if (!allyFile.exists()) {
            try {
                allyFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.allyManager = new AllyManager(this,allyFile);

        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new HitListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);

        getCommand("combatlogger").setExecutor(new CLCommand(combatManager,this));
        getCommand("combatlogger").setTabCompleter(new CLTabCompleter(this));
        //getCommand("combatLogger".setTabCompleter(new CLTabCompleter()), this);
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
    public AllyManager getAllyManager() {
        return allyManager;
    }

    @Override
    public void onDisable() {
        configManager.saveConfig();
    }
}
