package io.github.sawors.hitman.game;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.maps.GameQuest;
import io.github.sawors.hitman.game.maps.MapLoader;
import io.github.sawors.hitman.game.maps.MapSpawnpoint;
import io.github.sawors.hitman.game.sniper.items.SniperRifle;
import io.github.sawors.hitman.game.sniper.items.SniperSpyglass;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameManager {
    
    private HashMap<UUID, PlayerRole> playermap = new HashMap<>();
    private String gameid;
    // map data loading
    private List<Location> npcspawns = new ArrayList<>();
    private List<GameQuest> questlist = new ArrayList<>();
    private MapLoader mapdata;
    private List<Entity> npclist = new ArrayList<>();
    
    public GameManager() {
        super();
        gameid = RandomStringUtils.randomNumeric(8);
        Hitman.registerNewGame(this);
    }
    
    public void start(){

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // DO SOMETHING FOR INSTANTIATION HERE !!!!!!!!!!!
        // one map should never be able to be used by multiple different GameManager instances
        // 2 Options :
        //      - create a temp copy of the map (NEVER touch the reference map) and play on it (cool but very resource intensive)
        //        must implement some kind of limitation otherwise we'll find ourselves with 10k map instances
        //              -> dynamically create maps instances
        //      - allow for a maximum of games per map to be played and create BEFORE the game starts multiple map copies
        //              -> create in advance multiple maps instances

        spawnNpc();
        spawnSniper();
    }
    
    public void spawnNpc(){
        mapdata.loadMapData();
        List<LivingEntity> spies = new ArrayList<>();
        for(Map.Entry<UUID, PlayerRole> entry : playermap.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (entry.getValue().equals(PlayerRole.SPY) && p != null) {
                spies.add(p);
            }
        }
        for(int i1 = 0; i1 < 4; i1++){
            World w = mapdata.getSpySpawns().get(0).getLoc().getWorld();
            spies.add(mapdata.getSpySpawns().get(0).getLoc().getWorld().spawn(w.getSpawnLocation(), ArmorStand.class));
        }
        List<Location> spawnlocs = new ArrayList<>();
        for(MapSpawnpoint point : mapdata.getSpySpawns()){
            Location center = point.getLoc();
            int radius = point.getRadius();
            int npcamount = Math.max(0,Math.min((radius*radius*4)/9,512));
            for(int i = -radius; i <= radius; i++){
                for(int i2 = -radius; i2 <= radius; i2++){
                    Location tocheck = center.clone().add(i,0,i2);
                    if(
                            tocheck.clone().add(0,-1,0).getBlock().isSolid()
                            && tocheck.getBlock().isPassable()
                            && tocheck.clone().add(0,1,0).getBlock().isPassable()
                    ) {
                        spawnlocs.add(tocheck);
                    }
                }
            }
            Collections.shuffle(spawnlocs);

            int playeramount = spies.size();
            int totalspawns = npcamount+playeramount;

            // spawning the players first to ensure they are at least spawned
            if(spawnlocs.size() >= totalspawns){
                // moving players
                for(int i = 0; i<playeramount; i++) {
                    spies.get(i).teleport(spawnlocs.get(i));
                }
                // spawning NPCs
                for(int i = 1; i<=npcamount; i++) {
                    npclist.add(center.getWorld().spawnEntity(spawnlocs.get(playeramount+i), EntityType.VILLAGER));
                }
            } else {
                Hitman.logAdmin("NPC spawns failed : not enough suitable spawn locations found ! ("+spawnlocs.size()+" Locations for "+playeramount+" Players)");
            }
        }
    }

    public void spawnSniper(){
        Player sniper = null;

        for(Map.Entry<UUID, PlayerRole> entry : playermap.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (entry.getValue().equals(PlayerRole.SNIPER) && p != null) {
                sniper = p;
            }
        }

        if(sniper != null){
            // Teleport to sniper spawn
            sniper.teleport(mapdata.getSniperspawn());
            // Give items
            sniper.getInventory().setItem(0, new SniperRifle().get());
            sniper.getInventory().setItem(1, new SniperSpyglass().get());
            sniper.getInventory().setItem(9, new ItemStack(Material.ARROW));
        } else {
            Hitman.logAdmin(ChatColor.RED+"Error : No sniper in the game");
        }
    }
    
    public void despawnNpc(){
        for(Entity e : npclist){
            switch(e.getType()){
                case PLAYER -> {
                    e.teleport(mapdata.getSniperspawn());
                }
                case VILLAGER -> {
                    e.remove();
                }
                case ARMOR_STAND -> {
                    e.setGlowing(true);
                }
            }
        }
    }
    
    public void setMap(MapLoader maploader){
        this.mapdata = maploader;
    }
    
    public void setMap(World world){
        setMap(new MapLoader(world));
    }
    
    public MapLoader getMap(){
        return mapdata;
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
    
    public static NamespacedKey getGameManagerIdKey(){
        return new NamespacedKey(Hitman.getPlugin(),"gamemanager");
    }
}
