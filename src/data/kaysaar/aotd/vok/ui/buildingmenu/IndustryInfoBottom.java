package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;

public class IndustryInfoBottom implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    MarketAPI market;

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public IndustryInfoBottom(MarketAPI market, float width, float height) {
        this.market = market;
        mainPanel = Global.getSettings().createCustom(width,height,this);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width,height,false);
        tooltip.setParaInsigniaLarge();
        LabelAPI lb = tooltip.addPara("Credits: %s",0f, Misc.getGrayColor(), Color.ORANGE,Misc.getWithDGS(Global.getSector().getPlayerFleet().getCargo().getCredits().get()));
        lb.getPosition().setSize(320,30);
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 200;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Credits you currently have",5f);
            }
        }, TooltipMakerAPI.TooltipLocation.ABOVE,false);
        Color col = Color.ORANGE;
        if(Misc.getMaxIndustries(market)<Misc.getNumIndustries(market)){
            col = Misc.getNegativeHighlightColor();
        }
        LabelAPI label = tooltip.addPara("Industries : %s",5f,Misc.getGrayColor(), col,Misc.getNumIndustries(market)+" / "+Misc.getMaxIndustries(market));
        label.getPosition().inTL(330,0);
        mainPanel.addUIElement(tooltip).inTL(0,0);
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
