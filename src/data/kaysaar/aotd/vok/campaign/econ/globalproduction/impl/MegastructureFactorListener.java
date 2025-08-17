package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.ColonyOtherFactorsListener;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class MegastructureFactorListener implements ColonyOtherFactorsListener {
    @Override
    public void printOtherFactors(TooltipMakerAPI text, SectorEntityToken entity) {

    }

    @Override
    public boolean isActiveFactorFor(SectorEntityToken entity) {
        return false;
    }
}
