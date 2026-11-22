package me.wheelershigley.www.solace_crates.menus;

import me.wheelershigley.www.solace_crates.api.EntryOutcome;
import me.wheelershigley.www.solace_crates.blocks.CrateBlockEntity;
import me.wheelershigley.www.solace_crates.configurations.PlayersHistoryCodec;
import me.wheelershigley.www.solace_crates.data.CrateDefinition;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.loot.ItemStackOddsTables;
import me.wheelershigley.www.solace_crates.loot.LootHelper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ProbabilitiesMenu extends WrappedImmutableSimpleGui {
    private int page_index = 0;
    private int tick = 0;

    public CrateDefinition crateDefinition;
    private LootContext context;
    private final List<EntryOutcome> displayOutcomes;
    LinkedList<NumberProvider[]> poolNumberProviders;

    public ProbabilitiesMenu(
        @Nullable ServerPlayer player,
        @NotNull LinkedList<NumberProvider[]> poolNumberProviders,
        @NotNull ItemStackOddsTables oddsTables,
        @NotNull LootContext lootContext,
        @Nullable CrateDefinition crateDefinition,
        @Nullable ImmutableSimpleGui parent
    ) {
        this.crateDefinition = crateDefinition;
        this.context = lootContext;
        this.poolNumberProviders = poolNumberProviders;

        List<EntryOutcome> displayOutcomes = oddsTables.getAllEntries(lootContext);
        super(player, displayOutcomes, lootContext, parent);
        this.displayOutcomes = displayOutcomes;

        initializeMenu(page_index);
    }

    @Override
    public void setHeader() {
        CrateMetaItems menuItemsMaker = new CrateMetaItems(crateDefinition);

        /* Key(s) */ {
            int key_position = 0;

            ItemStack standardKeyMenuItem = menuItemsMaker.getStandardKeyMenuItem();
            if(standardKeyMenuItem != ItemStack.EMPTY) {
                setSlot( key_position++, menuItemsMaker.getStandardKeyMenuItem() );
            }

            ItemStack premiumKeyMenuItem = menuItemsMaker.getPremiumKeyMenuItem();
            if(premiumKeyMenuItem != ItemStack.EMPTY) {
                setSlot( key_position++, menuItemsMaker.getPremiumKeyMenuItem() );
            }
        }

        setSlot( 4, menuItemsMaker.getNextItem()                        );
        setSlot( 5, menuItemsMaker.getPoolInformationItem(this.context) );
        // TODO s
        //setSlot( 7, menuItemsMaker.getPityItem()                        );
        //setSlot( 8, menuItemsMaker.getStoreLinkItem()                   );
    }

    @Override
    public void onBodyClick(int index) {}

    @Override
    public String getTranslationKey() {
        return "solace_crates.crate.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        return getMinimumChestMenu(
            2*ROW_LENGTH + this.displayOutcomes.size()
        );
    }

    @Override
    public void onTick() {
        if(tick%20 == 0) {
            tickBody();
        }

        tick++;
    }

    public static class Builder {
        private LinkedList<NumberProvider[]> poolNumberProviders = new LinkedList<>();
        CrateBlockEntity crateBlockEntity;

        public Builder withLootPools(
            @NotNull List<LootPool> pools
        ) {
            this.poolNumberProviders.clear();
            for(LootPool pool : pools) {
                List<NumberProvider> currentProviders = new LinkedList<>();
                if(pool.rolls != null) {
                    currentProviders.add(pool.rolls);
                }
                if(pool.bonusRolls != null) {
                    currentProviders.add(pool.bonusRolls);
                }

                this.poolNumberProviders.add(
                    currentProviders.toArray( new NumberProvider[0] )
                );
            }

            return this;
        }

        public Builder ofCrateBlockEntity(
            @Nullable CrateBlockEntity crateBlockEntity
        ) {
            this.crateBlockEntity = crateBlockEntity;
            return this;
        }

        public ProbabilitiesMenu build(
            @Nullable ServerPlayer player,
            @NotNull ItemStackOddsTables oddsTables,
            @NotNull LootContext lootContext,
            @Nullable CrateDefinition crateDefinition
        ) {
            if(crateBlockEntity == null) {
                return new ProbabilitiesMenu(player, poolNumberProviders, oddsTables, lootContext, crateDefinition, null);
            } else {
                return new ProbabilitiesMenu(
                    player,
                    poolNumberProviders,
                    oddsTables,
                    lootContext,
                    crateDefinition,
                    null
                ) {
                    @Override
                    public String getTranslationKey() {
                        return crateBlockEntity.getIdentifiedTranslationKey();
                    }
                };
            }
        }
    }

    private class CrateMetaItems {
        CrateDefinition crateDefinition;

        public CrateMetaItems(CrateDefinition definition) {
            this.crateDefinition = definition;
        }

        private ItemStack getMetaMenuItem(
            Identifier itemIdentifier,
            String translationKey,
            Style nameStyle, Style loreStyle
        ) {
            if(this.crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            DataComponentMap name_map;
            Item item = ItemsHelper.getItem(itemIdentifier);
            if(item != null) {
                name_map = DataComponentMap.builder()
                    .set(
                        DataComponents.CUSTOM_NAME,
                        Component.translatable( item.getDescriptionId() ).withStyle(nameStyle)
                    )
                    .set(
                        DataComponents.LORE,
                        ItemLore.EMPTY.withLineAdded(
                            Component
                                .translatable( item.getDescriptionId() ).withStyle(loreStyle)
                        )
                    )
                    .build()
                ;
            } else {
                return WrappedImmutableSimpleGui.FILLER;
            }

            return getMenuItem(item, name_map, false, translationKey);
        }

        public ItemStack getStandardKeyMenuItem() {
            if(crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            return getMetaMenuItem(
                    crateDefinition.STANDARD_KEY(),
                "solace_crates.menu.probabilities.standard_key",
                Style.EMPTY.withColor(TextColor.GRAY).withItalic(true),
                Style.EMPTY.withColor(TextColor.YELLOW).withBold(true)
            );
        }
        public ItemStack getPremiumKeyMenuItem() {
            if(crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            return getMetaMenuItem(
                crateDefinition.PREMIUM_KEY(),
                "solace_crates.menu.probabilities.premium_key",
                Style.EMPTY.withColor(TextColor.GRAY).withItalic(true),
                Style.EMPTY.withColor(TextColor.GOLD).withBold(true)
            );
        }
        public ItemStack getNextItem() {
            ItemStack result = getMenuItem(
                Items.PAPER,
                false,
                "solace_crates.menu.probabilities.next_result"
            );

            LootContext context = LootHelper.createLootContext( getPlayer(), crateDefinition );
            LootTable table = crateDefinition.getLootTable();
            if(table == null) {
                return result;
            }

            List<Component> list; {
                list = new LinkedList<>();

                List<ItemStack> realizedOutcomes = new ArrayList<>();
                table.getRandomItems(context, realizedOutcomes::add);

                for(ItemStack stack : realizedOutcomes) {
                    list.add(
                        Component
                            .translatable(
                                "solace_crates.menu.probabilities.rolls.entry_prefix"
                            )
                            .setStyle( Style.EMPTY.withColor(TextColor.GRAY).withItalic(false) )
                            .append( Component.literal(" ") )
                            .append(
                                Component.translatable(
                                    stack.getItem().getDescriptionId()
                                )
                            )
                    );
                }
            }
            result.set(
                DataComponents.LORE,
                new ItemLore(list)
            );
            return result;
        }
        private static final Style ROLL_STYLE = Style.EMPTY.withColor(TextColor.GRAY).withItalic(false);
        public ItemStack getPoolInformationItem(LootContext context) {
            List<Component> poolRollsDescriptions = new LinkedList<>();
            int pool_index = 1;
            for(NumberProvider[] poolProviders : poolNumberProviders) {
                Component currentLine = Component.empty();

                int provider_index = 1;
                for(NumberProvider poolProvider : poolProviders) {
                    EntryOutcome.IntRange range = new EntryOutcome.IntRange(poolProvider, context);
                    if( range.equals(EntryOutcome.IntRange.EMPTY) ) {
                        continue;
                    }

                    if(1 < provider_index) {
                        currentLine = currentLine.copy().append(
                            Component.literal(", ")
                        );
                    }
                    currentLine = currentLine.copy().append(
                        range.toComponent()
                    );
                    provider_index++;
                }
                if( currentLine.getString().isEmpty() ) {
                    currentLine = currentLine.copy().append(
                        Component.literal("?")
                    );
                }

                poolRollsDescriptions.add(
                    Component.translatable(
                        "solace_crates.menu.probabilities.rolls.pool",
                        pool_index++,
                        currentLine
                    ).withStyle(ROLL_STYLE)
                );
            }

            DataComponentMap poolInformationContainer = DataComponentMap.builder()
                .set(
                    DataComponents.LORE,
                    new ItemLore(poolRollsDescriptions)
                )
                .build()
            ;

            return getMenuItem(
                Items.REDSTONE,
                poolInformationContainer,
                false,
                "solace_crates.menu.probabilities.rolls"
            );
        }
        // TODO
        public ItemStack getPityItem() {
            return getMenuItem(
                Items.SUNFLOWER,
                false,
                "solace_crates.menu.probabilities.pity"
            );
        }
        // TODO
        public ItemStack getStoreLinkItem() {
            return getMenuItem(
                Items.WRITTEN_BOOK,
                false,
                "solace_crates.menu.probabilities.link." + "todo"
            );
        }
    }
}
