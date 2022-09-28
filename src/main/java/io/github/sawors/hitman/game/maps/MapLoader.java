package io.github.sawors.hitman.game.maps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MapLoader {
    List<GameQuest> quests = new ArrayList<>();
    File source;
    YamlConfiguration data;
    String mapname;
    String variantname;
    List<MapSpawnpoint> spyspawns = new ArrayList<>();
    //sniper
    Location sniperspawn;
    boolean sniperweapon = true;
    boolean sniperspyglass = true;
    boolean snipercctv = false;
    World world;
    // config
    // ??????
    
    public MapLoader(World w) throws NullPointerException {
        source = new File(w.getWorldFolder()+File.separator+getDataFileName());
        if(!source.exists()){
            try{
                source.createNewFile();
                getMinimalConfig().save(source);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        
        data = YamlConfiguration.loadConfiguration(source);
        if(data.options().width() <= 1){
            // empty config detected, must do something here (warning and edit mode ?)
        }
        world = w;
        loadMapData();
    }
    
    public static String getDataFileName(){
        return "hitman.yml";
    }
    
    public void loadMapData(){
        data = YamlConfiguration.loadConfiguration(source);
        
        // Loading general data
        mapname = data.getString("name");
        variantname = data.getString("variant");
    
        // Loading sniper data
        ConfigurationSection snipersection = data.getConfigurationSection("sniper");
        if(snipersection == null){
            throw new NullPointerException("sniper config section not found");
        }
        ConfigurationSection sniperspawnsection = snipersection.getConfigurationSection("spawn");
        ConfigurationSection sniperconfigsection = snipersection.getConfigurationSection("config");
        if(sniperspawnsection == null || sniperconfigsection == null){
            throw new NullPointerException("sniper config section not found");
        }
        double sx = sniperspawnsection.getDouble("x");
        double sy = sniperspawnsection.getDouble("y");
        double sz = sniperspawnsection.getDouble("z");
        sniperspawn = new Location(world,sx,sy,sz);
    
        sniperspyglass = sniperconfigsection.getBoolean("spyglass");
        sniperweapon = sniperconfigsection.getBoolean("weapon");
        snipercctv = sniperconfigsection.getBoolean("cctv-access");
    
        // Loading spy and npc data
        ConfigurationSection spawnpoints = data.getConfigurationSection("spawnpoints");
        if(spawnpoints == null){
            throw new NullPointerException("spies and npc spawnpoints not found");
        }
        Set<String> spawnset = spawnpoints.getKeys(false);
        for(String point : spawnset){
            ConfigurationSection pointdata = spawnpoints.getConfigurationSection(point);
            if(pointdata != null){
                double spx = pointdata.getDouble("x");
                double spy = pointdata.getDouble("y");
                double spz = pointdata.getDouble("z");
                int radius = pointdata.getInt("radius");
                String name = pointdata.getName();
                spyspawns.add(new MapSpawnpoint(new Location(world,spx,spy,spz), radius,name));
            }
        }
    }
    
    public static YamlConfiguration getMinimalConfig(){
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection sniper = config.createSection("sniper");
        ConfigurationSection sniperspawn = sniper.createSection("spawn");
        ConfigurationSection sniperconfig = sniper.createSection("config");
        ConfigurationSection spawnpoints = config.createSection("spawnpoints");
        ConfigurationSection quests = config.createSection("quests");
        
        
        return config;
    }
    
    public static File getWorldHitmanData(World w){
        File source = new File(w.getWorldFolder()+File.separator+getDataFileName());
        
        if(!source.exists() || !YamlConfiguration.loadConfiguration(source).getKeys(false).containsAll(getMinimalConfig().getKeys(false))){
            try{
                source.createNewFile();
                getMinimalConfig().save(source);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        
        return source;
    }
    
    public List<GameQuest> getQuests() {
        return List.copyOf(quests);
    }
    
    public String getMapname() {
        return mapname;
    }
    
    public String getVariantname() {
        return variantname;
    }
    
    public List<MapSpawnpoint> getSpySpawns() {
        return List.copyOf(spyspawns);
    }
    
    public Location getSniperspawn() {
        return sniperspawn.clone();
    }
    
    public boolean isSniperweapon() {
        return sniperweapon;
    }
    
    public boolean isSniperspyglass() {
        return sniperspyglass;
    }
    
    public boolean isSnipercctv() {
        return snipercctv;
    }
}
