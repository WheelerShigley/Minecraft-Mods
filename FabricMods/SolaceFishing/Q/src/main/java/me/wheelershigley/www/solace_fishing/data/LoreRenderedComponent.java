package me.wheelershigley.www.solace_fishing.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public abstract class LoreRenderedComponent<C> {
    public final C customData;

    public LoreRenderedComponent(C customComponent) {
        this.customData = customComponent;
    }

    abstract ItemLore toLore();
    abstract CustomData toCustomData();

    //abstract static C get(ItemStack itemStack);
    abstract void set(ItemStack itemStack);
}
