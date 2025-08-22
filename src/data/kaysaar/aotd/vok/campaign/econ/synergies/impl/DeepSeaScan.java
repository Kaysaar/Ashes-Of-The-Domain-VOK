package data.kaysaar.aotd.vok.campaign.econ.synergies.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.TechMining;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.econ.impl.TechMining.TECH_MINING_MULT;
import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;
import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getTechMiningMult;

public class DeepSeaScan extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST, market);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.AQUATIC_BIOSPHERE_HARVEST, market);
        return tech &&
                TechMining.getTechMiningRuinSizeModifier(market) != 0 &&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE).toArray(new String[0])) &&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.TECHMINING).toArray(new String[0]));
    }

    @Override
    public String getSynergyName() {
        return "Deep Sea Scan";
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.TECHMINING));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        float baseVal = 0.3f * efficiency;
        tooltip.addPara("Slows down degradation of ruins by %s", 3f, base, highLight, AoTDMisc.getPercentageString(baseVal));
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.TECHMINING);
        tooltip.addPara("%s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids, market),
                getIndustriesListed(fuelIds, market));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) { }

    @Override
    public void unapply(MarketAPI market) { }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(26, 32, 197);
    }



    @Override
    public CargoAPI generateCargoForGatheringPoint(MarketAPI market, Random random) {
        if (!doesSynergyMetReq(market)) {
            return super.generateCargoForGatheringPoint(market, random);
        }
        float decay = Global.getSettings().getFloat("techMiningDecay");
        float multAfter = getTechMiningMult(market);

        if (multAfter <= 0f) {
            return super.generateCargoForGatheringPoint(market, random);
        }

        float decayPrime = 0.3f + 0.7f * decay;

        float factor = (decay <= 0f) ? 1f : (decayPrime / decay);

        float corrected = Math.min(1f, multAfter * factor);
        market.getMemoryWithoutUpdate().set(TECH_MINING_MULT, corrected);
        return super.generateCargoForGatheringPoint(market, random);
    }


    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 5;
    }
}
