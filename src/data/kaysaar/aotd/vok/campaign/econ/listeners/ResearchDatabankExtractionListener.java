package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import data.kaysaar.aotd.vok.campaign.econ.industry.ResearchFacility;

import static data.kaysaar.aotd.vok.campaign.econ.industry.ResearchFacility.amountDatabanksMonthly;
import static data.kaysaar.aotd.vok.campaign.econ.industry.ResearchFacility.subMarketId;

public class ResearchDatabankExtractionListener implements EconomyTickListener {
    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        Global.getSector().getEconomy().getMarketsCopy().forEach(x->{
            if(x.hasSubmarket(subMarketId)){
                if (x.hasCondition("pre_collapse_facility")) {
                    SubmarketAPI open = x.getSubmarket(subMarketId);
                    Industry research = x.getIndustries().stream().filter(y->y instanceof ResearchFacility).findFirst().orElse(null);
                    if (open != null&&research!=null) {
                        if(research.getAICoreId()!=null&&research.getAICoreId().equals(Commodities.ALPHA_CORE)){
                            open.getCargo().addCommodity("research_databank", 1);
                        }
                        open.getCargo().addCommodity("research_databank", amountDatabanksMonthly);
                    } else {
                        x.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("research_databank", amountDatabanksMonthly);
                    }
                }
            }
        });
    }
}
