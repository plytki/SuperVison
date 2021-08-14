package me.plytki.supervision.listeners;

import me.plytki.supervision.SuperVision;
import me.plytki.supervision.managers.ReportManager;
import me.plytki.supervision.objects.Report;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ReportsListener implements Listener {

    @EventHandler
    public void onReportsClick(InventoryClickEvent e) {
        Player player = ((Player) e.getWhoClicked());
        if (player == null) return;
        if (e.isCancelled()) {
            return;
        }
        if (!ReportManager.playersInReports.contains(player)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getCurrentItem() != null) {
            if (player.getInventory().getType().equals(e.getClickedInventory().getType())) return;
            if(!e.getCurrentItem().getType().equals(Material.PAPER)) return;
            ItemStack itemStack = e.getCurrentItem();
            String reportString = itemStack.getItemMeta().getDisplayName().split(" ")[0].replaceAll("#", "");
            int reportID = Integer.parseInt(reportString.substring(4));
            Report report = ReportManager.getReport(reportID);
            Player reportPlayer = Bukkit.getPlayer(report.getReportingUUID());

            if (report.getAssignedTo() != null) {
                if (report.getAssignedTo().equals(player.getUniqueId())) {
                    player.closeInventory();
                    player.openInventory(ReportManager.getConsiderInventory());
                    ReportManager.playersInConsideration.put(reportPlayer, report);
                    return;
                } else {
                    player.sendMessage(SuperVision.PREFIX + "This report is currently assigned!");
                }
                return;
            }
            player.closeInventory();
            ReportManager.playersInDecision.put(reportPlayer, report);
            player.openInventory(ReportManager.getDecisionInventory());
        }
    }

    @EventHandler
    public void onConsiderClick(InventoryClickEvent e) {
        Player player = ((Player) e.getWhoClicked());
        if (player == null) return;
        if (e.isCancelled()) {
            return;
        }
        if (ReportManager.playersInConsideration.get(player) == null) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getCurrentItem() != null) {
            if (player.getInventory().getType().equals(e.getClickedInventory().getType())) return;
            Report report = ReportManager.playersInConsideration.get(player);
            if (e.getSlot() == 2) {
                report.setSolved(true);
                report.setConsidered(true);
                Player reportingPlayer = Bukkit.getPlayer(report.getReportingUUID());
                reportingPlayer.sendMessage(SuperVision.PREFIX + "§aYour report has been signed as solved by " + player.getName() + "! §8(§7ID: " + report.getReportID() + "§8)");
                player.sendMessage(SuperVision.PREFIX + "§aReport has been signed as solved! §8(§7ID: " + report.getReportID() + "§8)");
                report.update();
            } else if(e.getSlot() == 6) {
                report.setSolved(false);
                report.setConsidered(true);
                Player reportingPlayer = Bukkit.getPlayer(report.getReportingUUID());
                reportingPlayer.sendMessage(SuperVision.PREFIX + "§cYour report has been signed as unsolved by " + player.getName() + "! §8(§7ID: " + report.getReportID() + "§8)");
                player.sendMessage(SuperVision.PREFIX + "§cReport has been signed as unsolved! §8(§7ID: " + report.getReportID() + "§8)");
                report.update();
            }
            player.closeInventory();
            if (ReportManager.getUnconsideredReports().size() > 0) {
                player.openInventory(ReportManager.getReportsInventory());
                ReportManager.playersInReports.add(player);
            }
        }
    }

    @EventHandler
    public void onDecisionClick(InventoryClickEvent e) {
        Player player = ((Player) e.getWhoClicked());
        if (player == null) return;
        if (e.isCancelled()) {
            return;
        }
        if (ReportManager.playersInDecision.get(player) == null) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getCurrentItem() != null) {
            if (player.getInventory().getType().equals(e.getClickedInventory().getType())) return;
            Report report = ReportManager.playersInDecision.get(player);
            if (e.getSlot() == 2) {
                Player reportingPlayer = Bukkit.getPlayer(report.getReportingUUID());
                report.assignReportTo(player.getUniqueId());
                player.sendMessage(SuperVision.PREFIX + "§fYou have attached yourself to " + reportingPlayer.getName() + "'s report! §8(§7ID: " + report.getReportID() + "§8)");
                reportingPlayer.sendMessage(SuperVision.PREFIX + "§aYour report is currently being solved by " + player.getName() + "! §8(§7ID: " + report.getReportID() + "§8)");
                report.update();
            } else if(e.getSlot() == 6) {
                report.setConsidered(true);
                Player reportingPlayer = Bukkit.getPlayer(report.getReportingUUID());
                reportingPlayer.sendMessage(SuperVision.PREFIX + "§cYour report has been disapproved by " + player.getName() + "! §8(§7ID: " + report.getReportID() + "§8)");
                player.sendMessage(SuperVision.PREFIX + "§cReport has been disapproved! §8(§7ID: " + report.getReportID() + "§8)");
                report.update();
            }
            player.closeInventory();
            if (ReportManager.getUnconsideredReports().size() > 0) {
                player.openInventory(ReportManager.getReportsInventory());
                ReportManager.playersInReports.add(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        ReportManager.playersInReports.remove(player);
        ReportManager.playersInConsideration.remove(player);
        ReportManager.playersInDecision.remove(player);
    }

}
