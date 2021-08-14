package me.plytki.supervision;

import me.plytki.supervision.commands.ReportCommand;
import me.plytki.supervision.commands.ReportsCommand;
import me.plytki.supervision.commands.SupervisionCommand;
import me.plytki.supervision.listeners.JoinLeaveListener;
import me.plytki.supervision.listeners.ReportsListener;
import me.plytki.supervision.managers.ReportManager;
import me.plytki.supervision.managers.UserManager;
import me.plytki.supervision.objects.User;
import me.plytki.supervision.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class SuperVision extends JavaPlugin {

    public static final String PREFIX = "§b§lSV §8§l» §7";
    private static SuperVision instance;

    public static String db_host, db_port, db_user, db_password, db_name;
    public static boolean useSSL, autoReconnect;

    @Override
    public void onEnable() {
        instance = this;
        loadData();
        registerCommands();
        registerListeners();
    }

    public void reloadPlugin(CommandSender sender) throws InvalidPluginException {
        sender.sendMessage(PREFIX + "§aReloading supervision.");
        this.reloadConfig();
        sender.sendMessage(PREFIX + "§aReload complete!");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = UserManager.getUser(player.getUniqueId());
            if (user != null) {
                user.update();
            }
        }
        try {
            SQL.getStatement().execute("SHUTDOWN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SuperVision getInstance() {
        return instance;
    }

    private void registerCommands() {
        this.getCommand("supervision").setExecutor(new SupervisionCommand());
        this.getCommand("report").setExecutor(new ReportCommand());
        this.getCommand("reports").setExecutor(new ReportsCommand());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        this.getServer().getPluginManager().registerEvents(new ReportsListener(), this);
    }

    private void loadData() {
        File file = new File(this.getDataFolder().getAbsolutePath(), "data");
        file.mkdir();
        saveDefaultConfig();
        saveConfig();
        db_host = this.getConfig().getString("mysql.host");
        db_port = this.getConfig().getString("mysql.port");
        db_name = this.getConfig().getString("mysql.db_name");
        db_user = this.getConfig().getString("mysql.db_user");
        db_password = this.getConfig().getString("mysql.db_password");
        useSSL = this.getConfig().getBoolean("mysql.useSSL");
        autoReconnect = this.getConfig().getBoolean("mysql.autoReconnect");
        SQL.connect();
        UserManager.loadUsers();
        ReportManager.loadReports();
    }

}
