package me.wheelershigley.itemlogger.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("containerlogger");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "close", at = @At("HEAD"))
    @SuppressWarnings("unchecked")
    private void close(CallbackInfo info) {
        HandledScreen<T> screen = (HandledScreen<T>)(Object)this;
        LOGGER.info( "Opened a \"" + screen.getClass() + "\".");
        if(  !( screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen )  ) {
            DefaultedList<Slot> slots = screen.getScreenHandler().slots;

            //remove player inventory
            for(int i = 0; i <= 27+8; i++) {
                slots.removeLast();
            }

            StringBuilder contents = new StringBuilder();
            contents
                .append("\n\n# \"")
                    .append( screen.getNarratedTitle().getString() )
                .append(":\n---\n")
            ;

            boolean includes_non_air = false;
            int slot_iterator = 0;
            for(Slot slot : slots) {
                if( slot.getStack().getItem() == Items.AIR) {
                    slot_iterator++;
                    continue;
                }
                includes_non_air = true;

                ItemStack stack = slot.getStack();
                String components; {
                    StringBuilder component_builder = new StringBuilder();
                    Set<ComponentType<?>> components_set = stack.getComponents().getTypes();
                    for(ComponentType<?> component : components_set) {
                        component_builder
                            .append("\t\t\"").append( component.toString() ).append("\": ")
                            .append(
                                stack.get(component)
                            )
                            .append("\n")
                        ;
                    }
                    components = component_builder.toString();
                }
                contents
                    .append("slot[").append(slot_iterator).append("]:\n")
                    .append("\tname = \"").append( stack.getItem().getName().getString() ).append("\"\n")
                    .append("\tamount: ").append( stack.getCount() ).append("\n")
                    .append("\tcomponents:\n").append(components)
                ;
                slot_iterator++;
            }

            LOGGER.info( contents.toString() + (includes_non_air ? "" : "! EMPTY !\n") );
        }
    }
}
