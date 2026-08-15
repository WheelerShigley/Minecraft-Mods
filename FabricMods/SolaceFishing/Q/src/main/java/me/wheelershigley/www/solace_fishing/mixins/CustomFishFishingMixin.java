package me.wheelershigley.www.solace_fishing.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wheelershigley.www.solace_fishing.SolaceFishing;
import me.wheelershigley.www.solace_fishing.data.AccessorizedFishingHook;
import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.FishingContext;
import me.wheelershigley.www.solace_fishing.data.RodAccessories;
import me.wheelershigley.www.solace_fishing.implementations.Catchables;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getFirstFishingRod;

@Mixin(FishingHook.class)
public abstract class CustomFishFishingMixin extends Projectile {
    public CustomFishFishingMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Shadow @Final
    private int luck = 0;

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Shadow
    private boolean openWater;

    @ModifyExpressionValue(
        method = "retrieve",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
        )
    )
    public ObjectArrayList<ItemStack> retrieveBobber(
        ObjectArrayList<ItemStack> original
    ) {
        Player caster = this.getPlayerOwner();
        if(caster == null) {
            return original;
        }

        Level level = caster.level();
        if( !(level instanceof ServerLevel) ) {
            return original;
        }
        assert level instanceof ServerLevel;

        FishingContext context; {
            BlockPos position = this.blockPosition().below();
            Block medium = level.getBlockState(position).getBlock();

            ItemStack rodStack = getFirstFishingRod(caster);
            Item rod = (rodStack == null ? Items.FISHING_ROD : rodStack.getItem() );

            float luck = caster.getLuck() + (float)this.luck;

            RodAccessories accessories = ( (AccessorizedFishingHook)this ).solace_fishing$getAccessories();

            ClimateData climate = ClimateData.sample(
                    (ServerLevel)level,
                    caster.getOnPos()
            );

            context = new FishingContext(
                medium, rod, luck, accessories, climate
            );

        }

        //TODO: resolve
        caster.sendSystemMessage(
            Component.literal(
                "TODO: integrate whole context."
            )
        );
        caster.sendSystemMessage(
            Component.literal(
                "This context: " + context.toString()
            )
        );

        ItemStack caught = Catchables.roll(
            context,
            this.openWater,
            level.getRandom(),
            level.registryAccess()
        );

        return ObjectArrayList.of(caught);
    }
}
