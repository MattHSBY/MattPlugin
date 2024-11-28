package me.whispy.mattPlugin.factions;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatEvent implements Listener{
    private FactionManager FM;
    public ChatEvent(FactionManager FM) {
        this.FM = FM;
    }
    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Component msg = e.message();
        String str = PlainTextComponentSerializer.plainText().serialize(msg);
        msg = Component.text("");
        int currentcolour = 0;
        for (int i =0;i<str.length();i++) {
            if (str.charAt(i) == '>') {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GREEN));
                currentcolour = 1;
            } else if (str.charAt(i) == '<') {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GOLD));
                currentcolour = 2;
            } else if (currentcolour == 1) {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GREEN));

            } else if (currentcolour == 2) {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GOLD));
            } else {
                msg = msg.append(Component.text(str.charAt(i)));
            }
        }
        for (int i = 0;i<FM.getFactions().size();i++) {
            for (int v = 0;v<FM.getFactions().get(i).getMembers().size();v++) {
                if (FM.getFactions().get(i).getMembers().get(v).equals(e.getPlayer().getUniqueId())) {
                    String playerName = e.getPlayer().getName();
                    Component ogmsg = msg.color(NamedTextColor.WHITE);
                    Component prefix = Component.text("["+FM.getFactions().get(i).getName()+"]", getColour(i));
                    e.setCancelled(true);
                    Component formattedMessage = prefix.append(Component.text("<"+playerName+"> ",getNamedTextColor(String.valueOf(ChatColor.WHITE)))).append(ogmsg);
                    Bukkit.broadcast(formattedMessage);
                }
            }
        }
    }
    private NamedTextColor getNamedTextColor(String colour) {

        if (colour.equalsIgnoreCase(String.valueOf(ChatColor.AQUA))) {
            return NamedTextColor.AQUA;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.BLACK))) {
            return NamedTextColor.BLACK;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.BLUE))) {
            return NamedTextColor.BLUE;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_AQUA))) {
            return NamedTextColor.DARK_AQUA;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_BLUE)))  {
            return NamedTextColor.DARK_BLUE;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_GRAY))) {
            return NamedTextColor.DARK_GRAY;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_GREEN))) {
            return NamedTextColor.DARK_GREEN;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_RED))) {
            return NamedTextColor.DARK_RED;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.DARK_PURPLE))) {
            return NamedTextColor.DARK_PURPLE;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.GOLD))) {
            return NamedTextColor.GOLD;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.GRAY))) {
            return NamedTextColor.GRAY;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.GREEN))) {
            return NamedTextColor.GREEN;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.LIGHT_PURPLE))) {
            return NamedTextColor.LIGHT_PURPLE;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.RED))) {
            return NamedTextColor.RED;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.WHITE))) {
            return NamedTextColor.WHITE;
        } else if (colour.equalsIgnoreCase(String.valueOf(ChatColor.YELLOW))) {
            return NamedTextColor.YELLOW;
        }
        return NamedTextColor.WHITE;
    }

    private TextColor getColour(int factionNum) {
        TextColor tc = getNamedTextColor(FM.getFactions().get(factionNum).getChatColour());

        return tc;
    }

}
