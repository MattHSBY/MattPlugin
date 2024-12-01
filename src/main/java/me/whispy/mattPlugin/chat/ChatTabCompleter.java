package me.whispy.mattPlugin.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatTabCompleter implements TabCompleter {

    private WhisperManager WM;

    public ChatTabCompleter(WhisperManager WM) {
        this.WM = WM;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //check if the label is not /r, if not, suggest first arg as all players.
        if (label.equalsIgnoreCase("r") || label.equalsIgnoreCase("reply")) {
            if (args.length == 1) {
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
                List<String> playersArray = new ArrayList<String>();
                for (int i =0;i<players.length;i++) {
                    playersArray.add(players[i].getName());
                }
                return playersArray;
            } else {
                return List.of();
            }
        }
        return List.of();
    }
}
