package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.SolaceFishing;
import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.menus.ImmutableChestMenu;
import me.wheelershigley.www.solace_fishing.menus.ProbabilitiesMenu;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;

public class PDA extends Item implements PolymerItem  {
    public PDA(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COMPASS;
    }

    @Override
    public @NonNull InteractionResult use(
        final @NonNull Level level,
        final @NonNull Player player,
        final @NonNull InteractionHand hand
    ) {
        if( !(level instanceof ServerLevel) ) {
            return InteractionResult.PASS;
        }
        assert level instanceof ServerLevel;

        HitResult hit = player.pick(5.0D, 0.0F, true);
        BlockPos position;
        if(hit.getType() == HitResult.Type.BLOCK) {
            position = ( (BlockHitResult)hit ).getBlockPos();
        } else {
            position = player.getOnPos();
        }

        if( player.isCrouching() ) {
            sendClimateData( (ServerLevel)level, position, player);
        } else {
            ImmutableChestMenu.open(
                player,
                new ProbabilitiesMenu( (ServerLevel)level, position, player.getInventory() )
            );
        }


        return InteractionResult.SUCCESS;
    }

    private static void sendClimateData(
        ServerLevel level, BlockPos position,
        Player player
    ) {
        ClimateData climate = ClimateData.sample(level, position);

        sendPDAPercentageMessage(player, "solace_fishing.pda.temperature",     100.0*climate.getTemperature()     );
        sendPDAPercentageMessage(player, "solace_fishing.pda.humidity",        100.0*climate.getHumidity()        );
        sendPDAPercentageMessage(player, "solace_fishing.pda.continentalness", 100.0*climate.getContinentalness() );
        sendPDAPercentageMessage(player, "solace_fishing.pda.erosion",         100.0*climate.getErosion()         );
        sendPDAPercentageMessage(player, "solace_fishing.pda.depth",           100.0*climate.getDepth()           );
        sendPDAPercentageMessage(player, "solace_fishing.pda.weirdness",       100.0*climate.getWeirdness()       );

        Holder<Biome> biome = climate.getBiome();
        ResourceKey<Level> dimension = climate.getDimension();
        if(biome == null && dimension == null) {
            return;
        }

        player.sendSystemMessage(
            Component.translatable("solace_fishing.pda.seperator_message")
        );
        if(biome != null) {
            sendPDAStringMessage(player, biome);
        }
        if(dimension != null) {
            sendPDAStringMessage(player, dimension);
        }
    }

    private static void sendPDAPercentageMessage(Player player, String label_key, double value) {
        player.sendSystemMessage(
            Component.translatable(
                "solace_fishing.pda.percentage_message",
                Component.translatable(label_key),
                Component.literal( String.format("%.2f", value) ),
                Component.literal("%")
            )
        );
    }
    private static void sendPDAStringMessage(Player player, Holder<Biome> biome) {
        String translationKey = biome
            .unwrapKey()
            .map(
                (key) -> {
                    return
                        "biome." +
                        key.identifier().getNamespace() +
                        "." +
                        key.identifier().getPath()
                    ;
                }
            )
            .orElse("biome.unknown")
        ;
        Component biomeName = Component.translatable(translationKey);

        player.sendSystemMessage(
            Component.translatable(
                "solace_fishing.pda.string_message",
                "biome",
                biomeName
            )
        );
    }
    private static void sendPDAStringMessage(Player player, ResourceKey<Level> dimension) {
        String translationKey =
            "dimension." +
            SolaceFishing.MOD_ID + '.' +
            dimension.identifier().getPath()
        ;
        Component dimensionName = Component.translatable(translationKey);

        player.sendSystemMessage(
            Component.translatable(
                "solace_fishing.pda.string_message",
                "dimension",
                dimensionName
            )
        );
    }
}
