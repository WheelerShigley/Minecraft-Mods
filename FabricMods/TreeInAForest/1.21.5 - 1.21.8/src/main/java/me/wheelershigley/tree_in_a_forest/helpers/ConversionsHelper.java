package me.wheelershigley.tree_in_a_forest.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.wheelershigley.tree_in_a_forest.TreeInAForest.*;

public class ConversionsHelper {
    public static GameProfile getProfileFromPlayerName(@Nullable String name) {
        if(
            name == null
            || !name.matches("^[a-zA-Z0-9_]{2,16}$")
            || Blacklist.nameBlacklist.contains(name)
        ) {
            return null;
        }

        UserCache userCache = server.getUserCache();
        if(userCache == null) {
            return null;
        }

        Optional<GameProfile> potentialGameProfile = userCache.findByName(name);
        return potentialGameProfile.orElse(null);

    }

    private static final StringBuilder uuidBuilder = new StringBuilder();
    public static UUID getUuidFromTrimmedUuidString(@Nullable String pseudoUUID) {
        if(
            pseudoUUID == null
            || !pseudoUUID.matches("^[0-9a-f]{32}$") ) {
            return null;
        }

        uuidBuilder.setLength(0);
        uuidBuilder
            .append(pseudoUUID, 0, 8)
            .append('-')
            .append(pseudoUUID, 8, 12)
            .append('-')
            .append(pseudoUUID, 12, 16)
            .append('-')
            .append(pseudoUUID, 16, 20)
            .append('-')
            .append(pseudoUUID, 20, 32)
        ;

        return UUID.fromString( uuidBuilder.toString() );
    }

    private static final String LOOKUP_PROFILE_BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public static GameProfile getGameProfileFromUUID(UUID uuid, boolean isOnlineMode) {
        if(uuid == null) {
            return null;
        }
        if( gameProfileCache.containsKey(uuid) ) {
            return gameProfileCache.get(uuid);
        }

        if(isOnlineMode) {
            try {
                URL url = URI.create(
                    LOOKUP_PROFILE_BASE_URL + uuid.toString()
                ).toURL();
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        url.openStream(),
                        StandardCharsets.UTF_8
                    )
                );
                String streamContents = br.lines().collect( Collectors.joining("\r\n") );
                JsonObject jsonContents = JsonParser.parseString(streamContents).getAsJsonObject();
                String name = jsonContents.get("name").getAsString();
                if(name == null || name.isBlank() ) {
                    return null;
                }

                return new GameProfile(uuid, name);
            } catch(IOException ioException) {
                TreeInAForest.LOGGER.error("An error occurred looking up a player!", ioException);
            }
        }
        return null;
    }
}
