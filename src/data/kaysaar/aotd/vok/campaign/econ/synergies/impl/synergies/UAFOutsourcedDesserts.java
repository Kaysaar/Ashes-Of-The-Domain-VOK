package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class UAFOutsourcedDesserts extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return Global.getSettings().getIndustrySpec("uaf_bakery_branch").getNewPluginInstance(market).isAvailableToBuild();
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market,"uaf_bakery_branch", AoTDIndustries.RESORT);
    }

    @Override
    public String getSynergyName() {
        return "Outsourced Desserts";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry("uaf_bakery_branch"));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESORT));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry("uaf_bakery_branch");
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESORT);
        tooltip.addPara("Requires %s and %s on the same planet.", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        tooltip.addPara("Increases %s income by %s.",3f,base,highLight,"Resort Center", AoTDMisc.getPercentageString(0.2f*efficiency));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        if (market.hasIndustry(AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().modifyMult(getIdForEffects(), 1f + (0.2f * efficiencyPercent), getSynergyName());
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
        return Color.pink;
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 4;
    }
}
