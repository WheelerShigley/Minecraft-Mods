package me.wheelershigley.www.solace_fishing.registrations;

import com.mojang.serialization.Codec;
import me.wheelershigley.www.solace_fishing.SolaceFishing;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class CustomComponents {
    @Deprecated
    public static final DataComponentType<ItemStack> STORED_ITEM = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath(SolaceFishing.MOD_ID, "stored_item"),
        DataComponentType
            .<ItemStack>builder()
            .persistent(ItemStack.CODEC)
            .build()
    );

    public static final DataComponentType<Map<String, ItemStack>> STORED_ITEMS = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath(SolaceFishing.MOD_ID, "stored_items"),
        DataComponentType.< Map<String, ItemStack> >builder()
            .persistent(
                Codec.unboundedMap(
                    Codec.STRING,
                    ItemStack.CODEC
                )
            )
            .build()
    );
}
