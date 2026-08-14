package me.wheelershigley.www.solace_fishing.data;

import me.wheelershigley.www.solace_fishing.implementations.Bobber;
import me.wheelershigley.www.solace_fishing.implementations.Hook;
import me.wheelershigley.www.solace_fishing.implementations.Line;
import me.wheelershigley.www.solace_fishing.registrations.CustomComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

public class RodAccessories {
    ItemStack
        hook,
        line,
        bobber
    ;

    public RodAccessories() {}
    public RodAccessories(ItemStack hook, ItemStack line, ItemStack bobber) {
        this.hook = hook;
        this.line = line;
        this.bobber = bobber;
    }

    public static @Nullable RodAccessories get(ItemStack rod) {
        if(  !isFishingRod( rod.getItem() )  ) {
            return null;
        }

        ItemStack
            stored_hook   = ItemStack.EMPTY,
            stored_line   = ItemStack.EMPTY,
            stored_bobber = ItemStack.EMPTY
        ; {
        Map<String, ItemStack> storedItems = rod.get(CustomComponents.STORED_ITEMS);
            if(storedItems != null) {
                stored_hook   = storedItems.getOrDefault("hook",   ItemStack.EMPTY);
                stored_line   = storedItems.getOrDefault("line",   ItemStack.EMPTY);
                stored_bobber = storedItems.getOrDefault("bobber", ItemStack.EMPTY);
            }
        }

        return new RodAccessories(stored_hook, stored_line, stored_bobber);
    }

    public void set(ItemStack item) {
        Map<String, ItemStack> items = new HashMap<>();
        if( !this.getHook().isEmpty() ) {
            items.put( "hook", this.getHook() );
        }
        if( !this.getLine().isEmpty() ) {
            items.put( "line", this.getLine() );
        }
        if( !this.getBobber().isEmpty() ) {
            items.put( "bobber", this.getBobber() );
        }

        if( items.isEmpty() ) {
            item.remove(CustomComponents.STORED_ITEMS);
        } else {
            item.set(CustomComponents.STORED_ITEMS, items);
        }
    }

    public ItemStack attemptSwap(ItemStack itemStack) {
        ItemStack returnStack = ItemStack.EMPTY;

        Item item = itemStack.getItem();
        if(item instanceof Hook) {
            returnStack = this.getHook().copy();
            this.setHook(itemStack);
        }
        if(item instanceof Line) {
            returnStack = this.getLine().copy();
            this.setLine(itemStack);
        }
        if(item instanceof Bobber) {
            returnStack = this.getBobber().copy();
            this.setBobber(itemStack);
        }

        return returnStack;
    }

    public boolean hasAnInstanceOf(Item item) {
        if(item instanceof Hook) {
            return !this.getHook().isEmpty();
        }
        if(item instanceof Line) {
            return !this.getLine().isEmpty();
        }
        if(item instanceof Bobber) {
            return !this.getBobber().isEmpty();
        }
        return false;
    }

    public boolean isEmpty() {
        return
               this.getHook().isEmpty()
            && this.getLine().isEmpty()
            && this.getBobber().isEmpty()
        ;
    }

    public ItemStack pop() {
        ItemStack returnStack = ItemStack.EMPTY;

        if( !this.getHook().isEmpty() ) {
            returnStack = this.getHook();
            this.removeHook();
            return returnStack;
        }
        if( !this.getBobber().isEmpty() ) {
            returnStack = this.getBobber();
            this.removeBobber();
            return returnStack;
        }
        if( !this.getLine().isEmpty() ) {
            returnStack = this.getLine();
            this.removeLine();
            return returnStack;
        }

        return returnStack;
    }

    public boolean setHook(ItemStack hook) {
        if(hook.getItem() instanceof Hook) {
            this.hook = hook;
            return true;
        }
        return false;
    }
    public boolean setLine(ItemStack line) {
        if(line.getItem() instanceof Line) {
            this.line = line;
            return true;
        }
        return false;
    }
    public boolean setBobber(ItemStack bobber) {
        if(bobber.getItem() instanceof Bobber) {
            this.bobber = bobber;
            return true;
        }
        return false;
    }

    public ItemStack getHook() {
        if(hook == null) {
            return ItemStack.EMPTY;
        }
        return hook;
    }
    public ItemStack getLine() {
        if(line == null) {
            return ItemStack.EMPTY;
        }
        return line;
    }
    public ItemStack getBobber() {
        if(bobber == null) {
            return ItemStack.EMPTY;
        }
        return bobber;
    }

    public void removeHook() {
        this.hook = ItemStack.EMPTY;
    }
    public void removeLine() {
        this.line = ItemStack.EMPTY;
    }
    public void removeBobber() {
        this.bobber = ItemStack.EMPTY;
    }

    public String toString() {
        boolean hasPrevious = false;
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        if( !this.getLine().isEmpty() ) {
            builder.append("line: ").append( this.getLine() );
            hasPrevious = true;
        }
        if( !this.getBobber().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append("bobber: ")
                .append( this.getBobber() )
            ;
            hasPrevious = true;
        }
        if( !this.getHook().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append("hook: ")
                .append( this.getHook() )
            ;
        }

        builder.append('}');
        return builder.toString();
    }
}
