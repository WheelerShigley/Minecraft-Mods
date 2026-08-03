package me.wheelershigley.www.silktouchplus.registrations;

import me.wheelershigley.www.silktouchplus.helpers.EnchantmentsHelper;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.*;

public class itemDropRegistrations {
    public static void registerDirtDropChanges() {
        LootTableEvents.MODIFY_DROPS.register(
            (lootTable, context, drops) -> {
                GameRules gameRules = context.getLevel().getGameRules();
                ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
                if( tool == null || tool.isEmpty() ) {
                    return;
                }
                boolean wasWithoutSilkTouched = !EnchantmentsHelper.hasSilkTouch(
                    context.getLevel(),
                    tool
                );
                if(wasWithoutSilkTouched) {
                    return;
                }

                if(
                    (
                        lootTable.is( Blocks.FARMLAND.getLootTable().orElseThrow() )
                        && gameRules.getBoolean(SILKTOUCH_FARMLAND)
                    ) || (
                        lootTable.is( Blocks.DIRT_PATH.getLootTable().orElseThrow() )
                        && gameRules.getBoolean(SILKTOUCH_DIRT_PATH)
                    )
                ) {
                    drops.clear();
                }
            }
        );
    }

    public static void registerCakeDrop() {
        PlayerBlockBreakEvents.AFTER.register(
            (world, player, pos, state, entity) -> {
                if( !state.getBlock().equals(Blocks.CAKE) ) {
                    return;
                }

                boolean shouldCakeBeSilkTouchable = false; {
                    MinecraftServer server = world.getServer();
                    if(server != null) {
                        shouldCakeBeSilkTouchable = server.getGameRules().getBoolean(SILKTOUCH_CAKE);
                    }
                }
                if(!shouldCakeBeSilkTouchable) {
                    return;
                }

                boolean toolUsedHasSilkTouch = false; {
                    ItemStack tool = player.getMainHandItem();
                    if( !tool.getItem().equals(Items.AIR) ) {
                        ItemEnchantments enchantments = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                        if( !enchantments.isEmpty() ) {
                            toolUsedHasSilkTouch = EnchantmentsHelper.includesEnchantment(
                                enchantments.entrySet(),
                                Enchantments.SILK_TOUCH
                            );
                        }
                    }
                }
                if(!toolUsedHasSilkTouch) {
                    return;
                }

                int bites = state.getValue(CakeBlock.BITES);
                if(bites == 0) {
                    CakeBlock.popResource( world, pos, new ItemStack(Items.CAKE) );
                }
                return;
            }
        );
    }
}
