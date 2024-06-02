package me.solacekairos.campfireeffects.event_listeners;

import me.solacekairos.campfireeffects.CampfireEffects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.List;

public class breakCampfire implements Listener {
    int radius;
    CampfireEffects plugin;
    BukkitScheduler scheduler;

    public breakCampfire(CampfireEffects plugin) {
        this.plugin = plugin;
        reloadCampfires(plugin);
        scheduler = Bukkit.getScheduler();
    }

    public void reloadCampfires(CampfireEffects plugin) {
        radius = plugin.getConfig().getInt("effect-radius");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent interaction) {
        Player person = interaction.getPlayer();
        Material active_item = person.getInventory().getItemInMainHand().getType();
        if(     active_item != Material.WOODEN_SHOVEL && active_item != Material.STONE_SHOVEL &&
                active_item != Material.GOLDEN_SHOVEL && active_item != Material.IRON_SHOVEL &&
                active_item != Material.DIAMOND_SHOVEL && active_item != Material.NETHERITE_SHOVEL) { return; }

        Block block = interaction.getClickedBlock(); if(block == null) { return; }
        Material material = block.getType(); Action click = interaction.getAction();
        if( (material == Material.CAMPFIRE || material == Material.SOUL_CAMPFIRE) && click == Action.RIGHT_CLICK_BLOCK ) {
            Lightable camp_block = (Lightable)block.getBlockData();
            if(!camp_block.isLit()) { return; }

            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(plugin, () -> {
                removeAssociatedClouds( interaction, interaction.getClickedBlock() );
            }, 1L);
        }
    }

    private void removeIfExtinguished(Lightable block, PlayerInteractEvent interaction) {
        if( block.isLit() ) { return; }
        removeAssociatedClouds( interaction, interaction.getClickedBlock() );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakCampfire(BlockBreakEvent breaking) {
        removeAssociatedClouds( breaking, breaking.getBlock() );
    }

    private void removeAssociatedClouds(Event event, Block block) {
        int size; Location broken_location = block.getLocation(); Location temp;
        Block placed_block = block; World world = placed_block.getWorld();
        //ensure block is campfire
        Material type = placed_block.getType();
        if(type != Material.CAMPFIRE && type != Material.SOUL_CAMPFIRE) { return; }

        //kill all nearby area_effect_clouds with an unnatural duration
        List<Entity> nearbyEntites = (List<Entity>)world.getNearbyEntities(placed_block.getLocation(), 0, radius+1, 0);
        Entity entity = null;
        for(int i = 0; i < nearbyEntites.size(); i++) {
            entity = nearbyEntites.get(i);
            if( entity instanceof AreaEffectCloud ) {
                temp = entity.getLocation(); size = entity.getMetadata(plugin.plugin_name).size();
                if( temp.getX() != broken_location.getX() || temp.getZ() != broken_location.getZ() ) { size = -1; }
                if( 0 < size && ( (String)entity.getMetadata(plugin.plugin_name).get(0).value() ).equals("campfire") ) { entity.remove(); }
            }
        }
    }
}
