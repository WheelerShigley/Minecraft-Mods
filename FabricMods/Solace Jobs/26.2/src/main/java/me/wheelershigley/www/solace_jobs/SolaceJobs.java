package me.wheelershigley.www.solace_jobs;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.solace_jobs.commands.JobsCommand;
import me.wheelershigley.www.solace_jobs.events.Events;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceJobs implements ModInitializer {
    public static final String MOD_ID = "solace_jobs";
    public static final String VERSION = "1.0.0+26.2";
    public static final Logger LOGGER = LogManager.getLogger();

    /* # TODO
     * ## Solace Jobs
     * levels (exponential)
     * level rewards(?)
     *
     * CLI commands join/leave
     * toggle/join/leave permissions check(s)
     * CLI commands for configurations (optional?)
     *
     * ## Permissions
     * job-count limit(s) (permission integrations)
     * permissions-based multiplier(s)
     */

    @Override
    public void onInitialize() {
        LOGGER.info("SolaceJobs-" + VERSION + " has started!");

        Events.register();
        JobsCommand.registerCommand();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }

    public static Identifier solaceJobsIdentifier(String value) {
        return Identifier.fromNamespaceAndPath(MOD_ID, value);
    }
}
