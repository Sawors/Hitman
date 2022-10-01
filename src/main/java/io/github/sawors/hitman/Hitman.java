package io.github.sawors.hitman;

import io.github.sawors.hitman.commands.*;
import io.github.sawors.hitman.game.GameManager;
import io.github.sawors.hitman.game.maps.MapCamera;
import io.github.sawors.hitman.game.sniper.SniperListeners;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public final class Hitman extends JavaPlugin {
    
    private static JavaPlugin instance;
    private static HashMap<String, GameManager> gamelist = new HashMap<>();
    private static HashMap<UUID, String> playerlink = new HashMap<>();
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(new SniperListeners(),this);
        getServer().getPluginManager().registerEvents(new GameCommands(),this);
        getServer().getPluginManager().registerEvents(new MapCamera(),this);
        try{
            Objects.requireNonNull(getServer().getPluginCommand("setrole")).setExecutor(new SetRole());
            Objects.requireNonNull(getServer().getPluginCommand("creategame")).setExecutor(new CreateGame());
            Objects.requireNonNull(getServer().getPluginCommand("giveitem")).setExecutor(new GiveItem());
            Objects.requireNonNull(getServer().getPluginCommand("editmap")).setExecutor(new EditMap());
            Objects.requireNonNull(getServer().getPluginCommand("game")).setExecutor(new GameCommands());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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
    
    public static void registerNewGame(GameManager game){
        gamelist.put(game.getId(),game);
    }
    
    public static void unregisterGame(String gameid){
        gamelist.remove(gameid);
    }
    
    public static @Nullable GameManager getGameById(String id){
        return gamelist.get(id);
    }
    
    public static void linkPlayer(UUID playerid, String gameid){
        playerlink.put(playerid,gameid);
    }
    
    public static void unlinkPlayer(UUID playerid){
        playerlink.remove(playerid);
    }
    
    public static @Nullable String getPlayerGameId(UUID playerid){
        return playerlink.get(playerid);
    }
    
    public static @Nullable GameManager getPlayerGame(UUID playerid){
        
        return gamelist.get(playerlink.get(playerid));
    }
    
    public static List<GameManager> getGameList(){
        return List.copyOf(gamelist.values());
    }
}
