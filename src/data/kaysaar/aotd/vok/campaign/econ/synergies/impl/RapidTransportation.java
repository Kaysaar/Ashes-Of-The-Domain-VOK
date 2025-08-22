package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class RapidTransportation extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return true;
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.SPACEPORT));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION));
    }

    @Override
    public String getSynergyName() {
        return "Rapid Transportation";
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market,Industries.SPACEPORT,Industries.WAYSTATION);
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.SPACEPORT);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION);
        tooltip.addPara("%s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        tooltip.addPara("Increase accessibility by %s",3f,base,highLight, AoTDMisc.getPercentageString(0.1f*efficiency));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        market.getAccessibilityMod().modifyFlat(getIdForEffects(),efficiencyPercent*0.1f,getSynergyName());
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getAccessibilityMod().unmodifyFlat(getIdForEffects());
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 4;

    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(79, 109, 171);
    }
}
