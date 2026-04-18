package data.kaysaar.aotd.vok.campaign.econ.industry.grandwonders;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.impl.campaign.econ.impl.Spaceport;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderManager;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeManager;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel.*;

public class DaedalusArray extends Spaceport implements GrandWonderAPI {
    @Override
    public LinkedHashMap<String, Integer> getDemandCostForRestoration() {
        LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
        demand.put(Commodities.METALS, 1);
        return demand;
    }
    public static float ACC_BONUS = 2f;

    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();

        boolean megaport = true;
        int extraSize = 0;
        if (megaport) extraSize = 5;

        demand(Commodities.FUEL, size - 2 + extraSize);
        demand(Commodities.SUPPLIES, size - 2 + extraSize);
        demand(Commodities.SHIPS, size - 2 + extraSize);

        supply(Commodities.CREW, size +2 + extraSize);


        String desc = getNameForModifier();

        Pair<String, Integer> deficit = getUpkeepAffectingDeficit();

        if (deficit.two > 0) {
            float loss = getUpkeepPenalty(deficit);
            getUpkeep().modifyMult("deficit", 1f + loss, getDeficitText(deficit.one));
        } else {
            getUpkeep().unmodifyMult("deficit");
        }

        market.setHasSpaceport(true);

        float a = ACC_BONUS;

        if (a > 0) {
            market.getAccessibilityMod().modifyFlat(getModId(0), a, desc);
        }

        float officerProb = OFFICER_PROB_MOD;
        officerProb = OFFICER_PROB_MOD_MEGA;
        market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyFlat(getModId(0), officerProb);
        //market.getStats().getDynamic().getMod(Stats.OFFICER_IS_MERC_PROB_MOD).modifyFlat(getModId(0), officerProb);

        if (!isFunctional()) {
//			if (isDisrupted() && !isBuilding()) {
//				market.getAccessibilityMod().modifyFlat(getModId(2), -1f, "Spaceport operations disrupted");
//				supply(Commodities.CREW, size - 1 + extraSize);
//			} else {
            supply.clear();
            unapply();
            market.setHasSpaceport(true);
//			}
        }

            if(HyperspaceTopographyEventIntel.get()!=null&&HyperspaceTopographyEventIntel.get().isStageActive(Stage.SLIPSTREAM_DETECTION)){
                String id1 = "hypertopology1_aotd";
                String id2 = "hypertopology2_aotd";
                String id3 = "hypertopology3_aotd";
                String id4 = "hypertopology4_aotd";
                StatBonus mod = market.getStats().getDynamic().getMod(Stats.SLIPSTREAM_REVEAL_RANGE_LY_MOD);
                if(!market.hasIndustry(Industries.SPACEPORT)&&!market.hasIndustry(Industries.MEGAPORT)){
                    mod.modifyFlat(id1, BASE_DETECTION_RANGE_LY, "Base detection range");
                    mod.modifyFlat(id2, market.getSize(), "Colony size");

                    float arraysBonus = gerSensorArrayBonusFor(market, RANGE_WITHIN_WHICH_SENSOR_ARRAYS_HELP_LY);

                    mod.modifyFlatAlways(id3, arraysBonus,
                            "Claimed sensor arrays within " + (int) RANGE_WITHIN_WHICH_SENSOR_ARRAYS_HELP_LY +
                                    " ly (max: " + (int) MAX_SENSOR_ARRAYS + " arrays)");
                }

                mod.modifyFlat(id4, 10, "Daedalus Array Antenna");
            }


    }

    @Override
    public void unapply() {
        super.unapply();
        String id1 = "hypertopology1_aotd";
        String id2 = "hypertopology2_aotd";
        String id3 = "hypertopology3_aotd";
        String id4 = "hypertopology4_aotd";
        StatBonus mod = market.getStats().getDynamic().getMod(Stats.SLIPSTREAM_REVEAL_RANGE_LY_MOD);
        mod.unmodify(id1);
        mod.unmodify(id2);
        mod.unmodify(id3);
        mod.unmodify(id4);
    }

    public float gerSensorArrayBonusFor(MarketAPI market, float range) {
        int countDomain = 0;
        int countMakeshift= 0;
        Vector2f locInHyper = market.getLocationInHyperspace();
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            float dist = Misc.getDistanceLY(locInHyper, system.getLocation());
            if (dist > range && Math.round(dist * 10f) <= range * 10f) {
                dist = range;
            }
            if (dist <= range) {
                for (SectorEntityToken entity : system.getEntitiesWithTag(Tags.SENSOR_ARRAY)) {
                    if (entity.getFaction() != null && entity.getFaction().isPlayerFaction()) {
                        if (entity.hasTag(Tags.MAKESHIFT)) {
                            countMakeshift++;
                        } else {
                            countDomain++;
                        }
                    }
                }
            }
        }

        float bonus = Math.min(countDomain, MAX_SENSOR_ARRAYS) * RANGE_PER_DOMAIN_SENSOR_ARRAY;
        float useMakeshift = Math.min(MAX_SENSOR_ARRAYS - countDomain, countMakeshift);
        if (useMakeshift < 0) useMakeshift = 0;
        bonus += useMakeshift * RANGE_PER_MAKESHIFT_SENSOR_ARRAY;
        //bonus += Math.min(Math.max(0, countMakeshift - countDomain), MAX_SENSOR_ARRAYS) * RANGE_PER_MAKESHIFT_SENSOR_ARRAY;

        return bonus;
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            MutableStat fake = new MutableStat(0);


            String desc = getNameForModifier();
            float a =ACC_BONUS;
            if (a > 0) {
                fake.modifyFlat(getModId(0), a, desc);
            }
            float total = a;
//			Pair<String, Integer> deficit = getAccessibilityAffectingDeficit();
//			float loss = getAccessibilityPenalty(deficit);
//			if (deficit.two > 0) {
//				fake.modifyFlat(getModId(1), -loss, getDeficitText(deficit.one));
//			}
//
//			float total = a - loss;
            String totalStr = "+" + (int)Math.round(total * 100f) + "%";
            Color h = Misc.getHighlightColor();
            if (total < 0) {
                h = Misc.getNegativeHighlightColor();
                totalStr = "" + (int)Math.round(total * 100f) + "%";
            }
            float opad = 10f;
            float pad = 3f;
            if (total >= 0) {
                tooltip.addPara("Accessibility bonus: %s", opad, h, totalStr);
            } else {
                tooltip.addPara("Accessibility penalty: %s", opad, h, totalStr);
            }

            float bonus = getPopulationGrowthBonus();
            tooltip.addPara("Population growth: %s", opad, h, "+" + (int)bonus);

            HyperspaceTopographyEventIntel intel = HyperspaceTopographyEventIntel.get();
            if (intel != null && intel.isStageActive(HyperspaceTopographyEventIntel.Stage.SLIPSTREAM_DETECTION)) {
                h = Misc.getHighlightColor();
                tooltip.addSectionHeading("Hyperspace topography", Alignment.MID, opad);
                if (!isFunctional()) {
                    tooltip.addPara("Slipstream detection requires functional Spaceport", Misc.getNegativeHighlightColor(), opad);
                } else {
                    int range = (int) Math.round(market.getStats().getDynamic().getMod(Stats.SLIPSTREAM_REVEAL_RANGE_LY_MOD).computeEffective(0f));
                    tooltip.addPara("Slipstream detection range: %s light-years", opad, h, "" + range);
                    tooltip.addStatModGrid(tooltip.getWidthSoFar(), 50, opad, pad, market.getStats().getDynamic().getMod(Stats.SLIPSTREAM_REVEAL_RANGE_LY_MOD));
                }

            }

//			tooltip.addStatModGrid(400, 50, opad, pad, fake, new StatModValueGetter() {
//				public String getPercentValue(StatMod mod) {
//					return null;
//				}
//				public String getMultValue(StatMod mod) {
//					return null;
//				}
//				public Color getModColor(StatMod mod) {
//					if (mod.value < 0) return Misc.getNegativeHighlightColor();
//					return null;
//				}
//				public String getFlatValue(StatMod mod) {
//					String prefix = mod.value >= 0 ? "+" : "";
//					return prefix + (int)Math.round(mod.value * 100f) + "%";
//				}
//			});

        }
    }
    protected void applyImproveModifiers() {
        // have to use a custom id - "spaceport_improve" - so that it's the same modifier when upgraded to megaport
        if (isImproved()) {
            market.getAccessibilityMod().modifyFlat("dadelous_improve", IMPROVE_ACCESSIBILITY,
                    getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getAccessibilityMod().unmodifyFlat("dadelous_improve");
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        if (GrandWonderTypeManager.getSpec(getWonderTypeId()).canBuildAdditionalWonderOfType(this.getSpec().getId(), this.market)) {
            for (String s : getRequirementsToBuildWonder().keySet()) {
                if(!hasReqBeenMetOnMarket(s)){
                    return false;
                }
            }
            return true;
        } else return false;
    }

    @Override
    public String getUnavailableReason() {
        return null;
    }

    @Override
    public void finishedConstruction(MarketAPI marketAPI) {
        Industry ind = marketAPI.getIndustry(Industries.SPACEPORT);
        if(ind==null)ind = marketAPI.getIndustry(Industries.MEGAPORT);
        SpecialItemData data = ind.getSpecialItem();
        String ai = ind.getAICoreId();
        boolean wasImproved = ind.isImproved();
        if(wasImproved){
            this.setImproved(true);
        }
        if(AshMisc.isStringValid(ai)){
            this.setAICoreId(ai);
        }
        if(data!=null){
            market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(data,1);
        }
        market.removeIndustry(ind.getId(), MarketAPI.MarketInteractionMode.REMOTE,false);
        this.apply();
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }

    @Override
    public String getWonderTypeId() {
        return "space_transportation";
    }

    @Override
    public void addToCustomSectionInTooltip(TooltipMakerAPI tooltipMakerAPI) {

    }


    @Override
    public String getCurrentImage() {
        if (market.getPrimaryEntity() instanceof PlanetAPI planet) {
            if (planet.hasCondition(Conditions.NO_ATMOSPHERE)) {
                return Global.getSettings().getSpriteName("industry", "aotd_daedalus_array_no_atmo");
            } else {
                getSpec().getImageName();
            }
        }
        return super.getCurrentImage();
    }

    @Override
    public LinkedHashMap<String, String> getRequirementsToBuildWonder() {
        LinkedHashMap<String, String> requirements = new LinkedHashMap<>();
        requirements.put("first", "Market must be size at least size 6.");
        requirements.put("second", "Market can't have extreme weather and tectonic activity conditions.");
        return requirements;
    }

    @Override
    public boolean hasReqBeenMetOnMarket(String s) {
        if(s.equals("first")){
            return market.getSize()>=6;
        }
        if(s.equals("second")){
            return !market.hasCondition(Conditions.EXTREME_TECTONIC_ACTIVITY)&&!market.hasCondition(Conditions.TECTONIC_ACTIVITY)&&!market.hasCondition(Conditions.EXTREME_WEATHER);
        }
        return true;
    }

    @Override
    public LinkedHashSet<String> getIndustriesToPreventFromAppearingInMenu(MarketAPI marketAPI) {
        LinkedHashSet<String> industries = new LinkedHashSet<>();
        industries.add(Industries.SPACEPORT);
        return industries;
    }

    @Override
    public boolean shouldShowInListOfWonders(MarketAPI marketAPI) {
        return GrandWonderTypeManager.getSpec(getWonderTypeId()).canBuildAdditionalWonderOfType(this.getSpec().getId(), marketAPI);
    }
}
