package me.wheelershigley.silktouchplus.registrations;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class GameRuleLootFunction implements LootFunction {
    public static LootFunctionType<GameRuleLootFunction> GAME_RULE;

    final boolean inverted;
    final GameRules.Key<GameRules.BooleanRule> rule;
    public GameRuleLootFunction(GameRules.Key<GameRules.BooleanRule> booleanRule) {
        rule = booleanRule;
        inverted = false;
    }
    public GameRuleLootFunction(GameRules.Key<GameRules.BooleanRule> booleanRule, boolean invert) {
        rule = booleanRule;
        inverted = invert;
    }

    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return GAME_RULE;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        if( lootContext.getWorld().getGameRules().get(rule).get() ) {
            return (inverted ? ItemStack.EMPTY : itemStack);
        } else {
            return (inverted ? itemStack : ItemStack.EMPTY);
        }
    }


    public static final MapCodec<GameRuleLootFunction> CODEC = MapCodec.unit(new GameRuleLootFunction(null) );
    public static void register() {
        GAME_RULE = Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            Identifier.ofVanilla("game_rule_lootfunction"),
            new LootFunctionType<>(CODEC)
        );
    }
}
