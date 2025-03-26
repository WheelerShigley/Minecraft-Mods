package me.wheelershigley.unlimited_anvil.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Unique
    private static final ArrayList< ArrayList<Identifier> > ForciblyCompatibleEnchants; static {
        ForciblyCompatibleEnchants = new ArrayList<>();

        /*Infinity + Mending*/ {
            ArrayList<Identifier> InfinityMendingList = new ArrayList<>();
            InfinityMendingList.add( Enchantments.MENDING.getValue() );
            InfinityMendingList.add( Enchantments.INFINITY.getValue() );

            ForciblyCompatibleEnchants.add(InfinityMendingList);
        }
    }

    @ModifyReturnValue(
        method = "canBeCombined",
        at = @At("RETURN")
    )
    private static boolean canBeCombined(boolean original, RegistryEntry<Enchantment> FirstEnchant, RegistryEntry<Enchantment> SecondEnchant) {
        boolean includes_first, includes_second;
        for(ArrayList<Identifier> ForcedCompatibility : ForciblyCompatibleEnchants) {
            includes_first =
                FirstEnchant.getKey().isPresent()
                && ForcedCompatibility.contains(FirstEnchant.getKey().get().getValue() )
            ;
            includes_second =
                SecondEnchant.getKey().isPresent()
                && ForcedCompatibility.contains(SecondEnchant.getKey().get().getValue() )
            ;
            if(includes_first && includes_second) {
                return true;
            }
        }
        return original;
    }
}
