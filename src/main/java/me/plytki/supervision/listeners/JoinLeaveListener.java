package me.plytki.supervision.listeners;

import me.plytki.supervision.managers.UserManager;
import me.plytki.supervision.objects.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        if(user == null) {
            user = UserManager.createUser(player.getUniqueId(), player.getName());
        }
        user.setLastSeenIPAddress(e.getPlayer().getAddress().getAddress().getHostAddress());
        user.loggedIn();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        assert user != null;
        user.loggedOut();
        user.update();
    }

}
