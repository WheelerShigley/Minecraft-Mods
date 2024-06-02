package me.wheelershigley.graphmaker.command;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.wheelershigley.graphmaker.GraphMaker;
import static me.wheelershigley.graphmaker.Creator.makeGraph;

public class Digraph implements TabExecutor  {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String command_name, @NotNull String[] command_arguments) {
        if( !command_name.equalsIgnoreCase("digraph") || command_arguments.length < 1 ) { return false; }

        SlimefunItem product = Slimefun.getRegistry().getSlimefunItemIds().get( command_arguments[0].toUpperCase() );
        if(product == null) {
            commandSender.sendMessage(GraphMaker.instance.PLUGIN_NAME_PREFIX+"§cInvalid id, \"§4"+command_arguments[0]+"§c\".");
            return true;
        }

        try {
            makeGraph(product);
        } catch (IOException e) {
            commandSender.sendMessage(GraphMaker.instance.PLUGIN_NAME_PREFIX+"§c§n"+product.getId()+".dot§r§c failed to be created at §o.../plugins/"+GraphMaker.instance.getName()+"§r§c." );
            return true;
        }
        commandSender.sendMessage(GraphMaker.instance.PLUGIN_NAME_PREFIX+"§7§n"+product.getId()+".dot§r§7 is at §o.../plugins/"+GraphMaker.instance.getName()+"§r§7." );
        return true;
    }

    private boolean startsWith(String origin, String includesAtStart) {
        final char[] quantized_origin = origin.toCharArray();
        final char[] quantized_query = includesAtStart.toCharArray();

        //unnecessary check, speeds up some testcases
        if(quantized_origin.length < quantized_query.length) { return false; }

        for(int i = 0; i < quantized_query.length; i++) {
            if( quantized_origin[i] != quantized_query[i] ) { return false; }
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String command_name, @NotNull String[] command_arguments) {
        List<String> arguments;

        if( command_name.isEmpty() ) {
            arguments = new ArrayList<>();
            arguments.add("digraph");

            return arguments;
        }

        if( command_name.equalsIgnoreCase("digraph") ) {
            String current_query = (0 < command_arguments.length) ? command_arguments[0].toUpperCase() : "";
            arguments = new ArrayList<>();

            SlimefunItem[] items = Slimefun.getRegistry().getAllSlimefunItems().toArray(new SlimefunItem[0]);
            String current_name;
            for(SlimefunItem item : items) {
                current_name = item.getId();
                if( startsWith(current_name, current_query) ) { arguments.add(current_name); }
            }

            return arguments;
        }

        return null;
    }
}
