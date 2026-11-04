package me.wheelershigley.www.solace_jobs.level;

import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.players.JobsPlayerData;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.*;

import static me.wheelershigley.www.solace_jobs.level.LevelHelper.*;

public class LevelBarHandler {
    private static final UUID JOB_BOSS_BAR_UUID = UUID.randomUUID();
    public static void tickBossBars(MinecraftServer server) {
        final int BAR_DURATION = ConfigurationsCodec.INSTANCE.getInstanceData().getData().job_experience_bar_duration_ticks();

        final int tick = server.getTickCount();
        List<UUID> playerIdentifiersToBeRemoved = new ArrayList<>();
        for( Map.Entry<UUID, Integer> playerWithBossBar : playersWithBossBars.entrySet() ) {
            int delta_tick = tick - playerWithBossBar.getValue();
            if(BAR_DURATION < delta_tick) {
                playerIdentifiersToBeRemoved.add( playerWithBossBar.getKey() );

                ServerPlayer player = server.getPlayerList().getPlayer( playerWithBossBar.getKey() );
                if(player == null) {
                    continue;
                }
                removeBossBar(player);
            }
        }
        for(UUID playerIdentifierToBeRemoved : playerIdentifiersToBeRemoved) {
            playersWithBossBars.remove(playerIdentifierToBeRemoved);
        }
    }

    public static void notifyJobLevel(
        ServerPlayer serverPlayer,
        Identifier jobIdentifier,
        MinecraftServer server
    ) {
        JobsPlayerData data = PlayersJobsDataCodec.INSTANCE.getInstanceData().getData().get( serverPlayer.getUUID() );
        Double total_experience = data.experiences().getOrDefault( jobIdentifier, 0.0 );
        if(total_experience == null) {
            total_experience = 0.0;
        }

        int level = (int)experienceToLevel(total_experience);
        double percentage = percentageToNextLevel(total_experience);

        Component name = Component.translatable(
            jobIdentifier.getNamespace() + ".job." + jobIdentifier.getPath()
        ).append(" ").append(
            Component.translatable(
                "solace_jobs.boss_bar.level",
                Integer.toString(level),
                Integer.toString(level+1)
            ).withColor(TextColor.GREEN)
        ).withColor(TextColor.GREEN);

        BossEvent event = new BossEvent(
            JOB_BOSS_BAR_UUID,
            name,
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS
        ) {};
        event.setProgress( (float)percentage );

        sendBossBar(
            serverPlayer,
            event,
            server
        );
    }

    private static final HashMap<UUID, Integer> playersWithBossBars = new HashMap<>();
    private static void sendBossBar(
        ServerPlayer serverPlayer,
        BossEvent event,
        MinecraftServer server
    ) {
        playersWithBossBars.put(
            serverPlayer.getUUID(),
            server.getTickCount()
        );

        serverPlayer.connection.send(
            ClientboundBossEventPacket.createAddPacket(event)
        );
    }
    private static void removeBossBar(
        ServerPlayer serverPlayer
    ) {
        serverPlayer.connection.send(
            ClientboundBossEventPacket.createRemovePacket(JOB_BOSS_BAR_UUID)
        );
    }
}
