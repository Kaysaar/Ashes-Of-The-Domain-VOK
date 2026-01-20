package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashSet;

public class DefensiveNetwork extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return true;
//        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST, market);
    }
    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean max_tier_station = market.getIndustries().stream().anyMatch(x->x.getSpec().hasTag(Industries.TAG_STARFORTRESS));
        return max_tier_station &&
                market.hasIndustry(Industries.HEAVYBATTERIES) && market.hasIndustry(Industries.HIGHCOMMAND);
    }
    @Override
    public String getSynergyName() {
        return "Defensive Network";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfIndustryWithSameTag(Industries.TAG_STARFORTRESS,market));
        industries.add(Industries.HEAVYBATTERIES);
        industries.add(Industries.HIGHCOMMAND);
        if(Global.getSettings().getModManager().isModEnabled("aotd_sop")){
            industries.add("aotd_hexagon");
        }

    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float baseValDR = 1.5f * efficiency;
        float baseValFS = 0.2f * efficiency;
        float baseValA = 0.1f * efficiency;
        tooltip.addPara("Increases total defense rating by %s", 3f, base, highLight, "x" + Misc.getRoundedValueMaxOneAfterDecimal(baseValDR));
        tooltip.addPara("Increases fleet size by %s", 3f, base, highLight, AoTDMisc.getPercentageString(baseValFS));
        tooltip.addPara("Decreases accessibility by %s", 3f, Misc.getNegativeHighlightColor(), highLight, AoTDMisc.getPercentageString(baseValA));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        String highCommandString = IndustrySynergiesMisc.getIndustryName(market,Industries.HIGHCOMMAND);
        if(Global.getSettings().getModManager().isModEnabled("aotd_sop")){
            highCommandString += "/" + IndustrySynergiesMisc.getIndustryName(market,"aotd_hexagon");
        }
        tooltip.addPara("%s, %s and %s are required to be functional", 3f, base, highLight,
                "A Starfortress of any type",
                IndustrySynergiesMisc.getIndustryName(market,Industries.HEAVYBATTERIES),
                highCommandString);
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) { /* ADD FUNCTIONALITY */
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getIdForEffects(),1.5f,"Defensive Network");
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyPercent(getIdForEffects(),20f,"Defensive Network");
        market.getAccessibilityMod().modifyPercent(getIdForEffects(),-10f,"Defensive Network");
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getIdForEffects());
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyPercent(getIdForEffects());
        market.getAccessibilityMod().unmodifyPercent(getIdForEffects());
    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(210,48,51);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 6;
    }
}
