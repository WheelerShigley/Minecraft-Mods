package me.wheelershigley.trampleless.mixin;

import me.wheelershigley.trampleless.Trampleless;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
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

import static me.wheelershigley.trampleless.TramplelessGameRules.*;

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
        for(EquipmentSlot equipmentSlot : AttributeModifierSlot.ARMOR) {
            if(equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = potentiallyArmoredEntity.getEquippedStack(equipmentSlot);
                if(
                    !armor.isEmpty()
                    && areBoots(armor)
                    && armor.contains(DataComponentTypes.ENCHANTMENTS)
                ) {
                    if( itemHasEnchantment(armor, Enchantments.FEATHER_FALLING) ) {
                        hasFeatherFallingBoots = true;
                    }
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
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        MinecraftServer server = world.getServer();
        if(server == null) {
            return;
        }

        GameRules gameRules = server.getGameRules();
        boolean farmlandTrampling = gameRules.getBoolean(FARMLAND_TRAMPLING);
        boolean featherFallingTrampling = gameRules.getBoolean(FEATHER_FALLING_TRAMPLING);
        boolean doMobGriefing = gameRules.getBoolean(GameRules.DO_MOB_GRIEFING);

        if(!farmlandTrampling) {
            return;
        }

        if(world instanceof ServerWorld serverWorld) {
            boolean randomlyBreaks = world.random.nextFloat() < fallDistance - 0.5F;
            if(!randomlyBreaks) {
                return;
            }

            boolean hasFeatherFallingBoots = entityHasFeatherFallingBoots(entity);

            //check that prevents small entities, like Items, from breaking Farmland
            boolean sufficientlyVoluminous = (entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F);

            boolean isPlayer = entity instanceof PlayerEntity;
            if(
                ( featherFallingTrampling || !hasFeatherFallingBoots)
                && (
                    isPlayer || (doMobGriefing && sufficientlyVoluminous)
                )
            ) {
                setToDirt(entity, state, world, pos);
            }
        }

        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }
}
