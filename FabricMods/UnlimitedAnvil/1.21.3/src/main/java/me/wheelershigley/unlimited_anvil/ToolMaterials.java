package me.wheelershigley.unlimited_anvil;

import me.wheelershigley.unlimited_anvil.helpers.HelperFunctions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToolMaterials {

    public static final HashMap<Item, Item[]> ToolMaterialMap = new HashMap<>();

    public static void initializeToolMaterials() {
        //
        final HashMap<String, Item[]> MaterialToolMap = new HashMap<>(); {
            String[] RepairableSuffixCriteria = new String[]{
                "sword", "pickaxe", "axe", "shovel", "hoe",
                "trident", "mace", "bow", "crossbow", "shield",

                "brush", "flint_and_steel", "fishing_rod", "shears",
                "on_a_stick",

                "helmet", "chestplate", "leggings", "boots",
                "wolf_armor"
            };

            /*dynamically populate tools*/ {
                String ItemIdentifier, ExpectedKey;
                for(Item item : Registries.ITEM) {
                    ItemIdentifier = item.toString().split(":")[1];
                    for(String RepairableSuffixCriterion : RepairableSuffixCriteria) {
                        if( ItemIdentifier.endsWith(RepairableSuffixCriterion) ) {
                            ExpectedKey = HelperFunctions.replaceLast(ItemIdentifier, RepairableSuffixCriterion, "");
                            if( ExpectedKey.endsWith("_") ) {
                                ExpectedKey = ExpectedKey.substring(0, ExpectedKey.length()-1 );
                            }
                            if( MaterialToolMap.containsKey(ExpectedKey) ) {
                                Item[] KnownItems  = MaterialToolMap.get(ExpectedKey);
                                Item[] NewItems = new Item[KnownItems.length+1]; {
                                    System.arraycopy(KnownItems, 0, NewItems, 0, KnownItems.length);
                                    NewItems[KnownItems.length] = item;
                                }
                                MaterialToolMap.replace(ExpectedKey, NewItems);
                            } else {
                                MaterialToolMap.put(ExpectedKey, new Item[]{item});
                            }
                        }
                    }
                }
            }
        }

        HashMap<String, Item[]> RepairMaterialsMap = new HashMap<>(); {
            //single-material items
            RepairMaterialsMap.put("carrot",        new Item[]{Items.CARROT}            );
            RepairMaterialsMap.put("chainmail",     new Item[]{Items.CHAIN}             );
            RepairMaterialsMap.put("diamond",       new Item[]{Items.DIAMOND}           );
            RepairMaterialsMap.put("golden",        new Item[]{Items.GOLD_INGOT}        );
            RepairMaterialsMap.put("iron",          new Item[]{Items.IRON_INGOT}        );
            RepairMaterialsMap.put("leather",       new Item[]{Items.LEATHER}           );
            RepairMaterialsMap.put("netherite",     new Item[]{Items.NETHERITE_INGOT}   );
            RepairMaterialsMap.put("turtle",        new Item[]{Items.TURTLE_SCUTE}      );
            RepairMaterialsMap.put("warped_fungus", new Item[]{Items.WARPED_FUNGUS}     );

            //multi-material item(s)
            RepairMaterialsMap.put(
                    "stone",
                    new Item[]{
                            Items.BLACKSTONE,
                            Items.COBBLESTONE,
                            Items.COBBLED_DEEPSLATE,

                            Items.ANDESITE,
                            Items.DIORITE,
                            Items.GRANITE
                    }
            );

            //tag-material item(s)
            ArrayList<Item> planks = new ArrayList<>();
            for(Item item : Registries.ITEM) {
                if( item.toString().endsWith("_planks") ) {
                    planks.add(item);
                }
            }
            RepairMaterialsMap.put( "wooden", planks.toArray(new Item[0]) );
        }
        String key;
        boolean handled = false;
        for( Map.Entry<String, Item[]> MaterialToolEntry : MaterialToolMap.entrySet() ) {
            key = MaterialToolEntry.getKey();
            for( Item tool : MaterialToolEntry.getValue() ) {
                //Must be damage-able
                if( !(new ItemStack(tool)).isDamageable() ) { continue; }

                //handle General Cases
                if(  RepairMaterialsMap.containsKey(key)  ) {
                    ToolMaterialMap.put( tool, RepairMaterialsMap.get(key) );
                    handled = true;
                }
                //handle Special Cases
                if( key.isBlank() ) {
                    for(Item SpecialCase : MaterialToolEntry.getValue() ) {
                        if( SpecialCase.equals(Items.CROSSBOW) || SpecialCase.equals(Items.FISHING_ROD) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.STICK});
                            handled = true;
                        }
                        if( SpecialCase.equals(Items.BRUSH) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.COPPER_INGOT});
                            handled = true;
                        }
                        if( SpecialCase.equals(Items.MACE) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.HEAVY_CORE});
                            handled = true;
                        }
                        if( SpecialCase.equals(Items.SHEARS) || SpecialCase.equals(Items.SHIELD) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.IRON_INGOT});
                            handled = true;
                        }
                        if( SpecialCase.equals(Items.TRIDENT) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.PRISMARINE_SHARD});
                            handled = true;
                        }
                        if( SpecialCase.equals(Items.WOLF_ARMOR) ) {
                            ToolMaterialMap.put(SpecialCase, new Item[]{Items.ARMADILLO_SCUTE});
                            handled = true;
                        }
                    }
                }
                if(!handled) {
                    UnlimitedAnvil.LOGGER.warn("Unknown material \"{}\".", key);
                }
            }
        }
        UnlimitedAnvil.LOGGER.info("test");
    }
}
