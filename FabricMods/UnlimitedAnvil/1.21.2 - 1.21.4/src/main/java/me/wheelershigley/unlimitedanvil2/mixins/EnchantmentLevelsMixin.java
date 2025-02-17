package me.wheelershigley.unlimitedanvil2.mixins;

import me.wheelershigley.unlimitedanvil2.EnchantmentsHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Enchantment.class)
public class EnchantmentLevelsMixin {
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
}
