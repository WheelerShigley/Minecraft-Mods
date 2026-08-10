package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.implementations.Catchables.getValidCatchesAt;
import static me.wheelershigley.www.solace_fishing.implementations.Catchables.getWeightsForItems;

public class ProbabilitiesMenu extends ImmutableChestMenu {
    public ProbabilitiesMenu(ServerLevel level, BlockPos position) {
        calculateProbabilities(level, position);
    }

    private LinkedHashMap<ItemStack, Double> probabilities;
    private MenuType<ChestMenu> menuType = MenuType.GENERIC_9x6;

    @Override
    public String getTranslationKey() {
        return "solace_fishing.probabilities_menu.title";
    }

    @Override
    public MenuType<ChestMenu> getMenuType() {
        return menuType;
    }

    @Override
    public Container getContainer() {
        Container container = getMinimumContainer();
        menuType = switch( container.getContainerSize() ) {
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
            container.setItem(4, emptinessIndicator);
        }

        int fish_index = 0;
        for( Map.Entry<ItemStack, Double> entry : probabilities.entrySet() ) {
            if(9*6 <= fish_index) {
                break;
            }
            container.setItem(
                fish_index++,
                entry.getKey()
            );
        }

        return container;
    }

    private Container getMinimumContainer() {
        for(int height = 1; height < 5; height++) {
            if (probabilities.size() <= 9*height) {
                return new SimpleContainer(9*height);
            }
        }
        return new SimpleContainer(9*6);
    }

    private void calculateProbabilities(ServerLevel level, BlockPos position) {
        ClimateData locationData = ClimateData.sample(level, position);
        Set<ClimateStatisticItem> validItems = getValidCatchesAt(locationData);
        Map<ClimateStatisticItem, Double> weights = getWeightsForItems(validItems, locationData);

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
}
