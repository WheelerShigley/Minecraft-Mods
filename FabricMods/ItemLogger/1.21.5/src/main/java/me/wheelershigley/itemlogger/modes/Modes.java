package me.wheelershigley.itemlogger.modes;

import me.wheelershigley.itemlogger.ItemLogger;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Modes {
    public static Mode[] modes = new Mode[]{
        Mode.OFF,
        Mode.LOG
    };

    public static String toString(Mode mode) {
        if( mode.equals(Mode.OFF) ) {
            return "off";
        }
        if( mode.equals(Mode.LOG) ) {
            return "log";
        }

        return null;
    }
    public static Mode toMode(String name) {
        if( name.equals("off") ) {
            return Mode.OFF;
        }
        if( name.equals("log") ) {
            return Mode.LOG;
        }

        return null;
    }

    public static class Logging {
        public static String containerContentsToMarkdown(@NotNull HandledScreen<?> container) {
            DefaultedList<Slot> slots; {
                slots = container.getScreenHandler().slots;
                //remove player inventory
                for (int i = 0; i <= 35; i++) {
                    slots.removeLast();
                }
            }

            ItemStack stack;
            String components;
            StringBuilder component_builder = new StringBuilder();
            StringBuilder contents = new StringBuilder(); {
                //Title
                contents
                    .append("\r\n\r\n# \"")
                    .append( container.getNarratedTitle().getString() )
                    .append("\"\r\n")
                ;

                final String itemSlot = Text.literal(
                    Text.translatable("item_logger.text.item_slot").getString()
                ).getString();
                final String itemName = Text.literal(
                    Text.translatable("item_logger.text.item_name").getString()
                ).getString();
                final String itemCount = Text.literal(
                    Text.translatable("item_logger.text.item_count").getString()
                ).getString();
                final String itemComponents = Text.literal(
                    Text.translatable("item_logger.text.item_components").getString()
                ).getString();
                final String emptyContainer = Text.literal(
                    Text.translatable("item_logger.text.empty_container").getString()
                ).getString();

                //Contents
                boolean isContainerEmpty = true;
                int slot_iterator = 0;
                for(Slot slot : slots) {
                    if(slot.getStack().getItem() == Items.AIR) {
                        slot_iterator++;
                        continue;
                    }
                    isContainerEmpty = false;

                    stack = slot.getStack();
                    /*components*/ {
                        component_builder.setLength(0);

                        Set< ComponentType<?> > components_set = stack.getComponents().getTypes();
                        for(ComponentType<?> component : components_set) {
                            component_builder
                                .append("\t\t- ")
                                .append("\"").append( component.toString() ).append("\": ")
                                .append( stack.get(component) )
                                .append("\r\n")
                            ;
                        }
                        components = component_builder.toString();
                    }
                    contents
                        .append("* ").append(itemSlot).append('[')
                            .append(slot_iterator)
                            .append("]:\r\n")
                        .append("\t+ ").append(itemName).append(" = \"")
                            .append( stack.getItem().getName().getString() )
                            .append("\"\r\n")
                        .append("\t+ ").append(itemCount).append(": ")
                            .append( stack.getCount() )
                            .append("\r\n")
                        .append("\t+ ").append(itemComponents).append(":\r\n")
                            .append(components)
                    ;
                    slot_iterator++;
                }
                if(isContainerEmpty) {
                    contents.append("* ").append(emptyContainer).append("\r\n");
                }
            }

            return contents.toString();
        }
    }

}
