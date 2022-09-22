package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.GameManager;
import io.github.sawors.hitman.game.PlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SetRole implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 2){
            String player = args[0];
            String role = args[1].toLowerCase(Locale.ROOT);
            Player p = Bukkit.getPlayer(player);
            if(p != null){
                GameManager playergame = Hitman.getGameById(Hitman.getPlayerGameId(p.getUniqueId()));
                if(playergame != null){
                    PlayerRole prole = PlayerRole.NONE;
                    if(role.equals(PlayerRole.SNIPER.toString().toLowerCase(Locale.ROOT))){
                        prole = PlayerRole.SNIPER;
                    } else if(role.equals(PlayerRole.SPY.toString().toLowerCase(Locale.ROOT))){
                        prole = PlayerRole.SPY;
                    }
                    
                    if(prole == PlayerRole.SPY || prole == PlayerRole.SNIPER){
                        playergame.setPlayerRole(p.getUniqueId(), prole);
                        sender.sendMessage("Successfully gave role "+role+" to player "+player);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
