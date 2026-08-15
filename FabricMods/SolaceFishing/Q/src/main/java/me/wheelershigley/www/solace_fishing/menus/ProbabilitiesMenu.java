package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.data.*;
import me.wheelershigley.www.solace_fishing.helpers.ItemsHelper;
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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.getMinimumChestMenu;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.*;
import static me.wheelershigley.www.solace_fishing.implementations.Catchables.*;

public class ProbabilitiesMenu extends ImmutableSimpleGui {
    private final FishingContext context;
    private final LinkedHashMap<ItemStack, Double> probabilities;

    public ProbabilitiesMenu(
        ServerLevel level, BlockPos position,
        ServerPlayer player, @Nullable ImmutableSimpleGui parent
    ) {
        ItemStack rod = getFirstFishingRod(player);
        if(rod == null) {
            //generic rod
            rod = new ItemStack(Items.FISHING_ROD);
        }
        FishingContext subContext; {
            Block medium = level.getBlockState(position).getBlock();

            float luck = player.getLuck() + (float)getLuckOfRod(level, rod);

            RodAccessories accessories = RodAccessoryLoreRenderedComponent.get(rod);

            ClimateData environment = ClimateData.sample(level, position);

            subContext = new FishingContext(
                medium, rod.getItem(), luck, accessories, environment
            );
        }

        LinkedHashMap<ItemStack, Double> probabilitiesCache = calculateProbabilities(level, position, subContext);
        MenuType<?> menuType = getMinimumChestMenu( probabilitiesCache.size()+ROW_LENGTH );
        this.context = subContext;
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
            return MenuType.GENERIC_9x2;
        }
        return getMinimumChestMenu( probabilities.size()+ROW_LENGTH );
    }

    @Override
    public void initializeMenu() {
        // Top row is for context-information
        setContextRow(this, context, 0);

        if( probabilities == null || probabilities.isEmpty() ) {
            ItemStack emptinessIndicator = new ItemStack(Items.BARRIER);
            emptinessIndicator.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("solace_fishing.probabilities_menu.empty")
            );
            this.setSlot(ROW_LENGTH+4, emptinessIndicator);
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
                ROW_LENGTH + fish_index++,
                getProbabilityMenuItem(
                    entry.getKey(),
                    entry.getValue(),
                    value == null ? null : value.getAverageStandardDeviation()
                )
            );
        }
    }

    //integrate luck, rods, and accessories
    private static LinkedHashMap<ItemStack, Double> calculateProbabilities(ServerLevel level, BlockPos position, FishingContext context) {
        boolean withTreasure = isOpenWater(level, position);

        Set<ClimateStatisticItem> validItems = getWeightedValidCatches(context, withTreasure, true);
        Map<ClimateStatisticItem, Double> weights = normalizeWeights(
            getWeightsForItems( validItems, context.environment() )
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

    private static ItemStack getProbabilityMenuItem(
        ItemStack sourceStack,
        double probability,
        @Nullable Double standard_deviation
    ) {
        ItemStack itemStack = ItemsHelper.getMenuItem(sourceStack.getItem(), false, null);

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
        itemStack.set(
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

        // Item Lore
        List<Component> lines = new ArrayList<>();
        lines.add(
            Component.literal(
                getHumanReadablePercentage(probability)
            ).withColor(color)
        );

        ItemLore existingLore = sourceStack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lines.addAll( existingLore.lines() );

        itemStack.set( DataComponents.LORE, new ItemLore(lines) );


        return itemStack;
    }

    private static String getHumanReadablePercentage(double chance) {
        //transform to percentage
        chance *= 100.0;

        int scientific_notation_order = 0;
        while(chance < 1.0-Double.MIN_NORMAL) {
            scientific_notation_order -= 1;
            chance *= 10.0;
        }

        chance = percentageRound(chance);
        double remainder = percentageRound(chance % 1);
        chance = (int)chance;

        //when rounded up, re-adjust
        if(scientific_notation_order < 0 && 10.0 <= chance) {
            chance /= 10.0;
            scientific_notation_order += 1;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if(remainder == 0.0) {
            stringBuilder.append(
                Integer.toString( (int) chance)
            );
        } else {
            stringBuilder.append(
                percentageRound(chance +remainder)
            );
        }

        stringBuilder.append('%');
        if(scientific_notation_order < 0) {
            stringBuilder.append('e').append(scientific_notation_order);
        }

        return stringBuilder.toString();
    }

    private static double percentageRound(double chance) {
        return Math.round(100.0*chance)/100.0;
    }
}
