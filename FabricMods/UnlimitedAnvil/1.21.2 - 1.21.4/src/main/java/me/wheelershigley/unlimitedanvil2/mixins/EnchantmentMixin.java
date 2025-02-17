package me.wheelershigley.unlimitedanvil2.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.wheelershigley.unlimitedanvil2.EnchantmentsHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Shadow @Final private Text description;

    //Enchantments.class::of()
    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
    }

    /**
     * @author Wheeler-Shigley
     * @reason Overwrite Maximum Enchantment Level with new ones
     */
    @Overwrite
    public int getMaxLevel() {
        Identifier Enchant = of(
                this.description.getString().toLowerCase().replace(' ','_')
        ).getValue();
        return EnchantmentsHelper.getMaximumEffectiveLevel(Enchant);
    }

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
