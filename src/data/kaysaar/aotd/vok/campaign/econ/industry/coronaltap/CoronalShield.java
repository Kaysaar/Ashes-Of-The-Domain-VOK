package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;

import java.awt.*;

public class CoronalShield extends CoronalSegment{
    float firstDays = 365;
    float started = 0;
    float gatheredResourcesFor = 0;
    float maxGatheredResourcesFor = 360;

    @Override
    public void apply() {

        super.apply();

    }

    @Override
    public void applyDemandForRepair() {
        demand(AoTDCommodities.REFINED_METAL,5);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS,6);

    }

    @Override
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Repair Cost ", Alignment.MID,0f);
        tooltip.addPara("Repair will require constant supply of 5 units of Refined Metal and 6 of Purified Transplutonics for "+this.getBuildTime()+" days", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addSectionHeading("Coronal Shielding : Effect after repair",Alignment.MID,10f);
        tooltip.addPara("Prevents shield failure as long as demand is met, protects crew and entire structure from star's heat",Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public void applyUpkeepWithRebuilding() {
        this.getUpkeep().modifyFlat("rebuilding",150000);
    }

    @Override
    public void unapplyUpkeepWithRebuilding() {
        this.getUpkeep().unmodifyFlat("rebuilding");
    }
    @Override
    public void applyDemandAfterRepair() {
        demand(AoTDCommodities.REFINED_METAL,0);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS,5);
        demand(AoTDCommodities.COMPOUNDS,5);
    }

    @Override
    public void advance(float amount) {
        if(started>=firstDays){
            market.getPrimaryEntity().getTags().add(Tags.SALVAGEABLE);
            market.getPrimaryEntity().getTags().remove(Tags.STATION);
            DecivTracker.decivilize(this.getMarket(),true,true);
            Misc.fadeAndExpire(market.getPrimaryEntity(),1);
            return;
        }
        super.advance(amount);
        if(!haveCompletedRestoration||!isWorking()){
            started+= Global.getSector().getClock().convertToDays(amount);
        }
        else{
            if(haveCompletedRestoration&&isWorking()){
                if(gatheredResourcesFor<maxGatheredResourcesFor){
                    gatheredResourcesFor+=Global.getSector().getClock().convertToDays(amount);
                }
            }
            if(!isWorking()){
                if(gatheredResourcesFor!=0){
                    started-=gatheredResourcesFor;
                    gatheredResourcesFor=0;
                }
                if(started<0){
                    started=0;
                }

            }
        }
    }
    @Override
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI) {
        if(haveCompletedRestoration&&isWorking()){
            tooltipMakerAPI.addPara("Coronal Shielding : %s",10f,Misc.getPositiveHighlightColor(),""+"protects Hypershunt from star's heat.");
        }
        if(haveCompletedRestoration&&!isWorking()){
            tooltipMakerAPI.addPara("Coronal Shielding : %s",10f,Misc.getPositiveHighlightColor(),""+"protects Hypershunt from star's heat.");
        }
        if(!isWorking()){
            tooltipMakerAPI.addPara("Currently running on emergency power!",Misc.getNegativeHighlightColor(),10f);

        }
    }

    @Override
    public Pair<String, Color> getCurrentStatusString() {
        Pair<String,Color> status = super.getCurrentStatusString();
        String danger = ((int)(firstDays-started))+" days before shield stops working";
        if(haveCompletedRestoration&&this.isWorking()){
            status.one="Shield functional ";
            status.two = Misc.getPositiveHighlightColor();
        }
        if(haveCompletedRestoration&&!this.isWorking()){
            status.one="Emergency Protocols activated "+danger;
            status.two = Misc.getPositiveHighlightColor();
        }
        if(!haveCompletedRestoration&&isBuilding()&&canProgress){
            status.one="Restoration in progress" +danger ;
            status.two = Misc.getBrightPlayerColor();
        }
        if(!haveCompletedRestoration&&isBuilding()&&!canProgress){
            status.one="Restoration halted! "+danger;
            status.two = Misc.getBrightPlayerColor();
        }
        if(!haveCompletedRestoration&&!isBuilding()){
            if(started<firstDays-10) {
                status.one = "Reactor not functional!!!, running on emergency power "+danger;
            }
            else{
                status.one = "Shield is about to fail!. Evacuate outpost NOW!";
            }
            status.two = Misc.getNegativeHighlightColor();
        }
        return status;
    }
}

