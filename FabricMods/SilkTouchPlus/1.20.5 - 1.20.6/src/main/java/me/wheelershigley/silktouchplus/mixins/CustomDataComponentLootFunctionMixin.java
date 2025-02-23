package me.wheelershigley.silktouchplus.mixins;

import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
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

@Mixin(CopyNbtLootFunction.class)
public abstract class CustomDataComponentLootFunctionMixin extends ConditionalLootFunction {
    protected CustomDataComponentLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }

    @Shadow @Final private LootNbtProvider source;
    @Shadow @Final private List<CopyNbtLootFunction.Operation> operations;

    /**
     * @author Wheeler-Shigley
     * @reason Spawners do not store their data as components, so they must be handed as a special-case for block_entities
     */
    @Overwrite
    public ItemStack process(ItemStack stack, LootContext context) {
        DataComponentType<NbtComponent> type = stack.getItem().equals(Items.SPAWNER) ? DataComponentTypes.BLOCK_ENTITY_DATA : DataComponentTypes.CUSTOM_DATA;

        NbtElement nbtElement = this.source.getNbt(context);
        if(nbtElement == null) {
            return stack;
        } else {
            MutableObject<NbtCompound> mutableObject = new MutableObject<>();
            Supplier<NbtElement> supplier = () -> {
                if(mutableObject.getValue() == null) {
                    mutableObject.setValue(
                        stack.getOrDefault(type, NbtComponent.DEFAULT)
                        .copyNbt()
                    );
                }

                return (NbtElement)mutableObject.getValue();
            };
            this.operations.forEach(
                (operation) -> {
                    operation.execute(supplier, nbtElement);
                }
            );
            NbtCompound nbtCompound = mutableObject.getValue();
            if(nbtCompound != null) {
                NbtComponent.set(type, stack, nbtCompound);
            }

            return stack;
        }
    }
}
