package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.data.DistributionData;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class DistributableItem extends Item implements PolymerItem {
    private final DistributionData distributionData;

    public DistributableItem(
        Properties properties,
        @NotNull DistributionData distributionData
    ) {
        super(properties);
        this.distributionData = distributionData;
    }

    public DistributionData getDistributionData() {
        return distributionData;
    }

    public double getDistributionResult(RandomSource random) {
        return distributionData.roll(random);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COD;
    }
}
