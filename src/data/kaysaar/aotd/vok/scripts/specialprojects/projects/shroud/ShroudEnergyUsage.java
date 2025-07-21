package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShroudEnergyUsage extends ShroudBasedProject{
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain access to a new industry: Exomatter Processing. Converts Shrouded Substrate into Tenebrium Cells — refined, stable power sources that unlock a new generation of weapons, hullmods, colony items, and more!", Misc.getPositiveHighlightColor(), 5f);
        
    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 2;
    }
}
