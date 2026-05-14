package data.kaysaar.aotd.vok.campaign.econ.industry.grandwonders;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeManager;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.industry.ResortCenter;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.buildingmenu.BuildingMenuMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static data.kaysaar.aotd.tot.conditions.AoTDToolboxFoodProd.prodId;
import static data.kaysaar.aotd.vok.campaign.econ.industry.SubsidisedFarming.FARMING_CONDITIONS;

public class GardensOfElysium extends ResortCenter implements GrandWonderAPI {
    @Override
    public LinkedHashMap<String, Integer> getDemandCostForRestoration() {
        LinkedHashMap<String, Integer> demand = new LinkedHashMap<>();
        demand.put(Commodities.METALS, 10);
        demand.put(AoTDCommodities.REFINED_METAL, 5);
        demand.put(Commodities.HEAVY_MACHINERY, 10);
        demand.put(Commodities.SUPPLIES, 5);
        return demand;
    }

    public float daysLeftForBuff = 0f;

    public void setDaysLeftForBuff(float daysLeftForBuff) {
        this.daysLeftForBuff = daysLeftForBuff;
    }


    @Override
    public void finishedConstruction(MarketAPI marketAPI) {
        IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.MONOCULTURE).forEach(x -> marketAPI.removeIndustry(x, MarketAPI.MarketInteractionMode.REMOTE, true));
        IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESORT).forEach(x -> marketAPI.removeIndustry(x, MarketAPI.MarketInteractionMode.REMOTE, true));

    }


    @Override
    public String getWonderTypeId() {
        return "biosphere";
    }

    @Override
    public boolean isAvailableToBuild() {
        if (GrandWonderTypeManager.getSpec(getWonderTypeId()).canBuildAdditionalWonderOfType(this.getSpec().getId(), this.market)) {
            for (String s : getRequirementsToBuildWonder().keySet()) {
                if (!hasReqBeenMetOnMarket(s)) {
                    return false;
                }
            }
            return true;
        } else return false;
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }

    @Override
    public void addToCustomSectionInTooltip(TooltipMakerAPI tooltipMakerAPI) {

    }

    @Override
    public LinkedHashMap<String, String> getRequirementsToBuildWonder() {
        LinkedHashMap<String, String> requirements = new LinkedHashMap<>();
        requirements.put("first", "Market must have bountiful farmland.");
        requirements.put("second", "Market must be habitable.");
        requirements.put("third", "Market can't be polluted.");
        return requirements;
    }

    @Override
    public boolean hasReqBeenMetOnMarket(String s) {
        if (s.equals("first")) {
            return market.hasCondition(Conditions.FARMLAND_BOUNTIFUL);
        }
        if (s.equals("second")) {
            return market.hasCondition(Conditions.HABITABLE);
        }
        if (s.equals("third")) {
            return !market.hasCondition(Conditions.POLLUTION);
        }
        return true;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(daysLeftForBuff>0){
            daysLeftForBuff -= Global.getSector().getClock().convertToDays(amount);
            for (FleetMemberAPI memberAPI : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                if(memberAPI.isFighterWing())continue;
                if(Misc.isAutomated(memberAPI))continue;
                memberAPI.getStats().getMaxCombatReadiness().modifyFlatAlways("aotd_elysian",0.2f,"Elysian Wonders");
            }
        }
        else{
            for (FleetMemberAPI memberAPI : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                if(memberAPI.isFighterWing())continue;
                if(Misc.isAutomated(memberAPI))continue;
                memberAPI.getStats().getMaxCombatReadiness().unmodify("aotd_elysian");
        }
            }
    }

    @Override
    public LinkedHashSet<String> getIndustriesToPreventFromAppearingInMenu(MarketAPI marketAPI) {
        LinkedHashSet<String> industries = new LinkedHashSet<>();
        industries.add(AoTDIndustries.MONOCULTURE);
        industries.add(AoTDIndustries.RESORT);
        return industries;
    }

    @Override
    public boolean shouldShowInListOfWonders(MarketAPI marketAPI) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket( AoTDTechIds.ELYSIAN_PROJECT,marketAPI)&&GrandWonderTypeManager.getSpec(getWonderTypeId()).canBuildAdditionalWonderOfType(this.getSpec().getId(), marketAPI);
    }

    @Override
    public void apply() {
        super.apply();
        int quantity = market.getSize() + 10;
        supply(Commodities.FOOD, quantity);
        supply(Commodities.LUXURY_GOODS, quantity - 2);
        demand(Commodities.HEAVY_MACHINERY, market.getSize() + 2);

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY);
        //applyDeficitToProduction(0, deficit, Commodities.FOOD, Commodities.ORGANICS);
        applyDeficitToProduction(2, deficit, Commodities.FOOD, Commodities.LUXURY_GOODS);
    }

    @Override
    protected void addPostUpkeepSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Bliss of the Garden", Alignment.MID, 5f);
        tooltip.addPara(
                "While visiting a market, you can take your crew to the Garden for a day of rest, boosting combat readiness for %s days.",
                5f,
                Color.ORANGE,
                "365"
        );

        tooltip.addPara(
                "Additionally, officers gain a one-time +1 level bonus after visiting the Garden.",
                3f
        );
        if (IndustryTooltipMode.ADD_INDUSTRY.equals(mode)) {
            tooltip.addSectionHeading("Industries that will be removed upon construction", Alignment.MID, 5f);
            ArrayList<String> ids = new ArrayList<>(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.MONOCULTURE));
            ids.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.RESORT));
            tooltip.addPara("%s",5f,Color.ORANGE,IndustrySynergiesMisc.getIndustriesListed(ids,market));

        }
    }
}
