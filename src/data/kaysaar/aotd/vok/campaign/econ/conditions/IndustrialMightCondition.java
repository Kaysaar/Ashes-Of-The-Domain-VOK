package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;

public class IndustrialMightCondition extends BaseMarketConditionPlugin {


    public void apply(String id) {
        super.apply(id);
        if(market.isInEconomy()&&market.getFaction().isShowInIntelTab()){
            AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(this.market.getFaction());
            if(manager==null)AoTDMainResearchManager.getInstance().addNewFactionIfNotPresent(market.getFaction());
            manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(this.market.getFaction());
            if(manager==null)return;
            if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(this.market.getFaction()).haveResearched("aotd_tech_advanced_logistic")){
                handleBonuses();
            }
        }
        if(market.isPlayerOwned()||market.getFaction().isPlayerFaction()){
            AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(this.market.getFaction());
            if(manager==null)AoTDMainResearchManager.getInstance().addNewFactionIfNotPresent(market.getFaction());
            manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(this.market.getFaction());
            if(manager==null)return;
            if(AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched("aotd_tech_advanced_logistic")){
                handleBonuses();
            }
        }


        }




    @Override
    public void unapply(String id) {
        super.unapply(id);

    }

    public void handleBonuses() {
        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.MILITARY_HEAVY)) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                    .modifyMult("IndSynergyMiliHeavy", 1.5f, "Presence of Military Industrial Complex (Placeholder)");

        } else {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                    .unmodifyMult("IndSynergyMiliHeavy");
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.SUPPLY_HEAVY)) {
            market.getStability().modifyFlat("IndSynergyStab", 1, "Industrial Synergy");

        } else {
            market.getStability().unmodifyFlat("IndSynergyStab");
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.BENEFICATION, AoTDIndustries.POLICRYSTALIZATOR)) {
            Industry poli = market.getIndustry(AoTDIndustries.POLICRYSTALIZATOR);
            poli.getSupply(AoTDCommodities.REFINED_METAL).getQuantity().modifyFlat("IndSynergyCascade", +1, "Industry Synergy");
            poli.getUpkeep().modifyMult("Oh boi that can be broken ", 0.75f, "Industry Synergy");

        } else {
            Industry poli = market.getIndustry(AoTDIndustries.POLICRYSTALIZATOR);
            if (poli != null) {
                poli.getUpkeep().unmodifyMult("Oh boi that can be broken ");
                poli.getSupply(AoTDCommodities.REFINED_METAL).getQuantity().unmodifyFlat("IndSynergyCascade");
            }
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.SUBLIMATION, AoTDIndustries.CASCADE_REPROCESSOR)) {
            Industry cascade = market.getIndustry(AoTDIndustries.CASCADE_REPROCESSOR);
            cascade.getUpkeep().modifyMult("Oh boi that can be broken part 2", 0.75f, "Industry Synergy");
            cascade.getSupply(AoTDCommodities.PURIFIED_TRANSPLUTONICS).getQuantity().modifyFlat("IndSynergyPoli", +1, "Industry Synergy");
        } else {
            Industry cascade = market.getIndustry(AoTDIndustries.CASCADE_REPROCESSOR);
            if (cascade != null) {
                cascade.getUpkeep().unmodifyMult("Oh boi that can be broken part 2");
                cascade.getSupply(AoTDCommodities.PURIFIED_TRANSPLUTONICS).getQuantity().unmodifyFlat("IndSynergyPoli");
            }
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.CASCADE_REPROCESSOR, AoTDIndustries.ORBITAL_SKUNKWORK)) {
            Industry triHeavy = market.getIndustry(AoTDIndustries.ORBITAL_SKUNKWORK);
            for (MutableCommodityQuantity mutableCommodityQuantity : triHeavy.getAllSupply()) {
                triHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyFlat("IndSynergyTri", +1, "Industry Synergy");
            }

        } else {
            Industry triHeavy = market.getIndustry(AoTDIndustries.ORBITAL_SKUNKWORK);
            if (triHeavy != null) {
                for (MutableCommodityQuantity mutableCommodityQuantity : triHeavy.getAllSupply()) {
                    triHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyFlat("IndSynergyTri");
                }
            }
        }

        if (hasTwoIndustriesForSynergy(AoTDIndustries.POLICRYSTALIZATOR, AoTDIndustries.ORBITAL_FLEETWORK)) {
            Industry hegeHeavy = market.getIndustry(AoTDIndustries.ORBITAL_FLEETWORK);
            for (MutableCommodityQuantity mutableCommodityQuantity : hegeHeavy.getAllSupply()) {
                hegeHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyFlat("IndSynergyHege", +1, "Industry Synergy");
            }
        } else {
            Industry hegeHeavy = market.getIndustry(AoTDIndustries.ORBITAL_FLEETWORK);
            if (hegeHeavy != null) {
                for (MutableCommodityQuantity mutableCommodityQuantity : hegeHeavy.getAllSupply()) {
                    hegeHeavy.getSupply(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyFlat("IndSynergyHege");
                }
            }
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.RESORT, AoTDIndustries.CONSUMER_INDUSTRY)) {
            Industry resort = market.getIndustry(AoTDIndustries.RESORT);
            resort.getIncome().modifyMult("IndSynergyResortIncome",1.1f,"Industry Synergy");
        } else {
            Industry resort = market.getIndustry(AoTDIndustries.ORBITAL_FLEETWORK);
            if (resort != null) {
                resort.getIncome().unmodifyMult("IndSynergyResortIncome");
            }
        }

        if (hasTwoIndustriesForSynergy(AoTDIndustries.ARTISANAL_FARMING, AoTDIndustries.RESORT)) {
            Industry resort = market.getIndustry(AoTDIndustries.ARTISANAL_FARMING);
            resort.getIncome().modifyMult("IndSynergyArtiIncome",1.1f,"Industry Synergy");
        } else {
            Industry resort = market.getIndustry(AoTDIndustries.ARTISANAL_FARMING);
            if (resort != null) {
                resort.getIncome().unmodifyMult("IndSynergyArtiIncome");
            }
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.UNDERWORLD, AoTDIndustries.RESORT)) {
            market.getIncomeMult().modifyMult("IndSynergyUnderworldIncome",1.1f,"Industry Synergy");
        } else {
            market.getIncomeMult().unmodifyMult("IndSynergyUnderworldIncome");
        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.RESORT)) {
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

        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.MILITARY_HEAVY)) {
            tooltip.addPara(
                    "Terminus and Militarized Heavy Industry: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "1.5 bonus to Military garrisons"
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.SUPPLY_HEAVY)) {
            tooltip.addPara(
                    "Terminus and Civilian Heavy Production: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "+1 stability"
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.BENEFICATION, AoTDIndustries.POLICRYSTALIZATOR)) {
            tooltip.addPara(
                    "Policrystalizator and Benefication: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decreases upkeep of Policrystalizator by 25% and increases Policrystalizator's production by 1 unit."
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.SUBLIMATION, AoTDIndustries.CASCADE_REPROCESSOR)) {
            tooltip.addPara(
                    "Cascade Reprocessor and Sublimation : %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decreases upkeep of Cascade Reprocessor by 25% and increases Cascade Reprocessor's production by 1 unit."
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.CASCADE_REPROCESSOR, AoTDIndustries.ORBITAL_SKUNKWORK)) {
            tooltip.addPara(
                    "Cascade Reprocessor and Orbital Skunkwork Facility: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases Orbital Skunkwork Facility production by 1 unit."
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.POLICRYSTALIZATOR, AoTDIndustries.ORBITAL_FLEETWORK)) {
            tooltip.addPara(
                    "Policrystalizator and Orbital Fleetwork Facility: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases Orbital Fleetwork Facility production by 1 unit."
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.CONSUMER_INDUSTRY, AoTDIndustries.RESORT)) {
            tooltip.addPara(
                    "Consumer Industry and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases Resort income by 10%"
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.ARTISANAL_FARMING, AoTDIndustries.RESORT)) {
            tooltip.addPara(
                    "Artisanal Farming and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases Artisanal Farming's income by 10%"
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.UNDERWORLD, AoTDIndustries.RESORT)) {
            tooltip.addPara(
                    "Underworld and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases planet income by 5% "
            );

        }
        if (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.RESORT)) {
            tooltip.addPara(
                    "Terminus and Resort Center: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Increases planet accessibility by 10% "
            );

        }

    }

    public String getModId() {
        return condition.getId();
    }

    @Override
    public boolean showIcon() {
        boolean firstarg = (hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.MILITARY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.SUPPLY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoTDIndustries.BENEFICATION, AoTDIndustries.POLICRYSTALIZATOR) ||
                hasTwoIndustriesForSynergy(AoTDIndustries.SUBLIMATION, AoTDIndustries.CASCADE_REPROCESSOR)||
                hasTwoIndustriesForSynergy(AoTDIndustries.CASCADE_REPROCESSOR, AoTDIndustries.ORBITAL_SKUNKWORK)||
                hasTwoIndustriesForSynergy(AoTDIndustries.POLICRYSTALIZATOR, AoTDIndustries.ORBITAL_FLEETWORK)||
                hasTwoIndustriesForSynergy(AoTDIndustries.CONSUMER_INDUSTRY, AoTDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoTDIndustries.ARTISANAL_FARMING, AoTDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoTDIndustries.UNDERWORLD, AoTDIndustries.RESORT)||
                hasTwoIndustriesForSynergy(AoTDIndustries.TERMINUS, AoTDIndustries.RESORT));
//
        return firstarg&& AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched("aotd_tech_advanced_logistic");

    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("industrial_might")) {
            marketAPI.addCondition("industrial_might");
        }
    }
}
