package me.wheelershigley.silktouchplus.registrations;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import me.wheelershigley.silktouchplus.helpers.EnchantmentsHelper;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;

import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.SILKTOUCH_CAKE;

public class CakeDrops {
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
                    ItemStack tool = player.getStackInHand( player.getActiveHand() );
                    if( tool != null && !tool.getItem().equals(Items.AIR) ) {
                        ItemEnchantmentsComponent enchantments = tool.get(DataComponentTypes.ENCHANTMENTS);
                        if( enchantments != null && !enchantments.isEmpty() ) {
                            toolUsedHasSilkTouch = EnchantmentsHelper.includesEnchantment(
                                enchantments.getEnchantments(),
                                Enchantments.SILK_TOUCH
                            );
                        }
                    }
                }
                if(!toolUsedHasSilkTouch) {
                    return;
                }

                int bites = state.get(CakeBlock.BITES).intValue();
                if(bites == 0) {
                    CakeBlock.dropStack( world, pos, new ItemStack(Items.CAKE) );
                }
                return;
            }
        );
    }
}
