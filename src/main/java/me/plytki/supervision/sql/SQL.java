package me.plytki.supervision.sql;

import me.plytki.supervision.SuperVision;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static me.plytki.supervision.SuperVision.*;

public class SQL {

    private static Connection connection;
    private static Statement statement;
    public static final long MYSQL_TIMEOUT_MS = 28800000;
    private static long lastQuery = System.currentTimeMillis();

    public static void connect() {
        try {
            if (SuperVision.getInstance().getConfig().getString("data-storage").equalsIgnoreCase("h2")) {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection("jdbc:h2:" + SuperVision.getInstance().getDataFolder().getAbsolutePath() + "\\data\\h2_svdata");
            } else {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name + "?useSSL=" + useSSL + "&autoReconnect=" + autoReconnect, db_user, db_password);
            }
            statement = connection.createStatement();
            createTables();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void createTables() {
        try {
            SQL.getStatement().execute("CREATE TABLE IF NOT EXISTS `sv_users` (`uuid` VARCHAR(36) NOT NULL, `username` VARCHAR(24) NOT NULL, `lastSeenIPAddress` VARCHAR(15) NOT NULL, `loginCount` BIGINT(17) NOT NULL, `isOnline` BOOL NOT NULL, `reportedPlayersCount` BIGINT(17) NOT NULL, `reportedByPlayersCount` BIGINT(17) NOT NULL)");
            SQL.getStatement().execute("CREATE TABLE IF NOT EXISTS `sv_reports` (`reportID` INTEGER(24) NOT NULL, `reportedUUID` VARCHAR(36) NOT NULL, `reportingUUID` VARCHAR(36) NOT NULL, `dateCreated` TIMESTAMP NOT NULL, `solved` BOOL NOT NULL, `assignedTo` VARCHAR(36) NOT NULL, `reportContent` TEXT(200) NOT NULL, `considered` BOOL NOT NULL)");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Statement getStatement() {
        try {
            if((System.currentTimeMillis() - lastQuery > MYSQL_TIMEOUT_MS) || (connection == null || statement == null || connection.isClosed() || statement.isClosed())) {
                if (connection != null) {
                    if (!connection.isClosed()) {
                        connection.close();
                        statement.close();
                    }
                }
                connect();
            }
            lastQuery = System.currentTimeMillis();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }

    public static Connection getConnection() {
        return connection;
    }
}
