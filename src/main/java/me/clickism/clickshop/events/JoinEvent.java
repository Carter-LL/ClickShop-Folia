package me.clickism.clickshop.events;

import me.clickism.clickshop.Main;
import me.clickism.clickshop.Util.FoliaCompat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        FoliaCompat.runPlayerRegion(
                Main.getMain(),
                player,
                () -> Main.getMain().getShopManager().sendWarning(player),
                20L
        );
    }

}
