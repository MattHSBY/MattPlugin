package me.whispy.mattPlugin;

import me.whispy.mattPlugin.chat.ChatListener;
import me.whispy.mattPlugin.chat.WhisperManager;
import me.whispy.mattPlugin.factions.FactionManager;
import me.whispy.mattPlugin.factions.FactionTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class MattPlugin extends JavaPlugin {

    private FactionManager FM = new FactionManager();
    private WhisperManager WM = new WhisperManager();
    @Override
    public void onEnable() {
        try {
            FM.onEnable();
            WM.onEnable();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        ChatListener chatListener = new ChatListener(FM);
        getCommand("faction").setExecutor(FM);
        getCommand("faction").setTabCompleter(new FactionTabCompleter(FM));
        getCommand("whisper").setExecutor(WM);

        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(WM, this);
    }

    @Override
    public void onDisable() {
        try {
            FM.onDisable();
            WM.onDisable();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
