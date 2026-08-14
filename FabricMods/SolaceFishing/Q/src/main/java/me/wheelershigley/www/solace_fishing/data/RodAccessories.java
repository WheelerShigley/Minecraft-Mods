package me.wheelershigley.www.solace_fishing.data;

import me.wheelershigley.www.solace_fishing.implementations.Bobber;
import me.wheelershigley.www.solace_fishing.implementations.Hook;
import me.wheelershigley.www.solace_fishing.implementations.Line;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

public class RodAccessories {
    private static final String
        CUSTOM_DATA_TAG = "accessories",
        HOOK_TAG = "hook",
        LINE_TAG = "line",
        BOBBER_TAG = "bobber",
        SEPERATOR = ": "
    ;

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

        Map<String, ItemStack> storedItems = getAccessories(rod);
        ItemStack
            stored_hook   = storedItems.getOrDefault(HOOK_TAG,   ItemStack.EMPTY),
            stored_line   = storedItems.getOrDefault(LINE_TAG,   ItemStack.EMPTY),
            stored_bobber = storedItems.getOrDefault(BOBBER_TAG, ItemStack.EMPTY)
        ;

        return new RodAccessories(stored_hook, stored_line, stored_bobber);
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


    public static Map<String, ItemStack> getAccessories(ItemStack itemStack) {
        Map<String, ItemStack> accessories = new HashMap<>();

        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if(customData == null) {
            return accessories;
        }

        CompoundTag tag = customData.copyTag();
        if( !tag.contains(CUSTOM_DATA_TAG) ) {
            return accessories;
        }

        CompoundTag accessoriesTag = tag.getCompoundOrEmpty(CUSTOM_DATA_TAG);
        for( String key : accessoriesTag.keySet() ) {
            Tag current = accessoriesTag.get(key);

            ItemStack.CODEC
                .parse(NbtOps.INSTANCE, current)
                .result()
                .ifPresent(
                    stack -> accessories.put(key, stack)
                )
            ;
        }

        return accessories;
    }

    public void set(ItemStack itemStack) {
        CompoundTag accessoriesTag = new CompoundTag();
        ArrayList<Component> accessoriesLore = new ArrayList<>();

        //TODO Abstract the below tripple into a function [and apply it, here]
        if( !this.getLine().isEmpty() ) {
            ItemStack.CODEC
                .encodeStart( NbtOps.INSTANCE, this.getLine() )
                .result()
                .ifPresent(
                    tag -> {
                        accessoriesTag.put(LINE_TAG, tag);
                        accessoriesLore.add(
                            Component
                                .literal(LINE_TAG+SEPERATOR)
                                .append(
                                    this.getLine().getHoverName()
                                )
                                .withStyle(
                                    Style.EMPTY.withColor(TextColor.GRAY)
                                )
                        );
                    }
                )
            ;
        }
        if( !this.getBobber().isEmpty() ) {
            ItemStack.CODEC
                .encodeStart( NbtOps.INSTANCE, this.getBobber() )
                .result()
                .ifPresent(
                    tag -> {
                        accessoriesTag.put(BOBBER_TAG, tag);
                        accessoriesLore.add(
                            Component
                                .literal(BOBBER_TAG+SEPERATOR)
                                .append(
                                    this.getBobber().getHoverName()
                                )
                                .withStyle(
                                    Style.EMPTY.withColor(TextColor.GRAY)
                                )
                        );
                    }
                )
            ;
        }
        if( !this.getHook().isEmpty() ) {
            ItemStack.CODEC
                .encodeStart( NbtOps.INSTANCE, this.getHook() )
                .result()
                .ifPresent(
                    tag -> {
                        accessoriesTag.put(HOOK_TAG, tag);
                        accessoriesLore.add(
                            Component
                                .literal(HOOK_TAG+SEPERATOR)
                                .append(
                                    this.getHook().getHoverName()
                                )
                                .withStyle(
                                    Style.EMPTY.withColor(TextColor.GRAY)
                                )
                        );
                    }
                )
            ;
        }

        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        ItemLore   loreData   = itemStack.getOrDefault(DataComponents.LORE,        ItemLore.EMPTY  );

        customData = customData.update(
            tag -> {
                tag.remove(CUSTOM_DATA_TAG);
            }
        );
        if( !accessoriesTag.isEmpty() ) {
            customData = customData.update(
                tag -> {
                    tag.put(CUSTOM_DATA_TAG, accessoriesTag);
                }
            );
        }
        /* Remove potentially-duplicate Lores */ {
            ArrayList<Component> newLoreLines = new ArrayList<>();
            loreData.lines().forEach(
                line -> {
                    boolean include = true;
                    String entry = line.getString();
                    if( entry.startsWith(LINE_TAG+SEPERATOR) ) {
                        include = false;
                    }
                    if( entry.startsWith(BOBBER_TAG+SEPERATOR) ) {
                        include = false;
                    }
                    if( entry.startsWith(HOOK_TAG+SEPERATOR) ) {
                        include = false;
                    }

                    if(include) {
                        newLoreLines.add(line);
                    }
                }
            );

            //newestLines
            newLoreLines.addAll(accessoriesLore);

            loreData = new ItemLore(newLoreLines);
        }

        if( customData.isEmpty() ) {
            itemStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            itemStack.set(DataComponents.CUSTOM_DATA, customData);
        }
        if( loreData.lines().isEmpty() ) {
            itemStack.remove(DataComponents.LORE);
        } else {
            itemStack.set(DataComponents.LORE, loreData);
        }
    }

    @Deprecated
    public static void removeAccessories(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if(customData == null) {
            return;
        }

        CompoundTag tag = customData.copyTag();
        tag.remove(CUSTOM_DATA_TAG);
        if( tag.isEmpty() ) {
            itemStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            itemStack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
            );
        }
    }

    public String toString() {
        boolean hasPrevious = false;
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        if( !this.getLine().isEmpty() ) {
            builder
                .append(LINE_TAG)
                .append(": ")
                .append( this.getLine() )
            ;
            hasPrevious = true;
        }
        if( !this.getBobber().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append(BOBBER_TAG)
                .append(": ")
                .append( this.getBobber() )
            ;
            hasPrevious = true;
        }
        if( !this.getHook().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append(HOOK_TAG)
                .append(": ")
                .append( this.getHook() )
            ;
        }

        builder.append('}');
        return builder.toString();
    }
}
