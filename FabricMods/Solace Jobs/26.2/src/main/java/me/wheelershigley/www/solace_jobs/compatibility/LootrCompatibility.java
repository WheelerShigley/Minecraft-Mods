package me.wheelershigley.www.solace_jobs.compatibility;

import me.wheelershigley.www.solace_jobs.data.jobs.IdentifiableReward;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import net.fabricmc.loader.api.FabricLoader;
import noobanidus.mods.lootr.fabric.init.ModBlocks;

import java.util.Set;

import static me.wheelershigley.www.solace_jobs.data.jobs.DefaultJobs.ARCHEOLOGIST;
import static me.wheelershigley.www.solace_jobs.data.jobs.DefaultJobs.getIdentifiedReward;

public class LootrCompatibility {
    public static void initialize() {
        if( !isLootrLoaded() ) {
            return;
        }

        ARCHEOLOGIST.addReward(
            getIdentifiedReward(
                "lootr_brushables",
                Set.of(
                        ModBlocks.SUSPICIOUS_SAND,
                        ModBlocks.SUSPICIOUS_GRAVEL
                ),
                ActionType.BRUSH,
                1.1, 5, 0.06
            )
        );
    }
    public static boolean isLootrLoaded() {
        return FabricLoader.getInstance().isModLoaded("lootr");
    }
}
