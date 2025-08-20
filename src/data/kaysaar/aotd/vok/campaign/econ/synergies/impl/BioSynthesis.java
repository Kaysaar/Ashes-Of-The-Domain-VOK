package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.HashSet;

public class BioSynthesis extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market,AoTDTechIds.DRUGS_AMPLIFICATION,AoTDTechIds.IMPROVED_FERTILIZERS);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech =canShowSynergyInUI(market);

        return  tech  && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.BIOSYNTH_LABORATORY,AoTDIndustries.SUBSIDISED_FARMING);
    }

    @Override
    public String getSynergyName() {
        return "Bio Synthesis";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries) {
        industries.add(AoTDIndustries.SUBSIDISED_FARMING);
        industries.add(AoTDIndustries.BIOSYNTH_LABORATORY);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        int bonus = (int) (2*efficiency);
        tooltip.addPara("Increase supply of %s, produced in %s by %s",3f,base,highLight,"Food","Subsidized Farming",bonus+"");
        tooltip.addPara("Increase %s demand for %s by %s",3f, Misc.getNegativeHighlightColor(),highLight,"Recreational Drugs","Subsidized Farming","1");

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s must be functional!",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.SUBSIDISED_FARMING),
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.BIOSYNTH_LABORATORY));

    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.SUBSIDISED_FARMING)) {
            Industry cryo = market.getIndustry(AoTDIndustries.SUBSIDISED_FARMING);
            cryo.getSupplyBonusFromOther().modifyFlat(getIdForEffects(),2,"Bio Synthesis");
            cryo.getDemand(Commodities.DRUGS).getQuantity().modifyFlat(getIdForEffects(),1,"Bio Synthesis");

        }

    }

    @Override
    public void unapply(MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.SUBSIDISED_FARMING)) {
            Industry cryo = market.getIndustry(AoTDIndustries.SUBSIDISED_FARMING);
            cryo.getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
            cryo.getDemand(Commodities.DRUGS).getQuantity().unmodifyFlat(getIdForEffects());

        }

    }

    @Override
    public Color getColorForWagons(String industry) {
            return new Color(248, 64, 64);

    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }
}
