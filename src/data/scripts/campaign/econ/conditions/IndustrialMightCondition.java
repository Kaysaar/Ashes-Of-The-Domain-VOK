package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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
            if(researchAPI.alreadyResearchedAmount()>=7){
                Global.getSector().getMemory().set("$aotd_can_scientist",true);
            }
        }


    }

    @Override
    public void unapply(String id) {
        handleBonuses();
        super.unapply(id);

    }


    //SPAGETTTHHHHIIII


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
        if (hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.POLICRYSTALIZATOR)) {
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
        if (hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.CASCADE_REPROCESSOR)) {
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
        if (hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.POLICRYSTALIZATOR)) {
            tooltip.addPara(
                    "Policrystalizator and Sublimation: %s",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decrease upkeep of Policrystalizator by 25% and +1 Production to Policrystalizator"
            );

        }
        if (hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.CASCADE_REPROCESSOR)) {
            tooltip.addPara(
                    "Policrystalizator and Benefication: %s ",
                    10f,
                    Misc.getStoryBrightColor(),
                    "Decrease upkeep of Cascade Reprocessor by 25% +1 Production to Cascade Reprocessor"
            );

        }

    }

    public String getModId() {
        return condition.getId();
    }

    @Override
    public boolean showIcon() {
        return hasTwoIndustriesForSynergy(AoDIndustries.POLICRYSTALIZATOR, AoDIndustries.CASCADE_REPROCESSOR) ||
                hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.MILITARY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoDIndustries.TERMINUS, AoDIndustries.SUPPLY_HEAVY) ||
                hasTwoIndustriesForSynergy(AoDIndustries.SUBLIMATION, AoDIndustries.POLICRYSTALIZATOR) ||
                hasTwoIndustriesForSynergy(AoDIndustries.BENEFICATION, AoDIndustries.CASCADE_REPROCESSOR);
    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_industrial_might")) {
            marketAPI.addCondition("aotd_industrial_might");
        }
    }
}
