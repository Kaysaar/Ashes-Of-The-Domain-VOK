package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class NanoforgeAnalysisProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain Ability to reconfigure Corrupted nanoforges.", Misc.getPositiveHighlightColor(),5f);
    }
}
