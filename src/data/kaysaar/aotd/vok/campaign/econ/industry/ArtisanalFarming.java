package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.HashSet;
import java.util.Set;

public class ArtisanalFarming extends BaseIndustry {

    public static Set<Pair<String, Integer>> FARMING_CONDITIONS = new HashSet<Pair<String, Integer>>();

    static {
        FARMING_CONDITIONS.add(new Pair<>(Conditions.FARMLAND_POOR, -1));
        FARMING_CONDITIONS.add(new Pair<>(Conditions.FARMLAND_ADEQUATE, 0));
        FARMING_CONDITIONS.add(new Pair<>(Conditions.FARMLAND_RICH, 1));
        FARMING_CONDITIONS.add(new Pair<>(Conditions.FARMLAND_BOUNTIFUL, 2));
        FARMING_CONDITIONS.add(new Pair<>(Conditions.SOLAR_ARRAY, 2));
    }

    public void apply() {
        super.apply(true);
        if (this.special != null) {
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special = null;
        }
        int quantity = market.getSize() - 2;
        supply(Commodities.LUXURY_GOODS, quantity);
        supply(Commodities.FOOD, quantity);
        for (Pair<String, Integer> farmingCondition : FARMING_CONDITIONS) {
            Pair<String, Integer> prodBonus = AoDUtilis.getProductionBonusFromCondition(market, farmingCondition.one, farmingCondition.two);
            if (prodBonus != null) {
                supply(prodBonus.one,Commodities.LUXURY_GOODS, prodBonus.two, prodBonus.one);
                supply(prodBonus.one,Commodities.FOOD, prodBonus.two, prodBonus.one);
            }
        }


        demand(Commodities.HEAVY_MACHINERY, market.getSize() - 3);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY);
        //applyDeficitToProduction(0, deficit, Commodities.FOOD, Commodities.ORGANICS);
        applyDeficitToProduction(2, deficit, Commodities.FOOD, Commodities.LUXURY_GOODS);


        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    protected void buildingFinished() {
        super.buildingFinished();
        this.spec.setUpgrade(null);
    }

    @Override
    public void unapply() {
        super.unapply();
    }


    @Override
    public boolean isAvailableToBuild() {
        if (!market.hasCondition(Conditions.FARMLAND_POOR) && !market.hasCondition(Conditions.FARMLAND_RICH)
                && !market.hasCondition(Conditions.FARMLAND_ADEQUATE) && !market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            return false;
        }
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.IMPROVED_FERTILIZERS,market)&&super.isAvailableToBuild();
    }


    @Override
    public boolean showWhenUnavailable() {
        if (!market.hasCondition(Conditions.FARMLAND_POOR) && !market.hasCondition(Conditions.FARMLAND_RICH)
                && !market.hasCondition(Conditions.FARMLAND_ADEQUATE) && !market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            return false;
        }
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.IMPROVED_FERTILIZERS,market);
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
