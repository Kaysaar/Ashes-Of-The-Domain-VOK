package data.console;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStage;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class UnlockAllProjects implements BaseCommand {
    @Override
    public org.lazywizard.console.BaseCommand.CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return org.lazywizard.console.BaseCommand.CommandResult.WRONG_CONTEXT;
        }
        SpecialProjectManager manager = SpecialProjectManager.getInstance();
        for (AoTDSpecialProject option : manager.getProjects().values()) {
            option.setWasEverDiscovered(true);
            option.createIntelForUnlocking();
        }

        Console.showMessage("Unlocked All Special Projects");

        return org.lazywizard.console.BaseCommand.CommandResult.SUCCESS;

    }
}
