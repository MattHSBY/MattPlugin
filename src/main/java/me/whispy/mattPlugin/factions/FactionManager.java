package me.whispy.mattPlugin.factions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FactionManager implements CommandExecutor {
    //attributes
    private final FactionManager INSTANCE;
    private ArrayList<Faction> factions;

    public static String FCMDColor = ChatColor.AQUA + "";
    public static String FactionsPrefix = "[Factions] ";

    private Gson gson = new Gson();

    private CommandSender sender;
    private Command cmd;
    private String label;
    private String[] args;

    private String HelpMessage = "This is a help message. Commands will be in SMP discord.";

    //constructor
    public FactionManager() {
        INSTANCE = this;
    }


    //public methods
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.sender = sender;
        this.cmd = cmd;
        this.label = label;
        this.args = args;
        //seperate cases which can ONLY be run by a player, and cases which can be run by anything.
        //help and create can both be run by anything, all other cases MUST be run by a player.
        //other cases are: invite, accept, decline, list, disband, leave, home and sethome.
        if (label.equalsIgnoreCase("fl")) {
            Player plr = null;
            if (sender instanceof Player) {
                plr = (Player) sender;
            }
            if (plr == null) {
                sendMessage(sender,"This command must be run by a player.");
                return true;
            }
            if (args.length != 0) {
                sendMessage(sender,"Wrong format, try /faction list ");
                return true;
            }
            return f_list(plr);
        }
        if (label.equalsIgnoreCase("fc")) {
            Player plr = null;
            if (sender instanceof Player) {
                plr = (Player) sender;
            }
            if (plr == null) {
                sendMessage(sender,"This command must be run by a player.");
                return true;
            }
            if (args.length == 0) {
                sendMessage(sender,"Wrong format, try /faction chat <Message>");
                return true;
            }
            String[] Targs = new String[args.length+1];
            Targs[0] = "";
            for (int i =0;i<args.length;i++) {
                Targs[i+1] = args[i];
            }
            this.args = Targs;
            return f_chat(plr);
        }
        if (args.length == 0) {
            return f_help();
        }//by this point, we now know that args is not empty, bc if it was, the method would return.
        if (args[0].equalsIgnoreCase("help")) {
            return f_help();
        }
        if (args[0].equalsIgnoreCase("create")) {
            return f_create();
        }

        //now, any other commands must be run by a player
        Player plr;
        if (sender instanceof Player) {
            plr = (Player) sender;
        }
        else {
            sendMessage(sender,"This command must be run by a player.");
            return true;
        }
        if (args[0].equalsIgnoreCase("invite")) {
            return f_invite(plr);
        }
        if (args[0].equalsIgnoreCase("accept")) {
            return f_accept(plr);
        }
        if (args[0].equalsIgnoreCase("decline")) {
            return f_decline(plr);
        }
        if (args[0].equalsIgnoreCase("list")) {
            return f_list(plr);
        }
        if (args[0].equalsIgnoreCase("disband")) {
            return f_disband(plr);
        }
        if (args[0].equalsIgnoreCase("leave")) {
            return f_leave(plr);
        }
        if (args[0].equalsIgnoreCase("chat")) {
            return f_chat(plr);
        }
        if (args[0].equalsIgnoreCase("home")) {
            return f_home(plr);
        }
        if (args[0].equalsIgnoreCase("sethome")) {
            return f_sethome(plr);
        }
        if (args[0].equalsIgnoreCase("kick")) {
            return f_kick(plr);
        }
        if (args[0].equalsIgnoreCase("colour") || args[0].equalsIgnoreCase("color")) {
            return f_colour(plr);
        }
        sendMessage(sender,"command not recognised, try /faction help");

        return true;
    }

    public FactionManager getInstance() {
        return INSTANCE;
    }
    public void onEnable() throws IOException {
        this.factions = loadFactions();
    }
    public void onDisable() throws IOException{
        saveFactions();
    }

    public List<Faction> getFactions() {

        return factions;
    }

    public Faction getFactionFromPlayer(UUID plr) {
        Faction match;
        for (int i =0;i<factions.size();i++) {
            //assume no player can be in more than 1 faction
            //therefore first faction match is the only faction match
            Faction faction = factions.get(i);
            List<UUID> members = faction.getMembers();
            for (int v =0;v<members.size();v++) {
                UUID member = members.get(v);
                if (member.equals(plr)) {
                    match = faction;
                    return match;
                }


            }
        }


        return null;
    }

    //private methods

    private void sendMessage(CommandSender sender, String message) {
        message = FCMDColor + FactionsPrefix + message;
        sender.sendMessage(message);
    }

    private void broadcastToFaction(Faction faction,String message) {
        for (int i =0;i<faction.getMembers().size();i++) {
            Player plr = Bukkit.getPlayer(faction.getMembers().get(i));
            if (plr != null) {
                sendMessage(plr,message);
            }
        }
    }

    private boolean f_help() {
        sendHelpMessage();
        return true;
    }

    private boolean f_create() {
        if (args.length != 2) {
            sendMessage(sender,"wrong format: /f create <Faction_Name>");
            return true;
        }//if there is no name written, don't do shit



        for (int i =0;i<factions.size();i++) {
            if (factions.get(i).getName().equalsIgnoreCase(args[1])) {
                sendMessage(sender,"That faction already exists!");
                return true;
            }
        }
        if (sender instanceof Player) {
            Player plr = (Player) sender;
            for (int i =0;i<factions.size();i++) {
                Faction faction = factions.get(i);
                List<UUID> members = faction.getMembers();
                for (int v = 0;v<members.size();v++) {
                    if (members.get(v).equals(plr.getUniqueId())) {
                        sendMessage(plr,"you are already in a faction! leave it before joining another. (/f leave)");
                        return true;
                    }
                }
            }
            Faction faction = new Faction(args[1]);
            faction.addMember(plr.getUniqueId(),plr.getName());
            sendMessage(sender,"faction created. Use /f invite <Player> to invite your first member. ");
            factions.add(faction);
            return true;
        }
        Faction faction = new Faction(args[1]);
        sendMessage(sender,"faction created. Use /f invite <Player> to invite your first member. ");
        factions.add(faction);

        return true;
    }

    private boolean f_invite(Player plr) {
        if (args.length != 2) {
            sendMessage(sender,"Wrong format, try: /faction invite <player>");
            return true;
        }


        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(sender,"You are not in a faction!");
            return true;
        }

        if (plr.getName().equalsIgnoreCase(args[1])) {
            sendMessage(sender, "You cannot invite yourself.");
            return true;
        }

        if (!faction.getMembers().getFirst().equals(plr.getUniqueId())) {
            sendMessage(sender,"You do not have permission to use this command.");
            return true;
        }



        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, "That player was not found, please try again.");
            return true;
        }
        boolean alreadyinfaction = false;
        for (int i=0;i<faction.getMembers().size();i++) {
            UUID member = faction.getMembers().get(i);
            if (member.equals(target.getUniqueId())) {
                sendMessage(sender, "This player is already in your faction!");
            }
        }


        boolean alreadyinvited = !faction.invitePlayer(target.getUniqueId());//invite player here
        if (alreadyinvited) {
            sendMessage(sender,"This player already has an invite!");
            return true;
        }

        sendMessage(plr,"you have sent an invite to "+target.getName()+" to join your faction");
        sendMessage(target, "you have been invited to join "+plr.getName()+"'s faction, "+faction.getName());
        sendMessage(target,"To join, use: /f accept "+faction.getName());
        sendMessage(target,"To leave, use: /f decline "+faction.getName());
        return true;
    }

    private boolean f_accept(Player plr) {
        if (args.length != 2) {
            sendMessage(plr,"wrong format, try: /faction accept <Faction_Name>");
            return true;
        }
        for (int i =0;i<factions.size();i++) {
            Faction faction = factions.get(i);
            List<UUID> members = faction.getMembers();
            for (int v = 0;v<members.size();v++) {
                if (members.get(v).equals(plr.getUniqueId())) {
                    sendMessage(plr,"you are already in a faction! leave it before joining another. (/f leave)");
                    return true;
                }
            }

        }

        for (int i = 0;i<factions.size();i++) {
            Faction faction = factions.get(i);
            if (faction.getName().equalsIgnoreCase(args[1])) {
                //correct arg[1].
                if (faction.acceptInvite(plr.getUniqueId(),plr.getName())) { //if accept went through
                    sendMessage(plr,"you have joined the "+faction.getName()+" faction.");
                    Player owner = Bukkit.getPlayer(faction.getMembers().getFirst());
                    if (owner != null) {
                        sendMessage(owner, plr.getName()+" has accepted your invite and joined your faction.");
                    }

                    return true;
                } else {
                    sendMessage(plr,"you do not have an invite to that faction.");
                    return true;
                }

            }
        }
        sendMessage(plr,"there is no faction by that name.");


        return true;
    }

    private boolean f_decline(Player plr) {
        //on decline, remove plr's UUID from args[1]'s Invite list.
        if (args.length != 2) {
            sendMessage(plr,"wrong format, try: /faction decline <Faction_Name>");
            return true;
        }

        for (int i =0;i<factions.size();i++) {
            Faction faction = factions.get(i);
            if (faction.getName().equalsIgnoreCase(args[1])) {
                List<UUID> invites = faction.getInvites();
                for (int v = 0;v<invites.size();v++) {
                    if (invites.get(v).equals(plr.getUniqueId())) {
                        invites.remove(v);
                        sendMessage(plr,"Invite to "+faction.getName()+" removed.");

                        Player owner = Bukkit.getPlayer(faction.getMembers().getFirst());
                        if (owner != null) {
                            sendMessage(owner, plr.getName()+" has declined your invite.");
                        }

                        return true;
                    }
                }
                sendMessage(plr,"you don't have an invite to that faction!");
                return true;
            }
        }
        sendMessage(plr,"that faction does not exist.");
        return true;
    }

    private boolean f_list(Player plr) {
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }
        sendMessage(plr,faction.getName()+" faction list:");
        for (int i= 0;i<faction.getMemberNames().size();i++) {


            String member = faction.getMemberNames().get(i);
            if (i == 0) {
                member = "Owner: " + member;
            } else {
                member = "Member: "+ member;
            }
            sendMessage(plr,"- " +member);

        }


        return true;
    }

    private boolean f_disband(Player plr) {
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }
        if (faction.getMembers().get(0).equals(plr.getUniqueId())) {
            for (int i =0;i<factions.size();i++) {
                if (factions.get(i).equals(faction)) {

                    broadcastToFaction(faction,"faction disbanded.");
                    factions.remove(i);
                    break;
                }
            }


        } else {
            sendMessage(plr,"you do not have permission to do this.");
        }


        return true;
    }

    private boolean f_leave(Player plr) {
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }
        if (faction.getMembers().get(0).equals(plr.getUniqueId())) {
            sendMessage(plr,"you can't leave this faction, disband it to leave.");
            return true;
        }
        faction.removeMember(plr.getUniqueId(),plr.getName());
        sendMessage(plr,"you have left the faction.");
        Player owner = Bukkit.getPlayer(faction.getMembers().getFirst());
        if (owner != null) {
            sendMessage(owner, plr.getName() + " has left the faction.");
        }
        return true;
    }

    private boolean f_kick(Player plr) {
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        String target = args[1];
        if (faction == null) {
            sendMessage(plr,"You are not in a faction!");
            return true;
        }
        boolean found = false;
        int index = 0;
        for (int i =0;i<faction.getMembers().size();i++) {
            if (faction.getMemberNames().get(i).equalsIgnoreCase(target)) {
                found = true;
                index = i;
            }
        }
        if (!found) {
            sendMessage(plr,"That player is not in your faction.");
            return true;
        }
        if (target.equalsIgnoreCase(plr.getName())) {
            sendMessage(plr, "You cannot kick yourself. Autistic piece of shit.");
            return true;
        }
        UUID plrtarget = faction.getMembers().get(index);


        if (faction.getMembers().getFirst().equals(plr.getUniqueId())) {
            sendMessage(plr,target +" has been kicked.");
            Player targetplr = Bukkit.getPlayer(target);
            faction.removeMember(plrtarget,target);
            if (targetplr != null) {
                sendMessage(targetplr,"You have been kicked from "+faction.getName());
            }
            return true;
        } else {
            sendMessage(plr,"you do not have permission to do this.");
            return true;
        }
    }

    private boolean f_home(Player plr) { //gonna mess up over dimensions, will need to revise at some point but cba
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }
        sendMessage(plr,"faction home :");
        sendMessage(plr,faction.getHome().toString());
        sendMessage(plr,"("+faction.getHome().distance(plr.getLocation()) + " blocks away)");

        return true;
    }

    private boolean f_sethome(Player plr) {
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }
        if (faction.getMembers().get(0).equals(plr.getUniqueId())) {
            faction.setHome(plr.getLocation());
            sendMessage(plr,"Home set.");
        } else {
            sendMessage(plr,"You do not have permission to set the faction home.");
        }
        return true;
    }

    private boolean f_chat(Player plr) {
        //format is /faction chat <message>
        Faction faction = getFactionFromPlayer(plr.getUniqueId());
        if (faction == null) {
            sendMessage(plr,"you are not in a faction!");
            return true;
        }


        String msg = "["+plr.getName()+"] ";

        for (int i =1;i<args.length;i++) {
            msg += args[i] + " ";
        }
        broadcastToFaction(faction,msg);
        return true;
    }

    private boolean f_colour(Player plr) {
        if (args.length!=2) {
            sendMessage(plr,"Wrong format, try /faction colour <colour>");
            return true;
        }
        //check if person is faction leader.
        Faction faction = getFactionFromPlayer(plr.getUniqueId());

        if (faction == null) {
            sendMessage(plr,"You are not in a faction!");
            return true;
        }
        if (!faction.getMembers().getFirst().equals(plr.getUniqueId())) {
            sendMessage(plr, "You do not have permission to run that command.");
            return true;
        }
        //player is in a faction, player is owner of that faction.
        String colour = args[1];
        if (colour.equalsIgnoreCase("aqua")) {
            faction.setChatColour(String.valueOf(ChatColor.AQUA));
        } else if (colour.equalsIgnoreCase("black")) {
            faction.setChatColour(String.valueOf(ChatColor.BLACK));
        } else if (colour.equalsIgnoreCase("blue")) {
            faction.setChatColour(String.valueOf(ChatColor.BLUE));
        } else if (colour.equalsIgnoreCase("dark_aqua")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_AQUA));
        } else if (colour.equalsIgnoreCase("dark_blue")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_BLUE));
        } else if (colour.equalsIgnoreCase("dark_gray") || colour.equalsIgnoreCase("dark_grey")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_GRAY));
        }  else if (colour.equalsIgnoreCase("dark_green")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_GREEN));
        }  else if (colour.equalsIgnoreCase("dark_red")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_RED));
        }  else if (colour.equalsIgnoreCase("dark_purple")) {
            faction.setChatColour(String.valueOf(ChatColor.DARK_PURPLE));
        }  else if (colour.equalsIgnoreCase("gold")) {
            faction.setChatColour(String.valueOf(ChatColor.GOLD));
        }  else if (colour.equalsIgnoreCase("gray") || colour.equalsIgnoreCase("grey")) {
            faction.setChatColour(String.valueOf(ChatColor.GRAY));
        }  else if (colour.equalsIgnoreCase("green")) {
            faction.setChatColour(String.valueOf(ChatColor.GREEN));
        }  else if (colour.equalsIgnoreCase("light_purple")) {
            faction.setChatColour(String.valueOf(ChatColor.LIGHT_PURPLE));
        }  else if (colour.equalsIgnoreCase("red")) {
           faction.setChatColour(String.valueOf(ChatColor.RED));
        }  else if (colour.equalsIgnoreCase("white")) {
            faction.setChatColour(String.valueOf(ChatColor.WHITE));
        }  else if (colour.equalsIgnoreCase("yellow")) {
            faction.setChatColour(String.valueOf(ChatColor.YELLOW));
        }

        return true;
    }

    private void saveFactions() throws IOException {
        FileWriter factionfile = new FileWriter("plugins/Factions/factions.txt");


        for (int i =0;i<factions.size();i++) {
            Faction faction = factions.get(i);
            factionfile.write(faction.getName());
            factionfile.write(System.lineSeparator());
            factionfile.write(faction.getChatColour());
            factionfile.write(System.lineSeparator());
            for (int v = 0;v<faction.getMembers().size();v++) {
                factionfile.write(faction.getMembers().get(v).toString());
                factionfile.write(System.lineSeparator());
            }
            factionfile.write("-");
            factionfile.write(System.lineSeparator());
            for (int v = 0;v<faction.getMemberNames().size();v++) {
                factionfile.write(faction.getMemberNames().get(v));
                factionfile.write(System.lineSeparator());
            }
            factionfile.write("#");
            factionfile.write(System.lineSeparator());

        }
        factionfile.close();
    }



    private ArrayList<Faction> loadFactions() throws FileNotFoundException {
        ArrayList<Faction> loadedFactions = new ArrayList<>();
        File file = new File("plugins/Factions/factions.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner factionfile = new Scanner(file);
        int LineNum = 0;
        int facNum = 0;
        String NameBuf = "";
        String ColBuf = "";
        ArrayList<UUID> MemBuf = new ArrayList<>();
        ArrayList<String> MemNameBuf = new ArrayList<>();
        Boolean dashpassed = false;
        System.out.println("LOADING FACTIONS-------");
        System.out.println(factionfile.hasNextLine());
        while (factionfile.hasNextLine()) {
            String line = factionfile.nextLine();
            System.out.println("LINE: "+line);

            if (line.equals("#")) {
                System.out.println("space found");
                System.out.println("NameBuf");
                LineNum = 0;
                facNum++;
                Faction faction = new Faction(NameBuf,MemBuf,MemNameBuf,ColBuf);
                loadedFactions.add(faction);
                MemBuf = new ArrayList<>();
                MemNameBuf = new ArrayList<>();
                dashpassed = false;
            } else {
                if (LineNum == 0) {
                    NameBuf = line;
                } else if (LineNum == 1) {
                    ColBuf = line;
                } else {
                    if (!dashpassed) {
                        if (line.equals("-")) {
                            dashpassed = true;
                        } else {
                            MemBuf.add(UUID.fromString(line));
                        }
                    } else {
                        MemNameBuf.add(line);
                    }
                }
                LineNum++;
            }
        }
        factionfile.close();
        return loadedFactions;
    }
    private void sendHelpMessage() {
        sendMessage(sender,HelpMessage);
    }



}
