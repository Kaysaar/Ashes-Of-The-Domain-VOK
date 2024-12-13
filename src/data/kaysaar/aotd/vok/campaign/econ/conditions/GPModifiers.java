package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class GPModifiers extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction())==null)return;
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS)){
            for (Industry industry : market.getIndustries()) {
                if(industry instanceof HeavyIndustry) {
                    industry.getSupply("advanced_components").getQuantity().modifyFlat("aotd_electronics",1,"Sophisticated electronics");
                }
            }
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry instanceof HeavyIndustry) {
                industry.getSupply("advanced_components").getQuantity().unmodifyFlat("aotd_electronics");
            }
        }
    }
    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_gp_mod")){
            if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(marketAPI.getFaction())!=null){
                marketAPI.addCondition("aotd_gp_mod");
            }

        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}

