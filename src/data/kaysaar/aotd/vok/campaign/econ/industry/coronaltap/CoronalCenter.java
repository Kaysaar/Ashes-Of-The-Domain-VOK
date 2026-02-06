package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class CoronalCenter extends BaseIndustry {
    public void setHavingRestorationProject(boolean havingRestorationProject) {
        isHavingRestorationProject = havingRestorationProject;
    }

    public boolean isHavingRestorationProject = false;

    public boolean isHavingRestorationProject() {
        return isHavingRestorationProject;
    }

    public boolean tenLY = false;

    public boolean extendedRange = false;
    @Override
    public void apply() {
        for (Industry industry : market.getIndustries()) {
            if (industry instanceof PlasmaCollector) {
                tenLY = ((CoronalSegment) industry).isWorking();
            }
            if (industry instanceof WormholeStabilizer) {
                extendedRange = ((CoronalSegment) industry).isWorking()&&tenLY;
            }
        }
        super.apply(true);
        for (MutableCommodityQuantity mutableCommodityQuantity : market.getIndustry(Industries.POPULATION).getAllDemand()) {
            this.demand.put(mutableCommodityQuantity.getCommodityId(), mutableCommodityQuantity);
        }
        for (MutableCommodityQuantity mutableCommodityQuantity : market.getIndustry(Industries.POPULATION).getAllSupply()) {
            this.supply.put(mutableCommodityQuantity.getCommodityId(), mutableCommodityQuantity);
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
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        tooltip.addSectionHeading("Megastructure State", Alignment.MID, 10f);
        boolean fixedAll = true;
        for (Industry industry : market.getIndustries()) {
            if (industry instanceof CoronalSegment) {
                if(!((CoronalSegment) industry).haveCompletedRestoration){
                    fixedAll=false;
                }
                tooltip.addPara(industry.getCurrentName() + " : %s", 10f,((CoronalSegment) industry).getCurrentStatusString().two, "" + ((CoronalSegment) industry).getCurrentStatusString().one);
            }
        }
        if(fixedAll){
            tooltip.addPara("Megastructure fully functional!", Misc.getPositiveHighlightColor(),10f);

        }

        if(fixedAll){
            tooltip.addPara("Provides 50% construction speed bonus to colony buildings for colonies, which Coronal Pylon structure is within Hypershunt's effective range.",Misc.getPositiveHighlightColor(),10f);
        }

    }


}
