package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashSet;

public class UAFAddictivePastries extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.DRUGS_AMPLIFICATION)&& Global.getSettings().getIndustrySpec("uaf_bakery_branch").getNewPluginInstance(market).isAvailableToBuild();
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech =canShowSynergyInUI(market);

        return  tech  && IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.NEUROCHEMICAL_LABORATORY,"uaf_bakery_branch");
    }

    @Override
    public String getSynergyName() {
        return "Addictive Pastries";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.add("uaf_bakery_branch");
        industries.add(AoTDIndustries.NEUROCHEMICAL_LABORATORY);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float bonus =  0.15f*efficiency;
        tooltip.addPara("Increases market income by %s, increases stability by %s.",3f,base,highLight, AoTDMisc.getPercentageString(bonus),"1");

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("Requires %s and %s on the same planet.",3f,base,highLight,
                IndustrySynergiesMisc.getIndustryName(market,"uaf_bakery_branch"),
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.NEUROCHEMICAL_LABORATORY));

    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        market.getIncomeMult().modifyFlat(getIdForEffects(),0.15f*efficiencyPercent,getSynergyName());
        market.getStability().modifyFlat(getIdForEffects(),1f,getSynergyName());
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getIncomeMult().unmodifyFlat(getIdForEffects());
        market.getStability().unmodifyFlat(getIdForEffects());

    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(201, 105, 255);

    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 3;
    }
}
