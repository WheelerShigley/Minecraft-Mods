package me.solacekairos.campfireeffects.command;

import me.solacekairos.campfireeffects.CampfireEffects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class reload implements TabExecutor {

    CampfireEffects occurance;
    public reload(CampfireEffects plugin) {
        this.occurance = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> possible_autocompletions = new ArrayList<>();
        if(args.length == 1) { possible_autocompletions.add("reload"); }

        return possible_autocompletions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1 || 2 < args.length) { sender.sendMessage("§cInsufficient arguments, try \"/campfires reload\"."); return true; /*fake false*/ }

        //reload if argument is "reload" or "r"
        if( args[0].equals("reload") || args[0].equals("r") ) {

            occurance.reloadConfig();
            occurance.placeEvent.reloadCampfires(occurance);

            if(sender instanceof Player) { sender.sendMessage("Reloaded campfires!"); }
            occurance.campfire_effects_logger.info("Reloaded config.");
            return true;
        }

        return true; /*fake false*/
    }
}
