package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.data.DistributionData;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistributableItem extends Item implements PolymerItem {
    private final DistributionData distributionData;
    private final Double minimum, maximum;

    public DistributableItem(
        Properties properties,
        @NotNull  DistributionData distributionData,
        @Nullable Double minimum,
        @Nullable Double maximum
    ) {
        super(properties);

        this.distributionData = distributionData;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public DistributionData getDistributionData() {
        return distributionData;
    }

    public double getDistributionResult(RandomSource random) {
        double result = distributionData.roll(random);

        if(minimum != null && maximum != null) {
            result = Math.clamp(result, minimum, maximum);
        }

        return result;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COD;
    }
}
