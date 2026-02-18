package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.HashSet;

public class VolatileLine extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.DEEP_MINING_METHODS);
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        return canShowSynergyInUI(market)&&IndustrySynergiesMisc.isIndustryFunctionalAndExistingIncludingUpgrades(market, AoTDIndustries.PLASMA_HARVESTER, Industries.FUELPROD);
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries, MarketAPI market) {
        industries.add(AoTDIndustries.PLASMA_HARVESTER);
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD));
    }

    @Override
    public String getSynergyName() {
        return "Volatile Line";
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("Requires %s and %s on the same planet.", 3f, base, highLight,
                IndustrySynergiesMisc.getIndustryName(market,AoTDIndustries.PLASMA_HARVESTER),
                IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD),market));
    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        int number = Math.round(2*efficiency);
        tooltip.addPara("Increases production of %s by %s.",3f,base,highLight,  IndustrySynergiesMisc.getIndustriesListed(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD),market),number+"");
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        for (String string : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD)) {
            if(market.hasIndustry(string)) {
                int number = Math.round(2*efficiencyPercent);
                market.getIndustry(string).getSupplyBonusFromOther().modifyFlat(getIdForEffects(),number,getSynergyName());
            }
        };
    }

    @Override
    public void unapply(MarketAPI market) {
        for (String string : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.FUELPROD)) {
            if(market.hasIndustry(string)) {
                market.getIndustry(string).getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
            }
        };
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 4;
    }

    @Override
    public Color getColorForWagons(String industry) {
        return new Color(59, 161, 137);
    }
}
