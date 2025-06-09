package me.wheelershigley.live_catch.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.HashMap;

public class FishMap {
    private static HashMap<Item, EntityType> FishItemToFishEntity; static {
        FishItemToFishEntity = new HashMap<>();
        FishItemToFishEntity.put(Items.COD,             EntityType.COD);
        FishItemToFishEntity.put(Items.SALMON,          EntityType.SALMON);
        FishItemToFishEntity.put(Items.TROPICAL_FISH,   EntityType.TROPICAL_FISH);
        FishItemToFishEntity.put(Items.PUFFERFISH,      EntityType.PUFFERFISH);
    }

    public static EntityType getFishTypeFromItem(Item item) {
        if( FishItemToFishEntity.containsKey(item) ) {
            return FishItemToFishEntity.get(item);
        }
        return EntityType.ITEM;
    }

    public static Entity getFishFromType(EntityType fish, World world) {
        Entity result = null;
        if( fish.equals(EntityType.COD) ) {
            result = new CodEntity(EntityType.COD, world);
        }
        if( fish.equals(EntityType.SALMON) ) {
            return new SalmonEntity(EntityType.SALMON, world);
        }
        if( fish.equals(EntityType.TROPICAL_FISH) ) {
            return new TropicalFishEntity(EntityType.TROPICAL_FISH, world);
        }
        if( fish.equals(EntityType.PUFFERFISH) ) {
            return new PufferfishEntity(EntityType.PUFFERFISH, world);
        }
        return result;
    }
}
