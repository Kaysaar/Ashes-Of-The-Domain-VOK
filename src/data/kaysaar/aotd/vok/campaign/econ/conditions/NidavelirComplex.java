package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyardVisual;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin2;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.NidavelirMegastructure;

public class NidavelirComplex extends BaseMarketConditionPlugin2 {
    boolean valid = true;
    protected boolean hasInitalized = false;
    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {
        super.init(market, condition);
    }


    public static NidavelirComplex getComplexCondition(MarketAPI market){
        if(market==null)return null;
        if(market.hasCondition("aotd_nidavelir_complex")){
            return (NidavelirComplex) market.getCondition("aotd_nidavelir_complex").getPlugin();
        }
        return null;
    }
    public MarketConditionAPI getConditionAPI(){
        return condition;
    }
    public NidavelirShipyardVisual getShipyardVisual(){
        NidavelirMegastructure megastructure = (NidavelirMegastructure) BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(market.getPrimaryEntity(),"aotd_nidavelir");
        return megastructure.getVisual();
    }
    @Override
    public void apply(String id) {
        if(!hasInitalized){
            hasInitalized = true;
            if(market.getPrimaryEntity() instanceof PlanetAPI planet){
                if(market.getPrimaryEntity().getMemory()!=null&&!market.getPrimaryEntity().getMemory().getKeys().isEmpty()){
                    if(BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(planet,"aotd_nidavelir")==null){
                        NidavelirMegastructure section = (NidavelirMegastructure) MegastructureSpecManager.getSpecForMegastructure("aotd_nidavelir").getScript();
                        section.trueInit("aotd_nidavelir",market.getPrimaryEntity(),market);
                    }
                    else{
                        NidavelirMegastructure megastructure = (NidavelirMegastructure) BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(planet,"aotd_nidavelir");
                        megastructure.tiedMarket = market;
                    }
                }


            }

        }
        if(!market.hasIndustry("nidavelir_complex")&&market.getFaction()!=null&&!market.getFactionId().equals(Factions.NEUTRAL)){
            market.addIndustry("nidavelir_complex");
        }
        try {
            if(BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(market.getPrimaryEntity(),"nidavelir_complex")!=null){
                BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(market.getPrimaryEntity(),"nidavelir_complex").tiedMarket = market;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);

    }
}
