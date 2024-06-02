package me.solacekairos.turtlesplus.turtle;

import me.solacekairos.turtlesplus.Turtles_Plus;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

public class Drops implements Listener {

    int i, roll_count, drop_count;
    Ageable maybe_baby;
    Random prng = new Random();

    //instance variables
    boolean change_drops = false;
    int roll_maximum = 0;
    double probability = 0.5;
    Material drop = Material.SEAGRASS;
    int drop_count_maximum = 1;

    private void setProbability(double value) {
        double positive_value = Math.abs(value);
        if( positive_value <= 1.0 ) { probability = positive_value;
        } else { probability = positive_value - (int)positive_value; }
    } //good
    private void setMaterial(String name) {
        String temporary_name = name.toUpperCase();

        if( temporary_name.equals("TURTLE_HELMET") || temporary_name.equals("TURTLE_SHELL") ) { drop = Material.TURTLE_HELMET; return; }
        if( temporary_name.equals("SCUTE") ) { drop = Material.SCUTE; return; }
        drop = Material.SEAGRASS;
    } //good
    private void setDropMaximum(int quantity) {
        if(quantity < 0) { drop_count_maximum = 0b01111111111111111111111111111111; /*maximum (int)*/ }
        drop_count_maximum = quantity;
    } //good

    public Drops(Turtles_Plus plugin) {
        reloadDrops(plugin);
    }

    public void reloadDrops(Turtles_Plus plugin) {
        int previous_roll_maximum = roll_maximum,
            previous_total_maximum = drop_count_maximum;
        //boolean did_drop = change_drops;
        Material previous_material = drop;
        double previous_probability = probability;

        change_drops = plugin.getConfig().getBoolean("change_turtles_drops");
        roll_maximum = plugin.getConfig().getInt("maximum_per_roll"        );
        setDropMaximum( plugin.getConfig().getInt("total_maximum")       );
        setProbability( plugin.getConfig().getDouble("drop_probability") );
        setMaterial(    plugin.getConfig().getString("drop_material")    );

        //compare with previous values, output if changes occured
        if ( change_drops && ( (previous_roll_maximum != roll_maximum) || !(previous_material.equals(drop) ) || (previous_probability != probability) || (previous_total_maximum != drop_count_maximum) ) ) {
            plugin.improved_turtles_logger.info("Turtles drop "+100*probability+"% "+ roll_maximum + " "+drop.toString()+" per roll: [0,"+drop_count_maximum+"].");
        }
        if (!change_drops) {
            plugin.improved_turtles_logger.info("Turtles drop [0,2] seagrass again (Vanilla)."); }

        if(change_drops) { plugin.improved_turtles_logger.info("Turtles now drop "+drop.toString()+" @ "+roll_maximum+" per roll."); }
    }

    @EventHandler
    public void onTurtleKill(EntityDeathEvent death) {
        //only enable if configured to be enabled
        if(!change_drops) { return; }

        //ensure entity is adult turtle
        if(death.getEntity().getType() != EntityType.TURTLE) { return; }
        if(death.getEntity() instanceof Ageable) {
            maybe_baby = (Ageable)death.getEntity();
            if( !maybe_baby.isAdult() ) { return; }
        }

        //roll_count = looting_level + 1;
        roll_count = 1 + death.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        if(roll_count < 1) { roll_count = 1; }
        //if(10 < roll_count) { roll_count = 10; } //maximum looting level + 1 ?

        //add drops, DO NOT add eggs as drop: they cracked through violence
        double linear_probability = ( (double)prng.nextInt() ) / ( (double)0b01111111111111111111111111111111 );

        ItemStack is_drop = new ItemStack(drop);
        drop_count = 0; for(i = 0; i < roll_count; i++) { if(linear_probability <= probability) { drop_count++; }  } //+ P(chance) every roll
        if(drop_count_maximum < drop_count) { drop_count = drop_count_maximum; }
        is_drop.setAmount(drop_count);

        death.getDrops().clear(); death.getDrops().add(is_drop);
    }

}
