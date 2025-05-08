package me.wheelershigley.diegetic.items;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.helper.MessageHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Compass {
    public static void use(ServerPlayerEntity player, ItemStack compass) {
        if(!Diegetic.diegeticCompassCoordinates) {
            return;
        }

        Vec3d position = null;
        boolean relative = false;
        if( compass.contains(DataComponentTypes.LODESTONE_TRACKER) ) {
            if(
                compass.get(DataComponentTypes.LODESTONE_TRACKER) != null
                && compass.get(DataComponentTypes.LODESTONE_TRACKER).target().isPresent()
                //dimensions must match
                && player.getWorld().getDimension().effects().equals(
                    compass.get(DataComponentTypes.LODESTONE_TRACKER).target().get().dimension().getValue()
                )
            ) {
                position = compass.get(DataComponentTypes.LODESTONE_TRACKER).target().get().pos().toCenterPos();
                position = position.subtract( player.getPos() );
            }
            if(Diegetic.diegeticLodestoneCompassRelativeCoordinates) {
                relative = true;
            }
        } else {
            position = player.getPos();
        }

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
