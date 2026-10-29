package me.wheelershigley.www.solace_jobs.menus;

import eu.pb4.sgui.api.elements.GuiElement;
import me.wheelershigley.www.solace_jobs.data.employments.Employments;
import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.wheelershigley.www.solace_jobs.menus.ItemsHelper.getMenuItem;

public class JobsGUI extends ImmutableSimpleGUI {
    public JobsGUI(ServerPlayer player) {
        //TODO: 9xX
        super(player, MenuType.GENERIC_9x1, null);
    }

    @Override
    public String getTranslationKey() {
        return "solace_jobs.menus.jobs";
    }

    @Override
    public MenuType<?> getMenuType() {
        //TODO: 9xX
        return MenuType.GENERIC_9x1;
    }
    @Override
    public void initializeMenu() {
        Set<JobDefinition> jobs = JobsCodec.INSTANCE.getInstanceData().getData();
        List<JobDefinition> jobList = new ArrayList<>(jobs);
        int slot_index = 0;

        int count = jobList.size();
        int rows = (count + 8) / 9;
        int baseCount = count / rows;
        int extra = count % rows;

        for (int row = 0; row < rows; row++) {
            int rowCount = baseCount + (row < extra ? 1 : 0);
            int slot = row * 9 + (9 - rowCount) / 2;

            for (int i = 0; i < rowCount; i++) {
                if (rowCount < 9 && rowCount % 2 == 0 && i == rowCount / 2) {
                    slot++;
                }

                JobDefinition job = jobList.get(slot_index++);
                setToggleableItemSlot(this, player, job, slot++);
            }
        }
    }
    private static void setToggleableItemSlot(
        ImmutableSimpleGUI jobsGUI,
        ServerPlayer player,
        JobDefinition job,
        int slot
    ) {
        String translationKey =
            job.identifier.getNamespace() + ".job." + job.identifier.getPath()
        ;
        AtomicBoolean isPlayerInJob = new AtomicBoolean(
            EmploymentsCodec.INSTANCE
                .getInstanceData()
                .employments.getOrDefault( player.getUUID(), new HashSet<>() )
                .contains(job.identifier)
        );

        GuiElement.ClickCallback[] toggle = new GuiElement.ClickCallback[1];
        toggle[0] = (index, type, action, gui) -> {
            Employments.attemptToggle(
                EmploymentsCodec.INSTANCE.getInstanceData(),
                player.getUUID(),
                job.identifier
            );

            isPlayerInJob.set(
                EmploymentsCodec.INSTANCE
                    .getInstanceData()
                    .employments.getOrDefault( player.getUUID(), new HashSet<>() )
                    .contains(job.identifier)
            );
            gui.setSlot(
                index,
                getMenuItem(
                    isPlayerInJob.get() ? job.IN_ITEM : job.OUT_ITEM,
                    false,
                    translationKey
                ),
                toggle[0]
            );
        };

        jobsGUI.setSlot(
            slot,
            getMenuItem(
                isPlayerInJob.get() ? job.IN_ITEM : job.OUT_ITEM,
                false,
                translationKey
            ),
            toggle[0]
        );
    }
}
