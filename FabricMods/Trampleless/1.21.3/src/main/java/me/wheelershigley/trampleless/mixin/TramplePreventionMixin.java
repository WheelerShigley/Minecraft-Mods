package me.wheelershigley.trampleless.mixin;

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
import net.minecraft.registry.RegistryKey;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(FarmlandBlock.class)
public abstract class TramplePreventionMixin extends Block {
    public TramplePreventionMixin(Settings settings) {
        super(settings);
    }

    @Unique
    private static boolean areBoots(ItemStack item) {
        AtomicBoolean areBoots = new AtomicBoolean(false);
        item.streamTags().forEach(
            itemTagKey -> {
                if( itemTagKey.equals(ItemTags.FOOT_ARMOR) ) {
                    areBoots.set(true);
                }
            }
        );
        return areBoots.get();
    }
    @Unique
    private static boolean itemHasEnchantment(ItemStack item, RegistryKey<Enchantment> registeredEnchant) {
        for(RegistryEntry<Enchantment> enchant : EnchantmentHelper.getEnchantments(item).getEnchantments() ) {
            if(
                enchant.getKey().isPresent()
                && enchant.getKey().get().equals(registeredEnchant)
            ) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean entityHasFeatherFallingBoots(Entity entity) {
        LivingEntity potentiallyArmoredEntity;
        if(entity instanceof LivingEntity) {
            potentiallyArmoredEntity = (LivingEntity)entity;
        } else {
            return false;
        }

        boolean hasFeatherFallingBoots = false;
        for( ItemStack armor : potentiallyArmoredEntity.getArmorItems() ) {
            if(
                    areBoots(armor)
                            && armor.contains(DataComponentTypes.ENCHANTMENTS)
            ) {
                if( itemHasEnchantment(armor, Enchantments.FEATHER_FALLING) ) {
                    hasFeatherFallingBoots = true;
                }
            }
        }
        return hasFeatherFallingBoots;
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
            boolean hasFeatherFallingBoots = entityHasFeatherFallingBoots(entity);
            boolean doMobGriefing = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);

            //check that prevents small entities, like Items, from breaking Farmland
            boolean sufficientlyVoluminous = (entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F);

            boolean isPlayer = entity instanceof PlayerEntity;
            boolean isNotPlayer = (entity instanceof LivingEntity) && !isPlayer;
            if(
                randomlyBreaks
                && !hasFeatherFallingBoots
                && (
                    isPlayer
                    || (isNotPlayer && doMobGriefing && sufficientlyVoluminous)
                )
            ) {
                setToDirt(entity, state, world, pos);
            }
        }

        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }
}