package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;
import java.util.ArrayList;

public class TriTachyonHeavy extends HeavyIndustry {
    public static float QUALITY_BONUS = 0.8f;

    public void apply() {
        super.apply(true);
        int size = market.getSize();

        float qualityBonus = QUALITY_BONUS;
        demand(Commodities.METALS, size+2);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS, size+1);

        supply(Commodities.HEAVY_MACHINERY, size+3);
        supply(Commodities.SUPPLIES, size+3);
        supply(Commodities.HAND_WEAPONS, size+5);
        supply(Commodities.SHIPS, size+5);
        supply(AoTDCommodities.DOMAIN_GRADE_MACHINERY, size-5);

        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(1), qualityBonus, "Orbital Skunkworks Facility");


        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.RARE_METALS, AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS,
                AoTDCommodities.DOMAIN_GRADE_MACHINERY);

        float stability = market.getPrevStability();
        if (stability < 5) {
            float stabilityMod = (stability - 5f) / 5f;
            stabilityMod *= 0.5f;
            //market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), stabilityMod, "Low stability at production source");
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), stabilityMod, getNameForModifier() + " - low stability");
        }


        if (!isFunctional()) {
            supply.clear();
            unapply();
        } else {
            if (!market.hasCondition(Conditions.POLLUTION)&&market.hasCondition(Conditions.HABITABLE)) {
                if(market.hasIndustry("BOGGLED_GENELAB")){
                    market.removeCondition(Conditions.POLLUTION);
                }

            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId(0));
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId(1));

    }


    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES,market)&&market.getSize()>=6;

    }
    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES,market);
    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(market.getSize()<6){
            reasons.add("Market must be size 6 or greater");
        }
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES));

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
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.ADD_INDUSTRY || isFunctional()) {
            float total = 0;
            String totalStr;
            Color h = Misc.getHighlightColor();
            h = Misc.getPositiveHighlightColor();
            totalStr = "From 1 to 3 ";

            float opad = 10f;

            tooltip.addPara("Allowing Patrol fleets to have %s S-mods", opad, h, totalStr);
            tooltip.addPara("*This bonus applies for every planet of " + market.getFaction().getDisplayName(),
                    Misc.getGrayColor(), opad);

        }


    }

}
