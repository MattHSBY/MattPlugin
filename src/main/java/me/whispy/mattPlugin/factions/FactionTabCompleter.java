package me.whispy.mattPlugin.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FactionTabCompleter implements TabCompleter {
    private FactionManager FM;

    private CommandSender sender;
    private Command cmd;
    private String alias;
    private String[] args;

    private List<String> commands = new ArrayList<>();


    public FactionTabCompleter(FactionManager FM) {
        this.FM = FM;
        commands.add("help");
        commands.add("create");//
        commands.add("invite");//
        commands.add("accept");//
        commands.add("decline");//
        commands.add("list");
        commands.add("disband");
        commands.add("leave");
        commands.add("home");
        commands.add("sethome");
        commands.add("colour");
        commands.add("kick");
    }

    private List<String> removeNotApplicable(List<String> cmdargs, int argindex) {
        String str = args[argindex];
        List<String> newargs = new ArrayList<String>();

        //loop through currently applicable cmd args, check if they match with the string so far, then remove the ones that don't
        for (String cmdarg : cmdargs) {
            if (str.length() < cmdarg.length()) {
                if (cmdarg.substring(0, str.length()).equalsIgnoreCase(str)) {
                    newargs.add(cmdarg);
                }
            }
        }
        return newargs;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        this.sender = sender;
        this.args = args;
        this.cmd = cmd;
        this.alias = alias;

        if (args.length == 1) {

            List<String> newcommands = removeNotApplicable(commands,0);
            return newcommands;
        }

        List<String> CMDargs = new ArrayList<>();
        if (args.length == 2) {
            Player localplayer = null;

            if (sender instanceof Player) {
                localplayer = (Player) sender;
            }
            if (args[0].equalsIgnoreCase("invite")) {
                //loop through online players and check if they are in the plr's faction, if not, suggest that one.
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

                for (int i =0;i<players.length;i++) {
                    Player player = players[i];
                    boolean thisplayerinfaction = false;
                    UUID uuid = player.getUniqueId();

                    if (localplayer != null) {
                        if (FM.getFactionFromPlayer(localplayer.getUniqueId()) != null) {
                            Faction F = FM.getFactionFromPlayer(localplayer.getUniqueId());
                            for (int v = 0;v<F.getMembers().size();v++) {
                                UUID member = F.getMembers().get(v);
                                if (member.equals(uuid)) {
                                    thisplayerinfaction = true;
                                }
                            }
                            if (!thisplayerinfaction) {
                                boolean thisplayerhasinvite = false;

                                for (int v = 0;v<F.getInvites().size();v++) {
                                    UUID invite = F.getInvites().get(v);
                                    if (invite.equals(uuid)) {
                                        thisplayerhasinvite = true;
                                    }
                                }


                                if (!thisplayerhasinvite) {
                                    CMDargs.add(player.getName());
                                }
                            }

                        }
                    }
                }



            }
            else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline")) {
                //accept and decline technically should produce the same args because it looks through to find any
                //invites to the player, then displays them there.
                for (int i =0;i<FM.getFactions().size();i++) {
                    Faction f = FM.getFactions().get(i);
                    for (int v = 0;v<f.getInvites().size();v++) {
                        UUID invite = f.getInvites().get(v);
                        if (localplayer != null) {
                            if (invite.equals(localplayer.getUniqueId())) {
                                CMDargs.add(f.getName());
                            }

                        }


                    }

                }

            }
            else if (args[0].equalsIgnoreCase("colour") || args[0].equalsIgnoreCase("color")) {
                String colour = args[1];
                ArrayList<String> colours = getStrings();
                return colours;
                }

            CMDargs = removeNotApplicable(CMDargs,1);
            return CMDargs;
        }
        return CMDargs;
    }

    private static @NotNull ArrayList<String> getStrings() {
        ArrayList<String> colours = new ArrayList<>();
        colours.add("aqua");
        colours.add("black");
        colours.add("blue");
        colours.add("dark_aqua");
        colours.add("dark_blue");
        colours.add("dark_gray");
        colours.add("dark_green");
        colours.add("dark_red");
        colours.add("dark_purple");
        colours.add("gold");
        colours.add("gray");
        colours.add("green");
        colours.add("light_purple");
        colours.add("white");
        colours.add("yellow");
        return colours;
    }
}
