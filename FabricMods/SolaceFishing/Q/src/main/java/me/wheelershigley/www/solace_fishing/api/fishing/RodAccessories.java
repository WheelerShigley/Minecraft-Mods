package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.Constants;
import me.wheelershigley.www.solace_fishing.api.ResultCategory;
import me.wheelershigley.www.solace_fishing.implementations.Bobber;
import me.wheelershigley.www.solace_fishing.implementations.Hook;
import me.wheelershigley.www.solace_fishing.implementations.Line;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RodAccessories {
    ItemStack hook, line, bobber;

    public RodAccessories() {}
    public RodAccessories(ItemStack hook, ItemStack line, ItemStack bobber) {
        this.hook = hook;
        this.line = line;
        this.bobber = bobber;
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

    private final Consumer<Item> onBreak = new Consumer<Item>() {
        @Override
        public void accept(Item item) {
            item = Items.AIR;
        }
    };
    public void damage(ServerLevel level, ServerPlayer player) {
        if( this.getHook().isDamageableItem() ) {
            this.hook.hurtAndBreak(1, level, player, onBreak);
        }
        if( this.getBobber().isDamageableItem() ) {
            this.bobber.hurtAndBreak(1, level, player, onBreak);
        }
        if( this.getLine().isDamageableItem() ) {
            this.line.hurtAndBreak(1, level, player, onBreak);
        }
    }
    public int mend(int experience, Level level) {
        int repair_amount = 0;

        ItemStack[] items = new ItemStack[]{this.hook, this.line, this.bobber};
        for(ItemStack item : items) {
            if( item.isDamageableItem() && hasMending(item, level) ) {
                repair_amount = Math.min(experience, item.getDamageValue() );
                item.setDamageValue( item.getDamageValue() - repair_amount );
                experience -= repair_amount;
                if(experience <= 0) {
                    return 0;
                }
            }
        }

        return experience;
    }
    private static boolean hasMending(ItemStack itemStack, Level level) {
        return 0 < EnchantmentHelper.getItemEnchantmentLevel(
                level
                    .registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.MENDING)
                ,
                itemStack
        );
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

    public int getLuck() {
        int accumulator = 0;

        if( !this.getLine().isEmpty() ) {
            accumulator += getLuck( this.getLine() );
        }
        if( !this.getBobber().isEmpty() ) {
            accumulator += getLuck( this.getBobber() );
        }
        if( !this.getHook().isEmpty() ) {
            accumulator += getLuck( this.getHook() );
        }

        return accumulator;
    }
    private int getLuck(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag().getInt(Constants.LUCK_TAG).orElse(0);
    }

    public Map<ResultCategory, Double> getCategoryWeightRatios(final boolean isInOpenWater) {
        Map<ResultCategory, Double> weightRatios = getDefaultWeightRatios(isInOpenWater);

        final Map<ResultCategory, Double> accessoryRations; {
            final Map<ResultCategory, Double> hookRatios   = getWeightRatios( this.getHook()   );
            final Map<ResultCategory, Double> bobberRatios = getWeightRatios( this.getBobber() );
            final Map<ResultCategory, Double> lineRatios   = getWeightRatios( this.getLine()   );
            accessoryRations = multiplyRatios(hookRatios, bobberRatios, lineRatios);
        }

        for(Map.Entry<ResultCategory, Double> entry : weightRatios.entrySet() ) {
            if(  accessoryRations.containsKey( entry.getKey() )  ) {
                weightRatios.put(
                    entry.getKey(),
                    entry.getValue() * accessoryRations.get( entry.getKey() )
                );
            }
        }

        //normalization
        double sum_weight = weightRatios.values().stream().reduce(0.0, Double::sum);
        weightRatios.values().forEach(
            weight -> weight = weight/sum_weight
        );

        return weightRatios;
    }
    @SafeVarargs
    private Map<ResultCategory, Double> multiplyRatios(final Map<ResultCategory, Double>... weightRatioses) {
        Map<ResultCategory, Double> weightRatios = new HashMap<>();

        for( Map<ResultCategory, Double> ratios : weightRatioses ) {
            for(  Map.Entry<ResultCategory, Double> ratio : ratios.entrySet() ) {
                if(  !weightRatios.containsKey( ratio.getKey() )  ) {
                    weightRatios.put( ratio.getKey(), ratio.getValue() );
                } else {
                    weightRatios.put(
                        ratio.getKey(),
                        weightRatios.get( ratio.getKey() ) + ratio.getValue()
                    );
                }
            }
        }

        return weightRatios;
    }
    private Map<ResultCategory, Double> getWeightRatios(final ItemStack itemStack) {
        if( itemStack == null || itemStack.isEmpty() ) {
            return new HashMap<>();
        }

        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        double catch_weight    = customData.copyTag().getDoubleOr(Constants.CATCH_MULTIPLIER_TAG,    0.0);
        double treasure_weight = customData.copyTag().getDoubleOr(Constants.TREASURE_MULTIPLIER_TAG, 0.0);
        double trash_weight    = customData.copyTag().getDoubleOr(Constants.TRASH_MULTIPLIER_TAG,    0.0);

        Map<ResultCategory, Double> weightRatios = new HashMap<>();
        if(catch_weight != 0.0) {
            weightRatios.put(ResultCategory.Catch, catch_weight);
        }
        if(treasure_weight != 0.0) {
            weightRatios.put(ResultCategory.Treasure, treasure_weight);
        }
        if(trash_weight != 0.0) {
            weightRatios.put(ResultCategory.Trash, trash_weight);
        }
        return weightRatios;
    }
    private Map<ResultCategory, Double> getDefaultWeightRatios(final boolean isInOpenWater) {
        double treasure_weight = (isInOpenWater ? 0.05 : 0.00);
        double catch_weight    = 0.9 - treasure_weight;
        double trash_weight    = 0.1;

        Map<ResultCategory, Double> defaultWeightRatios = new HashMap<>();
        defaultWeightRatios.put(ResultCategory.Treasure, treasure_weight);
        defaultWeightRatios.put(ResultCategory.Catch,    catch_weight   );
        defaultWeightRatios.put(ResultCategory.Trash,    trash_weight   );

        return defaultWeightRatios;
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
            builder
                .append("Line: ")
                .append( this.getLine() )
            ;
            hasPrevious = true;
        }
        if( !this.getBobber().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append("Bobber: ")
                .append( this.getBobber() )
            ;
            hasPrevious = true;
        }
        if( !this.getHook().isEmpty() ) {
            builder
                .append(hasPrevious ? "; " : "")
                .append("Hook: ")
                .append( this.getHook() )
            ;
        }

        builder.append('}');
        return builder.toString();
    }
}
