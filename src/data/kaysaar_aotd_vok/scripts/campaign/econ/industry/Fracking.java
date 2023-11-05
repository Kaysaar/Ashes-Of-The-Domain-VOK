package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashMap;
import java.util.Map;

public class Fracking extends BaseIndustry {
    private boolean isCryovolcanicOrFrozen() {
        boolean isCryovolcanicOrFrozen = false;
        if(market.getPlanetEntity()!=null){
            if(market.getPlanetEntity().getTypeId().equals("frozen") ||market.getPlanetEntity().getTypeId().equals("cryovolcanic")||market.getPlanetEntity().getTypeId().equals("frozen1")){
                isCryovolcanicOrFrozen= true;
            }
        }
        return isCryovolcanicOrFrozen;
    }
    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.DRUGS,  size - 2);
        if(isCryovolcanicOrFrozen()) {
            demand(AodCommodities.WATER,5);
        }
        else{
            demand(AodCommodities.WATER,0);

        }

        if(AoDUtilis.getOrganicsAmount(market)>=-1){
            supply(Commodities.ORGANICS,AoDUtilis.getOrganicsAmount(market)+(market.getSize()+2));
        }
        if(AoDUtilis.getNormalOreAmount(market)>=-1){
            supply(Commodities.ORE,AoDUtilis.getNormalOreAmount(market)+(market.getSize()+2));
        }
        if(AoDUtilis.getRareOreAmount(market)>=-1){
            supply(Commodities.RARE_ORE,AoDUtilis.getRareOreAmount(market)+(market.getSize()+2));
        }
        if(AoDUtilis.getVolatilesAmount(market)>=-1){
            supply(Commodities.VOLATILES,AoDUtilis.getVolatilesAmount(market)+(market.getSize()+2));
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS,AodCommodities.WATER,Commodities.RARE_ORE);
        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();

    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Lost Technology", Alignment.MID,10f);
        tooltip.addPara("This industry is capable of producing vast quantities of goods",10f);
        tooltip.addPara("Unfortunate due to incompatibilities and ensuring safety of our works Mantle Bore can't be used here!",Misc.getNegativeHighlightColor(),10f);
    }
    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
            return  (AoDUtilis.getOrganicsAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >=-1 || AoDUtilis.getRareOreAmount(market) >= -1 ||AoDUtilis.getVolatilesAmount(market)>=-1);

    }

    @Override
    public String getUnavailableReason() {
        return "There is no ore present on this planet";

    }

    @Override
    public boolean showWhenUnavailable() {
        Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
        return researchSaved != null ?  researchSaved.get(this.getId()) :(AoDUtilis.getOrganicsAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >=-1 || AoDUtilis.getRareOreAmount(market) >= -1);
    }


}
