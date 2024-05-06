package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalSegment;

import java.awt.*;

public class PlasmaCollector extends CoronalSegment{
    float currentDistanceoOfOperation;
    @Override
    public void apply() {
        super.apply();
        if(isWorking()){
            currentDistanceoOfOperation = 10;
            market.getPrimaryEntity().getMemory().set("$usable",true);

        }
        else{
            currentDistanceoOfOperation = 0;
            if(market.getPrimaryEntity()!=null){
                market.getPrimaryEntity().getMemory().unset("$usable");
            }

        }

        Industry ind = this.getMarket().getIndustry("coronal_wormhole");
        if(ind instanceof CoronalSegment){
            if(((CoronalSegment) ind).haveCompletedRestoration&& ((CoronalSegment) ind).isWorking()){
                currentDistanceoOfOperation = 50;
            }
        }
        if(this.isWorking()){
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFaction())) {
                if(factionMarket.hasTag("aotd_hypershunt")) continue;

            }
        }
    }
    @Override
    public void applyUpkeepWithRebuilding() {
        this.getUpkeep().modifyFlat("rebuilding",380000);
    }

    @Override
    public void unapplyUpkeepWithRebuilding() {
        this.getUpkeep().unmodifyFlat("rebuilding");
    }
    @Override
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Repair Cost ", Alignment.MID,0f);
        tooltip.addPara("Repair will require constant supply of 9 units of Refined Metal, 6 units of Metal and 7 units of Polymers for "+this.getBuildTime()+" days", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addSectionHeading("Plasma Collector: Effect after repair",Alignment.MID,10f);
        tooltip.addPara("Once repaired it adds 1 industry slot and increases production of all colonies with functional Coronal Pylon within 10 Light Years by 2 units.",Misc.getPositiveHighlightColor(),10f);
        tooltip.addPara("Once Wormhole Stabilizer is repaired range will increase to 50 Light years.",Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public void applyDemandForRepair() {
        super.applyDemandForRepair();
        demand(AoTDCommodities.REFINED_METAL,10);
        demand(AoTDCommodities.POLYMERS,7);
        demand(Commodities.METALS,6);
    }

    @Override
    public void applyDemandAfterRepair() {
        super.applyDemandAfterRepair();
        demand(AoTDCommodities.REFINED_METAL,0);
        demand(AoTDCommodities.POLYMERS,0);
        demand(Commodities.METALS,0);
    }

    @Override
    public Pair<String, Color> getCurrentStatusString() {
        Pair<String,Color> status = super.getCurrentStatusString();
        if(haveCompletedRestoration&&this.isWorking()){
            status.one="Collector functional. Ready to harvest energy.";
            status.two = Misc.getPositiveHighlightColor();
        }
        if(haveCompletedRestoration&&!this.isWorking()){
            status.one="Collector's demands not met. Harvest of energy stopped.";
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
            status.one="Heavily damaged.";
            status.two = Misc.getNegativeHighlightColor();
        }
        return status;
    }
    @Override
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI){
        if(haveCompletedRestoration&&isWorking()){
            tooltipMakerAPI.addPara("Plasma Collector : %s",10f,Misc.getPositiveHighlightColor(),""+"Adds 1 industry slot and and increases production of all colonies within 10 LY with functional Coronal Pylon structure by 2 units.");

        }
        if(haveCompletedRestoration&&!isWorking()){
            tooltipMakerAPI.addPara("Plasma Collector : %s",10f,Misc.getPositiveHighlightColor(),""+"Adds 1 industry slot and and increases production of all colonies within 10 LY with functional Coronal Pylon structure by 2 units.");
            tooltipMakerAPI.addPara("Not working due to lack of resources.",Misc.getNegativeHighlightColor(),2f);

        }
    }
}

