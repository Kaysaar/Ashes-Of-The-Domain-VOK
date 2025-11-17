package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.FuelProduction;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;

public class FuelRefinery extends FuelProduction {
    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.VOLATILES, size-2);
        demand(Commodities.HEAVY_MACHINERY, size);
        supply(Commodities.FUEL, size +4);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.VOLATILES,Commodities.HEAVY_MACHINERY);
        if(Global.getSector().getMemory().is("$aotd_compound_unlocked",true)){
            supply("compound",Math.floorDiv(size,2));
        }
        applyDeficitToProduction(1, deficit, Commodities.FUEL,"compound");

        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    public String getCurrentImage() {
        PlanetAPI planet = market.getPlanetEntity();
        if (planet == null || planet.isGasGiant() || planet.hasCondition(Conditions.NO_ATMOSPHERE)) {
            return Global.getSettings().getSpriteName("industry", "aotd_fuel_refinery_no_atmo");
        }
            return getSpec().getImageName();
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ANTIMATTER_SYNTHESIS,market);
    }
    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ANTIMATTER_SYNTHESIS,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.ANTIMATTER_SYNTHESIS));

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
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ANTIMATTER_SYNTHESIS,market);
    }
}
