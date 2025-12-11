package me.wheelershigley.default_arms;

import net.fabricmc.api.ModInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static me.wheelershigley.default_arms.gamerule.registerGameRule.registerGameRule;

/* TODO
 * Add Client-side texture of armless armor_stand (include resource pack in mod)
 * Add crafting recipe for armless armor_stand
 * Shift-clicking with the shears should prevent shearing (test with non-shears also)
 * When armless armor-stands are placed, there is a single tick where they have arms;
 *     this may just be visual and could require a client-server sync (Issue #9)
 */

public class DefaultArms implements ModInitializer {
    public static final String MOD_ID = "default_arms";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override public void onInitialize() {
        registerGameRule();
    }
}
