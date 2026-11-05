package me.wheelershigley.www.solace_jobs.data.employments;

import me.wheelershigley.www.solace_jobs.data.Savable;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.regex.Matcher;
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

        // Jobs Limit(s)
        final int MAXIMUM = getMaximumJobPermission(serverPlayer);
        if( MAXIMUM <= employments.size() ) {
            serverPlayer.sendSystemMessage(
                Component.translatable("solace_jobs.join_job.limited")
            );
            return false;
        }

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
    private static int getMaximumJobPermission(ServerPlayer serverPlayer) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser( serverPlayer.getUUID() );
        Integer maximum = null;
        if(user != null) {
            Collection<PermissionNode> permissions = user.resolveInheritedNodes(
                NodeType.PERMISSION,
                QueryOptions.builder(QueryMode.CONTEXTUAL).build()
            );

            for(PermissionNode permission : permissions) {
                final Matcher MATCHER = PATTERN.matcher( permission.getPermission() );
                if( MATCHER.matches() ) {
                    String value = permission.getPermission().replaceFirst("solace_jobs.jobs_limit.","");

                    try {
                        int test = Integer.parseInt(value);
                        maximum = (maximum == null) ? test : Math.max(maximum, test);
                    } catch(NumberFormatException _) {}
                }
            }
        }
        return (maximum == null) ? 0 : maximum;
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
