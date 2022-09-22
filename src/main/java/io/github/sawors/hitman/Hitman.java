package io.github.sawors.hitman;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public final class Hitman extends JavaPlugin {
    
    private static JavaPlugin instance;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        
        saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    
    public static JavaPlugin getPlugin(){
        return instance;
    }
    
    public static void logAdmin(TextComponent msg){
        Bukkit.getLogger().log(Level.INFO, "["+getPlugin().getName()+"] "+msg.content().replaceAll("§e", ""));
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.isOp()){
                p.sendMessage(ChatColor.YELLOW+"[DEBUG] "+getTimeText()+" : "+msg.content().replaceAll("§e", ""));
            }
        }
    }
    
    public static void logAdmin(String msg){
        logAdmin(Component.text(ChatColor.YELLOW+msg));
    }
    public static void logAdmin(Object msg){
        logAdmin(Component.text(ChatColor.YELLOW+msg.toString()));
    }
    
    static String getTimeText(){
        LocalDateTime time = LocalDateTime.now();
        return "["+time.getDayOfMonth()+"."+time.getMonthValue()+"."+time.getYear()+" "+time.getHour()+":"+time.getMinute()+":"+time.getSecond()+"]";
    }
}
