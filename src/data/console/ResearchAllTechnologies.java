package data.console;

import data.plugins.AoDUtilis;
import data.scripts.research.ResearchAPI;
import data.scripts.research.ResearchOption;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class ResearchAllTechnologies implements BaseCommand {
    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        ResearchAPI researchAPI = AoDUtilis.getResearchAPI();
        if(researchAPI==null){
            Console.showMessage("ResearchAPI has not been initalized");
            return CommandResult.ERROR;
        }
        for (ResearchOption researchOption : researchAPI.getResearchOptions()) {
            researchOption.isResearched=true;
        }
        Console.showMessage("All technologies have been researched");
        researchAPI.saveResearch(true);

        return CommandResult.SUCCESS;
    }
}
