package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;

import java.awt.*;

public class WormholeStabilizer extends CoronalSegment{

    @Override
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Repair Cost ", Alignment.MID,0f);
        tooltip.addPara("Repair will require constant supply of 9 units of Refined Metal, 6 units of Metal and 6 units of Polymers for "+this.getBuildTime()+" days", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addSectionHeading("Plasma Collector: Effect after repair",Alignment.MID,10f);
        tooltip.addPara("Once repaired it adds 1 industry slot and increases production of all colonies with functional Coronal Pylon within 10 Light Years by 2 units.",Misc.getPositiveHighlightColor(),10f);
        tooltip.addPara("Once Wormhole Stabilizer is repaired range will increase to 50 Light years.",Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public void applyDemandForRepair() {
        super.applyDemandForRepair();
        demand(AoTDCommodities.REFINED_METAL,10);
        demand(Commodities.METALS,8);
        demand(Commodities.RARE_METALS,6);
    }
    @Override
    public void applyUpkeepWithRebuilding() {
       this.getUpkeep().modifyFlat("rebuilding",280000);
    }

    @Override
    public void unapplyUpkeepWithRebuilding() {
        this.getUpkeep().unmodifyFlat("rebuilding");
    }
    @Override
    public void applyDemandAfterRepair() {
        super.applyDemandAfterRepair();
        demand(AoTDCommodities.REFINED_METAL,0);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS,6);
        demand(Commodities.METALS,0);
        demand(Commodities.RARE_METALS,0);
    }

    @Override
    public Pair<String, Color> getCurrentStatusString() {
        Pair<String,Color> status = super.getCurrentStatusString();
        if(haveCompletedRestoration&&this.isWorking()){
            status.one="Stabilizer functional. Ready to transfer energy.";
            status.two = Misc.getPositiveHighlightColor();
        }
        if(haveCompletedRestoration&&!this.isWorking()){
            status.one="Stabilizer's demands not met. Transfer of energy stopped.";
            status.two = Misc.getPositiveHighlightColor();
        }
        if(!haveCompletedRestoration&&isBuilding()&&canProgress){
            status.one="Restoration in progress.";
            status.two = Misc.getBrightPlayerColor();
        }
        if(!haveCompletedRestoration&&isBuilding()&&!canProgress){
            status.one="Restoration halted!";
            status.two = Misc.getBrightPlayerColor();
        }
        if(!haveCompletedRestoration&&!isBuilding()){
            status.one="Heavily damaged. Systems are offline.";
            status.two = Misc.getNegativeHighlightColor();
        }
        return status;
    }

    @Override
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI){
        if(haveCompletedRestoration&&isWorking()){
            tooltipMakerAPI.addPara("Wormhole Stabilizer : %s",10f,Misc.getPositiveHighlightColor(),""+"extends effective range of Plasma Collector to 50 light years");

        }
        if(haveCompletedRestoration&&!isWorking()){
            tooltipMakerAPI.addPara("Wormhole Stabilize : %s",10f,Misc.getPositiveHighlightColor(),""+"extends effective range of Plasma Collector to 50 light years");
            tooltipMakerAPI.addPara("Not working due to lack of resources.",Misc.getNegativeHighlightColor(),2f);

        }
    }

}
