package me.wheelershigley.tree_in_a_forest.blacklist;

import com.mojang.authlib.GameProfile;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.helpers.ConversionsHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import static me.wheelershigley.tree_in_a_forest.TreeInAForest.LOGGER;
import static me.wheelershigley.tree_in_a_forest.TreeInAForest.MOD_ID;
import static me.wheelershigley.tree_in_a_forest.helpers.ConversionsHelper.*;

public class Blacklist {
    public static ArrayList<GameProfile> profileBlacklist = new ArrayList<>();
    public static ArrayList<String> nameBlacklist = new ArrayList<>();

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
            LOGGER.error("Error making directory.", ioException);
            return false;
        }

        return true;
    }

    private static boolean writeFile() {
        try {
            //put currently-blacklisted player-ids in file
            StringBuilder playerListBuilder = new StringBuilder();
            for(String name : nameBlacklist) {
                playerListBuilder.append(name).append("\r\n");
            }
            for(GameProfile user : profileBlacklist) {
                playerListBuilder
                    .append( user.getId().toString().replace("-", "") )
                    .append("\r\n")
                ;
            }
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.write( playerListBuilder.toString() );
            writer.close();
        } catch(IOException ioException) {
            LOGGER.error("Error writing to blacklist.", ioException);
            return false;
        }
        return true;
    }

    public static void initializeBlackList(boolean isOnlineMode) {
        createFileIfMissing();

        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch(FileNotFoundException fileNotFoundException) {
            LOGGER.error("Error reading blacklist.", fileNotFoundException);
            return;
        }

        String currentLine;
        GameProfile currentProfile;
        while( reader.hasNext() ) {
            //TODO: convert to async
            currentLine = reader.next();
            currentProfile = ConversionsHelper.getGameProfileFromUUID(
                getUuidFromTrimmedUuidString(currentLine),
                isOnlineMode
            );

            if(
                currentLine.matches("[0-9a-f]{32}")
            ) {
                if(currentProfile != null) {
                    Blacklist.profileBlacklist.add(currentProfile);
                } else {
                    //TODO: logging
                }
            } else {
                Blacklist.nameBlacklist.add(currentLine);
            }
        }
    }

    public static String[] getBlacklistedNames() {
        ArrayList<String> blacklistedNames = new ArrayList<>();
        for(String name : Blacklist.nameBlacklist) {
            blacklistedNames.add(name);
        }

        String currentName;
        for(GameProfile profile : Blacklist.profileBlacklist) {
            currentName = profile.getName();
            if(currentName == null) {
                continue;
            }
            blacklistedNames.add(currentName);
        }
        return blacklistedNames.toArray(new String[0]);
    }

    public static boolean blacklistUser(GameProfile profile) {
        if( profileBlacklist.contains(profile) ) {
            return true;
        }
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

    public static boolean blacklistUser(String playerName) {
        if( nameBlacklist.contains(playerName) ) {
            return true;
        }
        nameBlacklist.add(playerName);

        if( createFileIfMissing() ) {
            return true;
        }
        return writeFile();
    }

    public static boolean unblacklistUser(String playerName) {
        nameBlacklist.remove(playerName);

        if( createFileIfMissing() ) {
            return true;
        }
        return writeFile();
    }
}
