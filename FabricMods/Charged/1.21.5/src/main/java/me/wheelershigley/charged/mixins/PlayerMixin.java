package me.wheelershigley.charged.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity {
    public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow public abstract ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

    @Inject(
        method = "onDeath",
        at = @At("HEAD")
    )
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        Entity e_Killer = damageSource.getAttacker();
        if(
            (e_Killer instanceof CreeperEntity)
            && ( (CreeperEntity)e_Killer ).isCharged()
        ) {
            ItemStack head = Items.PLAYER_HEAD.getDefaultStack();

            head.set(
                DataComponentTypes.PROFILE,
                new ProfileComponent( this.getGameProfile() )
            );
//            head.set(
//                DataComponentTypes.LORE,
//                LoreComponent.DEFAULT.with( this.getName() )
//            );

            this.dropItem(head, true, true);
        }
    }
}
