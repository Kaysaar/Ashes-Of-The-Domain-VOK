package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
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
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        float baseVal = 0.1f*efficiency;
        tooltip.addPara("Decreases %s demand for %s by %s.",3f,base,highLight,"Fuel",   getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION),market),"1");
        tooltip.addPara("Increases accessibility by %s.",3f,base,highLight, AoTDMisc.getPercentageString(baseVal));
    }



    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD);
        tooltip.addPara("Requires %s and %s on the same planet.", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
        if(market.hasCondition(Conditions.EXTREME_WEATHER)) {
            tooltip.addPara("Planet must not have Extreme Weather.", Misc.getNegativeHighlightColor(),3f);
        }
        else{
            tooltip.addPara("Planet must not have Extreme Weather.",Misc.getPositiveHighlightColor(),3f);
        }

    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(Industries.WAYSTATION)) {
            Industry waystation = market.getIndustry(Industries.WAYSTATION);
            waystation.getDemand(Commodities.FUEL).getQuantity().modifyFlat(getIdForEffects(),-1,getSynergyName());
        }
        float baseVal = 0.1f*efficiencyPercent;
        market.getAccessibilityMod().modifyFlat(getIdForEffects(),baseVal,getSynergyName());

    }

    @Override
    public void unapply(MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().unmodifyMult(getIdForEffects());
        }
        market.getAccessibilityMod().unmodifyFlat(getIdForEffects());
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
