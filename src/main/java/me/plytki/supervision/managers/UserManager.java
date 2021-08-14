package me.plytki.supervision.managers;

import me.plytki.supervision.objects.User;
import me.plytki.supervision.sql.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    public static Set<User> users = new HashSet<>();

    public static void loadUsers() {
        try {
            ResultSet rs = SQL.getStatement().executeQuery("SELECT * FROM `sv_users`");
            while(rs.next()) {
                users.add(new User(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static User getUser(UUID uuid) {
        for (User user : users) {
            if(user.getUuid().equals(uuid)) {
                return user;
            }
        }
        return null;
    }

    public static User createUser(UUID uuid, String username) {
        User user = new User(uuid, username);
        users.add(user);
        return user;
    }

}
