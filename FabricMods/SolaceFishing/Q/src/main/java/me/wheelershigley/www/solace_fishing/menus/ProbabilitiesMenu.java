package me.wheelershigley.www.solace_fishing.menus;

import com.mojang.datafixers.util.Pair;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimateData;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import me.wheelershigley.www.solace_fishing.api.fishing.RodAccessories;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedRodAccessoryComponent;
import me.wheelershigley.www.solace_fishing.helpers.ItemsHelper;
import me.wheelershigley.www.solace_fishing.helpers.MathsHelper;
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

import static me.wheelershigley.www.solace_fishing.helpers.MathsHelper.percentageRound;
import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.getMinimumChestMenu;
import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.setContextRow;
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

            RodAccessories accessories = LoreRenderedRodAccessoryComponent.get(rod, level);

            float luck = player.getLuck() + (float)getLuckOfRod(level, rod);
            if(accessories != null) {
                luck += accessories.getLuck();
            }

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
            ClimatePreferencedItem value = itemsCache.getOrDefault(key, null);

            this.setSlot(
                ROW_LENGTH + fish_index++,
                getProbabilityMenuItem(
                    entry.getKey(),
                    entry.getValue(),
                    value == null ? null : value.getAveragePickiness()
                )
            );
        }
    }

    //integrate luck, rods, and accessories
    private static LinkedHashMap<ItemStack, Double> calculateProbabilities(ServerLevel level, BlockPos position, FishingContext context) {
        boolean withTreasure = isOpenWater(level, position);

        Set<ClimatePreferencedItem> validItems = getWeightedValidCatches(context, withTreasure, true);
        Map<ClimatePreferencedItem, Double> weights = normalizeWeights(
            getWeightsForItems( validItems, context.environment() )
        );

        Map<ClimatePreferencedItem, Double> sortedWeights = weights.entrySet()
            .stream()
            .sorted(
                Map.Entry.<ClimatePreferencedItem, Double>comparingByValue().reversed()
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
        for( Map.Entry<ClimatePreferencedItem, Double> entry : sortedWeights.entrySet() ) {
            sortedProbabilities.put( entry.getKey().getItem(), entry.getValue() );
        }
        return sortedProbabilities;
    }

    private static ItemStack getProbabilityMenuItem(
        ItemStack sourceStack,
        double probability,
        @Nullable Double pickiness
    ) {
        ItemStack itemStack = ItemsHelper.getMenuItem(sourceStack.getItem(), false, null);

        Rarity rarity = Rarity.COMMON;
        if(pickiness != null) {
            if(1.0 <= pickiness) {
                rarity = Rarity.UNCOMMON;
            }
            if(2.0 <= pickiness) {
                rarity = Rarity.RARE;
            }
            if(4.0 <+ pickiness) {
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
        Pair<Double, Integer> notatedChance = MathsHelper.notate(100.0*chance);

        //force to be fixed-point
        while( 0 < notatedChance.getSecond() ) {
            notatedChance = new Pair<>(
                10.0 * notatedChance.getFirst(),
                notatedChance.getSecond() - 1
            );
        }

        chance = percentageRound( notatedChance.getFirst() );
        int order = notatedChance.getSecond();
        double remainder = percentageRound(chance % 1);
        chance = (int)chance;

        //when rounded up, re-adjust
        if(order < 0 && 10.0 <= chance) {
            chance /= 10.0;
            order += 1;
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
        if(order < 0) {
            stringBuilder.append('e').append(order);
        }

        return stringBuilder.toString();
    }
}
