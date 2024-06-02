package me.wheelershigley.graphmaker;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.wheelershigley.graphmaker.command.GraphMaker;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class GraphingPlugin extends JavaPlugin implements SlimefunAddon {
    public static GraphingPlugin instance;

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

        reload();
        /*Register Commands*/ {
            this.getCommand("graphmaker").setExecutor(     new GraphMaker() );
            this.getCommand("graphmaker").setTabCompleter( new GraphMaker() );
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

    public static HashMap<String,String> name_id_association = new HashMap<>();
    public static HashMap<String,String> id_name_association = new HashMap<>();
    public static void reload() {
        List<SlimefunItem> items = Slimefun.getRegistry().getAllSlimefunItems();

        String current_name, current_id;
        for(SlimefunItem item : items) {
            current_name = PlainTextComponentSerializer.plainText().serialize( item.getItem().displayName() ); current_name = current_name.substring(1,current_name.length()-1);
            current_id = item.getId();

            name_id_association.put(current_name,current_id);
            id_name_association.put(current_id,current_name);
        }
    }

}
