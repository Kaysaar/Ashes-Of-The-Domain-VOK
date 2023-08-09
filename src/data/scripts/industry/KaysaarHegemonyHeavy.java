package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;

import java.awt.*;

public class KaysaarHegemonyHeavy extends BaseIndustry {
    public static float WIDE_FACTION_PRODUCTION_BONUS = 0.25f;

    public void apply() {
        super.apply(true);
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
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
        demand(Commodities.METALS, size);
        demand(AodCommodities.REFINED_METAL, size - 3);
        demand(Commodities.RARE_METALS, size - 2);

        supply(Commodities.HEAVY_MACHINERY, size + 2);
        supply(Commodities.SUPPLIES, size + 2);
        supply(Commodities.HAND_WEAPONS, size + 2);
        supply(Commodities.SHIPS, size + 2);
        if(Global.getSettings().getModManager().isModEnabled("IndEvo")){
            supply("IndEvo_parts",market.getSize()-2);
            demand("IndEvo_parts",market.getSize()-2);
        }


        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.RARE_METALS,AodCommodities.REFINED_METAL);
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
                market.addCondition(Conditions.POLLUTION);
            }
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
                factionMarket.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(getModId(), (float) bonus/100, "Wide Bonus from Orbital Fleetwork Facility");
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
                    tooltip.addPara("Wide Faction Bonus Fleets : %s", opad, h, totalStr);
                    tooltip.addPara("*This bonus applies for every planet of " + market.getFaction().getDisplayName(),
                            Misc.getGrayColor(), opad);
                }
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
        if(market.getIndustry(Industries.ORBITALWORKS)==null){
            return false;
        }
        if(market.getIndustry(Industries.ORBITALWORKS).getSpecialItem()==null){
            return false;
        }
        return market.getIndustry(Industries.ORBITALWORKS).getSpecialItem().getId().equals(Items.PRISTINE_NANOFORGE);

    }

    @Override
    public String getUnavailableReason() {
        return "Pristine Nanoforge must be installed on Orbital Works to update";
    }

}
