package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isOpenWater;
import static me.wheelershigley.www.solace_fishing.implementations.Catchables.*;

@Deprecated
public class ProbabilitiesMenu extends ImmutableChestMenu {
    private static LinkedHashMap<ItemStack, Double> probabilities;
    private static final MenuType<ChestMenu> MENU_TYPE;
    private static final Container CONTAINER; static {
        CONTAINER = ProbabilitiesMenu.getMinimumContainer();
        MENU_TYPE = switch( CONTAINER.getContainerSize() ) {
            case 1*9 -> MenuType.GENERIC_9x1;
            case 2*9 -> MenuType.GENERIC_9x2;
            case 3*9 -> MenuType.GENERIC_9x3;
            case 4*9 -> MenuType.GENERIC_9x4;
            case 5*9 -> MenuType.GENERIC_9x5;
            default  -> MenuType.GENERIC_9x6;
        };

        if( probabilities.isEmpty() ) {
            ItemStack emptinessIndicator = new ItemStack(Items.BARRIER);
            emptinessIndicator.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("solace_fishing.probabilities_menu.empty")
            );
            CONTAINER.setItem(4, emptinessIndicator);
        }

        int fish_index = 0;
        for( Map.Entry<ItemStack, Double> entry : probabilities.entrySet() ) {
            if(9*6 <= fish_index) {
                break;
            }

            Item key = entry.getKey().getItem();
            ClimateStatisticItem value = itemsCache.getOrDefault(key, null);

            CONTAINER.setItem(
                fish_index++,
                adjustedItem(
                    entry.getKey(),
                    entry.getValue(),
                    value == null ? null : value.getAverageStandardDeviation()
                )
            );
        }
    }

    public ProbabilitiesMenu(
        ServerLevel level, BlockPos position,
        Inventory inventory
    ) {
        calculateProbabilities(level, position);
        super(
            ProbabilitiesMenu.MENU_TYPE,
            0,
            inventory,
            ProbabilitiesMenu.CONTAINER,
            ProbabilitiesMenu.CONTAINER.getContainerSize()/9
        );
    }

    private static Container getMinimumContainer() {
        for(int height = 1; height < 5; height++) {
            if (probabilities.size() <= 9*height) {
                return new SimpleContainer(9*height);
            }
        }
        return new SimpleContainer(9*6);
    }

    private static void calculateProbabilities(ServerLevel level, BlockPos position) {
        boolean withTreasure = isOpenWater(level, position);

        ClimateData locationData = ClimateData.sample(level, position);
        Set<ClimateStatisticItem> validItems = getValidCatchesAt(locationData, withTreasure, true);
        Map<ClimateStatisticItem, Double> weights = normalizeWeights(
            getWeightsForItems(validItems, locationData)
        );

        Map<ClimateStatisticItem, Double> sortedWeights = weights.entrySet()
            .stream()
            .sorted(
                Map.Entry.<ClimateStatisticItem, Double>comparingByValue().reversed()
            )
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a,
                    LinkedHashMap::new
                )
            )
        ;

        LinkedHashMap<ItemStack, Double> sortedProbabilities = new LinkedHashMap<>();
        for( Map.Entry<ClimateStatisticItem, Double> entry : sortedWeights.entrySet() ) {
            sortedProbabilities.put( entry.getKey().getItem(), entry.getValue() );
        }
        probabilities = sortedProbabilities;
    }

    private static ItemStack adjustedItem(
            ItemStack item,
            double probability,
            @Nullable Double standard_deviation
    ) {
        /* Rarity, based on standard-deviation (s)
           A s of 0.5 means it is likely always available (very common);
           0.25 means it is somewhat common
           0.125 means uncommon (and so forth)
         */
        Rarity rarity = Rarity.COMMON;
        if(standard_deviation != null) {
            if(standard_deviation < 1.0/8.0) {
                rarity = Rarity.UNCOMMON;
            }
            if(standard_deviation < 1.0/32.0) {
                rarity = Rarity.RARE;
            }
            if(standard_deviation < 1.0/64.0) {
                rarity = Rarity.EPIC;
            }
        }
        item.set(
            DataComponents.RARITY,
            rarity
        );

        // Probability
        TextColor color = switch(rarity) {
            case UNCOMMON -> TextColor.YELLOW;
            case RARE     -> TextColor.AQUA;
            case EPIC     -> TextColor.LIGHT_PURPLE;
            default       -> TextColor.GRAY;
        };
        double simplifiedChance = Math.round(10000.0*probability)/100.0;

        // Item Lore
        List<Component> lines = new ArrayList<>();
        lines.add(
            Component.literal(
                Double.toString(simplifiedChance) + "%"
            ).withColor(color)
        );

        ItemLore existingLore = item.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lines.addAll( existingLore.lines() );

        item.set( DataComponents.LORE, new ItemLore(lines) );


        return item;
    }

    @Override
    public void addChildren() {}

    @Override
    public MenuType<ChestMenu> getMenuType() {
        return ProbabilitiesMenu.MENU_TYPE;
    }
    @Override
    public Container getContainer() {
        return SellMenu.container;
    }

    @Override
    public String getTranslationKey() {
        return "solace_fishing.probabilities_menu.title";
    }
}
