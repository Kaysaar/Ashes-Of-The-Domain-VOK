package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;

public class Policrystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize()-8;
        if(size<=0){
            size=0;
        }
        demand(Commodities.ORE, 9 + size);
        demand(Commodities.RARE_ORE, 7 + size); // have to keep it low since it can be circular
        demand(AodCommodities.PURIFIED_ORE, 4+size);
        demand(AodCommodities.POLYMERS,5+size);
        supply(AodCommodities.REFINED_METAL, market.getSize()-1); //1+1+1 3 at size 6

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORE, AodCommodities.PURIFIED_ORE, Commodities.RARE_ORE,AodCommodities.POLYMERS);
        if(deficit.two>market.getSize()-4){
            deficit.two = market.getSize()-4;
            if(deficit.two<0){
                deficit.two=0;
            }
        }
        applyDeficitToProduction(2, deficit, AodCommodities.REFINED_METAL);

        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Lost Technology", Alignment.MID, 10f);
        tooltip.addPara("This industry is capable of producing sophisticated resources, that can't be used by normal industries", 10f);
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
        if(market.getIndustry(AoDIndustries.CRYSTALIZATOR)==null){
            return false;
        }
        if(market.getIndustry(AoDIndustries.CRYSTALIZATOR).getSpecialItem()==null ){
            return false;
        }
        return market.getIndustry(AoDIndustries.CRYSTALIZATOR).getSpecialItem().getId().equals(Items.CATALYTIC_CORE);
    }

    @Override
    public String getUnavailableReason() {
        return "Catalytic Core required to be installed in Crystalizator";

    }
}
