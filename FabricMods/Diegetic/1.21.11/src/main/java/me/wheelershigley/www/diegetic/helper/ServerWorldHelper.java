package me.wheelershigley.www.diegetic.helper;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRule;

public class ServerWorldHelper {
    public static Boolean getBooleanGameRuleValue(ServerWorld world, GameRule<Boolean> rule) {
        return world.getGameRules().getValue(rule);
    }
}
