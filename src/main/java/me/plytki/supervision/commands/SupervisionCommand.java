package me.plytki.supervision.commands;

import me.plytki.supervision.SuperVision;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidPluginException;

public class SupervisionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(SuperVision.PREFIX + "Correct usage: §b/supervision reload");
            return false;
        }
        try {
            SuperVision.getInstance().reloadPlugin(sender);
        } catch (InvalidPluginException e) {
            e.printStackTrace();
            sender.sendMessage(SuperVision.PREFIX + "§cReload error!");
        }
        return false;
    }

}
