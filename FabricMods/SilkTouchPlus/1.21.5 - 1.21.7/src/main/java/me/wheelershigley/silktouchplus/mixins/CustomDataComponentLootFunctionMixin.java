package me.wheelershigley.silktouchplus.mixins;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.loot.function.CopyNbtLootFunction.Operation;

@Mixin(CopyNbtLootFunction.class)
public abstract class CustomDataComponentLootFunctionMixin extends ConditionalLootFunction {
    protected CustomDataComponentLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }

    @Shadow @Final private LootNbtProvider source;
    @Shadow @Final private List<Operation> operations;

    /**
     * @author Wheeler-Shigley
     * @reason Spawners do not store their data as components, so they must be handed as a special-case for block_entities
     */
    @Overwrite
    public ItemStack process(ItemStack stack, LootContext context) {
        final ComponentType<NbtComponent> type;
        Item item = stack.getItem();
        if( item.equals(Items.SPAWNER) || item.equals(Items.TRIAL_SPAWNER) ) {
            type = DataComponentTypes.BLOCK_ENTITY_DATA;
        } else {
            type = DataComponentTypes.CUSTOM_DATA;
        }

        NbtElement nbtElement = this.source.getNbt(context);
        if (nbtElement == null) {
            return stack;
        } else {
            MutableObject<NbtCompound> mutableObject = new MutableObject();
            Supplier<NbtElement> supplier = () -> {
                if (mutableObject.getValue() == null) {
                    mutableObject.setValue(
                        ( (NbtComponent)stack.getOrDefault(type, NbtComponent.DEFAULT) ).copyNbt()
                    );
                }

                return (NbtElement)mutableObject.getValue();
            };
            this.operations.forEach((operation) -> {
                operation.execute(supplier, nbtElement);
            });
            NbtCompound nbtCompound = (NbtCompound)mutableObject.getValue();
            if (nbtCompound != null) {
                NbtComponent.set(type, stack, nbtCompound);
            }

            return stack;
        }
    }
}
