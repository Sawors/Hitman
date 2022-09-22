package io.github.sawors.hitman.game;

import io.github.sawors.hitman.Hitman;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {
    
    private List<UUID> playerlist = new ArrayList<>();
    private String gameid;
    // map data loading
    private List<Location> npcspawns = new ArrayList<>();
    private List<GameQuest> questlist = new ArrayList<>();
    
    public GameManager() {
        super();
        gameid = RandomStringUtils.randomNumeric(8);
        Hitman.registerNewGame(this);
    }
    
    public void start(){
    
    }
    
    public void addPlayer(Player p){
        playerlist.add(p.getUniqueId());
    }
    
    public void removePlayer(Player p){
        playerlist.remove(p.getUniqueId());
    }
    
    public String getId(){
        return gameid;
    }
}
