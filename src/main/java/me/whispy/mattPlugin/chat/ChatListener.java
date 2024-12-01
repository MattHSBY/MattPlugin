package me.whispy.mattPlugin.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whispy.mattPlugin.factions.Faction;
import me.whispy.mattPlugin.factions.FactionManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener, ChatRenderer {
    private FactionManager FM;


    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        String[] array = message.split(" ");
        if (array[0].equalsIgnoreCase("/whisper")
                || array[0].equalsIgnoreCase("/w")
                || array[0].equalsIgnoreCase("/dm")
                || array[0].equalsIgnoreCase("/msg")
                || array[0].equalsIgnoreCase("/message")//[msg, w, dm, message, r, reply]
                || array[0].equalsIgnoreCase("/r")
                || array[0].equalsIgnoreCase("/reply")
        ) {


        }
    }


    public ChatListener(FactionManager FM) {
        this.FM = FM;
    }
    @EventHandler
    public void onChat(AsyncChatEvent e) {
        e.renderer(this);
    }

    private Component greentext(Component message) {
        String str = PlainTextComponentSerializer.plainText().serialize(message);

        Component msg = Component.text("");
        int currentcolour = 0;
        for (int i =0;i<str.length();i++) {
            if (str.charAt(i) == '>') {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GREEN));
                currentcolour = 1;
            } else if (str.charAt(i) == '<') {
                msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GOLD));
                currentcolour = 2;
            } else {
                if (currentcolour == 1) {
                    msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GREEN));

                } else if (currentcolour == 2) {
                    msg = msg.append(Component.text(str.charAt(i),NamedTextColor.GOLD));
                } else {
                    msg = msg.append(Component.text(str.charAt(i)));
                }
            }
        }
        message = msg;
        return message;
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        message = greentext(message);
        Component newmessage = Component.text("");
        List<Faction> factions = FM.getFactions();
        Faction playerfaction = null;
        for (int i= 0;i<factions.size();i++) {
            Faction faction = factions.get(i);
            List<UUID> members = faction.getMembers();
            for (int v =0;v<members.size();v++) {
                UUID member = members.get(v);
                if (member.equals(source.getUniqueId())) {
                    playerfaction = faction;
                }
            }
        }
        if (playerfaction != null) {
            newmessage = Component.text("["+playerfaction.getName()+"]", getNamedTextColor(playerfaction.getChatColour()));
        }
        newmessage = newmessage.append(Component.text("<",NamedTextColor.WHITE));
        newmessage = newmessage.append(sourceDisplayName.color(NamedTextColor.WHITE));
        newmessage = newmessage.append(Component.text("> ", NamedTextColor.WHITE));
        message = newmessage.append(message.color(NamedTextColor.WHITE));
        return message;
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
