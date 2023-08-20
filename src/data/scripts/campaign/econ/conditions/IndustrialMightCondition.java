package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;
import data.plugins.AoDUtilis;
import data.scripts.research.ResearchAPI;

import java.awt.*;

public class IndustrialMightCondition extends BaseMarketConditionPlugin {


    public void apply(String id) {
        super.apply(id);
        ResearchAPI researchAPI = AoDUtilis.getResearchAPI();
        if(researchAPI!=null){
            if(researchAPI.alreadyResearchedAmount()>=10){
                handleBonuses();
            }

        }


    }

    @Override
    public void unapply(String id) {
        super.unapply(id);

    }

    public void handleBonuses() {
        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.MILITARY_HEAVY)) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                    .modifyMult("IndSynergyMiliHeavy", 1.5f, "Presence of Military Industrial Complex (Placeholder)");

        } else {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                    .unmodifyMult("IndSynergyMiliHeavy");
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.SUPPLY_HEAVY)) {
            market.getStability().modifyFlat("IndSynergyStab", 1, "Industrial Synergy");

        } else {
            market.getStability().unmodifyFlat("IndSynergyStab");
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.POLICRYSTALIZATOR)) {
            Industry poli = market.getIndustry(AoDIndustries.POLICRYSTALIZATOR);
            poli.getSupply(AodCommodities.REFINED_METAL).getQuantity().modifyFlat("IndSynergyCascade", +1, "Industry Synergy");
            poli.getUpkeep().modifyMult("Oh boi that can be broken ", 0.75f, "Industry Synergy");

        } else {
            Industry poli = market.getIndustry(AoDIndustries.POLICRYSTALIZATOR);
            if (poli != null) {
                poli.getUpkeep().unmodifyMult("Oh boi that can be broken ");
                poli.getSupply(AodCommodities.REFINED_METAL).getQuantity().unmodifyFlat("IndSynergyCascade");
            }
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.CASCADE_REPROCESSOR)) {
            Industry cascade = market.getIndustry(AoDIndustries.CASCADE_REPROCESSOR);
            cascade.getUpkeep().modifyMult("Oh boi that can be broken part 2", 0.75f, "Industry Synergy");
            cascade.getSupply(AodCommodities.PURIFIED_TRANSPLUTONICS).getQuantity().modifyFlat("IndSynergyPoli", +1, "Industry Synergy");
        } else {
            Industry cascade = market.getIndustry(AoDIndustries.CASCADE_REPROCESSOR);
            if (cascade != null) {
                cascade.getUpkeep().unmodifyMult("Oh boi that can be broken part 2");
                cascade.getSupply(AodCommodities.PURIFIED_TRANSPLUTONICS).getQuantity().unmodifyFlat("IndSynergyPoli");
            }
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.CASCADE_REPROCESSOR, AoDIndustries.TRI_TACHYON_HEAVY)) {
            Industry triHeavy = market.getIndustry(AoDIndustries.TRI_TACHYON_HEAVY);
            for (MutableCommodityQuantity mutableCommodityQuantity : triHeavy.getAllSupply()) {
                triHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyFlat("IndSynergyTri", +1, "Industry Synergy");
            }

        } else {
            Industry triHeavy = market.getIndustry(AoDIndustries.TRI_TACHYON_HEAVY);
            if (triHeavy != null) {
                for (MutableCommodityQuantity mutableCommodityQuantity : triHeavy.getAllSupply()) {
                    triHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyFlat("IndSynergyTri");
                }
            }
        }

        if (hasTwoIndustriesForSynergy(AoDIndustries.POLICRYSTALIZATOR, AoDIndustries.HEGEMONY_HEAVY)) {
            Industry hegeHeavy = market.getIndustry(AoDIndustries.HEGEMONY_HEAVY);
            for (MutableCommodityQuantity mutableCommodityQuantity : hegeHeavy.getAllSupply()) {
                hegeHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyFlat("IndSynergyHege", +1, "Industry Synergy");
            }
        } else {
            Industry hegeHeavy = market.getIndustry(AoDIndustries.HEGEMONY_HEAVY);
            if (hegeHeavy != null) {
                for (MutableCommodityQuantity mutableCommodityQuantity : hegeHeavy.getAllSupply()) {
                    hegeHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyFlat("IndSynergyHege");
                }
            }
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.RESORT, AoDIndustries.CONSUMER_INDUSTRY)) {
            Industry resort = market.getIndustry(AoDIndustries.RESORT);
            resort.getIncome().modifyMult("IndSynergyResortIncome",1.1f,"Industry Synergy");
        } else {
            Industry resort = market.getIndustry(AoDIndustries.HEGEMONY_HEAVY);
            if (resort != null) {
                resort.getIncome().unmodifyMult("IndSynergyResortIncome");
            }
        }

        if (hasTwoIndustriesForSynergy(AoDIndustries.ARTISANAL_FARMING, AoDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoDIndustries.ARTISANAL_FARMING);
            resort.getIncome().modifyMult("IndSynergyArtiIncome",1.1f,"Industry Synergy");
        } else {
            Industry resort = market.getIndustry(AoDIndustries.ARTISANAL_FARMING);
            if (resort != null) {
                resort.getIncome().unmodifyMult("IndSynergyArtiIncome");
            }
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.UNDERWORLD, AoDIndustries.RESORT)) {
            market.getIncomeMult().modifyMult("IndSynergyUnderworldIncome",1.1f,"Industry Synergy");
        } else {
            market.getIncomeMult().unmodifyMult("IndSynergyUnderworldIncome");
        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.RESORT)) {
            market.getAccessibilityMod().modifyFlat("IndSynergyAcessbility",0.1f,"Industry Synergy");
        } else {
            market.getAccessibilityMod().unmodifyFlat("IndSynergyAcessbility");
        }

    }

    public boolean hasTwoIndustriesForSynergy(String industryIdFirst, String IndustryidSecond) {
        return market.hasIndustry(industryIdFirst) && market.hasIndustry(IndustryidSecond);
    }


    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara(
                "Current Industry Synergies",
                Color.ORANGE,
                10f
        );

        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.MILITARY_HEAVY)) {
            tooltip.addPara(
                    "Terminus and Militarized Heavy Industry: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "1.5 bonus to Military garrisons"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.SUPPLY_HEAVY)) {
            tooltip.addPara(
                    "Terminus and Civilian Heavy Production: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "+1 stability"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.POLICRYSTALIZATOR)) {
            tooltip.addPara(
                    "Policrystalizator and Benefication: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decrease upkeep of Policrystalizator by 25% and +1 Production to Policrystalizator"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.CASCADE_REPROCESSOR)) {
            tooltip.addPara(
                    "Cascade Reprocessor and Sublimation : %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decrease upkeep of Cascade Reprocessor by 25% and +1 Production to Cascade Reprocessor"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.CASCADE_REPROCESSOR, AoDIndustries.TRI_TACHYON_HEAVY)) {
            tooltip.addPara(
                    "Cascade Reprocessor and Orbital Skunkwork Facility: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "+1 To Orbital Skunkwork Facility Production"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.POLICRYSTALIZATOR, AoDIndustries.HEGEMONY_HEAVY)) {
            tooltip.addPara(
                    "Policrystalizator and Orbital Fleetwork Facility: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "+1 To Orbital Fleetwork Facility Production"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.CONSUMER_INDUSTRY, AoDIndustries.RESORT)) {
            tooltip.addPara(
                    "Consumer Industry and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increase Resort income by 10%"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.ARTISANAL_FARMING, AoDIndustries.RESORT)) {
            tooltip.addPara(
                    "Artisanal Farming and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increase Artisanal Farming's income by 10%"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.UNDERWORLD, AoDIndustries.RESORT)) {
            tooltip.addPara(
                    "Underworld and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increase planet income by 5% "
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.RESORT)) {
            tooltip.addPara(
                    "Terminus and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increase planet accessibility by 10% "
            );

        }

    }

    public String getModId() {
        return condition.getId();
    }

    @Override
    public boolean showIcon() {
        boolean firstarg = (hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.MILITARY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.SUPPLY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.POLICRYSTALIZATOR) ||
                hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.CASCADE_REPROCESSOR)||
                hasTwoIndustriesForSynergy(AoDIndustries.CASCADE_REPROCESSOR, AoDIndustries.TRI_TACHYON_HEAVY)||
                hasTwoIndustriesForSynergy(AoDIndustries.POLICRYSTALIZATOR, AoDIndustries.HEGEMONY_HEAVY)||
                hasTwoIndustriesForSynergy(AoDIndustries.CONSUMER_INDUSTRY, AoDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoDIndustries.ARTISANAL_FARMING, AoDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoDIndustries.UNDERWORLD, AoDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.RESORT));
        ResearchAPI researchAPI = AoDUtilis.getResearchAPI();
        boolean secondArg = false;
        if(researchAPI!=null){
             secondArg = researchAPI.alreadyResearchedAmount()>=10;
        }
        return firstarg&&secondArg;

    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_industrial_might")) {
            marketAPI.addCondition("aotd_industrial_might");
        }
    }
}
