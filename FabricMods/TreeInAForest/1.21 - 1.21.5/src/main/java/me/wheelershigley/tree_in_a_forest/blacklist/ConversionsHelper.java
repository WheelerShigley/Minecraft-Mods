package me.wheelershigley.tree_in_a_forest.blacklist;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static me.wheelershigley.tree_in_a_forest.TreeInAForest.server;

public class ConversionsHelper {
    public static GameProfile getProfileFromPlayerName(@Nullable String name) {
        TreeInAForest.LOGGER.info("getProfileFromPlayerName("+name+")");
        if(
            name == null
            || !name.matches("^[a-zA-Z0-9_]{2,16}$")
        ) {
            TreeInAForest.LOGGER.info("A1");
            return null;
        }

        UserCache userCache = server.getUserCache();
        if(userCache == null) {
            TreeInAForest.LOGGER.info("B1");
            return null;
        }

        Optional<GameProfile> potentialGameProfile = userCache.findByName(name);
        if( !potentialGameProfile.isPresent() ) {
            TreeInAForest.LOGGER.info("C1");
            return null;
        }

        TreeInAForest.LOGGER.info("a => "+ potentialGameProfile.get().getName() );
        return potentialGameProfile.get();
    }

    private static StringBuilder uuidBuilder = new StringBuilder();
    public static UUID getUuidFromTrimmedUuidString(@Nullable String pseudoUUID) {
        TreeInAForest.LOGGER.info("getUuidFromTrimmedUuidString("+pseudoUUID+")");
        if(
            pseudoUUID == null
            || !pseudoUUID.matches("^[0-9a-f]{32}$") ) {
            return null;
        }

        uuidBuilder.setLength(0);
        uuidBuilder
            .append(pseudoUUID, 0, 7)
            .append('-')
            .append(pseudoUUID, 8, 11)
            .append('-')
            .append(pseudoUUID, 12, 15)
            .append('-')
            .append(pseudoUUID, 16, 19)
            .append('-')
            .append(pseudoUUID, 20, 32)
        ;

        UUID uuid = UUID.fromString( uuidBuilder.toString() );

        TreeInAForest.LOGGER.info("b => "+ uuid.toString());
        return UUID.fromString( uuidBuilder.toString() );
    }

    public static GameProfile getGameProfileFromUUID(@Nullable UUID uuid) {
        TreeInAForest.LOGGER.info("getGameProfileFromUUID("+uuid.toString()+")");
        if(uuid == null) {
            TreeInAForest.LOGGER.info("A3");
            return null;
        }

        ProfileResult temp = server.getSessionService().fetchProfile(uuid, true);
        TreeInAForest.LOGGER.info(
            (temp == null ? "null" : temp.profile().getName() )
        );

        UserCache userCache = server.getUserCache();
        if(userCache == null) {
            TreeInAForest.LOGGER.info("B3");
            return null;
        }

        Optional<GameProfile> potentialGameProfile = userCache.getByUuid(uuid);
        if( !potentialGameProfile.isPresent() ) {
            TreeInAForest.LOGGER.info("C3");
            return null;
        }

        TreeInAForest.LOGGER.info("c => "+ potentialGameProfile.get().getName() );
        return potentialGameProfile.get();
    }
}
