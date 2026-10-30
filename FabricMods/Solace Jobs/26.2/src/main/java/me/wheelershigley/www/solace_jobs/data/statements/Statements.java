package me.wheelershigley.www.solace_jobs.data.statements;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Statements {
    public static final Statements INSTANCE = new Statements();
    private static final HashMap<UUID, Statement> DATA = new HashMap<>();

    private Statements() {}

    public void add(UUID playerIdentifier, Identifier jobIdentifier, double amount) {
        Statement playerStatement = DATA.getOrDefault(playerIdentifier, new Statement() );
        playerStatement.add(jobIdentifier, amount);
        DATA.put(playerIdentifier, playerStatement);
    }

    public void notifyPlayersOfStatements(MinecraftServer server) {
        for( Map.Entry<UUID, Statement> entry : DATA.entrySet() ) {
            List<Component> incomeStatement = entry.getValue().popAsTextComponents();
            ServerPlayer player = server.getPlayerList().getPlayer( entry.getKey() );
            if( player != null ) {
                for(Component line : incomeStatement) {
                    player.sendSystemMessage(line);
                }
            }
        }
        DATA.clear();
    }
}
