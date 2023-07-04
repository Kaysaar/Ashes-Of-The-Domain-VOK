package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class KaysaarSindriaFuel extends BaseIndustry {
    public boolean receiveSynchrotron = false;
    @Override

    public void apply() {
        super.apply(true);
        int size = market.getSize();

        demand(Commodities.VOLATILES, size+3);
        demand(Commodities.HEAVY_MACHINERY, size);

        supply(Commodities.FUEL, size + 2);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.VOLATILES);

        applyDeficitToProduction(1, deficit, Commodities.FUEL);

        if (!isFunctional()) {
            supply.clear();
        }
    }
    @Override
    public void unapply() {
        super.unapply();

    }
    @Override
    public void downgrade() {
      receiveSynchrotron = true;
      super.downgrade();

    }
    
    @Override
    public void finishBuildingOrUpgrading() {
        super.finishBuildingOrUpgrading();
        if(receiveSynchrotron){
            SpecialItemData core = new SpecialItemData(Items.SYNCHROTRON, null);
            CargoAPI cargo = getCargoForInteractionMode(MarketAPI.MarketInteractionMode.REMOTE);
            if (cargo != null) {
                cargo.addSpecial(core, 1);
            }
            receiveSynchrotron=false;
        }

    }
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }


    @Override
    protected boolean canImproveToIncreaseProduction() {
        return false;
    }
    @Override
    protected void applyAlphaCoreModifiers() {
    }

    @Override
    protected void applyNoAICoreModifiers() {
    }
    @Override
    public boolean isAvailableToBuild() {
        if(Global.getSettings().getModManager().isModEnabled("aod_core")){
            if(market.hasIndustry(Industries.FUELPROD)){
                if(market.getIndustry(Industries.FUELPROD).getSpecialItem()==null ){
                    return false;
                }
            }

        }
        else{
            return Global.getSector().getPlayerFaction().knowsIndustry("sindrianfuel");
        }


        return true;
    }
    public String getUnavailableReason() {
        if(this.getSpec().hasTag("consumes")){
            return "Required Synchotron Core to be installed on Fuel Production Facility";
        }
        else{
            return "";
        }

    }
    @Override
    public boolean showWhenUnavailable() {


        if(Global.getSettings().getModManager().isModEnabled("aod_core")){
            if((Global.getSector().getPlayerFaction().knowsIndustry("sindrianfuel"))){
                Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
                return researchSaved != null ?  researchSaved.get(this.getId()) : false;
            }

        }
        else{
            return Global.getSector().getPlayerFaction().knowsIndustry("sindrianfuel");
        }
        
        return true;

    }
    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }
    

}
