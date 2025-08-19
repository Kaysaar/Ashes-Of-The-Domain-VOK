package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.HashSet;

public class AgroTourism extends BaseIndustrySynergy {
    @Override
    public String getIdForEffects() {
        return "agro_tourism";
    }

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AGRICULTURE_INDUSTRIALIZATION, market);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AGRICULTURE_INDUSTRIALIZATION, market);

        return  tech && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.RESORT, AoTDIndustries.ARTISANAL_FARMING);
    }

    @Override
    public String getSynergyName() {
        return "Agro Tourism";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries) {
        industries.add(AoTDIndustries.RESORT);
        industries.add(AoTDIndustries.ARTISANAL_FARMING);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency) {
        float baseValue = 0.1f * efficiency;
        tooltip.addPara("Increase income of %s by %s", 3f, base, highLight, Global.getSettings().getIndustrySpec(AoTDIndustries.RESORT).getName(), AoTDMisc.getPercentageString(baseValue));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional on same colony! ",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.RESORT),
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.ARTISANAL_FARMING));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().modifyMult(getIdForEffects(), 1f + (0.1f * efficiencyPercent), "Industry Synergy");
        }

    }

    @Override
    public void unapply(MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().unmodifyMult(getIdForEffects());
        }

    }

    @Override
    public Color getColorForWagons(String industry) {
        return Misc.getPositiveHighlightColor();
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }

}
