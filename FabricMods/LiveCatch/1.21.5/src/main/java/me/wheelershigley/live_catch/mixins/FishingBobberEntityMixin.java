package me.wheelershigley.live_catch.mixins;

import me.wheelershigley.live_catch.LiveCatch;
import me.wheelershigley.live_catch.helpers.FishMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
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
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
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
    @Shadow private boolean removeIfInvalid(PlayerEntity player) { return false; }
    @Shadow protected void pullHookedEntity(Entity entity) {}

    /**
     * @author Wheeler-Shigley
     * @reason Custom Fishing Implementation
     */
    @Inject(
        method = "use",
        at = @At("HEAD"),
        cancellable = true
    )
    public void use(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        if(
            !this.getWorld().isClient
            && playerEntity != null
            && !this.removeIfInvalid(playerEntity)
        ) {
            int i = 0;
            if(this.hookedEntity != null) {
                this.pullHookedEntity(this.hookedEntity);
                //Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, Collections.emptyList());
                this.getWorld().sendEntityStatus(this, (byte)31);
                i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
            } else if(0 < this.hookCountdown) {
                LootWorldContext lootWorldContext = ( new LootWorldContext.Builder(  (ServerWorld)this.getWorld() )  )
                    .add( LootContextParameters.ORIGIN, this.getPos() )
                    .add(LootContextParameters.TOOL, usedItem)
                    .add(LootContextParameters.THIS_ENTITY, this)
                    .luck( (float)this.luckBonus + playerEntity.getLuck() )
                    .build(LootContextTypes.FISHING)
                ;
                LootTable lootTable = this.getWorld().getServer().getReloadableRegistries().getLootTable(LootTables.FISHING_GAMEPLAY);
                List<ItemStack> list = lootTable.generateLoot(lootWorldContext);
                //Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);

                for(ItemStack itemStack : list) {
                    LiveCatch.LOGGER.info( itemStack.toString() );

                    double dx = playerEntity.getX() - this.getX();
                    double dy = playerEntity.getY() - this.getY();
                    double dz = playerEntity.getZ() - this.getZ();
                    double force = 0.1;

                    Entity caughtEntity;
                    if( itemStack.isIn(ItemTags.FISHES) ) {
                        caughtEntity = FishMap.getFishFromType(
                            FishMap.getFishTypeFromItem( itemStack.getItem() ),
                            this.getWorld()
                        );
                        caughtEntity.setPos(
                            this.getX(),
                            (float)( (int)this.getY() ) + (1.0f-1.99f/16.0f) + caughtEntity.getHeight()/2.0,
                            this.getZ()
                        );
                        //TODO: fix that fish are sometimes too high
                        while( this.getWorld().getBlockState( caughtEntity.getBlockPos() ).getBlock().equals(Blocks.WATER) ) {
                            caughtEntity.setPos(
                                caughtEntity.getX(),
                                caughtEntity.getY() + 1.0f,
                                caughtEntity.getZ()
                            );
                            dy -= 1.0f;
                        }
                        force = 0.16;
                        playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
                    } else {
                        caughtEntity = new ItemEntity(
                            this.getWorld(),
                            this.getX(), this.getY(), this.getZ(),
                            itemStack
                        );
                    }
                    //TODO: fix that Salmon/Cod[/TropicalFish?] don't go far enough (dx&dz)
                    caughtEntity.setVelocity(
                        force*dx,
                        force*( dy + 0.96*Math.sqrt( Math.sqrt(dx*dx + dy*dy + dz*dz) ) ),
                        force*dz
                    );
                    this.getWorld().spawnEntity(caughtEntity);
                    playerEntity.getWorld().spawnEntity(
                        new ExperienceOrbEntity(playerEntity.getWorld(), playerEntity.getX(), playerEntity.getY() + (double)0.5F, playerEntity.getZ() + (double)0.5F, this.random.nextInt(6) + 1)
                    );
                }

                i = 1;
            }

            if( this.isOnGround() ) {
                i = 2;
            }

            this.discard();
            cir.setReturnValue(i);
        } else {
            cir.setReturnValue(0);
        }
        cir.cancel();
    }
}
