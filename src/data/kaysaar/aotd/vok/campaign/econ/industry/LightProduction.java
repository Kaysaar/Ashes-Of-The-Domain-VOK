package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;


public class LightProduction extends BaseIndustry {

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
      return !AoDUtilis.checkForFamilyIndustryInstance(market,Industries.LIGHTINDUSTRY,Industries.LIGHTINDUSTRY,this.id,this.currTooltipMode) &&super.isAvailableToBuild();
    }

    @Override
    public boolean isAvailableToBuild() {

return true;
    }

    @Override
    public String getUnavailableReason() {
        if(AoDUtilis.checkForFamilyIndustryInstance(market,Industries.LIGHTINDUSTRY,Industries.LIGHTINDUSTRY,this.id,this.currTooltipMode)){
            return AoDUtilis.reason;
        }
        return super.getUnavailableReason();
    }


    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

}
