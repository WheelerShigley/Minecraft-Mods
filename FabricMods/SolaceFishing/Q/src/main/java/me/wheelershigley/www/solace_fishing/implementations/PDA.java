package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.menus.ProbabilitiesMenu;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;

public class PDA extends Item implements PolymerItem  {
    public PDA(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COMPASS;
    }

    @Override
    public @NonNull InteractionResult use(
        final @NonNull Level level,
        final @NonNull Player player,
        final @NonNull InteractionHand hand
    ) {
        if( !(level instanceof ServerLevel)
            || !(player instanceof ServerPlayer)
        ) {
            return InteractionResult.PASS;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        ServerPlayer serverPlayer = (ServerPlayer)player;

        HitResult hit = serverPlayer.pick(5.0D, 0.0F, true);
        BlockPos position;
        if(hit.getType() == HitResult.Type.BLOCK) {
            position = ( (BlockHitResult)hit ).getBlockPos();
        } else {
            position = serverPlayer.getOnPos();
        }

        ProbabilitiesMenu menu = new ProbabilitiesMenu(
            serverLevel, position, player.isCrouching(), serverPlayer, null
        );
        menu.open();


        return InteractionResult.SUCCESS;
    }
}
