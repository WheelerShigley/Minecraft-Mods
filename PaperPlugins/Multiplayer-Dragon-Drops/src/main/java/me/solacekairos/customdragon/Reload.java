package me.solacekairos.customdragon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Reload implements TabExecutor {

    DeathListener listener;
    public Reload(DeathListener pass_in) {
        listener = pass_in;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if( args.length < 1 || !args[0].equalsIgnoreCase("reload") ) { return false; }

        //reload plugin
        listener.reload();

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("reload");
            return list;
        }
        return null;
    }
}
