package me.wheelershigley.www.solace_crates.menus;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProbabilitiesMenu extends ImmutableSimpleGui {
    public ProbabilitiesMenu(
        ServerPlayer player, @Nullable ImmutableSimpleGui parent
    ) {
        //TODO
        MenuType<?> menuType = getMinimumChestMenu(9);
        super(player, menuType, parent);
    }

    @Override
    public String getTranslationKey() {
        //TODO
        return "solace_crates.crate.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        //TODO
        return getMinimumChestMenu(9);
    }

    @Override
    public void initializeMenu() {
        //TODO
    }

    private static ItemStack getProbabilityMenuItem(
        ItemStack sourceStack,
        double probability
    ) {
        boolean appears_enchanted = sourceStack.getOrDefault(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false) || sourceStack.isEnchanted();
        ItemStack itemStack = getMenuItem(
            sourceStack.getItem(),
            appears_enchanted,
            null
        );

        List<Component> lines = new ArrayList<>();
        lines.add(
            Component.literal(
                getHumanReadablePercentage(probability)
            ).withStyle(
                Style.EMPTY.withColor(TextColor.WHITE).withItalic(false)
            )
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

        chance = MathsHelper.percentageRound( notatedChance.getFirst() );
        int order = notatedChance.getSecond();
        double remainder = MathsHelper.percentageRound(chance % 1);
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
                MathsHelper.percentageRound(chance +remainder)
            );
        }

        stringBuilder.append('%');
        if(order < 0) {
            stringBuilder.append('e').append(order);
        }

        return stringBuilder.toString();
    }
}
