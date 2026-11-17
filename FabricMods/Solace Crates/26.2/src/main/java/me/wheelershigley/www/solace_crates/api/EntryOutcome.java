package me.wheelershigley.www.solace_crates.api;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.loot.LootTableEvaluator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.awt.*;
import java.util.List;

public class EntryOutcome {
    private final LootPoolEntry entry;
    private final NumberProvider numberProvider;
    private final List<ItemStack> stacks;
    private int position;

    public EntryOutcome(
        LootPoolEntry entry,
        NumberProvider numberProvider,
        List<ItemStack> stacks
    ) {
        this.entry = entry;
        this.numberProvider = numberProvider;
        this.stacks = stacks;
        this.position = 0;
    }

    public EntryOutcome(
        LootPoolEntry entry,
        List<ItemStack> stacks
    ) {
        this.entry = entry;
        this.stacks = stacks;
        this.numberProvider = LootTableEvaluator.getProvider(entry);
        this.position = 0;
    }

    public LootPoolEntry getEntry() {
        return entry;
    }
    public NumberProvider getNumberProvider() {
        return numberProvider;
    }
    public List<ItemStack> getStacks() {
        return this.stacks;
    }

    public ItemStack tick() {
        ItemStack stack = stacks.get(position++);
        position %= stacks.size();
        return stack;
    }

    public IntRange getRange(LootContext context) {
        return new IntRange(
            LootTableEvaluator.getProvider(entry),
            context
        );
    }

    public enum RangeType {
        Constant,
        Uniform,
        Binomial
    }
    public static class IntRange {
        public static final IntRange EMPTY = new IntRange(0, 0);

        private int minimum = 1;
        private int maximum = 1;
        private RangeType rangeType = RangeType.Constant;

        public IntRange(int minimum, int maximum) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public IntRange(NumberProvider provider, LootContext lootContext) {
            if(provider == null) {
                return;
            }

            if(provider instanceof ConstantValue constant) {
                int value = constant.getInt(lootContext);
                this.minimum = value;
                this.maximum = value;
            }

            if(provider instanceof UniformGenerator(NumberProvider minimumProvider, NumberProvider maximumProvider) ) {
                this.minimum = minimumProvider.getInt(lootContext);
                this.maximum = maximumProvider.getInt(lootContext);
                rangeType = RangeType.Uniform;
            }

            if(provider instanceof BinomialDistributionGenerator(NumberProvider n, NumberProvider p) ) {
                this.minimum = 0;
                this.maximum = n.getInt(lootContext);
                rangeType = RangeType.Binomial;
            }
        }

        public int getMinimum() {
            return minimum;
        }
        public int getMaximum() {
            return maximum;
        }
        public RangeType getRangeType() {
            return rangeType;
        }

        public static boolean isSimple(
            final IntRange range,
            final ItemStack referenceStack
        ) {
             return
                 range == null
                 || (
                    range.getRangeType() != null
                    && range.getRangeType().equals(EntryOutcome.RangeType.Constant)
                    && range.getMaximum() <= referenceStack.getMaxStackSize()
                 )
            ;
        }

        public String getTranslationKey() {
            return SolaceCrates.MOD_ID + ".rangetype." + rangeType.toString().toLowerCase();
        }

        public Component toComponent() {
            String rangeString = "(";
            if(rangeType == RangeType.Constant) {
                rangeString += Integer.toString(minimum);
            } else {
                rangeString += minimum +", "+ maximum;
            }
            rangeString += ")";

            return Component
                .translatable("solace_crates.intrange.counts")
                .append(" ")
                .append(
                    Component.translatable( getTranslationKey() )
                )
                .append(rangeString)
            ;
        }

        @Override
        public boolean equals(Object other) {
            if( !(other instanceof IntRange otherRange) ) {
                return false;
            }

            return
                   this.minimum == otherRange.getMinimum()
                && this.maximum == otherRange.getMaximum()
                && this.rangeType == otherRange.getRangeType()
            ;
        }

        @Override
        public String toString() {
            return
                rangeType.toString() +
                "( " + minimum + ", " + maximum + " )"
            ;
        }
    }
}
