package data.console;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
@SuppressWarnings("unused")
public class ResearchAllTechnologies implements BaseCommand {
    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
        for (ResearchOption option : manager.getResearchRepoOfFaction()) {
            option.setResearched(true);
            AoTDListenerUtilis.finishedResearch(option.getSpec().getId(), manager.getFaction());
        }

        Console.showMessage("All technologies researched");

        return CommandResult.SUCCESS;

    }
}
