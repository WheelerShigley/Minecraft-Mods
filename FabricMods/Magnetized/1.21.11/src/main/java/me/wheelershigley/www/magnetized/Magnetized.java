package me.wheelershigley.www.magnetized;

import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Magnetized implements ModInitializer {
    public static final String MOD_ID = "magnetized";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<Enchantment> MAGNETIC = RegistryKey.of(
        RegistryKeys.ENCHANTMENT,
        Identifier.of(MOD_ID, "magnetic")
    );

    @Override
    public void onInitialize() {
        MagneticEnchantment.register();
    }
}
