package me.wheelershigley.www.solace_jobs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class IdentifierHelper {
    public static Identifier solaceJobsIdentifier(String value) {
        return Identifier.fromNamespaceAndPath(SolaceJobs.MOD_ID, value);
    }

    public static Identifier identifierOf(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private static final Identifier AIR_IDENTIFIER = identifierOf(Items.AIR);
    public static Item itemOf(Identifier identifier) {
        Item item = BuiltInRegistries.ITEM.getValue(identifier);
        if(
            item.equals(Items.AIR) &&
            !identifier.equals(AIR_IDENTIFIER)
        ) {
            SolaceJobs.LOGGER.warn(
                "Identifier, \"{}\", could not be resolved, defaulted to AIR.",
                identifier
            );
        }
        return item;
    }
}
