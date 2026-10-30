package me.wheelershigley.www.solace_jobs;

import me.wheelershigley.www.solace_jobs.commands.JobsCommand;
import me.wheelershigley.www.solace_jobs.events.Events;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceJobs implements ModInitializer {
    public static final String MOD_ID = "solace_jobs";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger();

    /* TODO
     * hunter + farmer additions
     * grief-logger integration (break-block exploit prevention)
     * limits (permission integrations)
     * configurations into a folder
     * permissions-based multiplier(s)
     * mod-configurations, like provider:currency
     * global multiplier
     * dummy items (icons)
     * income updates (on some frequency)
     * update-frequency configuration
     * CLI commands join/leave
     * toggle/join/leave permissions check(s)
     * CLI commands for configurations (optional?)
     */

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " " + VERSION + " has started!");

        Events.register();
        JobsCommand.registerCommand();
    }

    public static Identifier solaceJobsIdentifier(String value) {
        return Identifier.fromNamespaceAndPath(MOD_ID, value);
    }
}
