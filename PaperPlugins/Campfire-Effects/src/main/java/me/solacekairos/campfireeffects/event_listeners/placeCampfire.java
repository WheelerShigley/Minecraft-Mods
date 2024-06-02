package me.solacekairos.campfireeffects.event_listeners;

import me.solacekairos.campfireeffects.CampfireEffects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class placeCampfire implements Listener {
    int radius;
    CampfireEffects plugin;

    public placeCampfire(CampfireEffects plugin) {
        this.plugin = plugin;
        reloadCampfires(plugin);
    }

    public void reloadCampfires(CampfireEffects plugin) {
        radius = plugin.getConfig().getInt("effect-radius");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceCampfire(BlockPlaceEvent placement) {
        Block placed_block = placement.getBlock(); World world = placed_block.getWorld();
        //ensure block is campfire
        Material type = placed_block.getType();
        if(type != Material.CAMPFIRE && type != Material.SOUL_CAMPFIRE) { return; }

        Entity cloud = null; Location temp_place = new Location(world, placed_block.getX(), placed_block.getY()-radius-1, placed_block.getZ() );
        for(int i = 0; i <= 2*radius; i++) {
            temp_place.setY( temp_place.getY()+1 );
            summonCloud(world, cloud, temp_place);
        }
    }

    private void summonCloud(World world, Entity cloud, Location place) {
        cloud = world.spawnEntity(place, EntityType.AREA_EFFECT_CLOUD);

        AreaEffectCloud effect_cloud = (AreaEffectCloud)cloud;
        effect_cloud.setRadius(radius+1);
        effect_cloud.setGlowing(true);
        effect_cloud.setParticle( Particle.BLOCK_CRACK, Material.AIR.createBlockData() );

        FixedMetadataValue meta = new FixedMetadataValue(plugin, "campfire");
        effect_cloud.setMetadata(plugin.plugin_name, meta );

        effect_cloud.setDuration(1893414528); //three years
        effect_cloud.setBasePotionData( new PotionData(PotionType.REGEN) );
    }
}
