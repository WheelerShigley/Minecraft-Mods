package me.wheelershigley.www.solace_jobs.events;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

public class Events {
    public static void register() {
        // Static Initialization(s)
        ServerLifecycleEvents.SERVER_STARTING.register(ActionReward::initialize);

        // Save config-files
        ServerLifecycleEvents.SERVER_STOPPING.register(
            (server) -> {
                JobsCodec.INSTANCE.save();
                ConfigurationsCodec.INSTANCE.save();
                PlayersJobsDataCodec.INSTANCE.save();
                EmploymentsCodec.INSTANCE.save();
            }
        );
        ServerLifecycleEvents.AFTER_SAVE.register(
            (server, flush, force) -> {
                JobsCodec.INSTANCE.save();
                ConfigurationsCodec.INSTANCE.save();
                PlayersJobsDataCodec.INSTANCE.save();
                EmploymentsCodec.INSTANCE.save();
            }
        );

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(
            (server, resource_manager) -> {
                ConfigurationsCodec.reload();
            }
        );
    }
}
