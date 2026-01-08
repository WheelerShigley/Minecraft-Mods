package me.wheelershigley.www.magnetized.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.wheelershigley.www.magnetized.Magnetized;
import me.wheelershigley.www.magnetized.helpers.Physics;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class MagneticSneakMixin {
    @Unique
    private static final double RANGE_PER_LEVEL = 7.0/3.0; //meters

    @Shadow public abstract boolean isSneaking();
    @Shadow public abstract World getEntityWorld();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();

    @Shadow private Vec3d pos;

    @Inject(
        method = "Lnet/minecraft/entity/Entity;tick()V",
        at = @At("TAIL")
    )
    public void tick(CallbackInfo ci) {
        //Entity must be server-side and sneaking
        if(
            this.getEntityWorld().isClient()
            || !this.isSneaking()
        ) {
            return;
        }

        //Chestplate must have the enchantment, "Magnetic"
        int level = getBodyMagneticEnchantmentLevel();
        double range = RANGE_PER_LEVEL * level;
        if(range <= 0) {
            return;
        }

        //Move nearby items
        double half_cube_range = Math.sqrt(2) * range/2;
        List<ItemEntity> nearbyItems = this.getEntityWorld().getNonSpectatingEntities(
            ItemEntity.class,
            new Box(
                this.getX() - half_cube_range, this.getY() - half_cube_range, this.getZ() - half_cube_range,
                this.getX() + half_cube_range, this.getY() + half_cube_range, this.getZ() + half_cube_range
            )
        );
        for(ItemEntity nearbyItem : nearbyItems) {
            //Item must be in range (this check is done because the items in this list are within a nearby cube, not sphere)
            if( range < this.pos.distanceTo( nearbyItem.getEntityPos() ) ) {
                continue;
            }
            Physics.attractOneEntityToAnother( (Entity)(Object)this, nearbyItem, level);
        }
    }

    @Unique
    private int getBodyMagneticEnchantmentLevel() {
        if( ( (Object)this ) instanceof LivingEntity) {
            final ItemStack CHEST_PLATE = ( (LivingEntity)(Object)this ).getEquippedStack(EquipmentSlot.CHEST);
            if(
                CHEST_PLATE.isEmpty()
                || !CHEST_PLATE.contains(DataComponentTypes.ENCHANTMENTS)
            ) {
                return 0;
            }
            ItemEnchantmentsComponent enchantmentsComponent = EnchantmentHelper.getEnchantments(CHEST_PLATE);
            if( enchantmentsComponent.isEmpty() ) {
                return 0;
            }

            double level = 0;
            for( Object2IntMap.Entry< RegistryEntry<Enchantment> > entry : enchantmentsComponent.getEnchantmentEntries() ) {
                RegistryEntry<Enchantment> enchantEntry = entry.getKey();

                if(  enchantEntry.matchesId( Magnetized.MAGNETIC.getValue() )  ) {
                    level = entry.getIntValue();
                    break;
                }
            }
            return (int)Math.max(0, Math.min(level, 3) ); //must be between 0 and 3 (inclusive)
        }
        return 0;
    }
}
