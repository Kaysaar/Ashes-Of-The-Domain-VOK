package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.LinkedHashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialog;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialogConsumed;

public class FloatingCity extends BaseColonyDevelopment {
    private String   sourceIdString = "aotd_floating_city";
    @Override
    public String getName() {
        return "Floating City";
    }

    @Override
    public float getOrder() {
        return 10f;
    }

    public int amFuel = 6;
    @Override
    public Color getBrightButtonColour(MarketAPI market) {
        return Misc.getStoryBrightColor();
    }

    @Override
    public Color getDarkButtonColour(MarketAPI market) {
        return Misc.getStoryDarkColor();
    }

    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara("Using a set of heavily modified colossal fuel-hungry thrusters, one could lift along an entire city in the atmosphere of a planet, allowing for high-altitude, efficient volatile extraction operations.", 5f);
        tooltip.addPara("As city expands, so does the burden on said thrusters, so it is essential to keep local fuel stockpiles as full as possible.", 5f);
    }

    @Override
    public boolean doesMeetAdditionalCriteriaForDevelopment(MarketAPI market) {
        return Global.getSector().getPlayerFleet().getCargo().getFuel()>=AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(amFuel,true,Commodities.FUEL);
    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        if (market.getPrimaryEntity() instanceof PlanetAPI planetAPI) {
            return planetAPI.isGasGiant();
        }
        return false;
    }


    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return canShowOnMarket(market);
    }

    public void generateEffectsForMarketCondition(MarketAPI marketAPI,TooltipMakerAPI tooltip, String fontForSections,boolean forMarketCondition){
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Massive Anti-Matter Thrusters", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("%s now has base fuel demand that scales with amount of structures",3f,Color.ORANGE,"Population and Infrastructure");
        tooltip.addSectionHeading("Fuel Consumption Information", Alignment.MID,5f);
        tooltip.addPara("For each structure / industry ",5f);
        tooltip.setBulletedListMode(BaseIntelPlugin.BULLET);
        tooltip.addPara("If structure is not industry: %s demand units of fuel",3f,Color.ORANGE,"3");
        tooltip.addPara("If structure is industry: %s demand units of fuel ",3f,Color.ORANGE,"4");
        tooltip.addPara("If structure is heavy industry type: %s demand units of fuel",3f,Color.ORANGE,"5");
        tooltip.setBulletedListMode(null);
        tooltip.addPara("Warning! Not meeting fuel demand will cause platform to lower more and more each month, if demand of fuel will not be met for 3 months entire colony will decivilize! ",Misc.getNegativeHighlightColor(),5f);

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Limited Expansion Space", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Colony size is capped at %s due to limited capacity for expansion.",
                3f,
                Misc.getNegativeHighlightColor(),
                "5"
        );

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("High-Attitude Positioning", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Mining and it's upgrades receives %s multiplier bonus to production",
                3f,
                Misc.getPositiveHighlightColor(),
                "x2"
        );
        tooltip.addPara(
                "Reduces cover-up costs of Black Site by %s",
                3f,
                Misc.getPositiveHighlightColor(),
                "60%"
        );
        tooltip.addPara(
                "Negates effects of \"Toxic Atmosphere\" and \"High Gravity\" condition.",
                Misc.getPositiveHighlightColor(),3f
        );
        tooltip.addPara(
                "Colony has \"No Atmosphere\" condition, without hazard rating penalty.",
                Misc.getPositiveHighlightColor(),3f
        );

        if(!forMarketCondition){
            tooltip.addSectionHeading("Additional Cost For Colonization (Available)",Alignment.MID,5f);
            LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
            costs.put(Commodities.FUEL, AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(amFuel,true,Commodities.FUEL));
            tooltip.addCustom(createTooltipOfResourcesForDialogConsumed(tooltip.getWidthSoFar()-10,45,45,costs,false),5f);
        }

    }
    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
       generateEffectsForMarketCondition(market, tooltip, fontForSections, !fontForSections.equals(Fonts.ORBITRON_20AABOLD));
    }

    @Override
    public void apply(MarketAPI market) {
        market.getStats().getDynamic().getMod(
                Stats.MAX_MARKET_SIZE).modifyFlat(sourceIdString,-Misc.getMaxMarketSize(market)+5,"Archeo-Site");
        if(market.getSize()>5){
            market.setSize(5);
        }
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getStats().getDynamic().getMod(
                Stats.MAX_MARKET_SIZE).unmodifyFlat(sourceIdString);
    }
}
