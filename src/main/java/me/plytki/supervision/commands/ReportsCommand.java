package me.plytki.supervision.commands;

import me.plytki.supervision.SuperVision;
import me.plytki.supervision.managers.ReportManager;
import me.plytki.supervision.managers.UserManager;
import me.plytki.supervision.objects.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command is available only for players!");
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info"))) {
            player.sendMessage(" ");
            player.sendMessage("   §8§l«§8§m                    §8[ §bSuperVision §8]§m                    §8§l»");
            player.sendMessage(" §8§l» §b/reports §8- §7Shows all reports in a GUI");
            player.sendMessage(" §8§l» §b/reports info <player> §8- §7Shows detailed information about player's reports");
            return false;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            User otherUser = UserManager.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            if (otherUser == null) {
                player.sendMessage(SuperVision.PREFIX + "§cThis player doesn't exist in the database!");
                return false;
            }
            player.sendMessage(" ");
            player.sendMessage("   §8§l«§8§m                    §8[ §bSuperVision §8]§m                    §8§l»");
            player.sendMessage(" §8§l» §7Online: §b" + (otherUser.isOnline() ? "§aYes" : "§cNo"));
            player.sendMessage(" §8§l» §7Reported players: §b" + otherUser.getReportedPlayersCount());
            player.sendMessage(" §8§l» §7Reported by other: §b" + otherUser.getReportedByPlayersCount());
            BaseComponent hiddenIP = new TextComponent(" §8§l» §7Last seen IP Address: §8*hidden*");
            hiddenIP.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("\n  §7IP Address: " + otherUser.getLastSeenIPAddress() + "  \n")}));
            player.spigot().sendMessage(hiddenIP);
            return false;
        }
        if (ReportManager.getUnconsideredReports().size() == 0) {
            player.sendMessage(SuperVision.PREFIX + "There are no pending reports.");
            return false;
        }
        player.openInventory(ReportManager.getReportsInventory());
        ReportManager.playersInReports.add(player);
        return false;
    }

}
