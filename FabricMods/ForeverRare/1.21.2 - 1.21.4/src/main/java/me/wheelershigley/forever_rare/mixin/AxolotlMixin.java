package me.wheelershigley.forever_rare.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(AxolotlEntity.class)
public abstract class AxolotlMixin extends AnimalEntity  {
    protected AxolotlMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private static boolean shouldBabyBeDifferent(Random random) {
        return false; //random.nextInt(1200) == 0;
    }

    @Shadow
    public AxolotlEntity.Variant getVariant() {
        return null;
    }

    @Shadow @Final private static TrackedData<Integer> VARIANT;

    /**
     * @author Wheeler-Shigley
     * @reason Keep Blue variants rare!
     */
    @Overwrite
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        AxolotlEntity OtherParent = (AxolotlEntity)entity;
        AxolotlEntity axolotlEntity = EntityType.AXOLOTL.create(world, SpawnReason.BREEDING);
        if(axolotlEntity != null) {
            AxolotlEntity.Variant variant;
            if( shouldBabyBeDifferent(this.random) ) {
                variant = AxolotlEntity.Variant.getRandomUnnatural(this.random);
            } else {
                /* ## VARIANT SELECTION
                 * 1. Non-blue parents' variants take priority.
                 * 2. When both parents are blue, a random non-blue variant will be chosen.
                 * 3. When neither parent is blue, a uniformly random one's variant will be chosen.
                 */
                if( this.getVariant().equals(AxolotlEntity.Variant.BLUE) ) {
                    if( OtherParent.getVariant().equals(AxolotlEntity.Variant.BLUE) ) {
                        variant = AxolotlEntity.Variant.getRandomNatural(this.random);
                    } else {
                        variant = OtherParent.getVariant();
                    }
                } else {
                    if( OtherParent.getVariant().equals(AxolotlEntity.Variant.BLUE) ) {
                        variant = this.getVariant();
                    } else {
                        variant = this.random.nextBoolean() ? this.getVariant() : OtherParent.getVariant();
                    }
                }
            }

            axolotlEntity.setVariant(variant);
            axolotlEntity.setPersistent();
        }

        return axolotlEntity;
    }
}
