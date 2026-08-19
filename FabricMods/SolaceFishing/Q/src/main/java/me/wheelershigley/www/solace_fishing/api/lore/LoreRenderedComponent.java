package me.wheelershigley.www.solace_fishing.api.lore;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public abstract class LoreRenderedComponent<C> {
    public final C data;

    public LoreRenderedComponent(C customComponent) {
        this.data = customComponent;
    }

    public C getData() {
        return data;
    }

    abstract ItemLore toLore();
    abstract CustomData toCustomData();

    public abstract void set(ItemStack itemStack);
}
