package me.wheelershigley.www.window.mixins;

import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static me.wheelershigley.www.window.api.CustomPoiTypes.CUSTOM_PORTAL;

@Mixin(PoiTypes.class)
public class CustomPortalPOIMixin {

    @Shadow
    private static PoiType register(final Registry<PoiType> registry, final ResourceKey<PoiType> id, final Set<BlockState> matchingStates, final int maxTickets, final int validRange) {
        return null;
    }

    @Shadow
    private static Set<BlockState> getBlockStates(final Block block) {
        return null;
    }

    @Inject(
        method = "bootstrap",
        at = @At("TAIL")
    )
    private static void bootstrap(Registry<PoiType> registry, CallbackInfoReturnable<PoiType> cir) {
        //TODO: consider all custom portals
        register(registry, CUSTOM_PORTAL, getBlockStates(WindowBlocks.WHITE_PORTAL), 0, 1);
    }
}
