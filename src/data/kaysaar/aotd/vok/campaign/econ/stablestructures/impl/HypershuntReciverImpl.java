package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.stablestructures.AoTDStableStructureImpl;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class HypershuntReciverImpl extends AoTDStableStructureImpl {

    @Override
    public void createTooltipForEffectSection(TooltipMakerAPI tooltip, AoTDStableStructureTooltipMode mode) {
        tooltip.addPara(
                "Channels energy from a functional Coronal Hypershunt to colonies in this star system. " +
                        "Provides %s maximum industry slot and %s production to same-faction colonies while connected.",
                5f, Misc.getHighlightColor(), "+1", "+1"
        );
    }

    @Override
    public boolean isAvailableToBuild(StarSystemAPI systemAPI, FactionAPI factionAttemptingToBuildIt) {
        return AoTDMainResearchManager.getInstance().isAvailableForFaction(AoTDTechIds.HP_ENERGY_DISTRIB,factionAttemptingToBuildIt);
    }

    @Override
    public boolean showWhenUnavailable(StarSystemAPI systemAPI, FactionAPI factionAttemptingToBuildIt) {
        return false;
    }
}
