package me.wheelershigley.live_catch.mixins;

import me.wheelershigley.live_catch.EntityLink;
import me.wheelershigley.live_catch.FishMap;
import me.wheelershigley.live_catch.LiveCatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {
    public FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow @Nullable private Entity hookedEntity;
    @Shadow private int hookCountdown;
    @Shadow @Final private int luckBonus;

    @Shadow @Nullable public PlayerEntity getPlayerOwner() { return null; }
    @Shadow private boolean removeIfInvalid(PlayerEntity player) { return false;}
    //@Shadow protected void pullHookedEntity(Entity entity) {}

    /**
     * @author Wheeler-Shigley
     * @reason a
     */
    @Inject(method = "use", at = @At("HEAD"))
    public void use(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        if(
            !this.getWorld().isClient && playerEntity != null
            && !this.removeIfInvalid(playerEntity)
            && this.hookedEntity == null
            && 0 < this.hookCountdown
        ) {
            //get loot Iterator
            Iterator loot; {
                LootWorldContext lootWorldContext = (new LootWorldContext.Builder((ServerWorld) this.getWorld()))
                        .add(LootContextParameters.ORIGIN, this.getPos())
                        .add(LootContextParameters.TOOL, usedItem)
                        .add(LootContextParameters.THIS_ENTITY, this)
                        .luck((float) this.luckBonus + playerEntity.getLuck())
                        .build(LootContextTypes.FISHING);
                LootTable lootTable = this.getWorld().getServer().getReloadableRegistries().getLootTable(LootTables.FISHING_GAMEPLAY);
                List<ItemStack> list = lootTable.generateLoot(lootWorldContext);
                //Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);
                loot = list.iterator();
            }

            while( loot.hasNext() ) {
                ItemStack result = (ItemStack) loot.next();

                //get Velocity
                double dx = playerEntity.getX() - this.getX();
                double dy = playerEntity.getY() - this.getY();
                double dz = playerEntity.getZ() - this.getZ();

                //get resulting fish/item
                EntityType CatchType = FishMap.getFishTypeFromItem( result.getItem() );
                boolean isFish = !CatchType.equals(EntityType.ITEM);
                Entity Catch = null; {
                    if(isFish) {
                        Catch = FishMap.getFishFromType(
                            CatchType,
                            this.getWorld()
                        );
                        Catch.setPos(
                            this.getX(),
                            Math.ceil( this.getY() )+1.0 + Catch.getHeight(),
                            this.getZ()
                        );
                        /*
                         * The player entity is the parent of the link because
                         * once the fishing rod pulls a fish/item, it is immediately deallocated;
                         * any bobber you see after having pulled a fish/item is merely visual.
                         */
                        new EntityLink(
                            this.getPlayerOwner(),
                            Catch
                        );
                    } else {
                        Catch = new ItemEntity(
                            this.getWorld(),
                            this.getX(), this.getY(), this.getZ(),
                            result
                        );
                        Catch.setVelocity(
                                0.1*dx,
                                0.1*dy + 0.08*Math.sqrt( Math.sqrt(dx*dx + dy*dy + dz*dz) ),
                                0.1*dz
                        );
                    }
                }

                this.getWorld().spawnEntity(Catch);
                this.hookedEntity = Catch;
                playerEntity.getWorld().spawnEntity(
                    new ExperienceOrbEntity(
                        playerEntity.getWorld(),
                        playerEntity.getX(),
                        playerEntity.getY() + 0.5,
                        playerEntity.getZ() + 0.5,
                        this.random.nextInt(6) + 1
                    )
                );
                if( !CatchType.equals(EntityType.ITEM) ) {
                    playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
                }
            }
        }
    }
}
