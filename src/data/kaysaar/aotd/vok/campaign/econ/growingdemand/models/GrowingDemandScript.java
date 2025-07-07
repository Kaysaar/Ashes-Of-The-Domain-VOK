package data.kaysaar.aotd.vok.campaign.econ.growingdemand.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry.getDeficitText;

public class GrowingDemandScript {
    public String idOfCommodity;
    public LinkedHashMap<String, Integer> commoditiesReplacementRatio = new LinkedHashMap<>();
    public float days;
    public float magnitiude = 0f;
    public boolean shouldReplaceCommodity = true;
    public boolean doesHaveMaxDemandReplace = false;
    public int maxPeakOfDemandReplace;
    public void setPeakOfMaxRemandReplace(int peak) {
        doesHaveMaxDemandReplace = true;
        maxPeakOfDemandReplace = peak;
    }
    public float getPercentageOfDemandGrowth() {
        return Math.min(magnitiude / maxMagnitude, 1);
    }

    public void setMaxMagnitude(float maxMagnitude) {
        this.maxMagnitude = maxMagnitude;
    }

    public boolean canStartToApply() {
        MarketAPI market = Global.getSector().getEconomy().getMarketsCopy().get(0);
        return market.getCommodityData(idOfCommodity).getCommodityMarketData().getMaxExportGlobal() > 0;
    }

    public int dayInterval;
    public boolean canDecrease = true;

    public void applyDemandOnMarket(MarketAPI market) {
        final String memForDemand = idOfCommodity + "_growing_demand";
        for (Industry industry : market.getIndustries()) {
            int max = 0;
            for (Map.Entry<String, Integer> entry : commoditiesReplacementRatio.entrySet()) {
                MutableStat quantityTotal = industry.getDemand(entry.getKey()).getQuantity();
                quantityTotal.unmodify(memForDemand);
                int total = quantityTotal.getModifiedInt();
                if (total == 0) continue;
                int shouldDemand = (int) (Math.floor(((double) total / entry.getValue()) * magnitiude));
                max = Math.max(max, shouldDemand);

            }
            if (market.getCommodityData(idOfCommodity).getCommodityMarketData().getMaxExportGlobal() <= max) {
                max = market.getCommodityData(idOfCommodity).getCommodityMarketData().getMaxExportGlobal();
            }
            if(doesHaveMaxDemandReplace &&max>= maxPeakOfDemandReplace){
                max = maxPeakOfDemandReplace;
            }
            final int maximum = max;
            if(shouldReplaceCommodity){
                commoditiesReplacementRatio.forEach((x, y) -> {
                    industry.getDemand(x).getQuantity().modifyFlat(memForDemand, -maximum * y);
                });
            }

            BaseIndustry ind = (BaseIndustry) industry;
            industry.getDemand(idOfCommodity).getQuantity().modifyFlat(memForDemand, maximum, "Growing Demand for " + Global.getSettings().getCommoditySpec(idOfCommodity).getName());

            applyDeficitToProduction(ind, 5, ind.getMaxDeficit(idOfCommodity));
            applyEffectsOnIndustry(industry, maximum, ind.getMaxDeficit(idOfCommodity).two);
        }
        applyEffectToEntireMarket(market);
    }

    protected void applyDeficitToProduction(BaseIndustry ind, int index, Pair<String, Integer> deficit) {

        for (MutableCommodityQuantity commodity : ind.getAllSupply()) {
//			if (this instanceof Mining && market.getName().equals("Louise")) {
//				System.out.println("efwefwe");
//			}
            if (commodity.getQuantity().isUnmodified()) continue;
            ind.supply(index, commodity.getCommodityId(), -deficit.two, getDeficitText(deficit.one))
            ;
        }
    }

    public void applyEffectToEntireMarket(MarketAPI market) {

    }

    public void unapplyEffectToEntireMarket(MarketAPI market) {

    }

    public void applyEffectsOnIndustry(Industry industry, int demanded, int deficit) {


    }

    public void unapplyEffectsOnIndustry(Industry industry) {

    }

    public void unapplyDemandOnMarket(MarketAPI market) {
        final String memForDemand = idOfCommodity + "_growing_demand";
        for (Industry industry : market.getIndustries()) {
            commoditiesReplacementRatio.forEach((x, y) -> {
                industry.getDemand(x).getQuantity().unmodify(memForDemand);
            });
            unapplyEffectsOnIndustry(industry);
        }
        unapplyEffectToEntireMarket(market);
    }

    public float decreaseRate = 0.08f;
    public float growthAdditionalRate = 0.2f;

    public void grow() {
        float magnitudeGrowth = 0f;
        int countOfProducers = Global.getSector().getEconomy().getMarketsCopy().stream().
                filter(MarketAPI::isInEconomy).filter(x -> x.getCommodityData(idOfCommodity).getMaxSupply() > 0).toList().size();
        magnitudeGrowth = growthAdditionalRate * countOfProducers;

        magnitiude += magnitudeGrowth;
        if (magnitiude >= maxMagnitude) {
            magnitiude = maxMagnitude;
        }


    }

    public void decrease() {
        magnitiude -= decreaseRate;
        if (magnitiude <= 0) {
            magnitiude = 0;
        }
    }

    public float maxMagnitude = 0.4f;

    public void setGrowthAdditionalRate(float growthAdditionalRate) {
        this.growthAdditionalRate = growthAdditionalRate;
    }

    public void setCanDecrease(boolean canDecrease) {
        this.canDecrease = canDecrease;
    }

    public void setDecreaseRate(float decreaseRate) {
        this.decreaseRate = decreaseRate;
    }

    public GrowingDemandScript(String idOfCommodity, LinkedHashMap<String, Integer> commoditiesReplacementRatio) {
        this.idOfCommodity = idOfCommodity;
        this.commoditiesReplacementRatio = commoditiesReplacementRatio;
    }

    public void advance(float amount) {
        if (canStartToApply()) {
            grow();
        } else if (canDecrease) {
            decrease();
        }
    }


}
