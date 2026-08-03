package me.wheelershigley.www.silktouchplus;

import net.fabricmc.api.ModInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.*;
import static me.wheelershigley.www.silktouchplus.registrations.itemDropRegistrations.*;

//TODO: {vault, trial-spawner} are pickaxe only!
public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silk_touch_plus";
    //public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        registerGameRules();

        registerCakeDrop();
        registerDirtDropChanges();
    }
}