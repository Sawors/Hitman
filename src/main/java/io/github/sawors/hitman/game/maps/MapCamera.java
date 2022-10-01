package io.github.sawors.hitman.game.maps;


import io.github.sawors.hitman.Hitman;
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
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class MapCamera implements Listener {

    private Location loc;
    private double yaw;
    private double pitch;
    private String name;
    private MapLoader loader;

    public static HashMap<UUID, ItemStack[]> connectedplayers = new HashMap<>();
    public static HashMap<UUID, MapCamera> camlink = new HashMap<>();

    // must be created by the MapLoader
    public MapCamera(MapLoader loader, @NotNull Location camloc, double yaw, double pitch, String name){
        this.loc = camloc;
        this.yaw = yaw;
        this.pitch = pitch;
        this.name = name;
        this.loader = loader;
    }
    
    public MapCamera(){}

    public void connectPlayer(Player p){
        Block ground = loc.getBlock().getLocation().add(0,-2,0).getBlock();
        
        connectedplayers.put(p.getUniqueId(),p.getInventory().getContents());
        camlink.put(p.getUniqueId(),this);
        // ensure the player can stand on the camera
        
        if(!ground.isSolid()){
            ground.setType(Material.BARRIER);
        }
        p.showTitle(Title.title(Component.text(ChatColor.GOLD+"Connected to : "+ChatColor.GREEN+name), Component.text(ChatColor.DARK_GREEN+"Sneak to disconnect"), Title.Times.times(Duration.ofMillis(500),Duration.ofMillis(1000),Duration.ofMillis(500))));
        new BukkitRunnable(){
            final UUID keepuuid = p.getUniqueId();
            @Override
            public void run() {
                if(p.isOnline() && connectedplayers.containsKey(keepuuid)){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,30*20,0,false,false));
                    // here I add invisibility with a potion effect to allow potentials spectators/admins to use the camera without interfering with Player.setInvisible()
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 70, 0, false,false));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Hitman.getPlugin(),10,60);
        
        p.getInventory().clear();

        p.teleport(loc.getBlock().getLocation().add(.5,-1,.5));
    }
    
    public void disconnectPlayer(Player p){
        UUID id = p.getUniqueId();
        if(connectedplayers.containsKey(id)){
            
            p.teleport(loader.getSniperspawn());
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            p.getInventory().clear();
            ItemStack[] content = connectedplayers.get(id);
            if(content!= null){
                p.getInventory().setContents(connectedplayers.get(id));
            }
            
            connectedplayers.remove(id);
            camlink.remove(id);
            // TODO : disconnect player
            
        }
    }
    
    @EventHandler
    public static void blockConnectedPlayerMovements(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(connectedplayers.containsKey(p.getUniqueId()) && event.hasChangedPosition()){
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public static void sneakDisconnectPlayer(PlayerToggleSneakEvent event){
        if(event.isSneaking() && connectedplayers.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            camlink.get(event.getPlayer().getUniqueId()).disconnectPlayer(event.getPlayer());
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
    
    public MapLoader getLoader(){
        return loader;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int hashCode() {
        return (this.loc.toString()+this.loader.toString()+this.name).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapCamera cam && cam.getLocation().equals(this.loc) && cam.getLoader().getMapname().equals(this.loader.getMapname()) && cam.getName().equals(this.name);
    }
}
