package www.wheelershigley.me.additional_crafting.recipeUtils;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class listHelper {
    @SafeVarargs
    public static List<ItemStack> listOfLists(List<ItemStack>... lists) {
        List<ItemStack> items = new ArrayList<>();
        for(List<ItemStack> list : lists) {
            for(ItemStack item : list) {
                items.add(item);
            }
        }
        return items;
    }
}
