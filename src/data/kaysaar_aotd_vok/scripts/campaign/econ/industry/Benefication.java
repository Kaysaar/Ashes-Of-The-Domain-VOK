package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashMap;
import java.util.Map;

public class Benefication extends BaseIndustry {
    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        demand(Commodities.HEAVY_MACHINERY, size-2);
        demand(AodCommodities.RECITIFICATES,  size);
        if(AoDUtilis.getRareOreAmount(market)>=0){
            supply(AodCommodities.PURIFIED_RARE_ORE,AoDUtilis.getRareOreAmount(market)+(market.getSize()-4)); //for ideal size 6 with alpha core 6
        }
        if(AoDUtilis.getNormalOreAmount(market)>=0){
            supply(AodCommodities.PURIFIED_ORE,AoDUtilis.getNormalOreAmount(market)+(market.getSize()-4)); ///for ideal size 6 with alpha core: 7
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AodCommodities.PURIFIED_ORE,AodCommodities.PURIFIED_RARE_ORE);
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
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        if(market.getIndustry(Industries.MINING)==null){
            return false;
        }
        if(market.getIndustry(Industries.MINING).getSpecialItem()==null ){
            return false;
        }
        return ((AoDUtilis.getRareOreAmount(market)>=0 || AoDUtilis.getNormalOreAmount(market) >= 0)&& market.getIndustry(Industries.MINING).getSpecialItem().getId().equals(Items.MANTLE_BORE));

    }


    @Override
    public String getUnavailableReason() {
        String reasoning= null;

        if( AoDUtilis.getNormalOreAmount(market)<-1){
            reasoning = "There is no ore on that planet large enough to support that industry ";
        }
        if(AoDUtilis.getRareOreAmount(market)<-1){
            if(reasoning!=null){
                reasoning+="\nThere is no transplutonic ore on that planet large enough to support that industry ";
            }

        }
        if(market.hasIndustry(Industries.MINING)){
            if(market.getIndustry(Industries.MINING).getSpecialItem()==null||!market.getIndustry(Industries.MINING).getSpecialItem().getId().equals(Items.MANTLE_BORE)){
                if(reasoning!=null){
                    reasoning+="\nMantle Bore required to be installed in Mining";
                }
                else{
                    reasoning = "Mantle Bore required to be installed in Mining";
                }
            }
        }

        return reasoning;
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Lost Technology", Alignment.MID,10f);
        tooltip.addPara("This industry is capable of producing sophisticated resources, that can't be used by normal industries",10f);
    }

    @Override
    public boolean showWhenUnavailable() {
        Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
        return researchSaved != null ?  researchSaved.get(this.getId()) :(AoDUtilis.getRareOreAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >= -1);
    }
}
