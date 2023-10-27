package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashSet;
import java.util.Set;

public class Fishery extends BaseIndustry {
    public static Set<String> AQUA_PLANETS = new HashSet<String>();

    static {
        AQUA_PLANETS.add(Planets.PLANET_WATER);
    }
    @Override
    public void apply() {
        super.apply(true);
        supply(Commodities.FOOD, 2);
        demand(Commodities.HEAVY_MACHINERY, 3);

    }
    @Override
    public void unapply() {
        super.unapply();
    }


    @Override
    public boolean isAvailableToBuild() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id)){
            return false;
        }
        return canAquaculture;
    }



    @Override
    public boolean showWhenUnavailable() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id)){
            return  false;
        }
        return canAquaculture;
    }


    @Override
    public String getUnavailableReason() {
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id)){
            return AoDUtilis.reason;
        }
        return "Requires water covered surface world";
    }



    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }


}
