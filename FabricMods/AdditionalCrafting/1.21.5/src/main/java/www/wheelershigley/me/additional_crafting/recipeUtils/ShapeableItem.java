package www.wheelershigley.me.additional_crafting.recipeUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ShapeableItem {
    public final Item item;

    public final List<ItemStack> chiseled;
    public final List<ItemStack> polished;

    public final List<ItemStack> stair;
    public final List<ItemStack> slab;
    public final List<ItemStack> wall;

    public final List<ItemStack> door;
    public final List<ItemStack> trapdoor;

    public ShapeableItem(
        Item input,
        List<ItemStack> chiseled, List<ItemStack> polished,
        List<ItemStack> stair, List<ItemStack> slab, List<ItemStack> wall,
        List<ItemStack> door,  List<ItemStack> trapdoor
    ) {
        this.item = input;

        this.chiseled = chiseled;
        this.polished = polished;

        this.stair = stair;
        this.slab  = slab;
        this.wall  = wall;

        this.door = door;
        this.trapdoor = trapdoor;
    }
}
