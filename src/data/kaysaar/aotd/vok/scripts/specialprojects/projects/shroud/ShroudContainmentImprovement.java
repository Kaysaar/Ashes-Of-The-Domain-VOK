package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShroudContainmentImprovement  extends ShroudBasedProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("From now on fighting Shroud Dwellers will yield greater amount of Shrouded Substrate,due to improvements and better understanding of how to contain them", Misc.getPositiveHighlightColor(),5f);
    }
    @Override
    public int getRequiredShroudExpertLevel() {
        return 1;
    }

    @Override
    public void projectCompleted() {
        ShroudProjectMisc.setBoolean(ShroudProjectMisc.hasBetterContaimentMethods,true);
    }
}
