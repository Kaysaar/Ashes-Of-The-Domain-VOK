package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.util.LinkedHashMap;

public class TierFourStation extends OrbitalStation {
    public static LinkedHashMap<String,Integer>costMap = new LinkedHashMap<>();
    static {
        costMap.put(AoTDCommodities.REFINED_METAL,150);
        costMap.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,50);
        costMap.put(AoTDCommodities.ADVANCED_COMPONENTS,100);
        costMap.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,150);
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
            if(isUpgrading()){
            days*= GPManager.getInstance().getTotalPenaltyFromResources(AoTDCommodities.PURIFIED_TRANSPLUTONICS,AoTDCommodities.DOMAIN_GRADE_MACHINERY,AoTDCommodities.ADVANCED_COMPONENTS,AoTDCommodities.REFINED_METAL);
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
            if(this.getSpec().hasTag("starcitadel")){
                stationFleet.getMemoryWithoutUpdate().set(Misc.DANGER_LEVEL_OVERRIDE,10);
            }
        }

    }

    public String getBuildOrUpgradeProgressText() {
//		float f = buildProgress / spec.getBuildTime();
//		return "" + (int) Math.round(f * 100f) + "%";
        if (isUpgrading()) {
            //return "" + (int) Math.round(Misc.getMarketSizeProgress(market) * 100f) + "%";
            return "Upgrade Progress : " + Misc.getRoundedValue(getBuildOrUpgradeProgress() * 100f) + "%";
        }

        return super.getBuildOrUpgradeProgressText();
    }

    @Override
    protected void addPostUpkeepSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        super.addPostUpkeepSection(tooltip, mode);
        if(mode.equals(IndustryTooltipMode.NORMAL)&&isUpgrading()){
            tooltip.addSectionHeading("Sophisticated Scaffolding (Global Production)", Alignment.MID,5f);
            tooltip.addPara("Due to intensive work required with upgrade of this station, massive resources are needed for it's construction.",Misc.getTooltipTitleAndLightHighlightColor(),3f);
            tooltip.addCustom(GPUIMisc.createResourcePanelForSmallTooltip(tooltip.getWidthSoFar()+10,30,30,costMap,null),5f);
        }
        if(mode.equals(IndustryTooltipMode.UPGRADE)){
            tooltip.addSectionHeading("Sophisticated Scaffolding (Global Production)", Alignment.MID,5f);
            tooltip.addPara("Due to intensive work required with upgrade of this station, massive resources are needed for it's construction.",Misc.getTooltipTitleAndLightHighlightColor(),3f);
            tooltip.addCustom(GPUIMisc.createResourcePanelForSmallTooltip(tooltip.getWidthSoFar()+10,30,30,costMap,null),5f);
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        if(this.getSpec().hasTag("starcitadel")){
            return market.getFaction().knowsIndustry(this.getSpec().getId());
        }
        return super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        if(this.getSpec().hasTag("starcitadel")){
            return false;
        }
        return super.showWhenUnavailable();
    }

}
