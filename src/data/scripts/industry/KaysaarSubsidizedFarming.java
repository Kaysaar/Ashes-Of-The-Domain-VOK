package data.scripts.industry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;
import data.plugins.AoDUtilis;


public class KaysaarSubsidizedFarming extends BaseIndustry {
    public void apply() {
        super.apply(true);
        int quantity = market.getSize()+2;
        quantity += AoDUtilis.getFoodQuantityBonus(market);
        supply(Commodities.FOOD,quantity);
        demand(Commodities.HEAVY_MACHINERY, market.getSize()-3);

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY);
        //applyDeficitToProduction(0, deficit, Commodities.FOOD, Commodities.ORGANICS);

        applyDeficitToProduction(2, deficit, Commodities.FOOD);


        if (!isFunctional()) {
            supply.clear();
        }
    }



    @Override
    public void unapply() {
        super.unapply();
    }
    @Override
    protected void buildingFinished() {
        super.buildingFinished();
        this.spec.setUpgrade(null);
    }

    @Override
    public boolean isAvailableToBuild() {
        if(!market.hasCondition(Conditions.FARMLAND_POOR)&&!market.hasCondition(Conditions.FARMLAND_RICH)
                &&!market.hasCondition(Conditions.FARMLAND_ADEQUATE)&&!market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)){
            return false;
        }
        return true;
    }


    @Override
    public boolean showWhenUnavailable() {
        if(!market.hasCondition(Conditions.FARMLAND_POOR)&&!market.hasCondition(Conditions.FARMLAND_RICH)
                &&!market.hasCondition(Conditions.FARMLAND_ADEQUATE)&&!market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)){
            return false;
        }
        return true;
    }


    @Override
    public String getUnavailableReason() {
        if (!super.isAvailableToBuild()) return super.getUnavailableReason();
        return "Requires farmland";
    }


    @Override
    public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltip(mode, tooltip, expanded);

    }


    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.add(Factions.LUDDIC_CHURCH, 10f);
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
