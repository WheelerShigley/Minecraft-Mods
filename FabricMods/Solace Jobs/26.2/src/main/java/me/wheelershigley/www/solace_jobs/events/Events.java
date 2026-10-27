package me.wheelershigley.www.solace_jobs.events;

import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Events {
    public static void register() {
        ServerLifecycleEvents.SERVER_STOPPING.register(
            (server) -> {
                JobsCodec.INSTANCE.save();
                PlayersJobsDataCodec.INSTANCE.save();
            }
        );
        ServerLifecycleEvents.AFTER_SAVE.register(
            (server, flush, force) -> {
                JobsCodec.INSTANCE.save();
                PlayersJobsDataCodec.INSTANCE.save();
            }
        );
    }
}
