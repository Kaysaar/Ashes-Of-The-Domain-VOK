package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
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

public class FacilitatedResearchLogistics extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.INTERSTELLAR_LOGISTICS);
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.add(Industries.MEGAPORT);
        industries.add(AoTDIndustries.TERMINUS);
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER));


    }

    @Override
    public String getSynergyName() {
        return "Facilitated Research Logistics";
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market,Industries.MEGAPORT,AoTDIndustries.TERMINUS,AoTDIndustries.RESEARCH_CENTER);
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.MEGAPORT);
        ArrayList<String> ter = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.TERMINUS);
        ArrayList<String> re = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER);
        tooltip.addPara("%s, %s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(ter,market),
                getIndustriesListed(re,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        ArrayList<String> re = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER);
        tooltip.addPara("Decrease upkeep of %s by %s",3f,base,highLight,getIndustriesListed(re,market), AoTDMisc.getPercentageString(0.1f*efficiency));
        tooltip.addPara("Increase research speed by %s",3f,base,highLight,AoTDMisc.getPercentageString(0.05f*efficiency));
        tooltip.addPara("Increase accessibility by %s",3f,base,highLight,AoTDMisc.getPercentageString(0.05f*efficiency));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        market.getAccessibilityMod().modifyFlat(getIdForEffects(),efficiencyPercent*0.05f,getSynergyName());
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER)) {
            if(market.hasIndustry(id)){
                market.getIndustry(id).getUpkeep().modifyFlat(getIdForEffects(),1-(efficiencyPercent*0.1f),getSynergyName());
            }
        }
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getAccessibilityMod().unmodifyFlat(getIdForEffects());
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESEARCH_CENTER)) {
            if(market.hasIndustry(id)){
                market.getIndustry(id).getUpkeep().unmodifyFlat(getIdForEffects());
            }
        }
    }

    @Override
    public void advance(MarketAPI market, float amount,boolean aboutToBeRemoved) {
        if(this.doesSynergyMetTotalReq(market)&&!aboutToBeRemoved){
            AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchSpeedBonus().modifyFlat(getIdForEffects()+"_"+market.getId(),0.05f);

        }
        else{
            AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchSpeedBonus().unmodifyFlat(getIdForEffects()+"_"+market.getId());
        }
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 4;

    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(100, 96, 96);
    }
}
