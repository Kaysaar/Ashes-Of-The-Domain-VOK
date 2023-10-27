package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;

public class CascadeReprocessor extends BaseIndustry {
    public void apply() {
        super.apply(true);
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        int size = market.getSize()-8;

        if(market.getSize()<=8){
            demand(Commodities.METALS, 9); // have to keep it low since it can be circular
            demand(AodCommodities.PURIFIED_RARE_ORE, 4);
            demand(AodCommodities.COMPOUNDS,6);
        }
        else{
            demand(Commodities.METALS, 9 + size); // have to keep it low since it can be circular
            demand(AodCommodities.PURIFIED_RARE_ORE, 4+size);
            demand(AodCommodities.COMPOUNDS,6+size);
        }
        supply(AodCommodities.PURIFIED_TRANSPLUTONICS, market.getSize()-3);




        Pair<String, Integer> deficit = getMaxDeficit(AodCommodities.PURIFIED_RARE_ORE, Commodities.METALS,AodCommodities.COMPOUNDS);
        int maxDeficit = market.getSize()-3 ;// to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit, AodCommodities.PURIFIED_TRANSPLUTONICS);

        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }


    public float getPatherInterest() {
        return 2f + super.getPatherInterest();
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
    @Override
    public boolean isAvailableToBuild() {
        if(market.getIndustry(AoDIndustries.ISOTOPE_SEPARATOR)==null){
            return false;
        }
        if(market.getIndustry(AoDIndustries.ISOTOPE_SEPARATOR).getSpecialItem()==null ){
           return false;
        }
        return market.getIndustry(AoDIndustries.ISOTOPE_SEPARATOR).getSpecialItem().getId().equals(Items.CATALYTIC_CORE);
    }

    @Override
    public String getUnavailableReason() {
        return "Catalytic Core required to be installed in Isotope Separator";

    }
}
