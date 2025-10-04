package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

public class ShroudBeacon extends ShroudBasedProject{
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain ability to summon even bigger manifestation, when approaching an Abyssal Light", Misc.getTooltipTitleAndLightHighlightColor(),5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return super.checkIfProjectShouldUnlock() &&
                BlackSiteProjectManager.getInstance().getProject("aotd_shroud_analysis").checkIfProjectWasCompleted() &&
                BlackSiteProjectManager.getInstance().getProject("aotd_shroud_better_contaiment").checkIfProjectWasCompleted();
    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 1;
    }

    @Override
    public void projectCompleted() {
        ShroudProjectMisc.setBoolean(ShroudProjectMisc.hasAbilityToSummonGreatFleet,true);
        super.projectCompleted();
    }
}


