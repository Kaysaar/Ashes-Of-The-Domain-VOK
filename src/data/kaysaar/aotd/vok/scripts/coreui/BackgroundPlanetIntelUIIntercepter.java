package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.listeners.CoreUiInterceptor;

public class BackgroundPlanetIntelUIIntercepter implements EveryFrameScript {
    MarketAPI marketAPI;
    NidavelirComplexMegastructure megastructure;
    public BackgroundPlanetIntelUIIntercepter(MarketAPI marketAPI, NidavelirComplexMegastructure megastructure) {
        this.marketAPI = marketAPI;
        this.megastructure = megastructure;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }


    @Override
    public void advance(float amount) {
        CoreUiInterceptor.initalizeBackgroundPLanet(megastructure);
        if(!CoreUITabId.OUTPOSTS.equals(Global.getSector().getCampaignUI().getCurrentCoreTab()))Global.getSector().removeTransientScriptsOfClass(this.getClass());

    }
}
