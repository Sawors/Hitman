package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.GameManager;
import io.github.sawors.hitman.game.PlayerRole;
import io.github.sawors.hitman.game.maps.MapCamera;
import io.github.sawors.hitman.game.maps.MapLoader;
import io.github.sawors.hitman.game.sniper.HitmanItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class GameCommands implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1){
            GameManager gm = null;
            int shift = 0;
            
            
            // global commands
            switch (args[0]){
                case "list" -> {
                    StringBuilder gameids = new StringBuilder();
                    gameids.append(ChatColor.YELLOW).append("Game List :");
                    for(GameManager tolist : Hitman.getGameList()){
                        gameids.append("\n").append(ChatColor.GREEN).append(" - ").append(tolist.getId());
                    }
                    sender.sendMessage(Component.text(gameids.toString()));
                }
            }
            
            if(sender instanceof Player player && Hitman.getPlayerGameId(player.getUniqueId()) != null){
                gm = Hitman.getGameById(Hitman.getPlayerGameId(player.getUniqueId()));
            } else if(args.length >= 2 && Hitman.getGameById(args[0])!=null){
                gm = Hitman.getGameById(args[0]);
                shift++;
            }
            
            
            if(gm == null){
                sender.sendMessage(ChatColor.RED+"game not found");
                return false;
            }
    
            String subcommand = args[shift];
            switch (subcommand) {
                case "spawn" -> {
                    sender.sendMessage(ChatColor.YELLOW+"Spawning NPCs...");
                    gm.spawnNpc();
                    sender.sendMessage(ChatColor.GREEN+"NPCs successfully spawned and spread !");
                    return true;
                }
                case "despawn" -> {
                    gm.despawnNpc();
                    sender.sendMessage(ChatColor.YELLOW+"NPCs removed and players moved back to lobby");
                }
                case "join" -> {
                    if (sender instanceof Player p) {
                        gm.addPlayer(p);
                        sender.sendMessage(ChatColor.GREEN+"You joined "+gm.getId());
                    }
                    return true;
                }
                
                case "setrole" -> {
                    if(args.length >= 3+shift){
                        Player p = Bukkit.getPlayer(args[1+shift]);
                        if(p != null){
                            UUID player = Objects.requireNonNull(Bukkit.getPlayer(args[1 + shift])).getUniqueId();
                            String rolestr = args[2+shift];
                            try{
                                PlayerRole role = PlayerRole.valueOf(PlayerRole.class, rolestr.toUpperCase(Locale.ROOT));
        
                                gm.setPlayerRole(player,role);
                                sender.sendMessage(ChatColor.GREEN+"Role \n"+rolestr.toLowerCase(Locale.ROOT)+"\n has been given to player "+p.getName());
                                return true;
                            } catch (IllegalArgumentException e){
                                sender.sendMessage(ChatColor.RED+"Role "+rolestr.toLowerCase(Locale.ROOT)+" does not exist");
                                return false;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED+"Player "+args[1+shift]+" not found");
                            return false;
                        }
                        
                    }
                }
                
                case "cameras" -> {
                    if(sender instanceof Player p){
                        // fixed inventory size, CHANGE THAT
                        Inventory camsmenu = Bukkit.createInventory(p, 9*2);
                        MapLoader map = gm.getMap();
                        for(MapCamera cam : map.getCameras()){
                            String camid = map.getCameraId(cam);
                            String camname = cam.getName();
                            if(camid != null){
                                ItemStack camselector = new ItemStack(Material.ENDER_EYE);
                                ItemMeta meta = camselector.getItemMeta();
                                
                                String itemidentifier = getCameraSelectorIdentifier();
                                meta.getPersistentDataContainer().set(HitmanItem.getItemKey(), PersistentDataType.STRING,itemidentifier);
                                meta.getPersistentDataContainer().set(HitmanItem.getVariantKey(), PersistentDataType.STRING,camid);
                                
                                meta.displayName(Component.text(ChatColor.RED+"Camera : "+camname));
    
                                camselector.setItemMeta(meta);
                                
                                camsmenu.addItem(camselector);
                            }
                        }
                        
                        p.openInventory(camsmenu);
                    }
                }
                case "start" -> {
                    gm.start();
                }
            }
        }
        return false;
    }
    
    
    public static String getCameraSelectorIdentifier(){
        return "snipercameraselector";
    }
    
    @EventHandler
    public static void cameraSelect(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        if(clicked != null && clicked.getType().equals(Material.ENDER_EYE) && clicked.hasItemMeta() && HitmanItem.getItemId(clicked).equals(getCameraSelectorIdentifier()) && event.getWhoClicked() instanceof Player p){
            event.setCancelled(true);
            
            String camid = clicked.getItemMeta().getPersistentDataContainer().get(HitmanItem.getVariantKey(),PersistentDataType.STRING);
            GameManager gm = Hitman.getPlayerGame(p.getUniqueId());
            if(camid != null && gm != null){
                MapCamera cam = gm.getMap().getCamera(camid);
                if(cam != null){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Hitman.getPlugin(),() -> {
                        p.closeInventory();
                        cam.connectPlayer(p);
                    });
                }
            }
        }
    }
}
