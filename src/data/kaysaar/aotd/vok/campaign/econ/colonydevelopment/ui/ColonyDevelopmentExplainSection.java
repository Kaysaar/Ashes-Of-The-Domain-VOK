package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

import java.util.List;

public class ColonyDevelopmentExplainSection implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    String id;
    MarketAPI market;

    public void setId(String id) {
        this.id = id;
    }

    public ColonyDevelopmentExplainSection(float width, float height, String id, MarketAPI market) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.id = id;
        this.market = market;
        createUI();

    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(componentPanel!=null) {
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltipHeader = componentPanel.createUIElement(mainPanel.getPosition().getWidth(),20,false);
        tooltipHeader.addSectionHeading("Development Details", Alignment.MID,0f);

        componentPanel.addUIElement(tooltipHeader).inTL(0,0);
        TooltipMakerAPI tooltipButton = componentPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight()-25,true);
        if(AshMisc.isStringValid(id)&&ColonyDevelopmentManager.getInstance().getColonyDevelopment(id)!=null){
            ColonyDevelopmentManager.getInstance().getColonyDevelopment(id).generateDetailingTooltip(market,tooltipButton);
        }
        componentPanel.addUIElement(tooltipButton).inTL(0,22);

        mainPanel.addComponent(componentPanel).inTL(0,0);
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
    public void clearUI(){

    }
}
