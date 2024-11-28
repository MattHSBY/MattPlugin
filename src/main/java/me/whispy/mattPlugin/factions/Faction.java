package me.whispy.mattPlugin.factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Faction {
    //faction class should basically only serve as a storage object (convert to json data)
    //all the functions of factions (faction commands, rendering faction tags etc) can be handled by faction command classes

    //1:23:52

    //faction attributes,
    private String Name;
    private ArrayList<UUID> Members;
    private ArrayList<String> MemberNames;
    private ArrayList<UUID> Invites;
    private String ChatColour;
    private Location Home;
    //So, I'm not going to add an 'owner' or a 'leader' variable, we're just going to use Members[0], a faction *could* be
    //created by the console, but the first player added to it would effectively become the leader.

    //constructor:
    public Faction(String Name) {
        this.Name = Name;
        this.Members = new ArrayList<UUID>();
        this.MemberNames = new ArrayList<String>();
        this.Invites = new ArrayList<UUID>();
        this.ChatColour = ChatColor.AQUA.toString();//new change
    }
    public Faction(String Name, ArrayList<UUID> Members, ArrayList<String> MemberNames,String ChatColor) {
        //Invites not included on purpose, invites will be deleted on server restart.

        this.Name = Name;
        this.Members = Members;
        this.MemberNames = MemberNames;
        this.Invites = new ArrayList<UUID>();
        this.ChatColour = ChatColor;

    }

    //private methods:


    //public methods:
    public void onPlayerJoin() {

    }
    public void onPlayerLeave() {

    }
    public void onFactionLoad() {

    }

    public boolean invitePlayer(UUID plr) {
        boolean found = false;
        for (int i = 0;i<Invites.size();i++) {
            if (Invites.get(i).equals(plr)) found = true;
        }
        if (!found) {
            Invites.add(plr);
            return true;
        }
        return false;
    }

    public boolean acceptInvite(UUID plr,String name) {

        for (int i = 0;i<Invites.size();i++) {
            if (Invites.get(i).equals(plr)) {

                addMember(plr,name);
                Invites.remove(i);

                return true;
            }
        }
        return false;
    }

    public void addMember(UUID plr, String name) {
        Members.add(plr);MemberNames.add(name);
    }
    public void removeMember(UUID plr, String name) {
        for (int i =0;i<Members.size();i++) {
            if (Members.get(i).equals(plr)) {
                Members.remove(i);
                MemberNames.remove(i);
            }
        }
    }
    public void setHome(Location Pos) {

    }
    public void setChatColour(String colour) {

        this.ChatColour = colour;
    }

    public void saveToFile() {

    }


    public ArrayList<UUID> getInvites() {

        return Invites;
    }

    public List<String> getMemberNames() {

        return MemberNames;
    }

    public List<UUID> getMembers() {
        return Members;
    }

    public String getName() {
        return Name;
    }
    public Location getHome() {
        return Home;
    }
    public String getChatColour() {
        return ChatColour;
    }

}
