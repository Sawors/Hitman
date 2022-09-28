package io.github.sawors.hitman.game.maps;

import org.bukkit.Location;

public class MapSpawnpoint {
    
    private Location loc;
    private int radius;
    private String name;
    
    public MapSpawnpoint(Location location, int radius, String name){
        this.loc = location;
        this.radius = radius;
        this.name = name;
    }
    
    public Location getLoc() {
        return loc;
    }
    
    public void setLoc(Location loc) {
        this.loc = loc;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
