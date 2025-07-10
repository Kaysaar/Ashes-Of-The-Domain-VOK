package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureUnlockIntel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.industry.BaseMegastructureIndustry;

public class PlutoStation extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(!market.hasIndustry("pluto_station")&&market.getFaction()!=null&&market.getFaction().isPlayerFaction()){
            GPBaseMegastructure megastructure = (GPBaseMegastructure) this.market.getPlanetEntity().getMemory().get(GPBaseMegastructure.memKey);
            if(megastructure!=null){
                if(!GPManager.getInstance().getMegastructures().contains(megastructure)){
                    GPManager.getInstance().addMegastructureToList(megastructure);
                    MegastructureUnlockIntel intel = new MegastructureUnlockIntel(megastructure);
                    Global.getSector().getIntelManager().addIntel(intel, false);
                    if(!megastructure.haveRecivedStoryPoint){
                        Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);
                        megastructure.setHaveRecivedStoryPoint(true);
                    }
                    if(!this.market.getPlanetEntity().getMarket().hasIndustry(megastructure.getIndustryIfIfPresent())){
                        BaseMegastructureIndustry.addMegastructureIndustry(megastructure.getIndustryIfIfPresent(),this.market,megastructure);
                    }
                }
                else{
                    BaseMegastructureIndustry.addMegastructureIndustry(megastructure.getIndustryIfIfPresent(),this.market,megastructure);
                }
            }



        }
    }
}
