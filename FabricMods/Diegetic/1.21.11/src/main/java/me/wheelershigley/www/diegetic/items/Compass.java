package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.Diegetic;
import me.wheelershigley.www.diegetic.helper.MessageHelper;
import me.wheelershigley.www.diegetic.helper.PlayerHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.COMPASS_COORDINATES;
import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.LODESTONE_COMPASS_RELATIVE_COORDINATES;
import static me.wheelershigley.www.diegetic.helper.ServerWorldHelper.getBooleanGameRuleValue;

public class Compass {
    public static void use(ServerPlayerEntity player, ItemStack compass) {
        boolean useAbsoluteCoordinates = getBooleanGameRuleValue(player.getEntityWorld(), COMPASS_COORDINATES);
        boolean useLodestoneCoordinates = getBooleanGameRuleValue(player.getEntityWorld(), LODESTONE_COMPASS_RELATIVE_COORDINATES);
        boolean isLodestoneCompass = compass.contains(DataComponentTypes.LODESTONE_TRACKER);

        //Input Validation
        if(
            !useAbsoluteCoordinates
            && (
                !useLodestoneCoordinates || !isLodestoneCompass
            )
        ) {
            return;
        }

        //get Position (relative or absolute)
        Vec3d position = null;
        boolean relative = false;

        if(useAbsoluteCoordinates) {
            position = PlayerHelper.getFootPosition(player);
        }
        if(isLodestoneCompass && useLodestoneCoordinates) {
            LodestoneTrackerComponent compassData = compass.get(DataComponentTypes.LODESTONE_TRACKER);
            if(
                compassData != null
                && compassData.target().isPresent()
                //dimensions must match
                && player.getEntityWorld().getDimensionEntry().matchesId(
                    compassData.target().get().dimension().getValue()
                )
            ) {
                position = compassData.target().get().pos().toCenterPos();
                position = position.subtract( PlayerHelper.getFootPosition(player) );
                relative = true;
            } else {
                position = null;
            }
        }

        //send Position
        if(position == null) {
            MessageHelper.sendMessage(player, "diegetic.text.compass.missing_lodestone");
        } else {
            MessageHelper.sendMessage(
                player,
                "diegetic.text.compass." + (relative ? "relative" : "absolute"),
                (int)position.x,
                (int)position.y,
                (int)position.z
            );
        }
    }
}
