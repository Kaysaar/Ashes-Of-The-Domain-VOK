package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin2;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class PlutoStation extends BaseMarketConditionPlugin2 {
    protected boolean hasInitalized = false;
    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {
        super.init(market, condition);


    }
    @Override
    public void apply(String id) {
        if(!hasInitalized){
            hasInitalized = true;
            if(market.getPrimaryEntity() instanceof PlanetAPI planet){
                if(market.getPrimaryEntity().getMemory()!=null&&!market.getPrimaryEntity().getMemory().getKeys().isEmpty()){
                    if(BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(planet,"aotd_pluto_station")==null){
                        PlutoMegastructure section = (PlutoMegastructure) MegastructureSpecManager.getSpecForMegastructure("aotd_pluto_station").getScript();
                        section.trueInit("aotd_pluto_station",market.getPrimaryEntity(),market);
                    }
                    else{
                        PlutoMegastructure megastructure = (PlutoMegastructure) BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(planet,"aotd_pluto_station");
                        megastructure.tiedMarket = market;

                    }
                }

            }
        }
        if(!market.hasIndustry("pluto_station")&&market.getFaction()!=null&&!market.getFactionId().equals(Factions.NEUTRAL)){
            market.addIndustry("pluto_station");
        }
        try {
            if(BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(market.getPrimaryEntity(),"aotd_pluto_station")!=null){
                BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(market.getPrimaryEntity(),"aotd_pluto_station").tiedMarket = market;
            }
        } catch (Exception ignored) {
        }
    }
}
