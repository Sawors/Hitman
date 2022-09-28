package io.github.sawors.hitman.game.sniper;

import io.github.sawors.hitman.Hitman;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class HitmanItem {
    
    List<Component> lore = new ArrayList<>();
    Component name = null;
    String id = getTypeId();
    String variant = "";
    Material mat = Material.STICK;
    
    
    public ItemStack get(){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
    
        meta.getPersistentDataContainer().set(getItemKey(), PersistentDataType.STRING,id != null ? id : getTypeId());
        if(name != null){
            meta.displayName(name);
        }
        if(variant != null){
            meta.getPersistentDataContainer().set(getVariantKey(), PersistentDataType.STRING,variant);
        }
        meta.lore(lore);
        meta.setUnbreakable(true);
        
        item.setItemMeta(meta);
        
        return item;
    }
    
    public List<Component> getLore() {
        return lore;
    }
    
    public void setLore(List<Component> lore) {
        this.lore = lore;
    }
    
    public Component getName() {
        return name;
    }
    
    public void setName(Component name) {
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getVariant() {
        return variant;
    }
    
    public void setVariant(String variant) {
        this.variant = variant;
    }
    
    public void setMaterial(Material material){
        this.mat = material;
    }
    
    public static NamespacedKey getItemKey(){
        return new NamespacedKey(Hitman.getPlugin(), "type");
    }
    
    public static NamespacedKey getVariantKey(){
        return new NamespacedKey(Hitman.getPlugin(), "variant");
    }
    
    public static String formatTextToId(String text){
        StringBuilder idformated = new StringBuilder();
        char lastchar = '/';
        for(char c : text.replaceAll(" ", "_").toCharArray()){
            if(Character.isUpperCase(c) && Character.isLowerCase(lastchar)){
                idformated.append("_");
            }
            idformated.append(Character.toLowerCase(c));
            lastchar = c;
        }
        return idformated.toString();
    }
    
    public static String formatTextToName(String text){
        StringBuilder nameformated = new StringBuilder();
        char lastchar = '/';
        for(char c : text.toCharArray()){
            if(Character.isUpperCase(c) && Character.isLowerCase(lastchar)){
                nameformated.append(" ");
            }
            nameformated.append(c);
            lastchar = c;
        }
        
        return nameformated.toString();
    }
    
    public static String getItemId(ItemStack item){
        String id = item.getType().toString().toLowerCase(Locale.ROOT);
        if(!item.hasItemMeta()){
            return id;
        }
        String hitmanid = item.getItemMeta().getPersistentDataContainer().get(getItemKey(),PersistentDataType.STRING);
        return hitmanid != null ? hitmanid : id;
    }
    
    public String getTypeId(){
        return formatTextToId(getClass().getSimpleName());
    }
}
