package me.wheelershigley.www.silktouchplus.helpers;

import me.wheelershigley.www.silktouchplus.SilkTouchPlus;
import net.fabricmc.fabric.mixin.event.interaction.ServerPlayNetworkHandlerInteractEntityHandlerMixin;
import net.fabricmc.fabric.mixin.event.interaction.ServerPlayNetworkHandlerMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ItemStacksHelper {
    //based on ServerPlayNetworkHandler::copyBlockDataToStack
    public static ItemStack copyBlockDataToStack(
        @Nullable BlockEntity blockEntity,
        ServerLevel world,
        BlockPos pos,
        ItemStack stack
    ) {
        /*if(blockEntity != null) {
            try(
                /*ErrorReporter.Logging logging = new ErrorReporter.Logging(
                    blockEntity.getReporterContext(),
                    SilkTouchPlus.LOGGER
                )*//*
            ) {
                NbtWriteView nbtWriteView = NbtWriteView.create(logging, world.getRegistryManager());
                blockEntity.writeComponentlessData(nbtWriteView);
                //blockEntity.removeFromCopiedStackData(nbtWriteView);
                BlockItem.setBlockEntityData(stack, blockEntity.getType(), nbtWriteView);
                stack.applyComponentsFrom(blockEntity.createComponentMap());
            }
        }*/
        return stack;
    }
}
