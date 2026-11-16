package me.wheelershigley.www.solace_crates;

import me.wheelershigley.www.solace_crates.blocks.ModBlockEntities;
import me.wheelershigley.www.solace_crates.blocks.ModBlocks;
import me.wheelershigley.www.solace_crates.commands.SetCrateCommand;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import me.wheelershigley.www.solace_crates.events.ServerLifeCycleListeners;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceCrates implements ModInitializer {
    public static final String MOD_ID = "solace_crates";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /* TODO
     * "**" is required to meet 0.7.0 (functional alpha release)
     *
     * ## Prototyping
     ** Premium-Key Menu + Premium-Key Functionality
     *
     * ## Appearance
     ** Players Configuration for current seed (UUID -> #)
     ** Store-Link in ProbabilitiesMenu
     * Custom Icons
     *
     * Display Types
     * ** Floating display of item (single?)
     * ** Floating display of items (multiple items) {collect all resulting-ItemStacks together for this}
     * * Random display
     * * Configuration for which Type (in CrateDefinition)
     * item-displays have titles above them (translated name)
     *
     * Facing Direction (crate directions)
     * Fix Double-Messaging for unset/unknown/invalid crates
     *
     * ## User-friendliness
     * set crate-type with a menu
     *
     * item-component can set identifier (when placing)
     * automatically adding all registered crates to creative menu (with item-components)
     *
     * menu of probability menus {suggested}
     */

    @Override
    public void onInitialize() {
        CrateRegistry.initialize();

        ModBlockEntities.initialize();
        ModBlocks.initialize();

        ServerLifeCycleListeners.register();

        SetCrateCommand.register();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
    public static Component getTranslatedSolaceCrateComponent(
        String translationKey,
        String... arguments
    ) {
        return
            Component.translatable("solace_crates.messages.prefix").append(
                Component.translatable(translationKey, (Object[])arguments)
            )
        ;
    }    public static Component getTranslatedSolaceCrateComponent(
        Component... arguments
    ) {
        Component result = Component.translatable("solace_crates.messages.prefix");
        for(Component argument : arguments) {
            result = result.copy().append(argument);
        }
        return result;
    }
}
