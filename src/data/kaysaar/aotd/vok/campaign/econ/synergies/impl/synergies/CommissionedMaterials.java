package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class CommissionedMaterials extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);

    }

    @Override
    public String getIdForEffects() {
        return "commisioned_materials";
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {
        boolean tech = IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);
        ArrayList<String> paths = IndustrySynergiesMisc.getIdsOfTreeFromIndustryTrimmed(Industries.HEAVYINDUSTRY,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.SUPPLY_HEAVY).toArray(new String[0]));

        return tech && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromListIncludingUpgrades(market, Industries.REFINING) && IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market, paths.toArray(new String[0]));
    }

    @Override
    public void apply(float efficiencyPercent, MarketAPI market) {
        for (String ids : IndustrySynergiesMisc.getIdsOfTreeFromIndustryTrimmed(Industries.HEAVYINDUSTRY,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.SUPPLY_HEAVY).toArray(new String[0]))) {
            if(market.hasIndustry(ids)){
                market.getIndustry(ids).getSupply(Commodities.SUPPLIES).getQuantity().modifyFlat(getIdForEffects(),2,"Commissioned Materials Synergy");
                market.getIndustry(ids).getSupply(Commodities.HAND_WEAPONS).getQuantity().modifyFlat(getIdForEffects(),2,"Commissioned Materials Synergy");

            }
        }
        for (String ids : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.REFINING).toArray(new String[0])) {
            if(market.hasIndustry(ids)){
                market.getIndustry(ids).getSupplyBonusFromOther().modifyFlat(getIdForEffects(),-1,"Commissioned Materials Synergy");
            }
        }
    }

    @Override
    public void unapply(MarketAPI market) {
        for (String ids : IndustrySynergiesMisc.getIdsOfTreeFromIndustryTrimmed(Industries.HEAVYINDUSTRY,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.SUPPLY_HEAVY).toArray(new String[0]))) {
            if(market.hasIndustry(ids)){
                market.getIndustry(ids).getSupply(Commodities.SUPPLIES).getQuantity().unmodifyFlat(getIdForEffects());
                market.getIndustry(ids).getSupply(Commodities.HAND_WEAPONS).getQuantity().unmodifyFlat(getIdForEffects());
            }
        }
        for (String ids : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.REFINING).toArray(new String[0])) {
            if(market.hasIndustry(ids)){
                market.getIndustry(ids).getSupplyBonusFromOther().unmodifyFlat(getIdForEffects());
            }
        }
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustryTrimmed(Industries.HEAVYINDUSTRY,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.SUPPLY_HEAVY).toArray(new String[0])));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.REFINING));
    }

    @Override
    public String getSynergyName() {
        return "Commissioned Materials";
    }
    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency,MarketAPI market) {
        int baseValue  = (int) (2*efficiency);
        tooltip.addPara("Increase amount of ships and heavy weapons, produced by %s by %s",3f,base,highLight,"Heavy Industry or one of it's upgrades",baseValue+"");
        tooltip.addPara("Decrease amount of metals , produced by %s by %s",3f,Misc.getNegativeHighlightColor(),highLight,"Refining or one of it's upgrades","1");
    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        tooltip.addPara("%s and %s are required to be functional! ", 3f, base, highLight,
                "Heavy Industry or one of it's upgrades excluding Civilian Heavy Industry",
                "Refining or one of it's upgrades");
    }
    @Override
    public Color getColorForWagons(String industry) {
        return Misc.getNegativeHighlightColor();
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        if(industry.equals(Industries.HEAVYINDUSTRY)){
            return 4;
        }
        if(industry.equals(Industries.ORBITALWORKS)){
            return 6;
        }
        if(industry.equals(AoTDIndustries.ORBITAL_SKUNKWORK)||industry.equals(AoTDIndustries.ORBITAL_FLEETWORK)){
            return 8;
        }
        return 4;
    }
}
