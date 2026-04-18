package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.IconRenderMode;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderTypeManager;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TierFourStation extends OrbitalStation implements GrandWonderAPI {
    /// TODO - Implement this later down the line as Grand Wonder

    public static LinkedHashMap<String, Integer> costMap = new LinkedHashMap<>();

    static {
        costMap.put(AoTDCommodities.REFINED_METAL, 10);
        costMap.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS, 5);
        costMap.put(AoTDCommodities.ADVANCED_COMPONENTS, 5);
        costMap.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY, 3);

    }

    @Override
    public String getCurrentName() {
        return super.getCurrentName();
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
    public void advance(float amount) {
        boolean disrupted = isDisrupted();
        if (!disrupted && wasDisrupted) {
            disruptionFinished();
        }
        wasDisrupted = disrupted;

//		if (disrupted) {
//			//if (DebugFlags.COLONY_DEBUG) {
//				String key = getDisruptedKey();
//				market.getMemoryWithoutUpdate().unset(key);
//			//}
//		}

        if (building && !disrupted) {
            float days = Global.getSector().getClock().convertToDays(amount);
            if (isUpgrading()) {
//                days *= GPManager.getInstance().getTotalPenaltyFromResources(AoTDCommodities.PURIFIED_TRANSPLUTONICS, AoTDCommodities.DOMAIN_GRADE_MACHINERY, AoTDCommodities.ADVANCED_COMPONENTS, AoTDCommodities.REFINED_METAL);
            }
            //DebugFlags.COLONY_DEBUG = true;
            if (DebugFlags.COLONY_DEBUG) {
                days *= 100f;
            }
            buildProgress += days;

            if (buildProgress >= buildTime) {
                finishBuildingOrUpgrading();
            }
        }

        if (Global.getSector().getEconomy().isSimMode()) return;


        if (stationEntity == null) {
            spawnStation();
        }

        if (stationFleet != null) {
            stationFleet.setAI(null);
            if (stationFleet.getOrbit() == null && stationEntity != null) {
                stationFleet.setCircularOrbit(stationEntity, 0, 0, 100);
            }
            if (this.getSpec().hasTag("starcitadel")) {
                stationFleet.getMemoryWithoutUpdate().set(Misc.DANGER_LEVEL_OVERRIDE, 10);
            }
        }

    }
    @Override
    public boolean isAvailableToBuild() {
        if (this.getSpec().hasTag("starcitadel")) {
            if (market.isPlayerOwned()) {
                return Global.getSector().getPlayerFaction().knowsIndustry(this.getSpec().getId());
            }
        }
        return super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        if (this.getSpec().hasTag("starcitadel")) {
            return false;
        }
        return super.showWhenUnavailable();
    }

    @Override
    public LinkedHashMap<String, Integer> getDemandCostForRestoration() {
        return costMap;
    }

    @Override
    public void finishedConstruction(MarketAPI marketAPI) {
        Industry toRemove = null;
        for (Industry industry : marketAPI.getIndustries()) {
            if(industry instanceof OrbitalStation station){
                toRemove  = industry;
                break;

            }
        }
        if(toRemove != null){
            this.setAICoreId(toRemove.getAICoreId());
            this.setImproved(toRemove.isImproved());
            this.setSpecialItem(toRemove.getSpecialItem());
            market.removeIndustry(toRemove.getId(), MarketAPI.MarketInteractionMode.REMOTE,false);
        }
    }

    @Override
    public String getWonderTypeId() {
        return "space_defence_station";
    }

    @Override
    public void addToCustomSectionInTooltip(TooltipMakerAPI tooltipMakerAPI) {

    }

    @Override
    public LinkedHashMap<String, String> getRequirementsToBuildWonder() {
        return new LinkedHashMap<>();
    }

    @Override
    public boolean hasReqBeenMetOnMarket(String s) {
        return true;
    }

    @Override
    public LinkedHashSet<String> getIndustriesToPreventFromAppearingInMenu(MarketAPI marketAPI) {
        LinkedHashSet<String> industriesToPreventFromAppearingInMenu = new LinkedHashSet<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if(allIndustrySpec.hasTag(Industries.TAG_STATION))industriesToPreventFromAppearingInMenu.add(allIndustrySpec.getId());
        }
        return industriesToPreventFromAppearingInMenu;
    }

    @Override
    public boolean shouldShowInListOfWonders(MarketAPI marketAPI) {
        return marketAPI.getFaction().knowsIndustry(this.getSpec().getId())&& GrandWonderTypeManager.getSpec(getWonderTypeId()).canBuildAdditionalWonderOfType(this.getSpec().getId(), marketAPI);
    }
}
