package io.github.sawors.hitman.game.maps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    List<GameQuest> quests = new ArrayList<>();
    File source;
    FileConfiguration data;
    String mapname;
    String variantname;
    
    MapLoader(File mapdirectory){
        if(mapdirectory.isDirectory()){
            source = new File(mapdirectory+File.separator+getDataFileName());
            try{
                source.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            
            data = YamlConfiguration.loadConfiguration(source);
        }
    }
    
    
    
    
    public static String getDataFileName(){
        return "hitman.yml";
    }
    
}
