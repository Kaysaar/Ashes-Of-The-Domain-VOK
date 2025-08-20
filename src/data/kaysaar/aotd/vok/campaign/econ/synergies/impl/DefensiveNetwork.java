package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.TechMining;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class DefensiveNetwork extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return true;
//        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST, market);
    }
    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean max_tier_station = market.hasIndustry(Industries.STARFORTRESS) ||
                market.hasIndustry(Industries.STARFORTRESS_MID) ||
                market.hasIndustry(Industries.STARFORTRESS_HIGH);
        return max_tier_station &&
                market.hasIndustry(Industries.HEAVYBATTERIES) && market.hasIndustry(Industries.HIGHCOMMAND);
    }
    @Override
    public String getSynergyName() {
        return "Defensive Network";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries) {
        industries.add(Industries.STARFORTRESS);
        industries.add(Industries.STARFORTRESS_MID);
        industries.add(Industries.STARFORTRESS_HIGH);
        industries.add(Industries.HEAVYBATTERIES);
        industries.add(Industries.HIGHCOMMAND);

    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float baseValDR = 1.5f * efficiency;
        float baseValFS = 0.2f * efficiency;
        float baseValA = 0.1f * efficiency;
        tooltip.addPara("Increases total defense rating by %s", 3f, base, highLight, "x" + baseValDR);
        tooltip.addPara("Increases fleet size by %s", 3f, base, highLight, AoTDMisc.getPercentageString(baseValFS));
        tooltip.addPara("Decreases accessibility by %s", 3f, base, highLight, AoTDMisc.getPercentageString(baseValA));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s, %s and %s are required to be functional", 3f, base, highLight,
                "A Starfortress of any type",
                IndustrySynergiesMisc.getIndustryName(market,Industries.HEAVYBATTERIES),
                IndustrySynergiesMisc.getIndustryName(market,Industries.HIGHCOMMAND));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) { /* ADD FUNCTIONALITY */
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getIdForEffects(),1.5f);
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyPercent(getIdForEffects(),20f);
        market.getAccessibilityMod().modifyPercent(getIdForEffects(),-10f);
    }

    @Override
    public void unapply(MarketAPI market) { /* ADD FUNCTIONALITY */ }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(210,48,51);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 6;
    }
}
