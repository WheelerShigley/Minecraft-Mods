package me.wheelershigley.www.solace_fishing.implementations;

import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.data.NormalDistribution;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistributableItem extends Item implements PolymerItem {
    private final NormalDistribution distributionData;
    private final Double minimum, maximum;

    public DistributableItem(
        Properties properties,
        @NotNull NormalDistribution distributionData,
        @Nullable Double minimum,
        @Nullable Double maximum
    ) {
        super(properties);

        this.distributionData = distributionData;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public NormalDistribution getDistributionData() {
        return distributionData;
    }

    public double getDistributionResult(RandomSource random) {
        if(minimum != null && maximum != null) {
            return distributionData.boundedRoll(
                random,
                new Pair<>(minimum, maximum)
            );
        }
        return distributionData.roll(random);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COD;
    }
}
