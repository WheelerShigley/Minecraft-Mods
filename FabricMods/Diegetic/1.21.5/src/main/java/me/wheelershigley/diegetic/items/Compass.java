package me.wheelershigley.diegetic.items;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.helper.MessageHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Compass {
    public static void use(ServerPlayerEntity player, ItemStack compass) {
        if( !(boolean) Diegetic.configurations.getConfiguration("compass").getValue() ) {
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
            relative = true;
        } else {
            position = player.getPos();
        }

        StringBuilder coordinateBuilder = new StringBuilder();
        coordinateBuilder.append("§f");
        if(position == null) {
            coordinateBuilder.append("Lodestone is in another dimension or was destroyed.");
        } else {
            if(relative) { coordinateBuilder.append('~'); }
            coordinateBuilder.append( (int)position.x ).append(' ');

            if(relative) { coordinateBuilder.append('~'); }
            coordinateBuilder.append( (int)position.y+1 ).append(' ');

            if(relative) { coordinateBuilder.append('~'); }
            coordinateBuilder.append( (int)position.z );
        }

        MessageHelper.sendMessage( player, coordinateBuilder.toString() );
    }
}
