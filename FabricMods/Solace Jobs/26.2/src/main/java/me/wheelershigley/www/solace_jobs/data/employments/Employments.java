package me.wheelershigley.www.solace_jobs.data.employments;

import me.wheelershigley.www.solace_jobs.data.Savable;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinitions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Employments implements Savable< HashMap< UUID, Set<Identifier> > > {
    public HashMap< UUID, Set<Identifier> > employments = new HashMap<>();

    public Employments() {}
    public Employments(HashMap< UUID, Set<Identifier> > employments) {
        this.employments = employments;
    }

    public static boolean join(
        Employments data,
        ServerPlayer serverPlayer,
        JobDefinition job
    ) {
        UUID playerIdentifier = serverPlayer.getUUID();
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        if( employments.contains(job.identifier) ) {
           return false;
        }

        //TODO: check join limits and react

        serverPlayer.sendSystemMessage(
            Component.translatable(
                "solace_jobs.join_job",
                Component.translatable( job.getTranslationKey() )
            )
        );

        employments.add(job.identifier);
        data.employments.put(playerIdentifier, employments);
        return true;
    }

    public static void leave(
        Employments data,
        ServerPlayer serverPlayer,
        JobDefinition job
    ) {
        UUID playerIdentifier = serverPlayer.getUUID();
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        if( !employments.contains(job.identifier) ) {
            return;
        }

        serverPlayer.sendSystemMessage(
            Component.translatable(
                "solace_jobs.leave_job",
                Component.translatable( job.getTranslationKey() )
            )
        );

        employments.remove(job.identifier);
        data.employments.put(playerIdentifier, employments);
    }

    public static boolean attemptToggle(
        Employments data,
        ServerPlayer serverPlayer,
        JobDefinition job
    ) {
        UUID playerIdentifier = serverPlayer.getUUID();
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        if( employments.contains(job.identifier) ) {
            leave(data, serverPlayer, job);
            return true;
        } else {
            return join(data, serverPlayer, job);
        }
    }

    @Override
    public HashMap< UUID, Set<Identifier> > getData() {
        return this.employments;
    }

    @Override
    public void setData(HashMap< UUID, Set<Identifier> > data) {
        this.employments = data;
    }
}
