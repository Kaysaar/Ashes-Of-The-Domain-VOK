package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TenebriumHullmodApplication extends ShroudBasedProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Unlock new hullmods related to shrouded stuff!", Misc.getPositiveHighlightColor(),5f);
    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }
}
