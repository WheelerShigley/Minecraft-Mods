package me.wheelershigley.www.solace_jobs;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.solace_jobs.commands.JobsCommand;
import me.wheelershigley.www.solace_jobs.commands.SolaceJobsCommand;
import me.wheelershigley.www.solace_jobs.events.Events;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceJobs implements ModInitializer {
    public static final String MOD_ID = "solace_jobs";
    public static final Logger LOGGER = LogManager.getLogger();

    /* # TODO
     * level passive-abilities
     * Tool-Abilities (active abilities)
     * Jobs-Menu Items-Lore for {Level | experience} and {ability}
     *
     * Dandelion - Automatic Default-Icons (optional)
     * Archeologist: Lootr (optional)
     * Farmer:   Mooblooms (optional)
     */

    @Override
    public void onInitialize() {
        Events.register();
        SolaceJobsCommand.register();
        JobsCommand.register();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }
}
