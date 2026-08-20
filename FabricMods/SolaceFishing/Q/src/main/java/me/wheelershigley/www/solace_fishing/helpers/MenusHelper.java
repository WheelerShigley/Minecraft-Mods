package me.wheelershigley.www.solace_fishing.helpers;

import me.wheelershigley.www.solace_fishing.api.fishing.ClimateData;
import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import me.wheelershigley.www.solace_fishing.api.fishing.RodAccessories;
import me.wheelershigley.www.solace_fishing.menus.ImmutableSimpleGui;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.appendLore;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.getMenuItem;
import static me.wheelershigley.www.solace_fishing.helpers.MathsHelper.percentageRound;

public class MenusHelper {
    private static final int ROW_SIZE = 9;

    public static int sizeOf(MenuType<?> type) {

        if(type == MenuType.GENERIC_9x1) { return 1*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x2) { return 2*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x3) { return 3*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x4) { return 4*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x5) { return 5*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x6) { return 6*ROW_SIZE; }

        throw new IllegalStateException("Not a chest menu");
    }

    public static MenuType<ChestMenu> getMinimumChestMenu(@Nullable Integer slot_count) {
        if(slot_count == null) {
            return MenuType.GENERIC_9x1;
        }

        if(slot_count <= 1*ROW_SIZE) { return MenuType.GENERIC_9x1; }
        if(slot_count <= 2*ROW_SIZE) { return MenuType.GENERIC_9x2; }
        if(slot_count <= 3*ROW_SIZE) { return MenuType.GENERIC_9x3; }
        if(slot_count <= 4*ROW_SIZE) { return MenuType.GENERIC_9x4; }
        if(slot_count <= 5*ROW_SIZE) { return MenuType.GENERIC_9x5; }
        return MenuType.GENERIC_9x6;
    }

    private static final Style DEFAULT_STYLE = Style.EMPTY.withItalic(false).withColor(TextColor.GRAY);
    @Unique
    public static void setContextRow(ImmutableSimpleGui menu, FishingContext context, int offset) {
        int slot = offset;

        // Menu Items
        ItemStack
            environment = getMenuItem(Items.GRASS_BLOCK,         false, "solace_fishing.context_row.environment"),
            medium      = getMenuItem(Items.WATER_BUCKET,        false, "solace_fishing.context_row.medium"     ),
            climate     = getMenuItem(Items.TORCHFLOWER,         false, "solace_fishing.context_row.climate"    ),
            rod         = getMenuItem(context.rod(),             false, "solace_fishing.context_row.fishing_rod"),
            accessories = getMenuItem(FishingItems.BASIC_BOBBER, false, "solace_fishing.context_row.accessories"),
            upgrades    = getMenuItem(Items.NETHER_STAR,         false, "solace_fishing.context_row.upgrades"   ),
            luck        = getMenuItem(Items.EXPERIENCE_BOTTLE,   true,  "solace_fishing.context_row.luck")
        ;

        // Lore
        setLevelAndBiomeLore(environment, context);
        setBlockLore( medium, context.medium() );
        setClimateLore( climate, context.environment() );
        setItemLore( rod, context.rod() );
        setAccessoriesLore( accessories, context.accessories() );
        setUpgradesLore(upgrades);
        setFloatLore(luck, context.luck() );

        // Set Row
        menu.setSlot(slot++, environment);
        menu.setSlot(slot++, medium      );
        menu.setSlot(slot++, climate     );
        slot++;
        menu.setSlot(slot++, rod         );
        slot++;
        menu.setSlot(slot++, accessories );
        menu.setSlot(slot++, upgrades    );
        menu.setSlot(slot++, luck        );
    }

    //TODO: level+biome context
    private static void setLevelAndBiomeLore(ItemStack itemStack, FishingContext context) {
        appendLore(
            itemStack,
            Component.literal("TODO").withStyle(DEFAULT_STYLE)
        );
    }

    private static void setItemLore(ItemStack itemStack, Item item) {
        appendLore(
            itemStack,
            Component.translatable(
                item.getDescriptionId()
            ).withStyle(DEFAULT_STYLE)
        );
    }
    private static void setBlockLore(ItemStack itemStack, Block block) {
        appendLore(
            itemStack,
            Component.translatable(
                block.getDescriptionId()
            ).withStyle(DEFAULT_STYLE)
        );
    }

    private static void setClimateLore(ItemStack itemStack, ClimateData climate) {
        List<Component> climateComponents = new ArrayList<>();

        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.temperature",
                percentageRound( 100.0*climate.getTemperature() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.humidity",
                percentageRound( 100.0*climate.getHumidity() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.erosion",
                percentageRound( 100.0*climate.getErosion() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.continentalness",
                percentageRound( 100.0*climate.getContinentalness() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.weirdness",
                percentageRound( 100.0*climate.getWeirdness() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.height",
                percentageRound( 100.0*climate.getHeight() )
            )
        );

        appendLore(itemStack, climateComponents);
    }
    private static Component getTranslatedPercentageLore(String nameTranslationKey, double percentage) {
        return Component
            .translatable(nameTranslationKey)
            .append(
                Component.literal(": " + percentage + "%")
            )
            .withStyle(DEFAULT_STYLE)
        ;
    }

    private static void setAccessoriesLore(ItemStack itemStack, RodAccessories accessories) {
        List<Component> accessoriesComponents = new ArrayList<>();

        if( !accessories.getLine().isEmpty() ) {
            accessoriesComponents.add(
                getLabeledLore("solace_fishing.context_row.line", accessories.getLine().getItem() )
            );
        }
        if( !accessories.getBobber().isEmpty() ) {
            accessoriesComponents.add(
                getLabeledLore("solace_fishing.context_row.bobber", accessories.getBobber().getItem() )
            );
        }
        if( !accessories.getHook().isEmpty() ) {
            accessoriesComponents.add(
                getLabeledLore("solace_fishing.context_row.hook", accessories.getHook().getItem() )
            );
        }

        if( accessoriesComponents.isEmpty() ) {
            accessoriesComponents.add(
                Component.translatable("solace_fishing.context_row.none").withStyle(DEFAULT_STYLE)
            );
        }

        appendLore(itemStack, accessoriesComponents);
    }
    private static Component getLabeledLore(String keyTranslationKey, Item item) {
        return Component
            .translatable(keyTranslationKey)
            .append(
                Component.literal(": ")
            )
            .append(
                Component.translatable( item.getDescriptionId() )
            )
            .withStyle(DEFAULT_STYLE)
        ;
    }

    //TODO
    private static void setUpgradesLore(ItemStack itemStack) {
        appendLore(
            itemStack,
            Component.literal("TODO").withStyle(DEFAULT_STYLE)
        );
    }

    private static void setFloatLore(ItemStack itemStack, float number) {
        appendLore(
            itemStack,
            Component.literal( Float.toString(number) ).withStyle(DEFAULT_STYLE)
        );
    }
}
