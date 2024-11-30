package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.HashSet;
import java.util.Set;

public class PurificationCenter extends BaseIndustry {


    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        demand(Commodities.HEAVY_MACHINERY, size - 3);
        demand(Commodities.ORGANICS, size - 3);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.HEAVY_MACHINERY);
        if(deficit.two<=0){
           Industry ind =  market.getIndustry(Industries.POPULATION);
           if(ind.getSupply(AoTDCommodities.WATER).getQuantity().getModifiedValue()>0){
               ind.getSupply(AoTDCommodities.WATER).getQuantity().modifyFlat("water_prod",2,"Purification Center");
           }
        }

        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        Industry ind =  market.getIndustry(Industries.POPULATION);
        if(ind!=null){
            ind.getSupply(AoTDCommodities.WATER).getQuantity().unmodifyFlat("water_prod");

        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Purification Center", Alignment.MID,10f);
        tooltip.addPara("Increases Water production by 2 units if market produces at least one supply of water.",10f);
    }

    @Override
    public boolean isAvailableToBuild() {

        return false;
    }


    @Override
    public boolean showWhenUnavailable() {

        return false;

    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
