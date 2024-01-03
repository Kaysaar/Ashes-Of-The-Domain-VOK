package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;

public class AegisSystem extends CoronalSegment{

    @Override
    public void apply() {
        super.apply();
        if(market.hasIndustry("starfortress_high")){
            for (MutableCommodityQuantity starfortressHigh : market.getIndustry("starfortress_high").getAllDemand()) {
                this.demand.put(starfortressHigh.getCommodityId(),starfortressHigh);
            }
            for (MutableCommodityQuantity starfortressHigh : market.getIndustry("starfortress_high").getAllSupply()) {
                this.supply.put(starfortressHigh.getCommodityId(),starfortressHigh);
            }
        }
    }

    @Override
    public void finishBuildingOrUpgrading() {
        super.finishBuildingOrUpgrading();
        if(!market.hasIndustry("starfortress_high")){
            market.addIndustry("starfortress_high");
            market.getIndustry("starfortress_high").setHidden(true);
        }
    }
    @Override
    public void createTooltipInfoForOption(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Repair Cost ", Alignment.MID,0f);
        tooltip.addPara("Repair will require constant supply of 8 units of Metal and 6 units of Supplies for "+this.getBuildTime()+" days", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addSectionHeading("Aegis System : Effect after repair",Alignment.MID,10f);
        tooltip.addPara("Re-awakens defence systems of Hypershunt",Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public void addSegmentEffect(TooltipMakerAPI tooltipMakerAPI) {
        if(haveCompletedRestoration){
            tooltipMakerAPI.addPara(this.getCurrentName()+" : %s",10f,Misc.getPositiveHighlightColor(),""+"provides for Hypershunt protection with massive battlestation.");
        }
    }

    @Override
    public void applyUpkeepWithRebuilding() {
        this.getUpkeep().modifyFlat("rebuilding",80000);
    }

    @Override
    public void unapplyUpkeepWithRebuilding() {
        this.getUpkeep().unmodifyFlat("rebuilding");
    }

    @Override
        public Pair<String, Color> getCurrentStatusString() {
            Pair<String,Color> status = super.getCurrentStatusString();
            if(haveCompletedRestoration&&this.isWorking()){
                status.one="All systems functional";
                status.two = Misc.getPositiveHighlightColor();
            }
            if(!haveCompletedRestoration&&isBuilding()&&canProgress){
                status.one="Restoration in progress";
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


