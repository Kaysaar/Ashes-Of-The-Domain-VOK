package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;

public class HegemonyHeavy extends HeavyIndustry {
    public static float WIDE_FACTION_PRODUCTION_BONUS = 0.25f;
    public int getAdvancedComponents(){
        return market.getSize()-7;
    }
    public void apply() {
        super.apply(true);
        int size = market.getSize();

        int shipBonus = 0;
        float qualityBonus = 0.75f;
        int bonus = 0;
        if(isFunctional()){
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
                if(factionMarket.hasIndustry("hegeheavy")){
                    bonus+=25;
                }
            }
        }
        if(market.getId().equals("chicomoztoc")){
            demand(AoTDCommodities.REFINED_METAL, size-1);
        }
        else{
            demand(AoTDCommodities.REFINED_METAL, size+2);
        }

        demand(Commodities.RARE_METALS, size - 2);

        supply(Commodities.HEAVY_MACHINERY, size + 4);
        supply(Commodities.SUPPLIES, size + 4);
        supply(Commodities.HAND_WEAPONS, size + 5);
        supply(Commodities.SHIPS, size + 4);
        if(Global.getSettings().getModManager().isModEnabled("IndEvo")){
            supply("IndEvo_parts",market.getSize());
            demand("IndEvo_parts",market.getSize()-2);
        }


        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.RARE_METALS, AoTDCommodities.REFINED_METAL);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), qualityBonus, "Orbital Fleetwork Facility");
        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);

        float stability = market.getPrevStability();
        if (stability < 5) {
            float stabilityMod = (stability - 5f) / 5f;
            bonus /= 5;
            bonus *= (int) stability;
            stabilityMod *= 0.5f;
            //market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), stabilityMod, "Low stability at production source");
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(1), stabilityMod, getNameForModifier() + " - low stability");
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
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
                factionMarket.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(getModId(), (float) bonus/100, "Faction Wide Bonus from Orbital Fleetwork Facility");
            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId(0));
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId(1));
        for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
            factionMarket.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(getModId());
        }
    }


    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.ADD_INDUSTRY || isFunctional()) {
                float total = 0;
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
                if(factionMarket.hasIndustry("hegeheavy")){
                    total+=WIDE_FACTION_PRODUCTION_BONUS;
                }
            }
                String totalStr = "+" + (int) Math.round(total * 100f) + "%";
                Color h = Misc.getHighlightColor();
                if (total < 0) {
                    h = Misc.getNegativeHighlightColor();
                    totalStr = "" + (int) Math.round(total * 100f) + "%";
                }
                float opad = 10f;
                if (total >= 0) {
                    tooltip.addPara("Faction Wide Bonus Fleets : %s", opad, h, totalStr);
                    tooltip.addPara("*This bonus applies for every planet of " + market.getFaction().getDisplayName(),
                            Misc.getGrayColor(), opad);
                }
            }
        if(market.getId().equals("chicomoztoc")){
            tooltip.addSectionHeading("Might of Chicomoztoc",market.getFaction().getBaseUIColor(),market.getFaction().getDarkUIColor(), Alignment.MID,10f);
            tooltip.addPara("Reduces demand for refined metal by 3 units!",Misc.getPositiveHighlightColor(),10f);
            tooltip.addPara("*For the High Hegemon does not ask. He commands.", Color.gray, 10f);
        }

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
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_FLEETWORK_FACILITIES,market)&&market.getSize()>=6;

    }
    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_FLEETWORK_FACILITIES,market);
    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(market.getSize()<6){
            reasons.add("Market must be size 6 or greater");
        }
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ORBITAL_FLEETWORK_FACILITIES,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.ORBITAL_FLEETWORK_FACILITIES));

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

}
