package me.wheelershigley.silktouchplus.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.item.BlockItem;
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

import static me.wheelershigley.silktouchplus.helpers.LootPoolHelpers.getBlockEntityType;

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
        Item item = stack.getItem();
        final ComponentType<?> type;

        final boolean isCustom;
        if( !item.equals(Items.SPAWNER) && !item.equals(Items.TRIAL_SPAWNER) ) {
            return stack;
        }
        if( item.equals(Items.SPAWNER) ) {
            type = DataComponentTypes.BLOCK_ENTITY_DATA;
        } else {
            type = DataComponentTypes.CUSTOM_DATA;
        }

        BlockEntityType<?> blockEntityType = null; {
            if( stack.getItem() instanceof BlockItem ) {
                Block block = ( (BlockItem) stack.getItem() ).getBlock();
                blockEntityType = getBlockEntityType(block);
            }
        }
        if(blockEntityType == null) {
            return stack;
        }

        NbtElement nbtElement = this.source.getNbt(context);
        if (nbtElement == null) {
            return stack;
        } else {
            MutableObject<NbtCompound> mutableObject = new MutableObject<>();
            Supplier<NbtElement> supplier = () -> {
                if (mutableObject.getValue() == null) {
                    mutableObject.setValue(
                        (
                            (NbtComponent)stack.getOrDefault(type, NbtComponent.DEFAULT)
                        ).copyNbt()
                    );
                }

                return (NbtElement)mutableObject.getValue();
            };
            this.operations.forEach(
                (operation) -> {
                    operation.execute(supplier, nbtElement);
                }
            );
            NbtCompound nbtCompound = (NbtCompound)mutableObject.getValue();
            if(nbtCompound == null) {
                return stack;
            }

            if( type.equals(DataComponentTypes.BLOCK_ENTITY_DATA) ) {
                stack.set(
                    DataComponentTypes.BLOCK_ENTITY_DATA,
                    TypedEntityData.create(blockEntityType, nbtCompound)
                );
            } else {
                stack.set(
                    DataComponentTypes.CUSTOM_DATA,
                    NbtComponent.of(nbtCompound)
                );
            }

            return stack;
        }
    }
}
