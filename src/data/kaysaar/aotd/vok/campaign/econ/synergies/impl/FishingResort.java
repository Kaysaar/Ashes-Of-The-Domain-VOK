package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashSet;

public class FishingResort extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return true;
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.RESORT) && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE).toArray(new String[0]));

    }

    @Override
    public String getSynergyName() {
        return "Fishing Resort";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.add(AoTDIndustries.RESORT);
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float percent = 0.1f * efficiency;
        tooltip.addPara("Increase income of %s by %s", 3f, base, highLight, "Resort Center", AoTDMisc.getPercentageString(percent));
        tooltip.addPara("Increase production of %s, by %s", 3f,base,highLight,IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE),market),"1");
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ", 3f, base, highLight,
                IndustrySynergiesMisc.getIndustryName(market, AoTDIndustries.RESORT),
                IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE),market));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().modifyMult(getIdForEffects(), 1f + (0.1f * efficiencyPercent), "Fishing Resort");
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
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(0, 105, 148, 255);
    }

}
