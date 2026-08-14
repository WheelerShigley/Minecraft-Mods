package me.wheelershigley.www.solace_fishing.data;

import me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

public class RodAccessoryLoreRenderedComponent extends LoreRenderedComponent<RodAccessories> {
    private static final String
        CUSTOM_DATA_TAG = "accessories",
        HOOK_TAG = "hook",
        LINE_TAG = "line",
        BOBBER_TAG = "bobber"
    ;

    public RodAccessoryLoreRenderedComponent(RodAccessories customComponent) {
        super(customComponent);
    }

    //@Override
    public static RodAccessories get(ItemStack itemStack) {
        if(  !isFishingRod( itemStack.getItem() )  ) {
            return null;
        }

        Map<String, ItemStack> storedItems = getAccessories(itemStack);
        ItemStack
            stored_hook   = storedItems.getOrDefault(HOOK_TAG,   ItemStack.EMPTY),
            stored_line   = storedItems.getOrDefault(LINE_TAG,   ItemStack.EMPTY),
            stored_bobber = storedItems.getOrDefault(BOBBER_TAG, ItemStack.EMPTY)
        ;

        return new RodAccessories(stored_hook, stored_line, stored_bobber);
    }

    @Unique
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

    @Override
    public void set(ItemStack itemStack) {
        // Get Existing components
        ItemLore existingLore   = itemStack.getOrDefault(DataComponents.LORE,        ItemLore.EMPTY  );
        CustomData existingData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

        // Needed components
        ItemLore   lore = toLore();
        CustomData data = toCustomData();

        // Combine Components
        existingLore = removePotentialDuplicates(existingLore);
        lore.lines().addAll( existingLore.lines() );

        final CustomData cleanedData = removeAccessoriesData(existingData);
        data = data.update(
            tag -> {
                tag.merge( cleanedData.copyTag() );
            }
        );

        // Set ItemStack components
        itemStack.remove(DataComponents.LORE);
        itemStack.remove(DataComponents.CUSTOM_DATA);

        if( !lore.lines().isEmpty() ) {
            itemStack.set(DataComponents.LORE, lore);
        }
        if( !data.isEmpty() ) {
            itemStack.set(DataComponents.CUSTOM_DATA, data);
        }
    }

    @Unique
    private CustomData removeAccessoriesData(CustomData customData) {
        return customData.update(
            tag -> {
                tag.remove(CUSTOM_DATA_TAG);
            }
        );
    }

    @Unique
    private ItemLore removePotentialDuplicates(ItemLore lore) {
        ArrayList<Component> lores = new ArrayList<>();

        for(Component line : lore.lines() ) {
            if( componentIsTranslationOfAccessory(line) ) {
                continue;
            }

            lores.add(line);
        }

        return new ItemLore(lores);
    }

    @Unique
    private boolean componentIsTranslationOfAccessory(Component component) {
        Item item = Items.AIR;
        if( component.getContents() instanceof TranslatableContents contents ) {
            item = getItemFromDescriptionId( contents.getKey() );
        }

        return MetaFishingHelper.isFishingAccessory(item);
    }

    @Unique
    private Item getItemFromDescriptionId(String descriptionId) {
        String prefix = "item.";

        if( !descriptionId.startsWith(prefix) ) {
            return null;
        }

        Identifier id = Identifier.parse(
            descriptionId
                .substring( prefix.length() )
                .replace('.', ':')
        );

        Optional< Holder.Reference<Item> > potentialItem = BuiltInRegistries.ITEM.get(id);
        return potentialItem.map(Holder.Reference::value).orElse(Items.AIR);
    }

    @Override
    public CustomData toCustomData() {
        CustomData data = CustomData.EMPTY;
        CompoundTag accessoriesTag = new CompoundTag();

        if( !this.customData.getLine().isEmpty() ) {
            accessoriesTag.put(
                    LINE_TAG,
                toTag( this.customData.getLine() )
            );
        }
        if( !this.customData.getBobber().isEmpty() ) {
            accessoriesTag.put(
                    BOBBER_TAG,
                toTag( this.customData.getBobber() )
            );
        }
        if( !this.customData.getHook().isEmpty() ) {
            accessoriesTag.put(
                    HOOK_TAG,
                toTag( this.customData.getHook() )
            );
        }

        if( !this.customData.isEmpty() ) {
            data = data.update(
                tag -> {
                    tag.put(CUSTOM_DATA_TAG, accessoriesTag);
                }
            );
        }
        return data;
    }

    @Unique
    private Tag toTag(ItemStack itemStack) {
        return ItemStack.CODEC
            .encodeStart(NbtOps.INSTANCE, itemStack)
            .result()
            .orElse(EndTag.INSTANCE)
        ;
    }

    @Override
    public ItemLore toLore() {
        List<Component> potentialLores = new ArrayList<>();
        potentialLores.add(  getItemStackAsTranslatableComponent( customData.getLine()   )  );
        potentialLores.add(  getItemStackAsTranslatableComponent( customData.getBobber() )  );
        potentialLores.add(  getItemStackAsTranslatableComponent( customData.getHook()   )  );

        // Only allow filled lines
        List<Component> lores = new ArrayList<>();
        for(Component potentialLore : potentialLores) {
            if( potentialLore.equals( Component.empty() ) ) {
                continue;
            }
            lores.add(potentialLore);
        }

        return new ItemLore(lores);
    }

    @Unique
    private static Component getItemStackAsTranslatableComponent(ItemStack itemStack) {
        Component result = Component.empty();

        if( itemStack.isEmpty() ) {
            return result;
        }

        return ItemStack.CODEC
            .encodeStart(NbtOps.INSTANCE, itemStack)
            .result()
            .map(
                _ -> Component
                    .translatable( itemStack.getItem().getDescriptionId() )
                    .withStyle( Style.EMPTY.withColor(TextColor.GRAY).withItalic(false) )
            )
            .orElse( Component.empty() )
        ;
    }
}
