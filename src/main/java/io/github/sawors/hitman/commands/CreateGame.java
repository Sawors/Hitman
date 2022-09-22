package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateGame implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    
        GameManager gm = new GameManager();
        
        sender.sendMessage("new game created : id="+gm.getId());
        if(sender instanceof Player p){
            gm.addPlayer(p);
        }
        return false;
    }
}
