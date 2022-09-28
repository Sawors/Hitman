package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.maps.MapLoader;
import io.github.sawors.hitman.game.maps.MapSpawnpoint;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditMap implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1 && sender instanceof Player player){
            File worldconfigfile = MapLoader.getWorldHitmanData(player.getWorld());
            YamlConfiguration worldconfig = YamlConfiguration.loadConfiguration(worldconfigfile);
            Location blockloc = player.getLocation().getBlock().getLocation();
            double x = blockloc.getX()+0.5;
            double y = blockloc.getY();
            double z = blockloc.getZ()+0.5;
            switch(args[0]){
                case "setspawn" -> {
                    if(args.length >= 2){
                        String category = args[1];
                        if(category.equals("sniper")){
                            // setting sniper spawn-point
                            ConfigurationSection sniper = worldconfig.getConfigurationSection("sniper");
                            if(sniper != null){
                                ConfigurationSection sniperspawn = sniper.getConfigurationSection("spawn");
                                if(sniperspawn!=null){
                                    
                                    sniperspawn.set("x",x);
                                    sniperspawn.set("y",y);
                                    sniperspawn.set("z",z);
                                    sender.sendMessage(Component.text(ChatColor.GREEN+"spawnpoint (sniper) set to "+x+" "+y+" "+z));
                                    try {
                                        worldconfig.save(worldconfigfile);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } else {
                            // setting NPCs and spies spawn-points
                            ConfigurationSection spawnpoints = worldconfig.getConfigurationSection("spawnpoints");
                            
                            if(spawnpoints != null){
                                
                                String radstring = "1";
                                int radius = 1;
                                if(args.length >= 3){
                                    radstring = args[2];
                                }
                                try{
                                    radius = Integer.parseInt(radstring);
                                } catch (NumberFormatException ignored){}
                                
                                if(radius >= 0){
                                    sender.sendMessage(Component.text(ChatColor.YELLOW+"spawnpoint "+category+" successfully removed !"));
                                    return true;
                                }
                                if(spawnpoints.getKeys(false).contains(category)){
                                    sender.sendMessage(Component.text(ChatColor.YELLOW+"spawnpoint "+category+" has already been set, changing its position"));
                                }
                                
                                ConfigurationSection point = spawnpoints.createSection(category);
                                point.set("x",x);
                                point.set("y",y);
                                point.set("z",z);
                                point.set("radius",radius);
                                
                                sender.sendMessage(Component.text(ChatColor.GREEN+"spawnpoint "+ChatColor.AQUA+category+ChatColor.GREEN+" (spies) set to "+x+" "+y+" "+z+" with radius "+radius));
                                Location center = new Location(player.getWorld(),x,y,z);
                                final int fradius = radius;
                                new BukkitRunnable(){
                                    int count = 3;
                                    @Override
                                    public void run() {
                                        if(count <= 0){
                                            this.cancel();
                                            return;
                                        }
    
                                        for(int i = -fradius; i <= fradius; i++){
                                            for(int i2 = -fradius; i2 <= fradius; i2++){
                                                if(Math.abs(i) == fradius || Math.abs(i2) == fradius){
                                                    center.getWorld().spawnParticle(Particle.REDSTONE,center.clone().add(i,.5,i2),8,0,0,0,0, new Particle.DustOptions(Color.ORANGE,1));
                                                } else {
                                                    center.getWorld().spawnParticle(Particle.REDSTONE,center.clone().add(i,.25,i2),8,.25,0,.25,0, new Particle.DustOptions(Color.YELLOW,.75f));
                                                }
                                            }
                                        }
                                        
                                        count--;
                                    }
                                }.runTaskTimer(Hitman.getPlugin(),0,20);
                                
                                
                                try {
                                    worldconfig.save(worldconfigfile);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                
                                return true;
                            }
                        }
                    }
                }
                case"listspawns" -> {
                    List<MapSpawnpoint> points = new ArrayList<>();
                    worldconfig = YamlConfiguration.loadConfiguration(worldconfigfile);
                    ConfigurationSection spawns = worldconfig.getConfigurationSection("spawnpoints");
                    StringBuilder builder = new StringBuilder();
                    if(spawns != null){
                        builder.append(ChatColor.YELLOW).append("Spawnpoints for map ").append(player.getWorld().getName()).append(" :");
                        for(String point : spawns.getKeys(false)){
                            builder.append("\n").append(ChatColor.GREEN).append(" - ").append(point);
                        }
                    }
                }
            }
        }
        return false;
    }
}
