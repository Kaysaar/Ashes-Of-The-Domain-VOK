package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import niko_SA.augments.core.stationAugmentStore;

import static data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.TenebriumHullmodApplication.STATION_AUGMENTS;
import static data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.TenebriumHullmodApplication.TENEBRIUM_HULLMODS;

public class ShroudEnergyUsage extends ShroudBasedProject{
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain access to a new industry: Exomatter Processing. Converts Shrouded Substrate into Tenebrium Cells — refined, stable power sources that unlock a new generation of weapons, colony items, and more!", Misc.getPositiveHighlightColor(), 5f);
        tooltip.addPara("Unlock new hullmods, that utilize Tenebrium Cells.", Misc.getPositiveHighlightColor(), 3f);

    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 2;
    }

    @Override
    public Object grantReward() {
        TENEBRIUM_HULLMODS.forEach(x -> Global.getSector().getPlayerFaction().addKnownHullMod(x));
        if (Global.getSettings().getModManager().isModEnabled("niko_stationAugments")) {
            STATION_AUGMENTS.forEach(x -> stationAugmentStore.teachAugmentExternal(Global.getSector().getPlayerFaction(), x, true));
        }
        return super.grantReward();
    }
}
