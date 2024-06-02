package me.solacekairos.customdragon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import static java.lang.System.currentTimeMillis;

public class DeathListener implements Listener {

    CustomDragon instance;
    Random rand;
    public DeathListener(CustomDragon pull_in) {
        instance = pull_in;
        rand = new Random( currentTimeMillis() );

        reload();
    }

    Location drop_location;
    Material[] drop_materials;
    double[] probabilities;
    boolean do_individual_drops;
    int exp_drop_quantity;
    double maximum_distance;
    public void reload() {
        instance.reloadConfig();

        @NotNull List<Integer> position = instance.getConfig().getIntegerList("drop-location");
        drop_location = new Location( instance.getServer().getWorld( instance.getConfig().getString("dimension-name") ), position.get(0),position.get(1),position.get(2) );

        @NotNull List<String> item_names = instance.getConfig().getStringList("drop-items");
        drop_materials = new Material[ item_names.size() ];
        for(int i = 0; i < item_names.size(); i++) {
            drop_materials[i] = Material.getMaterial( item_names.get(i) );
        }

        @Nullable List<Double> probability_list = instance.getConfig().getDoubleList("drop-probabilities");
        int larger; if( drop_materials.length < probability_list.size() ) { larger = probability_list.size(); } else { larger = drop_materials.length; }
        probabilities = new double[larger];
        for(int i = 0; i < larger; i++) { probabilities[i] = probability_list.get(i); }
        for(int i = probabilities.length; i < larger; i++) { probabilities[i] = 1.0; } //default to 100%

        do_individual_drops = instance.getConfig().getBoolean("do-individual-exp-drops");
        exp_drop_quantity = instance.getConfig().getInt("exp-drop-quantity");

        maximum_distance = instance.getConfig().getDouble("maximum-distance-for-rewards");

    }

    //pythagereous (3D) method of distance
    private double getDistance(Location a, Location b) {
        if( a.getWorld() != b.getWorld() ) { return Double.MAX_VALUE; }
        return Math.sqrt( ( a.getX()-b.getX() )*( a.getX()-b.getX() ) + ( a.getY()-b.getY() )*( a.getY()-b.getY() ) + ( a.getZ()-b.getZ() )*( a.getZ()-b.getZ() ) );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void entityDeathEventHandler(EntityDeathEvent event) {
        //verify correct death
        if(event.getEntityType() != EntityType.ENDER_DRAGON) { return; }
        event.setDroppedExp(0);

        Location dragon = event.getEntity().getLocation();
        //get all players nearby the dragon, give each a reward
        Bukkit.getOnlinePlayers().forEach(p -> {
            double distance = getDistance( p.getLocation(), dragon );
            if( do_individual_drops && ( maximum_distance == Double.MAX_VALUE || distance < maximum_distance ) ) {
                //summon EXP rewards here
                Entity orb = p.getWorld().spawnEntity( p.getLocation(), EntityType.EXPERIENCE_ORB );
                ( (ExperienceOrb)orb ).setExperience(exp_drop_quantity);
                //summon ITEM rewards here
                ItemStack item = new ItemStack( Material.AIR );
                for(int i = 0; i < drop_materials.length; i++) {
                    //determine if it should drop
                    if( probabilities[i] < rand.nextDouble(1.0) ) { continue; }

                    item.setType( drop_materials[i] );
                    p.getWorld().dropItemNaturally( p.getLocation(), item );
                    //System.out.println( "Dropped(local) item "+ drop_materials[i].toString() +" at " + p.getLocation() );
                }
            }
        });

        //summon over-all reward (if required)
        if(!do_individual_drops) {
            World world = event.getEntity().getWorld();
            //summon EXP here
            Entity orb = world.spawnEntity( drop_location, EntityType.EXPERIENCE_ORB );
            ( (ExperienceOrb)orb ).setExperience(exp_drop_quantity);
            //summon ITEMS here
            ItemStack item = new ItemStack( Material.AIR );
            for(int i = 0; i < drop_materials.length; i++) {
                //determine if it should drop
                if( probabilities[i] < rand.nextDouble(1.0) ) { continue; }

                item.setType( drop_materials[i] );
                world.dropItemNaturally( drop_location, item );
                //System.out.println( "Dropped item "+ drop_materials[i].toString() +" at " + event.getEntity().getLocation().toString() );
            }
        }

        return;
    }
}
