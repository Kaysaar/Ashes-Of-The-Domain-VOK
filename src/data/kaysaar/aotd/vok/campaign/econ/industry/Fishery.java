package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.Farming;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;
import java.util.Set;

public class Fishery extends BaseIndustry {
    public static Set<String> AQUA_PLANETS = Farming.AQUA_PLANETS;
    @Override
    public void apply() {
        super.apply(true);
        supply(Commodities.FOOD, market.getSize()+3);
        demand(Commodities.HEAVY_MACHINERY, 3);
        if(market.hasCondition(Conditions.VOLTURNIAN_LOBSTER_PENS)){
            supply(Commodities.LOBSTER,market.getSize()-1);
        }

    }
    @Override
    public void unapply() {
        super.unapply();
    }


    @Override
    public boolean isAvailableToBuild() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        return canAquaculture   &&super.isAvailableToBuild()&& AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST,market);
    }


    @Override
    public boolean showWhenUnavailable() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        return canAquaculture;
    }


    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST));

        }
        StringBuilder bd = new StringBuilder();
        boolean insert = false;
        for (String reason : reasons) {
            if(insert){
                bd.append("\n");
            }
            bd.append(reason);

            insert = true;
        }

        return bd.toString();
    }



    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }


}
