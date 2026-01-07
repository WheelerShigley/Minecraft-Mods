package me.wheelershigley.silktouchplus.helpers;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ItemStacksHelper {
    //based on ServerPlayNetworkHandler::copyBlockDataToStack
    public static ItemStack copyBlockDataToStack(
        @Nullable BlockEntity blockEntity,
        ServerWorld world,
        BlockPos pos,
        ItemStack stack
    ) {
        if(blockEntity != null) {
            try(
                ErrorReporter.Logging logging = new ErrorReporter.Logging(
                    blockEntity.getReporterContext(),
                    SilkTouchPlus.LOGGER
                )
            ) {
                NbtWriteView nbtWriteView = NbtWriteView.create(logging, world.getRegistryManager());
                blockEntity.writeComponentlessData(nbtWriteView);
                //blockEntity.removeFromCopiedStackData(nbtWriteView);
                BlockItem.setBlockEntityData(stack, blockEntity.getType(), nbtWriteView);
                stack.applyComponentsFrom(blockEntity.createComponentMap());
            }
        }
        return stack;
    }
}
