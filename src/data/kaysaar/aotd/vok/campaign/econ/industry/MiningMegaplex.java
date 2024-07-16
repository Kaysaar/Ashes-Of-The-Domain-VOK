package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.Mining;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;

public class MiningMegaplex extends BaseIndustry {
    @Override
    public void apply() {
        int bonus  = -4;
        if(this.special!=null&&this.special.getId().equals(Items.MANTLE_BORE)){
            bonus=3;
        }
        int size = market.getSize();
        if(AoDUtilis.getOrganicsAmount(market)>=-1){
            supply(Commodities.ORGANICS,AoDUtilis.getOrganicsAmount(market)+(market.getSize()+2)+bonus);
        }
        if(AoDUtilis.getNormalOreAmount(market)>=-1){
            supply(Commodities.ORE,AoDUtilis.getNormalOreAmount(market)+(market.getSize()+2)+bonus);
        }
        if(AoDUtilis.getRareOreAmount(market)>=-1){
            supply(Commodities.RARE_ORE,AoDUtilis.getRareOreAmount(market)+(market.getSize())+bonus);
        }
        if(AoDUtilis.getVolatilesAmount(market)>=-1){
            supply(Commodities.VOLATILES,AoDUtilis.getVolatilesAmount(market)+(market.getSize()+2)+bonus);
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS, AoTDCommodities.WATER,Commodities.RARE_ORE,Commodities.VOLATILES);
        super.apply(true);

    }

    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return  AoDUtilis.isMiningAvailable(market) && AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market)&&market.getSize()>=6;
    }

    @Override
    public String getUnavailableReason() {
        String reason = "";
        if(market.getSize()<6){
           reason+="Market size must be at least size 6 or greater.\n";
        }
        if(!AoDUtilis.isMiningAvailable(market)){
            reason+="There are no resources present on this market!";
        }
        return reason;
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.ADD_INDUSTRY || isFunctional()) {
            float total = 0;
            String totalStr;
            Color h = Misc.getHighlightColor();
            h = Misc.getPositiveHighlightColor();
            totalStr = "From 1 to 3 ";
            tooltip.addSectionHeading("Domain Integration", Alignment.MID,10f);
            float opad = 10f;
            Color[]colors = new Color[2];
            colors[0]=Misc.getPositiveHighlightColor();
            colors[1] = Misc.getNegativeHighlightColor();
            tooltip.addPara("With Mantle bore installed total bonus for production is %s, but without mantle bore installed, industry receives %s penalty towards production",5f,colors,"+6","-4");

        }


    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market);
    }
}
