package me.wheelershigley.www.window.api;

import com.mojang.math.Transformation;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;

import java.util.HashMap;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class DisplayHelper {
    private static final Transformation PITCH = new Transformation(
        null, null, null,
        new Quaternionf().rotationZ( (float)Math.toRadians(90) )
    );
    private static final HashMap<DyeColor, ItemStack> texturedPortalPanes; static {
        texturedPortalPanes = new HashMap<>();

        for( DyeColor color : DyeColor.values() ) {
            ItemStack pane = Blocks.STAINED_GLASS_PANE.pick(color).defaultBlockState().getBlock().asItem().getDefaultInstance();
            pane.set(
                DataComponents.ITEM_MODEL,
                getWindowIdentifier(color.getSerializedName() + "_portal")
            );

            texturedPortalPanes.put(color, pane);
        }
    }

    public static ItemDisplayElement getPaneDisplay(DyeColor color, Direction.Axis axis) {
        ItemDisplayElement paneDisplay = new ItemDisplayElement();
        paneDisplay.setItem( texturedPortalPanes.get(color) );

        switch(axis) {
            case Y:
                // ItemDisplays can't be pitched, so they need to be transformed to emulate pitch
                paneDisplay.setTransformation(PITCH);
                break;
            case Z:
                paneDisplay.setRotation(0.0f, 90.0f);
                break;
            default:
        }

        return paneDisplay;
    }
}
