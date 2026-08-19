package me.wheelershigley.www.solace_fishing.api.lore;

import me.wheelershigley.www.solace_fishing.api.NotatedNumber;
import me.wheelershigley.www.solace_fishing.helpers.MathsHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

public class LoreRenderedLengthComponent extends LoreRenderedComponent<Double> {
    private final String CUSTOM_DATA_TAG = "length";

    public LoreRenderedLengthComponent(Double length) {
        super(length);
    }

    @Override
    ItemLore toLore() {
        NotatedNumber length = new NotatedNumber(this.data);
        String prefix = length.setOrderAndGetPrefix();

        List<Component> lore = new ArrayList<>();
        lore.add(
            Component.translatable(
                "solace_fishing.lore.length",
                MathsHelper.percentageRound( length.getNumber() ),
                prefix
            ).withStyle( Style.EMPTY.withItalic(false).withColor(TextColor.GRAY) )
        );
        return new ItemLore(lore);
    }

    @Override
    CustomData toCustomData() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(CUSTOM_DATA_TAG, data);

        return CustomData.of(tag);
    }

    @Override
    public void set(ItemStack itemStack) {
        //existing preExistingCustomData
        CustomData preExistingCustomData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        ItemLore   preExistingLore       = itemStack.getOrDefault(DataComponents.LORE,        ItemLore.EMPTY  );
        CompoundTag tag = preExistingCustomData.copyTag();
        List<Component> lore = new ArrayList<>( preExistingLore.lines() );

        //remove potential duplicate(s)
        tag.remove(CUSTOM_DATA_TAG);
        List<Component> newLore = new ArrayList<>();
        for(Component line : lore) {
            if( !componentIsTranslationOfLength(line) ) {
                continue;
            }
            newLore.add(line);
        }

        //set new data
        tag.merge( this.toCustomData().copyTag() );
        if( tag.isEmpty() ) {
            return;
        }
        newLore.addAll( this.toLore().lines() );

        if( newLore.isEmpty() ) {
            itemStack.remove(DataComponents.LORE);
        } else {
            itemStack.set(
                DataComponents.LORE,
                new ItemLore(newLore)
            );
        }

        itemStack.set(
            DataComponents.CUSTOM_DATA,
            CustomData.of(tag)
        );
    }

    @Unique
    private boolean componentIsTranslationOfLength(Component component) {
        if( component.getContents() instanceof TranslatableContents contents ) {
            return contents.getKey().equals("solace_fishing.lore.length");
        }

        return false;
    }
}
