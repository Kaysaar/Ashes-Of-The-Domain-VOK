package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

import java.awt.*;
import java.util.HashSet;

public class PlausibleDeniability extends BaseIndustrySynergy {
    @Override
    public String getIdForEffects() {
        return "plausible_deniability";
    }

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return BlackSiteProjectManager.getInstance().canEngageInBlackSite();
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = BlackSiteProjectManager.getInstance().canEngageInBlackSite();

        return  tech && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, Industries.TECHMINING, AoTDIndustries.BLACK_SITE);
    }

    @Override
    public String getSynergyName() {
        return "Plausible Deniability";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.add(AoTDIndustries.BLACK_SITE);
        industries.add(Industries.TECHMINING);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        float percent = 0.1f*efficiency;
        tooltip.addPara("Reduces upkeep of %s by %s",3f,base,highLight,"Black Site", AoTDMisc.getPercentageString(percent));
        tooltip.addPara("Slightly reduced pather interest on market!",Misc.getPositiveHighlightColor(),3f);
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.BLACK_SITE),
                IndustrySynergiesMisc.getIndustryName(market,Industries.TECHMINING));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.BLACK_SITE)) {
            float percent = 0.1f*efficiencyPercent;
            market.getIndustry(AoTDIndustries.BLACK_SITE).getUpkeep().modifyMult(getIdForEffects(),1f-percent,"Plausible Deniability");
        }

    }

    @Override
    public void unapply(MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.BLACK_SITE)) {
            market.getIndustry(AoTDIndustries.BLACK_SITE).getUpkeep().unmodifyMult(getIdForEffects());
        }

    }

    @Override
    public void advance(MarketAPI market, float amount,boolean aboutToRemove) {

        if(this.doesSynergyMetTotalReq(market)&&!aboutToRemove) {
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().modifyFlat(getIdForEffects(),-5* IndustrySynergiesManager.getInstance().calculateEfficiency(market),"Plausible Deniability");
        }
        else{
            MiscHiddenIndustry.getInstance(market).getPatherInterestManipulator().unmodifyFlat(getIdForEffects());

        }
    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(75, 75, 75);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 2;
    }
}
