package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class BasicAIAlgorithmsProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain ability to produce gamma and beta cores", Misc.getPositiveHighlightColor(),5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS);
    }

    @Override
    public void grantReward() {
        Global.getSector().getMemory().set("$finished_basic_ai",true);

    }
}
