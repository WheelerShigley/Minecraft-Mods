package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import me.wheelershigley.www.solace_fishing.menus.ImmutableChestMenu;
import me.wheelershigley.www.solace_fishing.menus.MainMenu;
import me.wheelershigley.www.solace_fishing.menus.TestMenu;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ListenedEvents {
    public static void register() {
        UseItemCallback.EVENT.register(
            (player, level, hand) -> {
                ItemStack stack = player.getItemInHand(hand);
                boolean isRod =
                    (stack.getItem() == Items.FISHING_ROD)
                    || (stack.getItem() instanceof CustomFishingRod)
                ;

                if(
                    isRod
                    && !level.isClientSide()
                    && player.isCrouching()
                ) {
                    TestMenu gui = new TestMenu( (ServerPlayer)player );
                    gui.open();

                    /*ImmutableChestMenu.open(
                        player,
                        new MainMenu( (ServerPlayer)player )
                    );*/
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
        );
    }
}
