package me.wheelershigley.cocoa_anywhere.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.CocoaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CocoaBlock.class)
public class CocoaBlockMixin {
    /**
     * @author Wheeler-Shigley
     * @reason Place CocoaBeans anywhere
     */
    @ModifyReturnValue( method = "canPlaceAt", at = @At("RETURN") )
    protected boolean canPlaceAt(boolean original) {
        return true;
    }
}
