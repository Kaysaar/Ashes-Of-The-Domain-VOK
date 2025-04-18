package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureUnlockIntel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.industry.BaseMegastructureIndustry;

public class NidavelirComplex extends BaseMarketConditionPlugin {

    @Override
    public void apply(String id) {

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(!market.hasIndustry("nidavelir_complex")&&market.getFaction()!=null&&market.getFaction().isPlayerFaction()){
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
                    if(!this.market.getPlanetEntity().getMarket().hasIndustry("nidavelir_complex")){
                        BaseMegastructureIndustry.addMegastructureIndustry("nidavelir_complex",this.market,megastructure);
                    }
                }
                else{
                    BaseMegastructureIndustry.addMegastructureIndustry("nidavelir_complex",this.market,megastructure);
                }
            }



        }
        if(!market.getMemory().is("$aotd_hotfix",true)){
            market.getMemory().set("$aotd_hotfix",true);
            GPBaseMegastructure megastructure = (GPBaseMegastructure) this.market.getPlanetEntity().getMemory().get(GPBaseMegastructure.memKey);

            GPManager.getInstance().removeMegastructureFromList(megastructure);
        }
    }
}
