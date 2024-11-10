package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyDecivListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureUnlockIntel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class NidavelirClaimMegastructure implements PlayerColonizationListener, ColonyDecivListener {
    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        if(planet.getMemory().get(GPBaseMegastructure.memKey)!=null){
            GPBaseMegastructure megastructure = (GPBaseMegastructure) planet.getMemory().get(GPBaseMegastructure.memKey);
            GPManager.getInstance().addMegastructureToList(megastructure);
            MegastructureUnlockIntel intel = new MegastructureUnlockIntel(megastructure);
            Global.getSector().getIntelManager().addIntel(intel, false);
            if(!megastructure.haveRecivedStoryPoint){
                Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);
                megastructure.setHaveRecivedStoryPoint(true);
            }

        }
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {
        if(colony.getPrimaryEntity().getMemory().get(GPBaseMegastructure.memKey)!=null){
            GPBaseMegastructure mega = (GPBaseMegastructure) colony.getPrimaryEntity().getMemory().get(GPBaseMegastructure.memKey);
            for (GPMegaStructureSection megaStructureSection : mega.getMegaStructureSections()) {
                megaStructureSection.setRestoring(false);
                megaStructureSection.setProgressOfRestoration(0f);
            }
            GPManager.getInstance().removeMegastructureFromList((GPBaseMegastructure) colony.getPrimaryEntity().getMemory().get(GPBaseMegastructure.memKey));
        }
    }

    @Override
    public void reportColonyAboutToBeDecivilized(MarketAPI market, boolean fullyDestroyed) {

    }

    @Override
    public void reportColonyDecivilized(MarketAPI market, boolean fullyDestroyed) {
        if(market.getPrimaryEntity().getMemory().get(GPBaseMegastructure.memKey)!=null){
            GPManager.getInstance().removeMegastructureFromList((GPBaseMegastructure) market.getPrimaryEntity().getMemory().get(GPBaseMegastructure.memKey));
        }
    }
}
