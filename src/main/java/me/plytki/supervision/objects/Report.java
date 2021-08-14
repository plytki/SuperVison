package me.plytki.supervision.objects;

import me.plytki.supervision.SuperVision;
import me.plytki.supervision.managers.ReportManager;
import me.plytki.supervision.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class Report {

    private int reportID;
    private UUID reportedUUID;
    private UUID reportingUUID;
    private Timestamp dateCreated;
    private boolean solved;
    private UUID assignedTo;
    private String reportContent;
    private boolean considered;

    public Report(UUID reportedUUID, UUID reportingUUID, String reportContent) {
        this.reportID = ReportManager.reports.size() + 1;
        this.reportedUUID = reportedUUID;
        this.reportingUUID = reportingUUID;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
        this.reportContent = reportContent;
        announceReport();
        insert();
    }

    public Report(ResultSet resultSet) {
        try {
            this.reportID = resultSet.getInt("reportID");
            this.reportedUUID = UUID.fromString(resultSet.getString("reportedUUID"));
            this.reportingUUID = UUID.fromString(resultSet.getString("reportingUUID"));
            this.dateCreated = resultSet.getTimestamp("dateCreated");
            this.solved = resultSet.getBoolean("solved");
            this.assignedTo = resultSet.getString("assignedTo").isEmpty() ? null : UUID.fromString(resultSet.getString("assignedTo"));
            this.reportContent = resultSet.getString("reportContent");
            this.considered = resultSet.getBoolean("considered");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void announceReport() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("supervision.alerts")) {
                player.sendMessage(SuperVision.PREFIX + "§aNew report has appeared! §8(§7ID: " + this.getReportID() + "§8)");
            }
        }
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public UUID getReportedUUID() {
        return reportedUUID;
    }

    public void setReportedUUID(UUID reportedUUID) {
        this.reportedUUID = reportedUUID;
    }

    public UUID getReportingUUID() {
        return reportingUUID;
    }

    public void setReportingUUID(UUID reportingUUID) {
        this.reportingUUID = reportingUUID;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void assignReportTo(UUID uuid) {
        assignedTo = uuid;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public boolean isConsidered() {
        return considered;
    }

    public void setConsidered(boolean considered) {
        this.considered = considered;
    }

    private void insert() {
        try{
            SQL.getStatement().execute("INSERT INTO `sv_reports` (`reportID`, `reportedUUID`, `reportingUUID`, `dateCreated`, `solved`, `assignedTo`, `reportContent`, `considered`)"
                    + " VALUES" +
                    " ('" + this.reportID + "', '" + this.reportedUUID.toString() + "', '" + this.reportingUUID.toString() + "', '" + this.dateCreated + "', '" + false + "', '" + "" + "', '" + this.reportContent + "', '" + false + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try{
            SQL.getStatement().execute("UPDATE `sv_reports` SET " +
                    "`reportedUUID`='" + this.reportedUUID.toString() + "'," +
                    "`reportingUUID`='" + this.reportingUUID.toString() + "'," +
                    "`dateCreated`='" + this.dateCreated + "'," +
                    "`solved`='" + this.solved + "'," +
                    "`assignedTo`='" + (this.assignedTo == null ? "" : this.assignedTo.toString()) + "'," +
                    "`reportContent`='" + this.reportContent + "'," +
                    "`considered`='" + this.considered + "'" +
                    " WHERE `reportID`='" + this.reportID +"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
