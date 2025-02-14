package me.solacekairos.dualshells;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

//Based on tgb20's sheller: https://github.com/tgb20/DoubleShulkerShells/blob/master/src/main/java/tools/tgb/DoubleShulkerShells/DoubleShulkerShells.java
public class DualShells extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Shulkers drop two shells (when killed by player) now!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Shulker drops re-unmodified.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        Entity killer = event.getEntity().getKiller();
        Entity entity = event.getEntity();

        if(killer instanceof Player && entity.getType() == EntityType.SHULKER) {
            event.getDrops().clear();
            ItemStack shulkerStack = new ItemStack(Material.SHULKER_SHELL, 2);
            event.getEntity().getWorld().dropItemNaturally(entity.getLocation(), shulkerStack);
        }
    }
}
