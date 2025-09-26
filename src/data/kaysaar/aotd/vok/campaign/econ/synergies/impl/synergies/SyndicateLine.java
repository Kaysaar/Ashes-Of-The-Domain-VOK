package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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
import java.util.HashSet;

public class SyndicateLine extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return true;
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, market);

        return  tech &&market.isFreePort() &&IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.UNDERWORLD)&&IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, Industries.CRYOSANCTUM,AoTDIndustries.BIOSYNTH_LABORATORY,Industries.LIGHTINDUSTRY);
    }

    @Override
    public String getSynergyName() {
        return "Syndicate Line";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.add(AoTDIndustries.UNDERWORLD);
        industries.add(AoTDIndustries.BIOSYNTH_LABORATORY);
        industries.add(Industries.LIGHTINDUSTRY);
        industries.add(Industries.CRYOSANCTUM);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        float baseValue = 0.1f*efficiency;
        float highLightValue = 0.6f*efficiency;
        int bonus = (int) (3*efficiency);
        tooltip.addPara("If %s is present: Increase production of organs, produced in %s by %s",3f,base,highLight,"Cryosanctum","Cryosanctum",bonus+"");
        tooltip.addPara("If %s is present: Increase market income multiplier by %s",3f,base,highLight,"Light Industry", AoTDMisc.getPercentageString(baseValue));
        tooltip.addPara("If %s is present: Increase market income multiplier by %s",3f,base,highLight,"Neurochemical Laboratory", AoTDMisc.getPercentageString(highLightValue));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s must be functional!",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.UNDERWORLD));
        tooltip.addPara("Market must have Free Port!",base,3f);
        tooltip.addPara("One of following must be functional:",Misc.getTooltipTitleAndLightHighlightColor(),3f);
        for (String string : getIndustriesForSynergy(market)) {
            if(string.equals(AoTDIndustries.UNDERWORLD))continue;
            Color high = Misc.getNegativeHighlightColor();
            if(market.hasIndustry(string)){
                high = Misc.getPositiveHighlightColor();
            }
            tooltip.addPara("-"+IndustrySynergiesMisc.getIndustryName(market,string),high,3f);

        }
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(Industries.CRYOSANCTUM)) {
            Industry cryo = market.getIndustry(Industries.CRYOSANCTUM);
            cryo.getSupplyBonusFromOther().modifyFlat(getIdForEffects(),3,"Syndicate Line");
        }
        if(market.hasIndustry(Industries.LIGHTINDUSTRY)){
            float val =10f*efficiencyPercent;
            market.getIncomeMult().modifyPercent(getIdForEffects(), val, "Syndicate Line");
        }
        if(market.hasIndustry(AoTDIndustries.BIOSYNTH_LABORATORY)){
            float val = 60f*efficiencyPercent;
            market.getIncomeMult().modifyPercent(getIdForEffects(), val, "Syndicate Line");
        }

    }

    @Override
    public void unapply(MarketAPI market) {
        if (market.hasIndustry(Industries.CRYOSANCTUM)) {
            Industry cryo = market.getIndustry(Industries.CRYOSANCTUM);
            cryo.getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
        }
        market.getIncomeMult().unmodifyPercent(getIdForEffects());

    }

    @Override
    public Color getColorForWagons(String industry) {
        if(industry.equals(Industries.CRYOSANCTUM)){
            return new Color(243, 20, 20);
        }
        return new Color(255, 113, 246);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        if(industry.equals(Industries.CRYOSANCTUM)){
            return 4;
        }

        return 5;
    }
}
