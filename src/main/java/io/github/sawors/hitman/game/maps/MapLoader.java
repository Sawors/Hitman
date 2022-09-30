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
    private List<GameQuest> quests = new ArrayList<>();
    private File source;
    private YamlConfiguration data;
    private String mapname;
    private String variantname;
    private List<MapSpawnpoint> spyspawns = new ArrayList<>();
    //sniper
    private Location sniperspawn;
    private boolean sniperweapon = true;
    private boolean sniperspyglass = true;
    private boolean snipercctv = false;
    private World world;

    private List<MapCamera> cams = new ArrayList<>();

    // config
    // ??????


    //DECI
    // One World -> one hitman map
    //      Better instantiation management & better memory usage -> smaller partitions
    //      Easier to install and manage, small files and easier to isolate specifically one map
    //      Allow for better and more performant player detection : Player.getWorld() will always point to the true map the player is on and not multiple possible maps
    // Decision taken the 30.09.2022 by Sawors
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
        // TODO : CCTV system
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

        // loading camera data
        // TOTEST
        //  camera loading and if loaded camera work
        ConfigurationSection cameras = data.getConfigurationSection("cameras");
        if(cameras != null){
            for(String key : cameras.getKeys(false)){
                ConfigurationSection camsection = cameras.getConfigurationSection(key);
                // useless check here since we are looping through EXISTING keys
                if(camsection != null){
                    double x = camsection.getDouble("x");
                    double y = camsection.getDouble("y");
                    double z = camsection.getDouble("z");
                    Location camloc = new Location(world,x,y,z);
                    double pitch = camsection.getDouble("pitch");
                    double yaw = camsection.getDouble("yaw");
                    String name = camsection.getString("name");

                    cams.add(new MapCamera(this, camloc,yaw,pitch,name));
                }
            }
        }
    }

    // TODO : maybe here instead of creating the reference by hand we should use the map-config-example.yml (plugin resource) as a reference for configs (or at least create a reference as a .yml file in the resources)
    public static YamlConfiguration getMinimalConfig(){
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection sniper = config.createSection("sniper");
        ConfigurationSection sniperspawn = sniper.createSection("spawn");
        ConfigurationSection sniperconfig = sniper.createSection("config");
        ConfigurationSection spawnpoints = config.createSection("spawnpoints");
        ConfigurationSection quests = config.createSection("quests");
        ConfigurationSection camera = config.createSection("cameras");
        
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

    public List<MapCamera> getCameras(){
        return List.copyOf(cams);
    }
}
