package me.wheelershigley.silktouchplus.mixins;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractBlock.class)
public abstract class ItemDropDataAdditionsMixin {
    @Unique
    private static final List<Block> modifiedDropBlocks; static {
        modifiedDropBlocks = new ArrayList<>();

        modifiedDropBlocks.add(Blocks.SPAWNER);
        modifiedDropBlocks.add(Blocks.TRIAL_SPAWNER);
        modifiedDropBlocks.add(Blocks.VAULT);
    }
    @Unique
    private static final HashMap< Identifier, List<String> > modifiedDropDataNames; static {
        modifiedDropDataNames = new HashMap<>();

        //modifiedDatas.put()
    }

    @Shadow @Final protected Optional< RegistryKey<LootTable> > lootTableKey;

    @Inject(
        method = "getDroppedStacks",
        at = @At("HEAD")
    )
    protected void getDroppedStacks(
        BlockState state,
        LootWorldContext.Builder builder,
        CallbackInfoReturnable< List<ItemStack> > cir
    ) {
        /*
        Block block = state.getBlock();
        if(
            modifiedDropBlocks.contains(block)
            && this.lootTableKey.isPresent()
        ) {
            SilkTouchPlus.LOGGER.info( lootTableKey.get().toString() );
        }
         */
    }
}
