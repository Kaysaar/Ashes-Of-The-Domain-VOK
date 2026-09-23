package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialogConsumed;

public class BoreCity extends BaseColonyDevelopment {
    public static LinkedHashMap<String,Float>halfCondEffects = new LinkedHashMap<>();
    public static LinkedHashSet<String>fullCondEffects = new LinkedHashSet<>();
    public static LinkedHashSet<String>industriesToAFfect =new LinkedHashSet<>();
    public static float COST_MULT = 1.3f;
    static {
        fullCondEffects.add(Conditions.HOT);
        fullCondEffects.add(Conditions.COLD);
        fullCondEffects.add(Conditions.EXTREME_WEATHER);
        fullCondEffects.add(Conditions.METEOR_IMPACTS);

        halfCondEffects.put(Conditions.VERY_HOT,0.25f);
        halfCondEffects.put(Conditions.VERY_COLD,0.25f);
        halfCondEffects.put(Conditions.IRRADIATED,0.25f);
        industriesToAFfect.add(AoTDIndustries.EXTRACTIVE_OPERATION);
        industriesToAFfect.add(Industries.MINING);
    }
    @Override
    public String getName() {
        return "Bore City";
    }
    @Override
    public float getOrder() {
        return 120f;
    }
    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return canShowOnMarket(market);
    }
    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "By utilizing the boreholes left behind by an Autonomous Mantle Bore, entire subterranean cities can be constructed at reasonable cost. The boreholes serve as the central shafts of these settlements, connecting their countless levels with the surface.",
                5f
        );

        tooltip.addPara(
                "With most infrastructure buried deep underground, Bore Cities are exceptionally well protected from hostile planetary conditions and invasion. Their proximity to the planetary crust also greatly improves resource extraction, though at the cost of surface accessibility and population growth.",
                3f
        );
    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        if(incoming.getWeight().getModifiedValue()>=0){
            incoming.getWeight().modifyMult(getName(),0.75f,"Bore City Development Plan");

        }
        else{
            incoming.getWeight().unmodifyMult(getName());
        }
    }

    public void generateEffectsForMarketCondition(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections, boolean forMarketCondition) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Subterranean Environment", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addPara(
                "Neutralizes the hazard impact of %s, %s, %s and %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Hot", "Cold", "Extreme Weather", "Meteor Impacts"
        );

        tooltip.addPara(
                "Reduces the hazard impact of %s, %s and %s by %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Extreme Heat", "Extreme Cold", "Irradiated", "50%"
        );

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Deep-Shaft Extraction", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addPara(
                "Mining gains an additional %s production scaling from colony size.",
                3f,
                Misc.getPositiveHighlightColor(),
                "70%"
        );
        tooltip.addPara(
                "Can't upgrade mining industry further!",
                Misc.getNegativeHighlightColor(),
                3f
        );
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Buried Fortress", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addPara(
                "Ground defense strength is increased by %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "200%"
        );

        tooltip.addPara(
                "Fleet size contribution from this market is reduced by %s.",
                3f,
                Misc.getNegativeHighlightColor(),
                "50%"
        );

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Surface Isolation", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addPara(
                "Accessibility is reduced by %s.",
                3f,
                Misc.getNegativeHighlightColor(),
                "50%"
        );

        tooltip.addPara(
                "Population growth is reduced by %s.",
                3f,
                Misc.getNegativeHighlightColor(),
                "25%"
        );

        tooltip.addPara(
                "Construction costs is increased by %s.",
                3f,
                Misc.getNegativeHighlightColor(),
                "30%"
        );

        if (!forMarketCondition) {
            tooltip.addSectionHeading("Additional Cost For Colonization (Available)",Alignment.MID,5f);
            LinkedHashMap<String,Integer> costs = new LinkedHashMap<>();
            costs.put(Items.MANTLE_BORE, 1);
            tooltip.addCustom(createTooltipOfResourcesForDialogConsumed(tooltip.getWidthSoFar()-10,45,45,costs,false),5f);
        }
    }


    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        generateEffectsForMarketCondition(
                market,
                tooltip,
                fontForSections,
                !fontForSections.equals(Fonts.ORBITRON_20AABOLD)
        );
    }


    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        SectorEntityToken token = market.getPrimaryEntity();
        if(token instanceof PlanetAPI planet){
            return !planet.isGasGiant()&&!planet.hasCondition(Conditions.HABITABLE)&&super.canShowOnMarket(market);
        }
        return false;
    }
    @Override
    public boolean doesMeetAdditionalCriteriaForDevelopment(MarketAPI market) {
        return Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.MANTLE_BORE,null))!=0;
    }

    @Override
    public void executePlanBeforeColonization(MarketAPI market) {
        Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.MANTLE_BORE,null),1);
    }

    @Override
    public void apply(MarketAPI market) {
        fullCondEffects.forEach(market::suppressCondition);
        industriesToAFfect.forEach(x->{
            if(market.hasIndustry(x)){
                market.getIndustry(x).getAllSupply().forEach(y->y.getQuantity().modifyMult("bore_city",1.7f));
            }
        });
        halfCondEffects.forEach((x,y)->{
            if(market.hasCondition(x)){
                market.getHazard().modifyFlat(getName()+x,-y,"Bore City Countering - "+market.getCondition(x).getName());
            }
        });
        market.getAccessibilityMod().modifyFlat(getName(),-0.5f,"Bore City Development Plan");
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(getName(),-0.5f,"Bore City Development Plan");
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getName(),2f,"Bore City Development Plan");

    }

    @Override
    public void unapply(MarketAPI market) {
        fullCondEffects.forEach(market::unsuppressCondition);
        halfCondEffects.forEach((x,y)->{
            if(market.hasCondition(x)){
                market.getHazard().unmodifyFlat(getName()+x);
            }
        });
        industriesToAFfect.forEach(x->{
            if(market.hasIndustry(x)){
                market.getIndustry(x).getAllSupply().forEach(y->y.getQuantity().unmodifyMult("bore_city"));
            }
        });
        market.getAccessibilityMod().unmodifyFlat(getName());
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(getName());
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyFlat(getName());
    }
}
