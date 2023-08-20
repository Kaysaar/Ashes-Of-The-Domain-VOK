package data.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
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
            if(researchOption.isDisabled)continue;
            researchOption.isResearched=true;
            IndustrySpecAPI specApi = Global.getSettings().getIndustrySpec(researchOption.downgradeId);
            for (String tag : specApi.getTags()) {
                if (tag.contains("starter")) {
                    specApi.setUpgrade(researchOption.industryId);
                }
            }
        }
        Console.showMessage("All technologies have been researched");
        researchAPI.saveResearch(true);

        return CommandResult.SUCCESS;
    }
}
