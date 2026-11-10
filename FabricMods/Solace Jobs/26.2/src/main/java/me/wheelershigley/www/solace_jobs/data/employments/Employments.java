package me.wheelershigley.www.solace_jobs.data.employments;

import me.wheelershigley.www.solace_jobs.LuckPermsHelper;
import me.wheelershigley.www.solace_jobs.data.Savable;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.regex.Pattern;

public class Employments implements Savable< HashMap< UUID, Set<Identifier> > > {
    public HashMap< UUID, Set<Identifier> > employments = new HashMap<>();

    public Employments() {}
    public Employments(HashMap< UUID, Set<Identifier> > employments) {
        this.employments = employments;
    }

    private static final Pattern PATTERN = Pattern.compile("solace_jobs.jobs_limit.[0-9]+", Pattern.CASE_INSENSITIVE);
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

        final OptionalInt maximumPermission = LuckPermsHelper.getMaximumIntegerPermission(serverPlayer, "solace_jobs.jobs_limit.");
        final int MAXIMUM = maximumPermission.orElse(0);
        // When one can only be in one job, trying to join another should leave the current one
        if(MAXIMUM == 1) {
            for(Identifier identifier : employments) {
                JobDefinition employment = JobsCodec.INSTANCE.getInstanceData().getJob(identifier);
                if(employment == null) {
                    continue;
                }
                leave(data, serverPlayer, employment);
            }
        }
        // Jobs Limit(s)
        if( MAXIMUM <= employments.size() ) {
            serverPlayer.sendSystemMessage(
                Component.translatable("solace_jobs.join_job.limited")
            );
            return false;
        }

        employments.add(job.identifier);
        data.employments.put(playerIdentifier, employments);
        serverPlayer.sendSystemMessage(
            Component.translatable(
                "solace_jobs.join_job",
                Component.translatable( job.getTranslationKey() )
            )
        );
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
