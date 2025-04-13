package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class CompoundSpecialProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain access to produce new commodity : Compound, that can enhance efficiency of fuel.", Misc.getPositiveHighlightColor(),5f);
    }
}
