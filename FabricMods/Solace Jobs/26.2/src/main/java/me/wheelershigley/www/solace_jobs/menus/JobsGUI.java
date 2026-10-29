package me.wheelershigley.www.solace_jobs.menus;

import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;

import static me.wheelershigley.www.solace_jobs.menus.ItemsHelper.getMenuItem;

public class JobsGUI extends ImmutableSimpleGUI {
    public JobsGUI(ServerPlayer player) {
        super(player, MenuType.GENERIC_9x1, null);
    }

    @Override
    public String getTranslationKey() {
        return "solace_jobs.menus.jobs";
    }

    @Override
    public MenuType<?> getMenuType() {
        return MenuType.GENERIC_9x1;
    }

    @Override
    public void initializeMenu() {
        Set<JobDefinition> jobs = JobsCodec.INSTANCE.getInstanceData().getData();
        int slot = 0;
        for(JobDefinition job : jobs) {
            boolean isPlayerInJob = EmploymentsCodec.INSTANCE
                .getInstanceData().employments.getOrDefault( player.getUUID(), new HashSet<>() )
                .contains(job.identifier)
            ;
            String translationKey = job.identifier.getNamespace() + ".job." + job.identifier.getPath();
            this.setSlot(
                slot++,
                getMenuItem(
                    isPlayerInJob ? job.IN_ITEM : job.OUT_ITEM,
                    false,
                    translationKey
                )
            );
        }
    }
}
