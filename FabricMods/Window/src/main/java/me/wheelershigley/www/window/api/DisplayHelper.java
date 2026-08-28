package me.wheelershigley.www.window.api;

import com.mojang.math.Transformation;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import me.wheelershigley.www.window.portal.PortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
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

    public static InteractionElement getPortalInteraction(PortalBlockEntity portalBlockEntity, Direction.Axis axis) {
        InteractionElement interactable = new InteractionElement(
            new VirtualElement.InteractionHandler() {
                @Override
                public void attack(ServerPlayer player) {
                    BlockPos position = portalBlockEntity.getBlockPos();
                    if(     player.level() instanceof ServerLevel serverLevel
                         && player.gameMode.isCreative()
                    ) {
                        serverLevel.destroyBlock(position, false, player);
                    }
                }
            }
        );

        final float HEIGHT = 4.0f/16.0f;
        interactable.setOffset( new Vec3(0.0D, HEIGHT/-2.0D, 0.0D) );
        interactable.setHeight(HEIGHT);

        return interactable;
    }
}
