package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.industry.Aquaculture;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class TechnologyBonusesApplier extends BaseHazardCondition {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction())==null)return;
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.HAZMAT_WORKING_EQUIPMENT)){
            market.getHazard().modifyFlat("tech_hazmmat",-0.1f,"Hazmat Working Equipment");
        }
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST)){
            if(market.getPlanetEntity() != null&&Aquaculture.AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId())){
                market.getHazard().modifyFlat("tech_aquatic",-0.1f,"Aquatic Biosphere");

            }
        }
        if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STREAMLINED_PRODUCTION,market)){
            market.getIndustries().forEach(x->x.getSupplyBonusFromOther().modifyFlat(AoTDTechIds.STREAMLINED_PRODUCTION,1,"Streamlined Production"));
        }
        if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ADVANCED_MINING_EQUIPMENT,market)){
            market.getIndustries().stream().filter(x->x.getSpec().hasTag(Industries.MINING)).forEach(x->x.getSupplyBonusFromOther().modifyFlat(AoTDTechIds.ADVANCED_MINING_EQUIPMENT,1,"Advanced Mining Equipment"));
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getHazard().unmodifyFlat("tech_hazmmat");
        market.getHazard().unmodifyFlat("tech_aquatic");
        market.getIndustries().forEach(x->x.getSupplyBonusFromOther().unmodifyFlat(AoTDTechIds.STREAMLINED_PRODUCTION));
        market.getIndustries().forEach(x->x.getSupplyBonusFromOther().unmodifyFlat(AoTDTechIds.ADVANCED_MINING_EQUIPMENT));
    }
    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("TechnologyBonusesApplier")){
            if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(marketAPI.getFaction())!=null){
                marketAPI.addCondition("TechnologyBonusesApplier");
            }

        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}
