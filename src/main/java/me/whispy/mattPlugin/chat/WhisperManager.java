package me.whispy.mattPlugin.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


public class WhisperManager implements CommandExecutor, Listener {

    private ArrayList<UUID> replyDirectories;
    private ArrayList<UUID> replySenders;

    private CommandSender sender;
    private Command cmd;
    private String label;
    private String[] args;

    public WhisperManager() {
        replyDirectories = new ArrayList<>();
        replySenders = new ArrayList<>();



    }
    private boolean reply() {

        return true;
    }
    private UUID getReplyDirFromSender(UUID sender) {
        int index = 0;
        for (int i=0;i<replySenders.size();i++) {
            if (sender.equals(replySenders.get(i))) {
                index = i;
            }
        }
        return replyDirectories.get(index);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        //check label, if /r then deal with it differently,
        //check args, args[0] is a player to message, args[>1] are all the message to send.

        this.sender = sender;
        this.cmd = cmd;
        this.label = label;
        this.args = args;

        Player plr = null;
        if (sender instanceof Player) {
            plr = (Player) sender;
        }
        if (plr == null) {
            sender.sendMessage("This command must be run by a player.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Component.text("Wrong format, try /whisper <Player> <Message>", NamedTextColor.RED));
            return true;
        }
        //sender is a player, there are more than 0 args.


        if (label.equalsIgnoreCase("r")) {
            UUID replyID = getReplyDirFromSender(plr.getUniqueId());
            if (replyID == null) {
                sender.sendMessage(Component.text("Could not find player to reply to.",NamedTextColor.RED));
                return true;
            }

            Player ReplyTarget = Bukkit.getPlayer(replyID);
            if (ReplyTarget == null) {
                sender.sendMessage(Component.text("Could not find player to reply to.",NamedTextColor.RED));
                return true;
            }


            whisper(ReplyTarget,plr);
            return true;


        } else {
            String targetName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                sender.sendMessage(Component.text("Could not find player to message.",NamedTextColor.RED));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(Component.text("Wrong format, try /whisper <Player> <Message>", NamedTextColor.RED));
                return true;
            }
            this.args = Arrays.copyOfRange(args,1,args.length);
            whisper(targetPlayer,plr);
            return true;

        }


    }

    private void whisper(Player targetPlayer, Player plr) {
        StringBuilder str = new StringBuilder();
        for (int i= 0;i<args.length;i++) {
            str.append(args[i]).append(" ");
        }
        targetPlayer.sendMessage(Component.text("from "+plr.getName()+": "+str.toString(),NamedTextColor.LIGHT_PURPLE));
        for (int i = 0;i<replySenders.size();i++) {
            if (replySenders.get(i).equals(targetPlayer.getUniqueId())) {
                replyDirectories.set(i,plr.getUniqueId());
            }
        }
        plr.sendMessage(Component.text("To "+targetPlayer.getName()+": "+str.toString(),NamedTextColor.LIGHT_PURPLE));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        replySenders.add(e.getPlayer().getUniqueId());
        replyDirectories.add(null);

    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        for (int i =0;i<replySenders.size();i++) {
            if (replySenders.get(i).equals(e.getPlayer().getUniqueId())) {
                replySenders.remove(i);
                replyDirectories.remove(i);
            }
        }

    }

    public void onEnable() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (int i=0;i<players.length;i++) {
            Player plr = players[i];
            replySenders.add(plr.getUniqueId());
            replyDirectories.add(null);

        }
    }
    public void onDisable() {

    }
}
