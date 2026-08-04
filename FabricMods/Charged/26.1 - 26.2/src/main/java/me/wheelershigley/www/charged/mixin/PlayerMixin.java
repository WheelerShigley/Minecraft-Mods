package me.wheelershigley.www.charged.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_DROP;
import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_DROP_TEXTURES;

@Mixin(ServerPlayer.class)
public abstract class PlayerMixin extends Player {
    public PlayerMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow public abstract ItemEntity drop(final ItemStack itemStack, final boolean randomly, final boolean thrownFromHand);
    @Shadow public abstract boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage);
    @Shadow public abstract ServerLevel level();

    @Inject(
        method = "die",
        at = @At("HEAD")
    )
    public void onDeath(DamageSource source, CallbackInfo ci) {
        boolean doPlayerHeadDrops = this.level().getGameRules().get(ENABLE_PLAYER_HEAD_DROP);
        boolean doPlayerHeadTextureDrops = this.level().getGameRules().get(ENABLE_PLAYER_HEAD_DROP_TEXTURES);
        if(!doPlayerHeadDrops) {
            return;
        }

        boolean isKillerAChargedCreeper; {
            Entity killer = source.getEntity();
            isKillerAChargedCreeper =
                killer instanceof Creeper
                && ( (Creeper)killer ).isPowered()
            ;
        }
        if(!isKillerAChargedCreeper) {
            return;
        }

        ItemStack head = Items.PLAYER_HEAD.getDefaultInstance();
        if(doPlayerHeadTextureDrops) {
            head.set(
                DataComponents.PROFILE,
                ResolvableProfile.createResolved(
                    this.getGameProfile()
                )
            );
        }

//      head.set(
//          DataComponentTypes.LORE,
//          LoreComponent.DEFAULT.with( this.getName() )
//      );

        this.drop(head, true, true);
    }
}
