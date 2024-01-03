package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class CoronalSegment extends BaseIndustry {
    public boolean canProgress = true;
    public boolean haveCompletedRestoration = false;

    @Override
    public void apply() {

        super.apply(true);
        if(isBuilding()){
            applyDemandForRepair();
            applyUpkeepWithRebuilding();
        }

        else if (!haveCompletedRestoration){
            applyDemandBeforeRepair();
        }
        else{
            applyDemandAfterRepair();
            unapplyUpkeepWithRebuilding();
        }
        canProgress=true;
        for (Pair<String, Integer> stringIntegerPair : this.getAllDeficit()) {
            if(this.getDemand(stringIntegerPair.one).getQuantity().getModifiedValue()>0){
                if(stringIntegerPair.two>0){
                    canProgress = false;
                    break;
                }
            }
        }


    }
    public void applyUpkeepWithRebuilding(){

    }
    public void unapplyUpkeepWithRebuilding(){

    }
    @Override
    public boolean canInstallAICores() {
        return false;
    }

    public Pair<String, Color> getCurrentStatusString(){
        Pair<String,Color> status = new Pair<>();
        status.two=Misc.getNegativeHighlightColor();
        status.one="Damaged";
        return status;
    }
    public boolean isWorking(){
        boolean isFunc = true;
        for (Pair<String, Integer> stringIntegerPair : this.getAllDeficit()) {
            if(this.getDemand(stringIntegerPair.one).getQuantity().getModifiedValue()>0){
                if(stringIntegerPair.two>0){
                    isFunc = false;
                    break;
                }
            }
        }
        return isFunc&&haveCompletedRestoration;
    }
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip){

    }
    public void applyDemandForRepair(){

    }
    public void applyDemandAfterRepair(){

    }
    public void applyDemandBeforeRepair(){

    }
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

        if (building && !disrupted&&canProgress) {
            float days = Global.getSector().getClock().convertToDays(amount);
            //DebugFlags.COLONY_DEBUG = true;
            if (DebugFlags.COLONY_DEBUG) {
                days *= 50f;
            }
            if(market.hasIndustry("coronal_drones")&&((CoronalSegment)market.getIndustry("coronal_drones")).haveCompletedRestoration){
                days*=2;
            }
            buildProgress += days;

            if (buildProgress >= buildTime) {
                finishBuildingOrUpgrading();
            }
        }

    }
    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }
    @Override
    public void finishBuildingOrUpgrading() {
        building = false;
        buildProgress = 0;
        buildTime = 1f;
        haveCompletedRestoration = true;
        buildingFinished();
        reapply();
    }

    @Override
    public String getBuildOrUpgradeProgressText() {
        int left = (int) (buildTime - buildProgress);
        if (left < 1) left = 1;
        String days = "days";
        if (left == 1) days = "day";

//		if (isBuilding() && !isUpgrading()) {
//			//return left + " " + days;
//			return "building: " + (int)Math.round(buildProgress / buildTime * 100f) + "%";
//		}
        return "Restoring: " + left + " " + days + " left";

    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        tooltip.addSectionHeading("Current Status of  "+this.getCurrentName(), Alignment.MID,10f);
        if(!haveCompletedRestoration&&!isBuilding()){
            tooltip.addPara("Currently "+this.getCurrentName()+" is damaged, and requires necessary repairs first.", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        }
        if(!haveCompletedRestoration&&isBuilding()){
            tooltip.addPara("Currently "+this.getCurrentName()+" is damaged, but restoration project has began to repair it.", Misc.getTooltipTitleAndLightHighlightColor(),10f);
            if(!canProgress){
                tooltip.addPara("Can't progress with restoration due to lack of resources!",Misc.getNegativeHighlightColor(),10f);
            }
        }
        if(haveCompletedRestoration){
            tooltip.addPara(this.getCurrentName()+ " is fully operational!", Misc.getTooltipTitleAndLightHighlightColor(),10f);

        }
        addSegmentEffect(tooltip);

    }
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI){
        tooltipMakerAPI.addPara("None",10f);
    }
}
