package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.industry.MaglevNetwork;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

import java.awt.*;
import java.util.HashSet;

public class LockheedInitiative extends BaseIndustrySynergy {
    public float efficinecy=0;

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES)&& BlackSiteProjectManager.getInstance().canEngageInBlackSite();

    }

    @Override
    public boolean runsInEveryFrameScript() {
        return true;
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
     ;
        return canShowSynergyInUI(market) && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.ORBITAL_SKUNKWORK,AoTDIndustries.BLACK_SITE);
    }

    @Override
    public String getSynergyName() {
        return "Lockheed Initiative";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.add(AoTDIndustries.ORBITAL_SKUNKWORK);
        industries.add(AoTDIndustries.BLACK_SITE);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        float baseValue  =  (0.02f*efficiency);
        tooltip.addPara("Increase speed of black site projects by %s",3f,base,highLight, AoTDMisc.getPercentageString(baseValue));
        tooltip.addPara("Maximum speed bonus from synergy : %s",3f, base, highLight, AoTDMisc.getPercentageString(0.1f));

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ", 3f, base, highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.ORBITAL_SKUNKWORK),
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.BLACK_SITE));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        float baseValue  =  (0.02f*efficiencyPercent);
        efficinecy+=baseValue;
        if(efficinecy>=0.1f){
            efficinecy = 0.1f;
        }

    }


    @Override
    public void unapply(MarketAPI market) {


    }



    @Override
    public Color getColorForWagons(String industry) {
        return new Color(0, 82, 155, 255);
    }


    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 5;
    }

    @Override
    public void advanceInEveryFrameScript(float amount) {
        efficinecy =0;
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites()<=0)return;
        for (MarketAPI market : AoTDMisc.getPlayerFactionMarkets()) {
            if(doesSynergyMetTotalReq(market)){
                MaglevNetwork network = (MaglevNetwork) market.getIndustry(AoTDIndustries.MAGLEV_CENTRAL_HUB);
                network.applySynergyEffect(this);
            }
        }
        if(efficinecy==0){
            AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().unmodifyFlat(getIdForEffects());
        }
        else{
            AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().modifyFlat(getIdForEffects(),efficinecy);
        }
    }
}
