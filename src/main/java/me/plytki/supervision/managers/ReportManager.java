package me.plytki.supervision.managers;

import me.plytki.supervision.SuperVision;
import me.plytki.supervision.objects.Report;
import me.plytki.supervision.objects.User;
import me.plytki.supervision.sql.SQL;
import me.plytki.supervision.utils.Compare;
import me.plytki.supervision.utils.ItemBuilder;
import me.plytki.supervision.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReportManager {

    public static HashMap<Player, Report> playersInDecision = new HashMap<>();
    public static List<Player> playersInReports = new ArrayList<>();
    public static HashMap<Player, Report> playersInConsideration = new HashMap<>();

    public static Set<Report> reports = new HashSet<>();
    public static Inventory inventory = Bukkit.createInventory(null, 54,  SuperVision.PREFIX + "§fReports");
    public static BukkitTask refreshingTask;


    // UPDATING INVENTORY
    static {
        AtomicBoolean slashed = new AtomicBoolean();
        refreshingTask = new BukkitRunnable() {
            @Override
            public void run() {
                Report[] reportsArray = getUnconsideredReports().toArray(new Report[0]);
                Arrays.sort(reportsArray, new Compare());
                slashed.getAndSet(!slashed.get());
                int slot = 0;
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, new ItemStack(Material.AIR));
                }
                for (Report report : reportsArray) {
                    User reportingUser = UserManager.getUser(report.getReportingUUID());
                    User reportedUser = UserManager.getUser(report.getReportedUUID());
                    String createdTime;
                    if((System.currentTimeMillis() - report.getDateCreated().getTime()) / 1000 < 60) {
                        createdTime = TimeUtil.parseLongToDate(System.currentTimeMillis() - report.getDateCreated().getTime(), TimeUnit.SECONDS) + " seconds ago";
                    } else if ((System.currentTimeMillis() - report.getDateCreated().getTime()) / 1000 >= 60) {
                        createdTime = TimeUtil.parseLongToDate(System.currentTimeMillis() - report.getDateCreated().getTime(), TimeUnit.MINUTES) + " minutes ago";
                    } else {
                        createdTime = TimeUtil.parseLongToDate(System.currentTimeMillis() - report.getDateCreated().getTime(), TimeUnit.HOURS) + " hours ago";
                    }
                    inventory.setItem(slot++, new ItemBuilder(Material.PAPER).setTitle("§8#§7" + report.getReportID() + " §8§l| §f" + reportingUser.getUsername() + " §8§l» §f" + reportedUser.getUsername())
                            .addLore("§8» §7Assigned to: §f" + (report.getAssignedTo() == null ? "§c§m    " :  "§a" + UserManager.getUser(report.getAssignedTo()).getUsername()))
                            .addLore("§8» §7Reason: §f" + report.getReportContent())
                            .addLore("§8» §7Created: §f" + createdTime)
                            .addLore(" ")
                            .addLore("     §f" + (slashed.get() ? "\\" : "/") + "  §fAwaiting for " + (report.getAssignedTo() != null ? "consideration" : "assignment") + "...  " + (slashed.get() ? "\\" : "/") + "     ")
                            .addLore(" ").build());
                }
                for (HumanEntity viewer : inventory.getViewers()) {
                    ((Player) viewer).updateInventory();
                }
            }
        }.runTaskTimer(SuperVision.getInstance(), 20, 20);
    }

    public static void loadReports() {
        try {
            ResultSet rs = SQL.getStatement().executeQuery("SELECT * FROM `sv_reports`");
            while(rs.next()) {
                reports.add(new Report(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Set<Report> getUnconsideredReports() {
        Set<Report> unconsideredReports = new HashSet<>();
        for (Report report : reports) {
            if (!report.isConsidered()) {
                unconsideredReports.add(report);
            }
        }
        return unconsideredReports;
    }

    public static Report getReport(int id) {
        for (Report report : reports) {
            if (report.getReportID() == id) {
                return report;
            }
        }
        return null;
    }

    public static Inventory getReportsInventory() {
        return inventory;
    }

    public static Report createReport(UUID reported, UUID reporting, String content) {
        Report report = new Report(reported, reporting, content);
        reports.add(report);
        return report;
    }

    public static Inventory getConsiderInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, SuperVision.PREFIX + "§fConsideration");
        inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)13).setTitle(" ").build());
        inventory.setItem(2, new ItemBuilder(Material.STAINED_CLAY, 1,(short)5).setTitle("§aSolved").build());
        inventory.setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)13).setTitle(" ").build());
        inventory.setItem(5, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)14).setTitle(" ").build());
        inventory.setItem(6, new ItemBuilder(Material.STAINED_CLAY, 1,(short)14).setTitle("§cUnsolved").build());
        inventory.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)14).setTitle(" ").build());
        return inventory;
    }


    public static Inventory getDecisionInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, SuperVision.PREFIX + "§fDecision");
        inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)13).setTitle(" ").build());
        inventory.setItem(2, new ItemBuilder(Material.STAINED_CLAY, 1,(short)5).setTitle("§aAssign").build());
        inventory.setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)13).setTitle(" ").build());
        inventory.setItem(5, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)14).setTitle(" ").build());
        inventory.setItem(6, new ItemBuilder(Material.STAINED_CLAY, 1,(short)6).setTitle("§cDisapprove").build());
        inventory.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE, 1,(short)14).setTitle(" ").build());
        return inventory;
    }

}
