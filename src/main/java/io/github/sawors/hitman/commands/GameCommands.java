package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.GameManager;
import io.github.sawors.hitman.game.PlayerRole;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class GameCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1){
            GameManager gm = null;
            int shift = 0;
            
            
            // global commands
            switch (args[0]){
                case "list" -> {
                    StringBuilder gameids = new StringBuilder();
                    gameids.append(ChatColor.YELLOW).append("Game List :");
                    for(GameManager tolist : Hitman.getGameList()){
                        gameids.append("\n").append(ChatColor.GREEN).append(" - ").append(tolist.getId());
                    }
                    sender.sendMessage(Component.text(gameids.toString()));
                }
            }
            
            if(sender instanceof Player player && Hitman.getPlayerGameId(player.getUniqueId()) != null){
                gm = Hitman.getGameById(Hitman.getPlayerGameId(player.getUniqueId()));
            } else if(args.length >= 2 && Hitman.getGameById(args[0])!=null){
                gm = Hitman.getGameById(args[0]);
                shift++;
            }
            
            
            if(gm == null){
                sender.sendMessage(ChatColor.RED+"game not found");
                return false;
            }
    
            String subcommand = args[shift];
            switch (subcommand) {
                case "spawn" -> {
                    sender.sendMessage(ChatColor.YELLOW+"Spawning NPCs...");
                    gm.spawnNpc();
                    sender.sendMessage(ChatColor.GREEN+"NPCs successfully spawned and spread !");
                    return true;
                }
                case "join" -> {
                    if (sender instanceof Player p) {
                        gm.addPlayer(p);
                        sender.sendMessage(ChatColor.GREEN+"You joined "+gm.getId());
                    }
                    return true;
                }
                
                case "setrole" -> {
                    if(args.length >= 3+shift){
                        Player p = Bukkit.getPlayer(args[1+shift]);
                        if(p != null){
                            UUID player = Objects.requireNonNull(Bukkit.getPlayer(args[1 + shift])).getUniqueId();
                            String rolestr = args[2+shift];
                            try{
                                PlayerRole role = PlayerRole.valueOf(PlayerRole.class, rolestr.toUpperCase(Locale.ROOT));
        
                                gm.setPlayerRole(player,role);
                                return true;
                            } catch (IllegalArgumentException e){
                                sender.sendMessage(ChatColor.RED+"Role "+rolestr.toLowerCase(Locale.ROOT)+" does not exist");
                                return false;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED+"Player "+args[1+shift]+" not found");
                            return false;
                        }
                        
                    }
                }
            }
        }
        return false;
    }
}
