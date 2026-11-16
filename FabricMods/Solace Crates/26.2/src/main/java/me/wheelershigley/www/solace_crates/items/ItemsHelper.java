package me.wheelershigley.www.solace_crates.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemsHelper {
    public static Identifier getIdentifier(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
    public static @Nullable Item getItem(Identifier identifier) {
        return BuiltInRegistries.ITEM.getOptional(identifier).orElse(null);
    }

    public static void giveItemsToPlayer(ServerPlayer player, List<ItemStack> stacks) {
        for(ItemStack stack : stacks) {
            player.getInventory().placeItemBackInInventory(stack);
        }
    }
}
