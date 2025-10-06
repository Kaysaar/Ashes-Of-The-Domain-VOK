package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColonyDevelopmentList implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    ButtonAPI chosen;
    MarketAPI market;
    public ColonyDevelopmentList(float width, float height, MarketAPI market) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.market= market;
        createUI();

    }
    public boolean needsToUpdateUI = false;

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
        tooltipHeader.addSectionHeading("Development Plans", Alignment.MID,0f);
        TooltipMakerAPI tooltipButton = componentPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight()-25,true);
        componentPanel.addUIElement(tooltipHeader).inTL(0,0);
        float opad = 0f;
        for (Map.Entry<String, BaseColonyDevelopment> entry : ColonyDevelopmentManager.getInstance().getDevelopmentScripts().entrySet()) {
            ButtonAPI button = tooltipButton.addButton(entry.getValue().getName(),entry.getKey(), Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE, componentPanel.getPosition().getWidth()-9,40,opad);
            button.setEnabled(entry.getValue().canBeAppliedOnMarket(market));
            buttons.add(button);
            opad =5f;
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

    public void setNeedsToUpdateUI(boolean needsToUpdateUI) {
        this.needsToUpdateUI = needsToUpdateUI;
    }

    @Override
    public void render(float alphaMult) {
        for (ButtonAPI button : buttons) {
            button.unhighlight();
            if(button.isChecked()){
                button.setChecked(false);
                needsToUpdateUI = true;
                if(chosen!=null){
                    chosen.unhighlight();
                }
                chosen = button;
                break;
            }
        }
        if(chosen!=null){
            chosen.highlight();
        }
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
