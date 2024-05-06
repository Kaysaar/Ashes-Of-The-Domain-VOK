package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalSegment;

import java.awt.*;

public class DroneHub extends CoronalSegment {
    @Override
    public void apply() {
        super.apply();
       int def =  getMaxDeficit(AoTDCommodities.REFINED_METAL).two;
       float bonus  = 2.5f;
       bonus-= 0.5 * def;
       if(haveCompletedRestoration){
           Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).modifyMult("aotd_coronal", bonus, "Repaired Drone Mega-Assembly");

       }
    }

    @Override
    public void unapply() {
        super.unapply();
        Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).unmodifyMult("aotd_coronal");

    }
    @Override
    public void applyUpkeepWithRebuilding() {
        this.getUpkeep().modifyFlat("rebuilding",420000);
    }

    @Override
    public void unapplyUpkeepWithRebuilding() {
        this.getUpkeep().unmodifyFlat("rebuilding");
    }
    @Override
    public void applyDemandForRepair() {
        demand(AoTDCommodities.REFINED_METAL,10);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS,5);
    }

    @Override
    public void applyDemandAfterRepair() {
        demand(AoTDCommodities.REFINED_METAL,6);
    }

    @Override
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Repair Cost ", Alignment.MID,0f);
        tooltip.addPara("Repair will require constant supply of 10 units of Refined Metal and 5 units of Purified Transplutonics for "+this.getBuildTime()+" days", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addSectionHeading("Drone Mega-Assembly : Effect after repair",Alignment.MID,10f);
        tooltip.addPara("Allows for repairing other modules 50% faster.",Misc.getPositiveHighlightColor(),10f);
        tooltip.addPara("Provides 2 times multiplier for ship production.",Misc.getPositiveHighlightColor(),10f);
    }

    @Override
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI) {
        if(haveCompletedRestoration&&isWorking()){
            tooltipMakerAPI.addPara(this.getCurrentName()+" : %s",10f,Misc.getPositiveHighlightColor(),""+"50% speed towards restoration of other Hypershunt segments.");
            tooltipMakerAPI.addPara("%s",10f,Misc.getPositiveHighlightColor(),""+"2 times multiplier towards ship production.");
        }
    }

    @Override
    public Pair<String, Color> getCurrentStatusString() {
        Pair<String,Color> status = super.getCurrentStatusString();
        if(haveCompletedRestoration&&this.isWorking()){
            status.one="All systems functional.";
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
            status.one="Slightly damaged. Systems are dormant.";
            status.two = Misc.getNegativeHighlightColor();
        }
        return status;
    }
}
