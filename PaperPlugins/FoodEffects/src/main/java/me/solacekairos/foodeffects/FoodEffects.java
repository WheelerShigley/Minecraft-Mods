package me.solacekairos.foodeffects;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//inspired by: https://github.com/OzzyMar-DEV/Glow-Berries-Reloaded/blob/master/src/main/java/com/github/ozzymar/glowberriesreloaded/GlowBerriesReloaded.java
public final class FoodEffects extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Added food effects!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Food effects lost.");
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        // Considering adding healing to beetroot soup?
        if( event.getItem().getType() != Material.GLOW_BERRIES ) { return; }
        //if( event.getPlayer().hasPotionEffect( PotionEffectType.GLOWING) ) { return; } //unsure if this is needed

        event.getPlayer().addPotionEffect( new PotionEffect( PotionEffectType.GLOWING, 15 * 20 /*15 seconds*/, 0 /*level 1*/, false, false ) );
    }
}
