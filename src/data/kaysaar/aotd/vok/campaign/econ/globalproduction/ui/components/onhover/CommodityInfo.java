package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.awt.*;
import java.util.Map;

public class CommodityInfo implements TooltipMakerAPI.TooltipCreator {
    String id;
    boolean expand;
    float width;
    boolean isReqSection;
    public CommodityInfo(String id,float width, boolean expandable,boolean isReqSection){
        this.id = id;
        this.width = width;
        this.expand = expandable;
        this.isReqSection = isReqSection;
    }
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return expand;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return width;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(id);
        tooltip.addTitle(spec.getName());
        tooltip.addPara(Global.getSettings().getDescription(spec.getId(), Description.Type.RESOURCE).getText1(),10f);
        tooltip.addSectionHeading("Global Production (GP)", Alignment.MID,5f);
        int currentTotal =GPManager.getInstance().getTotalResources().get(id);
        int currentTotal2 =GPManager.getInstance().getReqResources().get(id);
        Color[] colors = new Color[2];
        colors[0] = Color.ORANGE;
        colors[1] = Misc.getTooltipTitleAndLightHighlightColor();
        if(!isReqSection){
            tooltip.addPara("Currently this faction is capable of producing globally around %s units of %s",10f,colors,""+currentTotal,spec.getName());

        }
        else{
            tooltip.addPara("Currently this faction is consuming globally around %s units of %s",10f,colors,""+currentTotal2,spec.getName());

        }
        tooltip.addPara("One unit of supply in colony translates towards %s units of global production of commodity",5,Color.ORANGE,""+GPManager.scale);
        tooltip.addPara("To increase Global Production of %s you need to either increase production across colonies, or make new colonies and develop them to support production.",5,Color.ORANGE,spec.getName());
        tooltip.addSectionHeading("Production data from markets",Alignment.MID,10f);
        colors = new Color[3];
        colors[0] = Misc.getTooltipTitleAndLightHighlightColor();
        colors[1] = Misc.getPositiveHighlightColor();
        colors[2] = Color.ORANGE;
        for (Map.Entry<MarketAPI, Integer> marketAPIIntegerEntry : GPManager.getInstance().getTotalResourceProductionFromMarkets(id).entrySet()) {
            tooltip.addPara("%s producing:%s GP units (%s supply units)",5f,colors,marketAPIIntegerEntry.getKey().getName(),""+marketAPIIntegerEntry.getValue(),""+(marketAPIIntegerEntry.getValue()/GPManager.scale));
        }

    }
}
