package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class OreToCore extends BaseIndustrySynergy {
    @Override
    public String getIdForEffects() {
        return "ore_to_core";
    }

    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.EXO_SKELETONS);

    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.EXO_SKELETONS);
        ArrayList<String> paths = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.MINING);
        paths.remove("fracking");
        return tech && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromListIncludingUpgrades(market, Industries.REFINING) && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, paths.toArray(new String[0]));
    }

    @Override
    public String getSynergyName() {
        return "Ore-To-Core";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries) {
        ArrayList<String>ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.MINING);
        ids.remove("fracking");

        industries.addAll(ids);
        ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.REFINING);
        industries.addAll(ids);
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        int baseValue  = (int) (2*efficiency);
        tooltip.addPara("Increase amount of metals, produced by %s by %s",3f,base,highLight,"Refining or one of it's upgrades",baseValue+"");

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ", 3f, base, highLight,
                "Refining or one of it's upgrades",
               "Mining or one of it's upgrades, excluding Plasma Harvester");
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {


    }


    @Override
    public void unapply(MarketAPI market) {


    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(227, 130, 19,255);
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 6;
    }
}

