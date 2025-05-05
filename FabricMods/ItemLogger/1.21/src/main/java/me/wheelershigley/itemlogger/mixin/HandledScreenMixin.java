package me.wheelershigley.itemlogger.mixin;

import me.wheelershigley.itemlogger.ItemLogger;
import me.wheelershigley.itemlogger.client.ItemLoggerClient;
import me.wheelershigley.itemlogger.modes.Mode;
import me.wheelershigley.itemlogger.modes.Modes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "close", at = @At("HEAD"))
    @SuppressWarnings("unchecked")
    private void close(CallbackInfo info) {
        if( ItemLoggerClient.mode.equals(Mode.OFF) ) {
            return;
        }

        HandledScreen<T> screen = (HandledScreen<T>)(Object)this;
        boolean is_player_inventory = screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen;
        if(!is_player_inventory) {
            if( ItemLoggerClient.mode.equals(Mode.LOG) ) {
                ItemLogger.LOGGER.info(
                    Text.literal(
                        Text.translatable("item_logger.text.opened_container").getString()
                    ).getString(),
                    screen.getClass().getName()
                );

                ItemLogger.LOGGER.info( Modes.Logging.containerContentsToMarkdown(screen) );
            }
        }
    }
}
