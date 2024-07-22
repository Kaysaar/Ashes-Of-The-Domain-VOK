package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.Farming;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Aquaculture extends Farming {

    @Override
    public boolean isAvailableToBuild() {

        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        return canAquaculture&&AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST,market);

    }



    @Override
    public boolean showWhenUnavailable() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());

        return canAquaculture&&AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST,market)&&super.isAvailableToBuild();


    }


    @Override
    public String getUnavailableReason() {
        return "Requires a world with water-covered surface.";
    }






    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
