package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class DrumsInTheDeep extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return canShowSynergyInUI(market)&&IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market, AoTDIndustries.RESEARCH_CENTER, Industries.HEAVYINDUSTRY,Industries.AQUACULTURE);
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE));
    }

    @Override
    public String getSynergyName() {
        return "Drums in the Deep";
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER);
        ArrayList<String> ids2 = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY);
        ArrayList<String> ids3 = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE);
        tooltip.addPara("Requires %s, %s and %s on the same planet.", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(ids2,market),
                getIndustriesListed(ids3,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER);
        ArrayList<String> ids2 = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY);
        ArrayList<String> ids3 = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE);
        tooltip.addPara("Reduces the upkeep of %s and %s by %s.",3f,base,highLight,getIndustriesListed(ids,market),getIndustriesListed(ids2,market), AoTDMisc.getPercentageString(0.1f*efficiency));
        tooltip.addPara("Increases the upkeep of %s by %s.",3f,Misc.getNegativeHighlightColor(),highLight,getIndustriesListed(ids3,market),AoTDMisc.getPercentageString(0.15f*efficiency));
        tooltip.addPara("Reduces Luddic Path interest.", Misc.getPositiveHighlightColor(),3f);
        tooltip.addPara("Reduces accessibility by %s.",3f,Misc.getNegativeHighlightColor(),highLight,AoTDMisc.getPercentageString(0.1f*efficiency));

    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        Industry research = IndustrySynergiesMisc.getIndustryFromUpgradeTree(AoTDIndustries.RESEARCH_CENTER,market);
        Industry heavy =  IndustrySynergiesMisc.getIndustryFromUpgradeTree(Industries.HEAVYINDUSTRY,market);
        Industry aqua = IndustrySynergiesMisc.getIndustryFromUpgradeTree(Industries.AQUACULTURE,market);
        if(research!=null) {
            research.getUpkeep().modifyFlat(getIdForEffects(),1-(0.1f*efficiencyPercent),getSynergyName());
        }
        if(heavy!=null) {
            heavy.getUpkeep().modifyFlat(getIdForEffects(),1-(0.1f*efficiencyPercent),getSynergyName());
        }
        if(aqua!=null) {
            aqua.getUpkeep().modifyFlat(getIdForEffects(),1+(0.15f*efficiencyPercent),getSynergyName());
        }
        market.getAccessibilityMod().modifyFlat(getIdForEffects(),-0.1f,getSynergyName());
    }

    @Override
    public void unapply(MarketAPI market) {
        Industry research = IndustrySynergiesMisc.getIndustryFromUpgradeTree(AoTDIndustries.RESEARCH_CENTER,market);
        Industry heavy =  IndustrySynergiesMisc.getIndustryFromUpgradeTree(Industries.HEAVYINDUSTRY,market);
        Industry aqua = IndustrySynergiesMisc.getIndustryFromUpgradeTree(Industries.AQUACULTURE,market);
        if(research!=null) {
            research.getUpkeep().unmodifyFlat(getIdForEffects());
        }
        if(heavy!=null) {
            heavy.getUpkeep().unmodifyFlat(getIdForEffects());
        }
        if(aqua!=null) {
            aqua.getUpkeep().unmodifyFlat(getIdForEffects());
        }
        market.getAccessibilityMod().unmodifyFlat(getIdForEffects());
    }

    @Override
    public void advance(MarketAPI market, float amount,boolean aboutToRemove) {
        if(this.doesSynergyMetTotalReq(market)&&!aboutToRemove) {
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().modifyFlat(getIdForEffects(),-8,getSynergyName());
        }
        else{
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat(getIdForEffects());

        }
    }

}
