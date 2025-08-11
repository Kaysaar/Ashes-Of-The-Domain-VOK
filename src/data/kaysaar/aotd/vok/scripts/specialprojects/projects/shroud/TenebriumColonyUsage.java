package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;

public class TenebriumColonyUsage extends ShroudBasedProject {
    public static ArrayList<String> TENEBRIUM_ITEMS = new ArrayList<>();

    static {
        TENEBRIUM_ITEMS.add("aotd_shrouded_nanoforge");
    }

    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain ability to produce Tenebrium Nanoforge, Tenebrium Catalyst and Tenebrium Refinement Matrix, new generation of colony items, that will heavily benefit us!", Misc.getPositiveHighlightColor(), 5f);

    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return super.checkIfProjectShouldUnlock() && AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
    }

    @Override
    public Object grantReward() {
        TENEBRIUM_ITEMS.forEach(x -> Global.getSector().getPlayerFaction().getMemory().set("$aotd" + x, true));

        return null;
    }
}
