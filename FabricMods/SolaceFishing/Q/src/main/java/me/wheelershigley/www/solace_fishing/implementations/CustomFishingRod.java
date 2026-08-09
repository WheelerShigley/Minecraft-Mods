package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

public class CustomFishingRod extends FishingRodItem implements PolymerItem {
    public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties()
        .stacksTo(1)
        .durability(100)
    ;

    public CustomFishingRod(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(
        final @NonNull Level level,
        final @NonNull Player player,
        final @NonNull InteractionHand hand
    ) {
        if(player.fishing != null) {
            retrieveCast(level, player, hand);
        } else {
            summonCast(level, player, hand);
        }

        return InteractionResult.SUCCESS ;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.FISHING_ROD;
    }

    @Unique
    private void retrieveCast(Level level, Player player, InteractionHand hand) {
        assert player.fishing != null;
        ItemStack heldStack = player.getItemInHand(hand);

        //remove cast-tag (for client-side texturing)
        //heldStack.remove(DataComponents.CUSTOM_MODEL_DATA);

        if( !level.isClientSide() ) {
            int damage = player.fishing.retrieve(heldStack);
            heldStack.hurtAndBreak(
                damage,
                player,
                hand.asEquipmentSlot()
            );
        }

        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
            1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        heldStack.causeUseVibration(player, GameEvent.ITEM_INTERACT_FINISH);
    }

    @Unique
    private void summonCast(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        //set cast (for client-side texturing)
        itemStack.set(
            DataComponents.CUSTOM_MODEL_DATA,
            new CustomModelData(
                List.of(),
                List.of(),
                List.of("cast"),
                List.of()
            )
        );

        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
            0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if(level instanceof ServerLevel serverLevel) {
            int lureSpeed = (int)(EnchantmentHelper.getFishingTimeReduction(serverLevel, itemStack, player) * 20.0F);
            int luck = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemStack, player);

            FishingHook hook = new FishingHook(player, level, luck, lureSpeed);
            Projectile.spawnProjectile(hook, serverLevel, itemStack);
        }

        player.awardStat( Stats.ITEM_USED.get(this) );
        itemStack.causeUseVibration(player, GameEvent.ITEM_INTERACT_START);
    }
}
