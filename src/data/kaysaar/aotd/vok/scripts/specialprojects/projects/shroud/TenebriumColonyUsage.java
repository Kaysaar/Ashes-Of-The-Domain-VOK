package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class TenebriumColonyUsage extends ShroudBasedProject{
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
       tooltip.addPara("Gain ability to produce Tenebrium Nanoforge, Tenebrium Catalyst and Tenebrium Refinement Matrix, new generation of colony items, that will heavily benefit us!", Misc.getPositiveHighlightColor(),5f);

    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return super.checkIfProjectShouldUnlock()&& AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
    }
}
