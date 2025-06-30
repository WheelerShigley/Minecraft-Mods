package me.wheelershigley.default_arms.mixins;

import me.wheelershigley.default_arms.DefaultArms;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ArmorStandItem.class)
public class placeArmorStandMixin {
    /**
     * @author Wheeler-Shigley
     * @reason showsArms:0b -> summon without arms
     */
    @Overwrite
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
            BlockPos blockPos = itemPlacementContext.getBlockPos();
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Box box = EntityType.ARMOR_STAND.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            if(world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity)null, box).isEmpty()) {
                if(world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    Consumer<ArmorStandEntity> consumer = EntityType.copier(serverWorld, itemStack, context.getPlayer());
                    ArmorStandEntity armorStandEntity = (ArmorStandEntity)EntityType.ARMOR_STAND.create(serverWorld, consumer, blockPos, SpawnReason.SPAWN_ITEM_USE, true, true);
                    if (armorStandEntity == null) {
                        return ActionResult.FAIL;
                    }
                    Boolean showsArms = getShowsArmsData(itemStack);
                    armorStandEntity.setShowArms(
                        showsArms == null || showsArms.booleanValue()
                    );

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
                    serverWorld.spawnEntityAndPassengers(armorStandEntity);
                    world.playSound((Entity)null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    armorStandEntity.emitGameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
                }

                itemStack.decrement(1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    private static Boolean getShowsArmsData(ItemStack armorStandItem) {
        NbtComponent customDataComponent;
        if( !armorStandItem.contains(DataComponentTypes.CUSTOM_DATA) ) {
            return null;
        }

        customDataComponent = armorStandItem.get(DataComponentTypes.CUSTOM_DATA);
        if(customDataComponent == null) {
            return null;
        }

        NbtElement showsArms = customDataComponent.copyNbt().get("showsArms");
        if(showsArms == null) {
            return null;
        }

        Optional<Boolean> result = showsArms.asBoolean();
        return result.orElse(null);
    }
}
