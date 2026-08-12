package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import me.wheelershigley.www.solace_fishing.helpers.MenusHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.getMinimumChestMenu;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isOpenWater;
import static me.wheelershigley.www.solace_fishing.implementations.Catchables.*;

public class ProbabilitiesMenu extends ImmutableSimpleGui {
    private final LinkedHashMap<ItemStack, Double> probabilities;

    public ProbabilitiesMenu(
        ServerLevel level, BlockPos position,
        ServerPlayer player, @Nullable ImmutableSimpleGui parent
    ) {
        LinkedHashMap<ItemStack, Double> probabilitiesCache = calculateProbabilities(level, position);
        MenuType<?> menuType = getMinimumChestMenu( probabilitiesCache.size() );
        this.probabilities = probabilitiesCache;

        super(player, menuType, parent);
    }

    @Override
    public String getTranslationKey() {
        return "solace_fishing.probabilities_menu.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        if( this.probabilities == null) {
            return MenuType.GENERIC_9x1;
        }
        return getMinimumChestMenu( probabilities.size() );
    }

    @Override
    public void initializeMenu() {
        if( probabilities == null || probabilities.isEmpty() ) {
            ItemStack emptinessIndicator = new ItemStack(Items.BARRIER);
            emptinessIndicator.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("solace_fishing.probabilities_menu.empty")
            );
            this.setSlot(4, emptinessIndicator);
            return;
        }

        int fish_index = 0;
        for( Map.Entry<ItemStack, Double> entry : probabilities.entrySet() ) {
            if( MenusHelper.sizeOf( this.getMenuType() ) <= fish_index ) {
                break;
            }

            Item key = entry.getKey().getItem();
            ClimateStatisticItem value = itemsCache.getOrDefault(key, null);

            this.setSlot(
                fish_index++,
                adjustedItem(
                    entry.getKey(),
                    entry.getValue(),
                    value == null ? null : value.getAverageStandardDeviation()
                )
            );
        }
    }


    private static LinkedHashMap<ItemStack, Double> calculateProbabilities(ServerLevel level, BlockPos position) {
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
        return sortedProbabilities;
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
}
