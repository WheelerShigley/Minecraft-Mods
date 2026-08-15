package me.wheelershigley.www.solace_fishing.menus;

import eu.pb4.sgui.api.gui.SimpleGui;
import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.FishingContext;
import me.wheelershigley.www.solace_fishing.data.RodAccessories;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.appendLore;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.getMenuItem;

public abstract class ImmutableSimpleGui extends SimpleGui {
    public static final int ROW_LENGTH = 9;
    private static final Style DEFAULT_STYLE = Style.EMPTY.withItalic(false).withColor(TextColor.GRAY);

    private final ImmutableSimpleGui parent;

    public abstract String getTranslationKey();
    public abstract MenuType<?> getMenuType();
    public abstract void initializeMenu();

    public ImmutableSimpleGui(ServerPlayer player, MenuType<?> menuType, ImmutableSimpleGui parent) {
        super(menuType, player, false);
        this.parent = parent;

        this.setTitle(  Component.translatable( getTranslationKey() )  );
        this.initializeMenu();
    }

    @Override
    public void afterRemoval() {
        if(parent != null) {
            parent.open();
        }
    }

    @Unique
    public static void setContextRow(ImmutableSimpleGui menu, FishingContext context, int offset) {
        int slot = offset;

        // Menu Items
        ItemStack
            enviornment = getMenuItem(Items.GRASS_BLOCK,        false, "solace_fishing.context_row.environment"),
            medium      = getMenuItem(Items.WATER_BUCKET,       false, "solace_fishing.context_row.medium"     ),
            climate     = getMenuItem(Items.TORCHFLOWER,        false, "solace_fishing.context_row.climate"    ),
            rod         = getMenuItem(context.rod(),            false, "solace_fishing.context_row.fishing_rod"),
            accessories = getMenuItem(FishingItems.RED_BOBBER,  false, "solace_fishing.context_row.accessories"),
            upgrades    = getMenuItem(Items.NETHER_STAR,        false, "solace_fishing.context_row.upgrades"   ),
            luck        = getMenuItem(Items.EXPERIENCE_BOTTLE,  true,  "solace_fishing.context_row.luck")
        ;

        // Lore
        setLevelAndBiomeLore(enviornment, context);
        setBlockLore( medium, context.medium() );
        setClimateLore( climate, context.environment() );
        setItemLore( rod, context.rod() );
        setAccessoriesLore( accessories, context.accessories() );
        setUpgradesLore(upgrades);
        setFloatLore(luck, context.luck() );

        // Set Row
        menu.setSlot(slot++, enviornment );
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

        /*String translationKey =
            "dimension." +
            SolaceFishing.MOD_ID + '.' +
            dimension.identifier().getPath()
        ;
        Component dimensionName = Component.translatable(translationKey);
         */
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
                ClimateData.percentize( climate.getTemperature() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.humidity",
                ClimateData.percentize( climate.getHumidity() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.erosion",
                ClimateData.percentize( climate.getErosion() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.continentalness",
                ClimateData.percentize( climate.getContinentalness() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.weirdness",
                ClimateData.percentize( climate.getWeirdness() )
            )
        );
        climateComponents.add(
            getTranslatedPercentageLore(
                "solace_fishing.pda.depth",
                ClimateData.percentize( climate.getDepth() )
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
