package me.plytki.supervision.objects;

import me.plytki.supervision.sql.SQL;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private String lastSeenIPAddress;
    private int loginCount;
    private boolean isOnline;
    private int reportedPlayersCount;
    private int reportedByPlayersCount;

    public User(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.loginCount = 1;
        this.isOnline = true;
        insert();
    }

    public User(ResultSet resultSet) {
        try {
            this.uuid = UUID.fromString(resultSet.getString("uuid"));
            this.username = resultSet.getString("username");
            this.lastSeenIPAddress = resultSet.getString("lastSeenIPAddress");
            this.loginCount = resultSet.getInt("loginCount");
            this.isOnline = resultSet.getBoolean("isOnline");
            this.reportedPlayersCount = resultSet.getInt("reportedPlayersCount");
            this.reportedByPlayersCount = resultSet.getInt("reportedByPlayersCount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastSeenIPAddress() {
        return lastSeenIPAddress;
    }

    public void setLastSeenIPAddress(String lastSeenIPAddress) {
        this.lastSeenIPAddress = lastSeenIPAddress;
    }

    public int getLoginCount() {
        return this.loginCount;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public int getReportedPlayersCount() {
        return reportedPlayersCount;
    }

    public int getReportedByPlayersCount() {
        return reportedByPlayersCount;
    }

    public void reportedByPlayer(Player player) {
        this.reportedByPlayersCount = ++reportedByPlayersCount;
    }

    public void reportedPlayer(Player player) {
        this.reportedPlayersCount = ++reportedPlayersCount;
    }

    public void loggedIn() {
        loginCount++;
        isOnline = true;
    }

    public void loggedOut() {
        isOnline = false;
    }

    private void insert() {
        try{
            SQL.getStatement().execute("INSERT INTO `sv_users` (`uuid`, `username`, `lastSeenIPAddress`, `loginCount`, `isOnline`, `reportedPlayersCount`, `reportedByPlayersCount`)"
                    + " VALUES" +
                    " ('" + this.uuid.toString() + "', '" + this.username + "', '" + this.lastSeenIPAddress + "', '" + this.loginCount + "', '" + this.isOnline + "', '" + this.reportedPlayersCount + "', '" + this.reportedPlayersCount + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try{
            SQL.getStatement().execute("UPDATE `sv_users` SET " +
                    "`username`='" + this.username + "'," +
                    "`lastSeenIPAddress`='" + this.lastSeenIPAddress + "'," +
                    "`loginCount`='" + this.loginCount + "'," +
                    "`isOnline`='" + this.isOnline + "'," +
                    "`reportedPlayersCount`='" + this.reportedPlayersCount + "'," +
                    "`reportedByPlayersCount`='" + this.reportedByPlayersCount + "'" +
                    " WHERE `uuid`='" + this.uuid.toString() +"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
