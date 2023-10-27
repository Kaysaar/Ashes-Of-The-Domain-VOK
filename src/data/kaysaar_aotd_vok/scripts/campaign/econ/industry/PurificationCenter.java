package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashSet;
import java.util.Set;

public class PurificationCenter extends BaseIndustry {
    public static Set<String> AQUA_PLANETS = new HashSet<String>();

    static {
        AQUA_PLANETS.add(Planets.PLANET_WATER);
    }
        @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        supply(AodCommodities.WATER, size-2);
        supply(AodCommodities.BIOTICS,  size - 2);
        demand(Commodities.HEAVY_MACHINERY,  size - 3);
        demand(Commodities.ORGANICS,  size - 3);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AodCommodities.WATER,AodCommodities.BIOTICS);
        if (!isFunctional()) {
            supply.clear();
            unapply();
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

        return canAquaculture && AoDUtilis.checkIfResearched(this.id);
    }


    @Override
    public String getUnavailableReason() {


        return"There is bug, please report it to mod author";

    }

    @Override
    public boolean showWhenUnavailable() {

        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());

        return canAquaculture && AoDUtilis.checkIfResearched(this.id);

    }
    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
