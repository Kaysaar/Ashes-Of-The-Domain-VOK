package data.console;

import data.kaysaar_aotd_vok.plugins.AoDUtilis;
import data.kaysaar_aotd_vok.scripts.research.ResearchAPI;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
@SuppressWarnings("unused")
public class FinishResearch implements BaseCommand {
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
        if(researchAPI.getCurrentResearching()==null){
            Console.showMessage("Currently nothing is being researched right now");
            return CommandResult.WRONG_CONTEXT;
        }else{
            Console.showMessage(researchAPI.getCurrentResearching().industryName+" has been researched");
            researchAPI.finishResearch();

        }

        return CommandResult.SUCCESS;

    }
}
