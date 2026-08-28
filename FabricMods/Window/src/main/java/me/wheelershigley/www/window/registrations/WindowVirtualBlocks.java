package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.portal.PortalBlockEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;

public class WindowVirtualBlocks {
    public static void register() {
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(
            (blockEntity, level) -> {
                if(blockEntity instanceof PortalBlockEntity portal) {
                    portal.initializeHolder(level);
                }
            }
        );
    }
}
