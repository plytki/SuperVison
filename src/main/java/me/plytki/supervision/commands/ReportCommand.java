package me.plytki.supervision.commands;

import me.plytki.supervision.SuperVision;
import me.plytki.supervision.managers.ReportManager;
import me.plytki.supervision.managers.UserManager;
import me.plytki.supervision.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ReportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command is available only for players!");
            return false;
        }
        if(args.length < 2) {
            sender.sendMessage(SuperVision.PREFIX + "Correct usage: §b/report <player> <reason>");
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        Player reportedPlayer = Bukkit.getPlayer(args[0]);
        if(reportedPlayer == null) {
            player.sendMessage("§cYou can't report offline player!");
            return false;
        }
        if (ReportManager.getUnconsideredReports().size() >= 54) {
            player.sendMessage(SuperVision.PREFIX + "§cReports list is currently full!");
            return false;
        }
        User reportedUser = UserManager.getUser(reportedPlayer.getUniqueId());
        user.reportedPlayer(reportedPlayer);
        reportedUser.reportedByPlayer(player);
        String content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (content.length() > 26) {
            player.sendMessage(SuperVision.PREFIX + "§cYour report reason is too long. Please shorten your text!");
            return false;
        }
        player.sendMessage(SuperVision.PREFIX + "§aYou have successfully reported " + reportedPlayer.getName() + "!");
        ReportManager.createReport(reportedPlayer.getUniqueId(), player.getUniqueId(), content);
        return false;
    }

}
