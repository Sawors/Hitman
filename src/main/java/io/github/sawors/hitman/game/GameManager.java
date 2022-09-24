package io.github.sawors.hitman.game;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.maps.GameQuest;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameManager {
    
    private HashMap<UUID, PlayerRole> playermap = new HashMap<>();
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
        playermap.put(p.getUniqueId(), PlayerRole.NONE);
        Hitman.linkPlayer(p.getUniqueId(),this.getId());
    }
    
    public void removePlayer(Player p){
        playermap.remove(p.getUniqueId());
        Hitman.unlinkPlayer(p.getUniqueId());
    }
    
    public void setPlayerRole(UUID player, PlayerRole role){
        playermap.put(player,role);
    }
    
    public PlayerRole getPlayerRole(UUID player){
        return playermap.get(player);
    }
    
    public String getId(){
        return gameid;
    }
}
