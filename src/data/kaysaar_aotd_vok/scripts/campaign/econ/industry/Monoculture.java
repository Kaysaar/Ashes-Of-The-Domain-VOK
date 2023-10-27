package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashSet;
import java.util.Set;

public class Monoculture extends BaseIndustry {
    public static Set<String> AQUA_PLANETS = new HashSet<String>();

    static {
        AQUA_PLANETS.add(Planets.PLANET_WATER);
    }
    public void apply() {
        super.apply(true);
        supply(Commodities.FOOD, 2);
        demand(Commodities.HEAVY_MACHINERY, 2);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY);
        //applyDeficitToProduction(0, deficit, Commodities.FOOD, Commodities.ORGANICS);
        if (!isFunctional()) {
            supply.clear();
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
        if(canAquaculture){
            return false;
        }
        if (AoDUtilis.getFoodQuantityBonus(market)<=-2) {
            return false;
        }

        return !AoDUtilis.checkForFamilyIndustryInstance(market, Industries.FARMING, Industries.FARMING,this.id);
    }


    @Override
    public boolean showWhenUnavailable() {
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());
        if(canAquaculture) {
            return false;
        }
            if (AoDUtilis.getFoodQuantityBonus(market)<=-5) {
            return false;
        }

        return !AoDUtilis.checkForFamilyIndustryInstance(market, Industries.FARMING, Industries.FARMING,this.id);

    }


    @Override
    public String getUnavailableReason() {
        if  (AoDUtilis.getFoodQuantityBonus(market)<=-5) {
            return "Requires farmland";
        }
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.FARMING, Industries.FARMING,this.id)){
            return AoDUtilis.reason;
        }
        return null;

    }


    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }


    @Override
    public MarketCMD.RaidDangerLevel adjustCommodityDangerLevel(String commodityId, MarketCMD.RaidDangerLevel level) {
        boolean aquaculture = Industries.AQUACULTURE.equals(getId());
        if (aquaculture) return level;
        return level.prev();
    }

    @Override
    public MarketCMD.RaidDangerLevel adjustItemDangerLevel(String itemId, String data, MarketCMD.RaidDangerLevel level) {
        boolean aquaculture = Industries.AQUACULTURE.equals(getId());
        if (aquaculture) return level;
        return level.prev();
    }
}
