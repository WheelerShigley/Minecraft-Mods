package me.wheelershigley.www.solace_crates.menus;

import me.wheelershigley.www.solace_crates.api.EntryOutcome;
import me.wheelershigley.www.solace_crates.blocks.CrateBlock;
import me.wheelershigley.www.solace_crates.data.CrateDefinition;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.mixin.LootTableAccessor;
import me.wheelershigley.www.solace_crates.mixin.LootTableBuilderAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChoiceMenu extends WrappedImmutableSimpleGui {
    private final List<EntryOutcome> displayOutcomes;
    private final CrateDefinition definition;
    private final LootContext context;

    public ChoiceMenu(
        @Nullable ServerPlayer player,
        @Nullable CrateDefinition crateDefinition,
        @NotNull LootContext lootContext,
        @NotNull List<EntryOutcome> displayOutcomes,
        @Nullable ImmutableSimpleGui parent
    ) {
        this.displayOutcomes = displayOutcomes;
        this.definition = crateDefinition;
        this.context = lootContext;

        super(player, displayOutcomes, lootContext, parent);
    }

    @Override
    public void setHeader() {
        ItemStack descriptionStack; {
            descriptionStack = new ItemStack(Items.PAPER);
            descriptionStack.set(
                DataComponents.CUSTOM_NAME,
                Component
                    .translatable("solace_crates.menu.choice.next_item")
                    .withStyle( Style.EMPTY.withColor(TextColor.GRAY).withItalic(false) )
            );
            descriptionStack.set(
                DataComponents.LORE,
                ItemLore.EMPTY.withLineAdded(
                    Component
                        .translatable("solace_crates.menu.choice.description")
                        .withStyle( Style.EMPTY.withColor(TextColor.WHITE).withItalic(false) )
                )
            );
        }

        setSlot(4, descriptionStack);
    }

    @Override
    public void onBodyClick(int index) {
        EntryOutcome forcedOutcome = displayOutcomes.get(index);
        if(forcedOutcome == null) {
            CrateBlock.fail(player, "solace_crates.messages.choice_missing");
            return;
        }
        attemptChoice(forcedOutcome);
        this.close();
    }
    private void attemptChoice(EntryOutcome forcedOutcome) {
        Item keyItem = ItemsHelper.getItem( definition.PREMIUM_KEY() );
        if(keyItem == null) {
            CrateBlock.fail(player, "solace_crates.message.key_definition_missing");
            return;
        }

        LootTable table = definition.getLootTable();
        if(table == null) {
            CrateBlock.fail(player, "solace_crates.messages.loottable_missing");
            return;
        }

        //take a key, return if not
        ItemStack usedStack = player.getActiveItem();
        if(usedStack.getItem() != keyItem) {
            CrateBlock.fail(player, "solace_crates.message.key_missing.none");
            return;
        }
        usedStack.consume(1, player);

        List<ItemStack> outcome = attemptAdjustedRoll(forcedOutcome, table);
        ItemsHelper.giveItemsToPlayer(player, outcome);
        CrateBlock.succeede(player);
        player.awardStat(
            Stats.ITEM_USED.get( usedStack.getItem() )
        );
    }
    private List<ItemStack> attemptAdjustedRoll(
        EntryOutcome forcedOutcome,
        LootTable table
    ) {
        //construct an adjusted table: with one fewer rolls in the chosen pool
        LootTable adjustedTable; {
            List<LootPool> adjustedPools = new LinkedList<>();

            LootPoolEntry entry = forcedOutcome.getEntry();
            List<LootPool> pools = ( (LootTableAccessor)table ).solace_crates$getPools();
            for(LootPool pool : pools) {
                boolean found = false;
                if( !found && poolContains(pool, entry) ) {
                    adjustedPools.add(
                        copyWithRolls( pool, decrement(pool.rolls, 1) )
                    );
                    found = true;
                } else {
                    adjustedPools.add(pool);
                }
            }

            adjustedTable = copyWithPools(table, adjustedPools);
        }

        List<ItemStack> realizedOutcomes; {
            realizedOutcomes = new ArrayList<>();

            adjustedTable.getRandomItems(context, realizedOutcomes::add);
            realizedOutcomes.addAll( forcedOutcome.getStacks() );
        }
        return realizedOutcomes;
    }
    private boolean poolContains(LootPool pool, LootPoolEntry entry) {
        List<LootPoolEntry> entries = new ArrayList<>();
        for(LootPoolEntryContainer container : pool.entries) {
            entries.clear();
            container.expand(context, entries::add);
            if( entries.contains(entry) ) {
                return true;
            }
        }
        return false;
    }
    private NumberProvider decrement(NumberProvider provider, double amount) {
        if(provider instanceof ConstantValue(float value) ) {
            float adjusted = Math.max(0.0f, value - (float)amount);
            return new ConstantValue(adjusted);
        }
        if( provider instanceof UniformGenerator(NumberProvider minimum, NumberProvider maximum) ) {
            NumberProvider adjustedMinimum = decrement(minimum, amount);
            NumberProvider adjustedMaximum = decrement(maximum, amount);
            return new UniformGenerator(adjustedMinimum, adjustedMaximum);
        }
        if( provider instanceof BinomialDistributionGenerator(NumberProvider n, NumberProvider p) ) {
            NumberProvider adjusted = decrement(n, amount);
            return new BinomialDistributionGenerator(adjusted, n);
        }
        throw new IllegalStateException("Impossible NumberProvider instance");
    }
    private LootPool copyWithRolls(LootPool pool, NumberProvider provider) {
        LootPool.Builder adjustedBuilder = LootPool.lootPool();

        adjustedBuilder.setRolls(provider);
        adjustedBuilder.setBonusRolls( pool.bonusRolls );

        for(LootPoolEntryContainer container : pool.entries) {
            adjustedBuilder.add(container);
        }
        for(LootItemCondition condition : pool.conditions) {
            adjustedBuilder.when(condition);
        }
        for(LootItemFunction function : pool.functions) {
            adjustedBuilder.apply(function);
        }

        return adjustedBuilder.build();
    }
    private LootTable copyWithPools(LootTable table, List<LootPool> pools) {
        LootTable.Builder builder = LootTable.lootTable();

        LootTableAccessor accessor = (LootTableAccessor)table;
        List<LootItemFunction> functions = accessor.solace_crates$getFunctions();
        for(LootItemFunction function : functions) {
            builder.apply(function);
        }

        for(LootPool pool : pools) {
            ( (LootTableBuilderAccessor)builder ).solace_crates$getPools().add(pool);
        }

        return builder.build();
    }

    @Override
    public String getTranslationKey() {
        return "solace_crates.menu.choice.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        return getMinimumChestMenu(
            2*ROW_LENGTH + this.displayOutcomes.size()
        );
    }
}
