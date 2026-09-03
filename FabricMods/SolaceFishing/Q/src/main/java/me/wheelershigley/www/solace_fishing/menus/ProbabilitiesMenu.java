package me.wheelershigley.www.solace_fishing.menus;

import com.mojang.datafixers.util.Pair;
import me.wheelershigley.www.solace_fishing.api.fishing.*;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wheelershigley.www.solace_fishing.helpers.MathsHelper.percentageRound;
import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.getMinimumChestMenu;
import static me.wheelershigley.www.solace_fishing.helpers.MenusHelper.setContextRow;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.*;

public class ProbabilitiesMenu extends ImmutableSimpleGui {
    private final FishingContext context;
    private final LinkedHashSet<ClimatePreferencedItem> probabilities;

    public ProbabilitiesMenu(
        ServerLevel level, BlockPos position, boolean forceTreasure,
        ServerPlayer player, @Nullable ImmutableSimpleGui parent
    ) {
        ItemStack rod = getFirstFishingRod(player);
        if(rod == null) {
            //generic rod
            rod = new ItemStack(Items.FISHING_ROD);
        }
        FishingContext subContext; {
            FishingHook hook = new FishingHook(EntityTypes.FISHING_BOBBER, level);
            boolean isOpenWater = ( forceTreasure || hook.calculateOpenWater(position) );
            hook.remove(Entity.RemovalReason.DISCARDED);

            Block medium = level.getBlockState(position).getBlock();
            int medium_depth = FishingContext.discoverDepthOfMedium(level, position);
            RodAccessories accessories = RodAccessories.of(rod, level);

            float luck = player.getLuck() + (float)getLuckOfRod(level, rod);
            if(accessories != null) {
                luck += accessories.getLuck();
            }

            ClimateData environment = ClimateData.sample(level, position);

            subContext = new FishingContext(
                medium, medium_depth, rod.getItem(), luck, accessories, environment, isOpenWater
            );
        }

        LinkedHashSet<ClimatePreferencedItem> sortedLocalSampleSpace = Fishing
            .getLocalSampleSpace(subContext)
            .getSortedSubSpace(subContext)
            .getCleaned()
            .withLuck(subContext)
            .getSamples()
        ;

        MenuType<?> menuType = getMinimumChestMenu( sortedLocalSampleSpace.size()+ROW_LENGTH );
        this.context = subContext;
        this.probabilities = sortedLocalSampleSpace;

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
        for(ClimatePreferencedItem entry : probabilities) {
            if( MenusHelper.sizeOf( this.getMenuType() ) <= fish_index ) {
                break;
            }

            this.setSlot(
                ROW_LENGTH + fish_index++,
                getProbabilityMenuItem(
                    entry.getItem(),
                    entry.getArea(),
                    entry.getAveragePickiness()
                )
            );
        }
    }

    private static ItemStack getProbabilityMenuItem(
        ItemStack sourceStack,
        double probability,
        @Nullable Double pickiness
    ) {
        boolean appears_enchanted = sourceStack.getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false) || sourceStack.isEnchanted();
        ItemStack itemStack = ItemsHelper.getMenuItem(
            sourceStack.getItem(),
            appears_enchanted,
            null
        );

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
                Integer.toString( (int)chance )
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
