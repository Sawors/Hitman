package io.github.sawors.hitman.game.maps;


import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.sniper.items.SniperSpyglass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class MapCamera implements Listener {

    private Location loc;
    private double yaw;
    private double pitch;
    String name;
    MapLoader loader;

    public static HashMap<UUID, PlayerInventory> connectedplayers = new HashMap<>();

    // must be created by the MapLoader
    public MapCamera(MapLoader loader, @NotNull Location camloc, double yaw, double pitch, String name){
        this.loc = camloc;
        this.yaw = yaw;
        this.pitch = pitch;
        this.name = name;
        this.loader = loader;
    }

    public void connectPlayer(Player p){
        Block ground = loc.getBlock().getLocation().add(0,-1,0).getBlock();
        // TOTEST
        //  player registering and teleportation
        connectedplayers.put(p.getUniqueId(),p.getInventory());
        // ensure the player can stand on the camera
        // TOTEST
        //  fall prevention
        if(!ground.isSolid()){
            ground.setType(Material.BARRIER);
        }
        // TOTEST
        //  title and night vision
        p.showTitle(Title.title(Component.text(ChatColor.GOLD+"Connected to : "+ChatColor.GREEN+name), Component.text("")));
        new BukkitRunnable(){
            final UUID keepuuid = p.getUniqueId();
            @Override
            public void run() {
                if(p.isOnline() && connectedplayers.containsKey(keepuuid)){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,25,0,false,false));
                    // here I add invisibility with a potion effect to allow potentials spectators/admins to use the camera without interfering with Player.setInvisible()
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 25, 0, false,false));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Hitman.getPlugin(),10,20);

        // here I do this to allow for a "camera zoom" but maybe another item more suited to a camera could be better (like for instance a
        // sort of "CameraSpyglass" item
        // IF ever I add an ability to sniper spyglass I will modifiy this and add its own item to the camera
        // TOTEST
        //  inventory clearing and spyglass giving
        p.getInventory().clear();
        p.getInventory().setItem(0, new SniperSpyglass().get());

        p.teleport(loc);
    }


    // TOTEST
    //   Does this check work
    @EventHandler
    public static void blockConnectedPlayerMovements(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(connectedplayers.containsKey(p.getUniqueId()) && event.hasChangedPosition()){
            event.setCancelled(true);
        }
    }

    public void disconnectPlayer(Player p){
        UUID id = p.getUniqueId();
        if(connectedplayers.containsKey(id)){
            connectedplayers.remove(id);

            // TOTEST
            //  check if all the "going back from camera" protocol works
            p.teleport(loader.getSniperspawn());
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            p.getInventory().setContents(connectedplayers.get(id).getContents());



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
