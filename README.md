#MattPlugin
This plugin was developed by me - Matt.
This plugin is for paper servers 1.21, it was developed for a private 1.21.3 SMP server.
The plugin has functionality for cosmetic factions, some chat functions and will (probably) eventually also include 'vanity-hats' functionality.

#How 2 use
##Building the plugin
Create a new folder and open a terminal in that location (if on windows, click on the path bar at the top of file explorer, and replace the path text with the text 'cmd'.) Type : `git clone https://github.com/MattHSBY/MattPlugin.git` (have git installed also lol),
the build will be in the folder `build/libs`. Alternatively, just download the latest release.
##Installation
Put jar in the plugins folder of paper server, donezo.
##Commands and stuffs
###factions:
Command is /faction, shortcut /f,
- /f create <Faction_Name> : create a faction with given name.
- /f invite <Player> : invite a player, only works if you are in a faction and also the owner of said faction.
- /f accept <Faction_Name> : accept an invitation from a faction if you have one from that faction -- also i just remember i should probably look at the or error messages in this command because I think maybe it might tell you if a faction exists by telling you you don't have an invite.
- /f decline <Faction_Name> : remove invitation from that faction - will tell owner if you do (same for accept)
- /f list (shortcut : /fl) : list all players in your faction.
- /f disband : delete faction bai bai - owner only command.
- /f leave : leave a faction if you're in it (owner can't leave has to disband)
- /f kick <Player> : kick a player from your faction if they are currently in it.
- /f sethome : get location from the player then store it into the faction - can be retrieved with /f home by any player.
- /f home : tells you location set by /f sethome. (any player in faction)
- /f chat <message> (shortcut : /fc) : send a message to only your faction.
- /f colour <ChatColor> : changes the colour of your faction chat tag.

###Chat functions
- /whisper <Player> <message> (shortcuts : /w, /msg, /message, /dm) : message a specific player a specific message (Yes, this looks exactly the same as the default whisper system, but it's my code running it)
- /reply <message> (shortcut : /r) : reply to the last person who messaged you with the message.
- > <message> : greentext, everything after (and including) a '>' in any message becomes coloured green.
- < <message> : orangetext, everything after (and including) a '<' in any message becomes coloured orange.
