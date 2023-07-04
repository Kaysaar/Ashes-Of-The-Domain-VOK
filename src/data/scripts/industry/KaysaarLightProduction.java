package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.LightIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Pair;
import data.plugins.AoDUtilis;

public class KaysaarLightProduction extends BaseIndustry {

    public void apply() {
        super.apply(true);

        int size = market.getSize();

        demand(Commodities.ORGANICS, size);

        supply(Commodities.DOMESTIC_GOODS, size-1);
        //supply(Commodities.SUPPLIES, size - 3);


        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS);

        applyDeficitToProduction(2, deficit,
                Commodities.DOMESTIC_GOODS );

        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean showWhenUnavailable() {
      return !AoDUtilis.checkForFamilyIndustryInstance(market,Industries.LIGHTINDUSTRY,Industries.LIGHTINDUSTRY,this.id);
    }

    @Override
    public boolean isAvailableToBuild() {

        return !AoDUtilis.checkForFamilyIndustryInstance(market,Industries.LIGHTINDUSTRY,Industries.LIGHTINDUSTRY,this.id);

    }

    @Override
    public String getUnavailableReason() {
        if(AoDUtilis.checkForFamilyIndustryInstance(market,Industries.LIGHTINDUSTRY,Industries.LIGHTINDUSTRY,this.id)){
            return AoDUtilis.reason;
        }
        return null;
    }


    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

}
