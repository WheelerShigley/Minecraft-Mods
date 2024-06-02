package me.wheelershigley.graphmaker.command;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.wheelershigley.graphmaker.GraphingPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.wheelershigley.graphmaker.Creator.makeGraph;
import static me.wheelershigley.graphmaker.command.CommandHelper.PLUGIN_NAME_PREFIX;
import static me.wheelershigley.graphmaker.command.CommandHelper.startsWith;

public class GraphMaker implements TabExecutor  {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String command_name, @NotNull String[] command_arguments) {
        if(command_arguments[0].equalsIgnoreCase("reload") ) {
            GraphingPlugin.reload();
            commandSender.sendMessage(PLUGIN_NAME_PREFIX+" reloaded.");
            return true;
        }

        if(command_arguments.length < 1) { return false; }

        if(command_arguments[0].equalsIgnoreCase("digraph") && 2 <= command_arguments.length ) {
            System.out.println( command_arguments[1] );
            SlimefunItem product = Slimefun.getRegistry().getSlimefunItemIds().get( command_arguments[1].toUpperCase().replace(' ','_') );
            if(product == null) {
                commandSender.sendMessage(PLUGIN_NAME_PREFIX+"§cInvalid id, \"§4"+command_arguments[1]+"§c\".");
                return true;
            }

            try {
                makeGraph(product);
            } catch (IOException e) {
                commandSender.sendMessage(PLUGIN_NAME_PREFIX+"§c§n"+product.getId()+".dot§r§c failed to be created at §o.../plugins/"+ GraphingPlugin.instance.getName()+"§r§c." );
                return true;
            }
            commandSender.sendMessage(PLUGIN_NAME_PREFIX+"§7§n"+product.getId()+".dot§r§7 is at §o.../plugins/"+ GraphingPlugin.instance.getName()+"§r§7." );
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String command_name, @NotNull String[] command_arguments) {
        List<String> arguments = new ArrayList<>();

        if(command_arguments.length == 1) {
            String current = command_arguments[0].toLowerCase();

            String[] subcommands = new String[]{"reload","digraph"};
            for(String subcommand : subcommands) {
                if( startsWith(subcommand, current) ) { arguments.add(subcommand); }
            }
        }

        if(2 <= command_arguments.length && command_arguments[0].equalsIgnoreCase("digraph") ) {
            String current_query = (2 < command_arguments.length) ? "" : command_arguments[1].toUpperCase();
            arguments = new ArrayList<>();

            SlimefunItem[] items = Slimefun.getRegistry().getAllSlimefunItems().toArray(new SlimefunItem[0]);
            String current_name;
            for(SlimefunItem item : items) {
                current_name = item.getId();
                if( startsWith(current_name, current_query) ) { arguments.add(current_name); }
            }
        }

        return arguments;
    }
}
