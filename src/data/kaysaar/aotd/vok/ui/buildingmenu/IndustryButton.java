package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class IndustryButton extends CustomButton {
    public IndustrySpecAPI spec = (IndustrySpecAPI) buttonData;
    public MarketAPI market;

    public IndustryButton(float width, float height, Object buttonData, float indent, MarketAPI market,Color base,Color bg, Color bright) {
        super(width, height, buttonData, indent,base,bg,bright);
        this.market = market;
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI tooltip) {
        return super.createButton(tooltip);

    }

    @Override
    public void initializeUI() {
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,false);
        TooltipMakerAPI tooltipActualButton = panel.createUIElement(width,height,false);
        mainButton = createButton(tooltipActualButton);
        createButtonContent(tooltip);
        spec = (IndustrySpecAPI) buttonData;
//        final BaseIndustry industry = (BaseIndustry) spec.getNewPluginInstance(market);
//        tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
//            @Override
//            public boolean isTooltipExpandable(Object tooltipParam) {
//                return true;
//            }
//
//            @Override
//            public float getTooltipWidth(Object tooltipParam) {
//                return 400;
//            }
//
//            @Override
//            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
//                BuildingMenuMisc.createTooltipForIndustry((BaseIndustry) ((IndustrySpecAPI) buttonData).getNewPluginInstance(market), Industry.IndustryTooltipMode.ADD_INDUSTRY,tooltip,expanded,true,getTooltipWidth(tooltipParam),true);
//            }
//        }, panel,TooltipMakerAPI.TooltipLocation.RIGHT,false);
        panel.addUIElement(tooltipActualButton).inTL(0,0);
        panel.addUIElement(tooltip).inTL(0,0);
    }

    @Override
    public void createButtonContent(TooltipMakerAPI tooltip) {
        //290 width of Entire section
        spec = (IndustrySpecAPI) buttonData;
        BaseIndustry industry = (BaseIndustry) spec.getNewPluginInstance(market);

        tooltip.addImage(industry.getCurrentImage(), 70, 35, 0f);
        tooltip.getPrev().getPosition().inTL(8, 2.5f);
        LabelAPI label;

        if(industry.isAvailableToBuild()||industry.getSpec().hasTag("parent_item")){
            label=  tooltip.addPara(spec.getName(), Misc.getTooltipTitleAndLightHighlightColor(), 0f);
        }
        else{
            label=  tooltip.addPara(spec.getName(), Misc.getNegativeHighlightColor(), 0f);

        }
        label.autoSizeToWidth(200);
        label.getPosition().inTL(90, 20 - (label.computeTextHeight(label.getText()) / 2));
        float heightLabel = 20 - (label.computeTextHeight(label.getText()) / 2);
        label = tooltip.addPara(getIndustryString(industry).one, getIndustryString(industry).two, 0f);
        label.autoSizeToWidth(100);
        label.getPosition().inTL(324 - indent - (label.computeTextWidth(label.getText()) / 2), heightLabel);

        label = tooltip.addPara(getBuildingTimeToStr(spec), Color.ORANGE, 0f);
        label.autoSizeToWidth(125);
        label.getPosition().inTL(438 - indent - (label.computeTextWidth(label.getText()) / 2), heightLabel);
        if (spec.getCost() > Global.getSector().getPlayerFleet().getCargo().getCredits().get()) {
            label = tooltip.addPara(getCostStr(spec), Misc.getNegativeHighlightColor(), 0f);
        } else {
            label = tooltip.addPara(getCostStr(spec), Color.ORANGE, 0f);

        }

        label.autoSizeToWidth(115);
        label.getPosition().inTL(560 - indent - (label.computeTextWidth(label.getText()) / 2), heightLabel);
    }

    private Pair<String, Color> getIndustryString(BaseIndustry industry) {
        Pair<String, Color> type = new Pair<>("",Misc.getTextColor());
        if (industry.getSpec().hasTag("parent_item")) {
            type.one = "Variable";
            type.two = Color.ORANGE;
        } else if (industry.isIndustry()) {
            type.one = "Industry";
            type.two = Color.ORANGE;
            if (market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).getFlatBonus()-getAmountOfIndustriesFromMarket()<=0){
                type.two = Misc.getNegativeHighlightColor();
            }
        } else if (industry.isStructure()) {
            type.one = "Structure";
            type.two = Misc.getTextColor();
        }
        return type;
    }

    public String getBuildingTimeToStr(IndustrySpecAPI spec) {
        if (spec.hasTag("parent_item")) {
            return "Variable";
        }

        return "" + (int) spec.getBuildTime();
    }

    public String getCostStr(IndustrySpecAPI spec) {
        if (spec.hasTag("parent_item")) {
            return "Variable";
        }
        return Misc.getDGSCredits(spec.getCost());
    }

    public int getAmountOfIndustriesFromMarket() {
        int am = 0;
        for (Industry industry : market.getIndustries()) {
            if (industry.isIndustry()) {
                am++;
            }
        }
        return am;
    }
}
