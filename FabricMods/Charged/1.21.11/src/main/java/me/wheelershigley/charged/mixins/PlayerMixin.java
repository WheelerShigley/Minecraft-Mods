package me.wheelershigley.charged.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_DROP;
import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_DROP_TEXTURES;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity {
    public PlayerMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow public abstract ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);
    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);
    @Shadow public abstract ServerWorld getEntityWorld();

    @Inject(
        method = "onDeath",
        at = @At("HEAD")
    )
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if(this.getEntityWorld() == null) {
            return;
        }
        boolean doPlayerHeadDrops = this.getEntityWorld().getGameRules().getValue(ENABLE_PLAYER_HEAD_DROP);
        boolean doPlayerHeadTextureDrops = this.getEntityWorld().getGameRules().getValue(ENABLE_PLAYER_HEAD_DROP_TEXTURES);
        if(!doPlayerHeadDrops) {
            return;
        }

        boolean isKillerAChargedCreeper; {
            Entity killer = damageSource.getAttacker();
            isKillerAChargedCreeper =
                killer instanceof CreeperEntity
                && ( (CreeperEntity)killer ).isCharged()
            ;
        }
        if(!isKillerAChargedCreeper) {
            return;
        }

        ItemStack head = Items.PLAYER_HEAD.getDefaultStack();
        if(doPlayerHeadTextureDrops) {
            head.set(
                DataComponentTypes.PROFILE,
                ProfileComponent.ofStatic(
                    this.getGameProfile()
                )
            );
        }

//      head.set(
//          DataComponentTypes.LORE,
//          LoreComponent.DEFAULT.with( this.getName() )
//      );

        this.dropItem(head, true, true);
    }
}
