package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class InterstellarGasStation extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.INTERSTELLAR_LOGISTICS, market);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.INTERSTELLAR_LOGISTICS, market);
        return tech && !market.hasCondition(Conditions.EXTREME_WEATHER) &&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market,  IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION).toArray(new String[0])) &&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD).toArray(new String[0]));

    }

    @Override
    public String getSynergyName() {
        return "Interplanetary Gas Station";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        float baseVal = 0.1f*efficiency;
        tooltip.addPara("Lowers %s demand in %s by %s",3f,base,highLight,"Fuel",   getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION),market),"1");
        tooltip.addPara("Increase accessibility by %s",3f,base,highLight, AoTDMisc.getPercentageString(baseVal));
    }



    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD);
        tooltip.addPara("%s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
        if(market.hasCondition(Conditions.EXTREME_WEATHER)) {
            tooltip.addPara("No Extreme Weather", Misc.getNegativeHighlightColor(),3f);
        }
        else{
            tooltip.addPara("No Extreme Weather",Misc.getPositiveHighlightColor(),3f);
        }

    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().modifyMult(getIdForEffects(), 1f + (0.3f * efficiencyPercent), "Industry Synergy");
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
        return new Color(106, 218, 32);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }
}
