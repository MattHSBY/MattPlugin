package me.whispy.mattPlugin;

import me.whispy.mattPlugin.factions.ChatEvent;
import me.whispy.mattPlugin.factions.FactionManager;
import me.whispy.mattPlugin.factions.FactionTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class MattPlugin extends JavaPlugin {

    private FactionManager FM = new FactionManager();

    @Override
    public void onEnable() {
        try {
            FM.onEnable();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        ChatEvent chatEvent = new ChatEvent(FM);
        getCommand("faction").setExecutor(FM);
        getCommand("faction").setTabCompleter(new FactionTabCompleter(FM));
        getServer().getPluginManager().registerEvents(chatEvent, this);
    }

    @Override
    public void onDisable() {
        try {
            FM.onDisable();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
