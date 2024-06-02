package me.wheelershigley.graphmaker;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.wheelershigley.graphmaker.command.Digraph;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.logging.Logger;

public final class GraphMaker extends JavaPlugin implements SlimefunAddon {
    public final String PLUGIN_NAME_PREFIX = "§r§9[§2"+this.getName()+"§9]§7:§r ";
    public static GraphMaker instance;

    @Override
    public void onEnable() {
        instance = this;
        /*Log Startup*/ {
            String padding = "      ";
            String startup_message = padding+this.getName()+" v"+this.getPluginVersion()+padding;

            char[] cap = new char[startup_message.length()]; Arrays.fill(cap,'#');
            padding = new String(cap);

            Logger log = this.getLogger();
                log.info(padding);
                log.info("");
                log.info(startup_message);
                log.info("");
                log.info(padding);
        }

        /*Register Command*/ {
            this.getCommand("digraph").setExecutor(     new Digraph() );
            this.getCommand("digraph").setTabCompleter( new Digraph() );
        }
    }

    @Override
    public void onDisable() {}

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() { return this; }

    @Nullable
    @Override
    public String getBugTrackerURL() { return null; }
}
