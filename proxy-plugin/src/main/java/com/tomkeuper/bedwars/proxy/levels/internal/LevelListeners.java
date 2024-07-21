package com.tomkeuper.bedwars.proxy.levels.internal;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LevelListeners implements Listener {

    public static LevelListeners instance;

    public LevelListeners() {
        instance = this;
    }

    //create new level data on player join
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            final UUID u = e.getPlayer().getUniqueId();
            // create empty level first
            new PlayerLevel(u);
            Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
                Object[] levelData = BedWarsProxy.getRemoteDatabase().getLevelData(u);
                if (levelData.length == 0) return;
                PlayerLevel.getLevelByPlayer(u).lazyLoad((Integer) levelData[0], (Integer) levelData[1], (String) levelData[2], (Integer)levelData[3]);
            });
        }catch (Exception error){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&e[BedWarsProxy] &cERROR! &f+ "+error.getMessage()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        final UUID u = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            PlayerLevel pl = PlayerLevel.getLevelByPlayer(u);
            pl.destroy();
        });
    }
}
