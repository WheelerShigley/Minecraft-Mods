package me.wheelershigley.www.solace_jobs.events;

import me.wheelershigley.www.solace_jobs.data.config.Configuration;
import me.wheelershigley.www.solace_jobs.data.statements.Statements;
import me.wheelershigley.www.solace_jobs.rewards.ActionReward;
import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;

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

        // Reload
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(
            (server, resource_manager) -> {
                ConfigurationsCodec.reload();
            }
        );

        // Income Updates
        ServerTickEvents.END_SERVER_TICK.register(
            (server) -> {
                final Configuration configuration = ConfigurationsCodec.INSTANCE.getInstanceData().getData();
                final boolean ENABLE = configuration.income_statements();
                if(!ENABLE) {
                    return;
                }
                final int FREQUENCY  = configuration.income_statements_frequency_ticks();

                final int TICK = server.getTickCount();
                if(TICK % FREQUENCY == 0) {
                    Statements.INSTANCE.notifyPlayersOfStatements(server);
                }
            }
        );
    }
}
