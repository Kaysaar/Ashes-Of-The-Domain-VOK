package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;

import java.awt.*;

public class TriTachyonHeavy extends HeavyIndustry {
    public static float QUALITY_BONUS = 0.8f;

    public void apply() {
        super.apply(true);
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        int size = market.getSize();
        float qualityBonus = QUALITY_BONUS;
        demand(Commodities.METALS, size);
        demand(AodCommodities.PURIFIED_TRANSPLUTONICS, size - 2);
        demand(Commodities.RARE_METALS, size - 2);

        supply(Commodities.HEAVY_MACHINERY, size+1);
        supply(Commodities.SUPPLIES, size+1);
        supply(Commodities.HAND_WEAPONS, size+1);
        supply(Commodities.SHIPS, size+1);
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(1), qualityBonus, "Orbital Skunkworks Facility");


        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.RARE_METALS, AodCommodities.PURIFIED_TRANSPLUTONICS);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);

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
                market.addCondition(Conditions.POLLUTION);
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
        if (market.getIndustry(Industries.ORBITALWORKS) == null) {
            return false;
        }
        if (market.getIndustry(Industries.ORBITALWORKS).getSpecialItem() == null) {
            return false;
        }
        return market.getIndustry(Industries.ORBITALWORKS).getSpecialItem().getId().equals(Items.PRISTINE_NANOFORGE);

    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.ADD_INDUSTRY || isFunctional()) {
            float total = 0;
            String totalStr;
            Color h = Misc.getHighlightColor();
            h = Misc.getNegativeHighlightColor();
            totalStr = "From 1 to 3 ";

            float opad = 10f;

            tooltip.addPara("Allowing Patrol fleets to have %s S-mods", opad, h, totalStr);
            tooltip.addPara("*This bonus applies for every planet of " + market.getFaction().getDisplayName(),
                    Misc.getGrayColor(), opad);

        }


    }
        @Override
    public String getUnavailableReason() {
        return "Pristine Nanoforge must be installed on Orbital Works to update";
    }
}
