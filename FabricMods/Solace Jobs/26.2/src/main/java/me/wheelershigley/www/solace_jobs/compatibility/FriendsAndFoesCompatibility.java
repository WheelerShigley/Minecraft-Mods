package me.wheelershigley.www.solace_jobs.compatibility;

import com.faboslav.friendsandfoes.common.init.FriendsAndFoesEntityTypes;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Set;

import static me.wheelershigley.www.solace_jobs.data.jobs.DefaultJobs.*;

public class FriendsAndFoesCompatibility implements Compatibility {
    @Override
    public boolean isLoaded() {
        return
               FabricLoader.getInstance().isModLoaded("friendsandfoes")
            && FabricLoader.getInstance().isModLoaded("resourcefullib")
        ;
    }

    public void addFarmerReward() {
        if( !isLoaded() ) {
            return;
        }

        FARMER.addReward(
            getIdentifiedReward(
                "friendsandfoes_disinfection",
                Set.of(
                    FriendsAndFoesEntityTypes.MOOBLOOM.get()
                ),
                ActionType.SHEAR,
                2.2, 26.7, 2.6666
            )
        );
    }
}
