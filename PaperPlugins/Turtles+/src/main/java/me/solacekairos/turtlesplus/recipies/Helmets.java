package me.solacekairos.turtlesplus.recipies;

import com.google.common.collect.Multimap;
import me.solacekairos.turtlesplus.Turtles_Plus;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
import static org.bukkit.inventory.EquipmentSlot.HEAD;

public class Helmets implements Listener {
    Turtles_Plus plugin;
    final String plugin_name = "improvedturtles"; //lowercase!

    //instance variables
    boolean enable_diamond_upgrade = true;
    boolean enable_netherite_upgrades = true;

    public Helmets(Turtles_Plus plugin) {
        this.plugin = plugin;
        reloadHelmets(plugin);
    }

    public void reloadHelmets(Turtles_Plus plugin) {
        boolean did_diamond = enable_diamond_upgrade,
                did_netherite = enable_netherite_upgrades;

        enable_diamond_upgrade =    plugin.getConfig().getBoolean("enable_diamond_turtle_helmets"   );
        enable_netherite_upgrades = plugin.getConfig().getBoolean("enable_netherite_turtle_helmets" );

        if(did_diamond && !enable_diamond_upgrade)      { plugin.improved_turtles_logger.info("Turtle Shells are now upgradable to Diamond Shells.");                }
        if(!did_diamond && enable_diamond_upgrade)      { plugin.improved_turtles_logger.info("Turtle Shells are nolonger upgradable to Diamond Shells.");           }
        if(did_netherite && !enable_netherite_upgrades) { plugin.improved_turtles_logger.info("Turtle/Diamond Shells are now upgradable to Netherite Shells.");      }
        if(!did_netherite && enable_netherite_upgrades) { plugin.improved_turtles_logger.info("Turtle/Diamond Shells are nolonger upgradable to Netherite Shells."); }
    }

    @EventHandler
    void onSmithingTableEvent(PrepareSmithingEvent smith) {

        String prefix = "§r§b", name = "Turtle Shell", persistence = "", value = "";
        boolean enable = false;
        double armor = 2.0, toughness = 0.0, knockback_resistence = 0.0; //defaults

        //get items for recipe
        SmithingInventory inventory = smith.getInventory();
        ItemStack item = inventory.getItem(0);
        ItemStack modifier = inventory.getItem(1);

        //ensure items are valid
        if(item == null || modifier == null) { return; }
        if(item.getType() != Material.TURTLE_HELMET) { return; }
        ItemMeta meta = item.getItemMeta();

        //get current name; then, attributes and PDC
        if( meta.hasDisplayName() ) { name = meta.getDisplayName(); }
        {
            String temp = "";
            boolean save_continuous = false;
            for(int i = 0; i < name.length(); i++) { //§ \/
                if( !(save_continuous || name.charAt(i) == '&') ) { temp += name.charAt(i); //save_continuous = true;
                } else { i++; }
                //if(save_continuous) { temp += name.charAt(i); }
            }
            name = temp;
        }

        Multimap<Attribute, AttributeModifier> modifications = meta.getAttributeModifiers();
        if( modifications != null && !modifications.isEmpty() ) {
            Collection<AttributeModifier> collection = modifications.values();
            for( AttributeModifier local_am : collection) {
                switch( local_am.getName() ) {
                    case "Generic.Armor":               armor = local_am.getAmount(); break;
                    case "Generic.Armor_Toughness":     toughness = local_am.getAmount(); break;
                    case "Generic.Knockback_Resistance": knockback_resistence = local_am.getAmount();
                }
            }
        }
        {
            //get first key
            PersistentDataContainer PDC = item.getItemMeta().getPersistentDataContainer();
            if( PDC.has(new NamespacedKey(plugin, plugin_name), PersistentDataType.STRING) ) {
                persistence = PDC.get(new NamespacedKey(plugin, plugin_name), PersistentDataType.STRING);
            }
        }

        //conditionally, change the result (if it's a verified custom item)
        ItemStack result = item.clone();
        if( armor == 2.0 && toughness == 0.0 && modifier.getType() == Material.DIAMOND_HELMET && persistence.isEmpty() ) {
            if(!enable_netherite_upgrades || !enable_diamond_upgrade) {
                smith.setResult( new ItemStack(Material.AIR) );
                List<HumanEntity> viewers = smith.getViewers();
                viewers.forEach( person -> ( (Player)(person) ).updateInventory() );
                return;
            }
            enable = true; value = "diamond_shell";
            if( name.equals("Turtle Shell") ) { prefix = "§r§b"; name = "Diamond Shell"; } else { prefix = "§b§o"; }
            armor = 3.0; toughness = 2.0;
        }
        if( armor == 2.0 && toughness == 0.0 && modifier.getType() == Material.NETHERITE_HELMET && persistence.isEmpty() ) {
            if(!enable_netherite_upgrades) {
                smith.setResult( new ItemStack(Material.AIR) );
                List<HumanEntity> viewers = smith.getViewers();
                viewers.forEach( person -> ( (Player)(person) ).updateInventory() );
                return;
            }
            enable = true; value = "netherite_shell";
            if( name.equals("Turtle Shell") ) { prefix = "§r§e"; name = "Netherite Shell"; } else { prefix = "§e§o"; }
            armor = 3.0; toughness = 3.0; knockback_resistence = 1.0;
        }
        if( armor == 3.0 && toughness == 2.0 && modifier.getType() == Material.NETHERITE_INGOT && persistence.equals("diamond_shell") ) {
            if(!enable_netherite_upgrades) {
                smith.setResult( new ItemStack(Material.AIR) );
                List<HumanEntity> viewers = smith.getViewers();
                viewers.forEach( person -> ( (Player)(person) ).updateInventory() );
                return;
            }
            enable = true; value = "netherite_shell";
            if( name.equals("Diamond Shell") ) { prefix = "§r§e"; name = "Netherite Shell"; } else { prefix = "§e§o"; }
            armor = 3.0; toughness = 3.0; knockback_resistence = 1.0;
        }

        if(enable) {
            meta.setDisplayName(prefix + name);

            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);                  meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "Generic.Armor", armor, ADD_NUMBER, HEAD ) );
            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(),"Generic.Armor_Toughness", toughness, ADD_NUMBER, HEAD ) );
            if(knockback_resistence != 0.0) { meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);   meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(),"Generic.Knockback_Resistance", knockback_resistence/10.0, ADD_NUMBER, HEAD ) ); }

            //remove all plugin keys,
            {
                Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();
                for (NamespacedKey local_snk : keys) {
                    if( local_snk.getKey().equals(plugin_name) ) {
                        meta.getPersistentDataContainer().remove(local_snk);
                    }
                }
            }
            //then add the correct one
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, plugin_name), PersistentDataType.STRING,  value );

            result.setItemMeta( (ItemMeta)(meta) );
            smith.setResult(result);
        } else {
            smith.setResult( new ItemStack(Material.AIR) );
        }

        //update viewer
        List<HumanEntity> viewers = smith.getViewers();
        viewers.forEach( person -> ( (Player)(person) ).updateInventory() );
    }

}