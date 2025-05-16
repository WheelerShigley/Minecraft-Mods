package me.wheelershigley.tree_in_a_forest.blacklist;

import com.mojang.authlib.GameProfile;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import static me.wheelershigley.tree_in_a_forest.TreeInAForest.MOD_ID;
import static me.wheelershigley.tree_in_a_forest.blacklist.ConversionsHelper.*;

public class Blacklist {
    public static ArrayList<GameProfile> profileBlacklist = new ArrayList<>();
    //public static ArrayList<String> nameBlacklist = new ArrayList<>();

    private static final String fileName = (MOD_ID + ".blacklist").toLowerCase();
    private static final File file = FabricLoader.getInstance().getConfigDir().resolve(fileName).toFile();

    private static boolean createFileIfMissing() {
        if( file.exists() && !file.isDirectory() ) {
            return false;
        }

        //if files are missing, create them
        try {
            file.getParentFile().mkdirs();
            Files.createFile( file.toPath() );

            writeFile();
        } catch(IOException ioException) {
            //TODO
        }

        return true;
    }

    private static boolean writeFile() {
        try {
            //put currently-blacklisted player-ids in file
            StringBuilder playerListBuilder = new StringBuilder();
            for (GameProfile user : profileBlacklist) {
                playerListBuilder
                    .append( user.getId().toString().replace("-", "") )
                    .append("\r\n")
                ;
            }
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.write( playerListBuilder.toString() );
            writer.close();
        } catch(IOException ioException) {
            //TODO
            return false;
        }
        return true;
    }

    public static ArrayList<GameProfile> getBlackListedUsers(boolean isOnlineMode) {
        createFileIfMissing();

        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch(FileNotFoundException fileNotFoundException) {
            //TODO: logging
            return new ArrayList<GameProfile>();
        }

        String currentTrimmedUuid;
        GameProfile currentProfile;
        ArrayList<GameProfile> blacklistedUUIDs = new ArrayList<>();
        while( reader.hasNext() ) {
            currentTrimmedUuid = reader.next();
            currentProfile = ConversionsHelper.getGameProfileFromUUID(
                getUuidFromTrimmedUuidString(currentTrimmedUuid),
                isOnlineMode
            );

            if(
                currentTrimmedUuid.matches("[0-9a-f]{32}")
                && currentProfile != null
            ) {
                blacklistedUUIDs.add(currentProfile);
            } else {
                TreeInAForest.LOGGER.warn(
                    Text.literal(
                        Text.translatable(
                            "tree_in_a_forest.text.invalid_uuid",
                            currentTrimmedUuid
                        ).getString()
                    ).getString()
                );
            }
        }

        return blacklistedUUIDs;
    }

    public static boolean blacklistUser(GameProfile profile) {
        profileBlacklist.add(profile);

        if( createFileIfMissing() ) {
            return true;
        }
        return writeFile();
    }

    public static boolean unblacklistUser(GameProfile profile) {
        profileBlacklist.remove(profile);

        if( createFileIfMissing() ) {
            return true;
        }
        return writeFile();
    }
}
