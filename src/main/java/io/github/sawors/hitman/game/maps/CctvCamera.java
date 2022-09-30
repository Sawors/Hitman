package io.github.sawors.hitman.game.maps;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CctvCamera implements Listener {

    private Location loc;
    private double yaw;
    private double pitch;

    public static List<UUID> connectedplayers = new ArrayList<>();

    // must be created by the MapLoader
    public CctvCamera(@NotNull Location camloc, double yaw, double pitch){
        this.loc = camloc;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void connectPlayer(Player p){
        Block ground = loc.getBlock().getLocation().add(0,-1,0).getBlock();
        // ensure the player can stand on the camera
        if(!ground.isSolid()){
            ground.setType(Material.BARRIER);
        }

        connectedplayers.add(p.getUniqueId());
        p.teleport(loc);
    }

    @EventHandler
    public static void blockConnectedPlayerMovements(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(connectedplayers.contains(p.getUniqueId()) && event.hasChangedPosition()){
            event.setCancelled(true);
        }
    }

    public void disconnectPlayer(Player p){
        if(connectedplayers.contains(p.getUniqueId())){
            // TODO : disconnect player
        }
    }

    public Location getLocation() {
        return loc.clone();
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}
