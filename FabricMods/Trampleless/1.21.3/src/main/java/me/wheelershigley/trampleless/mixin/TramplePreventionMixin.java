package me.wheelershigley.trampleless.mixin;

import me.wheelershigley.trampleless.Trampleless;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(FarmlandBlock.class)
public abstract class TramplePreventionMixin extends Block {
    public TramplePreventionMixin(Settings settings) {
        super(settings);
    }

    @Shadow public static void setToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {}

        /**
         * @author Wheeler-Shigley
         * @reason Having FeatherFalling will prevent FarmLand-trampling
         */
    @Overwrite
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if(world instanceof ServerWorld serverWorld) {
            boolean randomlyBreaks = world.random.nextFloat() < fallDistance - 0.5F;
            boolean playerMayBreak = true; {
                if(entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    for( ItemStack armor : player.getArmorItems() ) {
                        AtomicBoolean areBoots = new AtomicBoolean(false);
                        armor.streamTags().forEach(
                                itemTagKey -> {
                                    if( itemTagKey.equals(ItemTags.FOOT_ARMOR) ) {
                                        areBoots.set(true);
                                    }
                                }
                        );
                        if(
                            areBoots.get()
                            && armor.contains(DataComponentTypes.ENCHANTMENTS)
                        ) {
                            for(RegistryEntry<Enchantment> enchant : EnchantmentHelper.getEnchantments(armor).getEnchantments() ) {
                                if(
                                    enchant.getKey().isPresent()
                                    && enchant.getKey().get().equals(Enchantments.FEATHER_FALLING)
                                ) {
                                    playerMayBreak = false;
                                }
                            }
                        }
                    }
                }
            }
            //isHeavyEnough
            boolean sufficientlyVoluminous = (entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F);
            boolean doMobGriefing = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            boolean isNotPlayer = (entity instanceof LivingEntity) && !(entity instanceof PlayerEntity);
            if(
                randomlyBreaks
                && (
                    (isNotPlayer && doMobGriefing && sufficientlyVoluminous)
                    || ( (entity instanceof PlayerEntity) && playerMayBreak )
                )
            ) {
                setToDirt(entity, state, world, pos);
            }
        }

        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }
}
