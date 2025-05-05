package me.wheelershigley.itemlogger.mixins;

import me.wheelershigley.itemlogger.client.ItemLoggerClient;
import me.wheelershigley.itemlogger.client.Mode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("containerlogger");
    private static final String TAB = "    "; //four spaces

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private String containerContentsToMarkdown(@NotNull HandledScreen<T> container) {
        DefaultedList<Slot> slots; {
            slots = container.getScreenHandler().slots;
            //remove player inventory
            for (int i = 0; i <= 27+8; i++) {
                slots.removeLast();
            }
        }

        ItemStack stack;
        String components;
        StringBuilder component_builder = new StringBuilder();
        StringBuilder contents; {
            contents = new StringBuilder();

            //Title
            contents
                .append("\r\n\r\n# \"")
                .append( container.getNarratedTitle().getString() )
                .append("\"\r\n")
            ;

            //Contents
            boolean is_empty = true;
            int slot_iterator = 0;
            for(Slot slot : slots) {
                if(slot.getStack().getItem() == Items.AIR) {
                    slot_iterator++;
                    continue;
                }
                is_empty = false;

                stack = slot.getStack();
                /*components*/ {
                    //empty
                    components = "";
                    component_builder.setLength(0);

                    Set<ComponentType<?>> components_set = stack.getComponents().getTypes();
                    for(ComponentType<?> component : components_set) {
                        component_builder
                            .append("\t\t- ")
                            .append("\"").append(component.toString()).append("\": ")
                            .append(stack.get(component))
                            .append("\r\n")
                        ;
                    }
                    components = component_builder.toString();
                }
                contents
                    .append("* slot[").append(slot_iterator).append("]:\r\n")
                    .append("\t+ name = \"").append(stack.getItem().getName().getString()).append("\"\r\n")
                    .append("\t+ count: ").append( stack.getCount() ).append("\r\n")
                    .append("\t+ components:\r\n").append(components)
                ;
                slot_iterator++;
            }
            if(is_empty) {
                contents.append("! EMPTY !\r\n");
            }
        }

        return contents.toString();
    }
    @Inject(method = "close", at = @At("HEAD"))
    @SuppressWarnings("unchecked")
    private void close(CallbackInfo info) {
        if(ItemLoggerClient.mode != Mode.LOG) { return; }

        HandledScreen<T> screen = (HandledScreen<T>)(Object)this;
        LOGGER.info( "Opened a \"" + screen.getClass() + "\".");

        boolean is_inventory = screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen;
        if(!is_inventory) {
            LOGGER.info(
                containerContentsToMarkdown(screen)
            );
        }
    }
}
