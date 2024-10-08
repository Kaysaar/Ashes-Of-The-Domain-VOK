package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.Farming;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;


import java.util.HashSet;
import java.util.Set;

public class Fishery extends BaseIndustry {
    public static Set<String> AQUA_PLANETS = Farming.AQUA_PLANETS;
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
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id,this.currTooltipMode)){
            return false;
        }
        return canAquaculture   &&super.isAvailableToBuild();
    }


    @Override
    public boolean showWhenUnavailable() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id,this.currTooltipMode)){
            return  false;
        }
        return canAquaculture;
    }


    @Override
    public String getUnavailableReason() {
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.AQUACULTURE,Industries.AQUACULTURE,this.id,this.currTooltipMode)){
            return AoDUtilis.reason;
        }
        return "Requires a world with water-covered surface.";
    }



    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }


}
