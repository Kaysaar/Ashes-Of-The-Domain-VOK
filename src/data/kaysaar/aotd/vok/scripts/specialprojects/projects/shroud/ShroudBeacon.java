package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShroudBeacon extends ShroudBasedProject{
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain ability to summon even bigger manifestation, when approaching Abyssal Light", Misc.getTooltipTitleAndLightHighlightColor(),5f);
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


