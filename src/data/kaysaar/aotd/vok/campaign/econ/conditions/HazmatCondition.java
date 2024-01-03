package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class HazmatCondition extends BaseHazardCondition {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction())==null)return;
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.HAZMAT_WORKING_EQUIPMENT)){
            market.getHazard().modifyFlat("tech_hazmmat",-0.1f,"Hazmat Working Equipment");
        }

    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getHazard().unmodifyFlat("tech_hazmmat");
    }
    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_tech_hazard")){
            if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(marketAPI.getFaction())!=null){
                marketAPI.addCondition("aotd_tech_hazard");
            }

        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}
