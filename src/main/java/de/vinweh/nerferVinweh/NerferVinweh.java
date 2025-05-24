package de.vinweh.nerferVinweh;

import org.bukkit.plugin.java.JavaPlugin;

public class NerferVinweh extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new NerferEventHandler(this), this);
    }

    @Override
    public void onDisable() {
    }
}
