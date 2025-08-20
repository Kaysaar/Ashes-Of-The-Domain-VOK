package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.ui.basecomps.ExtendUIPanelPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SynergyInfoDisplay implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel, synergyInfo,unavailableSynergyList, availableSynergyInfo;
    public ButtonAPI currentlyChosen;
    public ArrayList<ButtonAPI>buttons = new ArrayList<>();
    public MarketAPI market;
    public UILinesRenderer unAvailRenderer, availRenderer, synergyInfoRenderer;

    public SynergyInfoDisplay(MarketAPI market) {
        mainPanel = Global.getSettings().createCustom(705, 500, this);
        this.market = market;
        synergyInfo = Global.getSettings().createCustom(285, mainPanel.getPosition().getHeight()-10, null);
        createUI();

        unAvailRenderer = new UILinesRenderer(0f);
        unAvailRenderer.setPanel(unavailableSynergyList);
        availRenderer = new UILinesRenderer(0f);
        availRenderer.setPanel(availableSynergyInfo);
        synergyInfoRenderer = new UILinesRenderer(0f);
        synergyInfoRenderer.setPanel(synergyInfo);
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(availableSynergyInfo ==null){
            createAvailableSynergyInfo();
            createUnavailableSynergyInfo();
            TooltipMakerAPI tooltipHeader;

            tooltipHeader = synergyInfo.createUIElement(synergyInfo.getPosition().getWidth(),20,false);
            LabelAPI label = tooltipHeader.addSectionHeading("Synergy Info", Alignment.MID,0f);
            synergyInfo.addUIElement(tooltipHeader).inTL(0,0);
            mainPanel.addComponent(synergyInfo).inTL(availableSynergyInfo.getPosition().getWidth()+10+unavailableSynergyList.getPosition().getWidth()+5,5);
        }


    }

    private void createAvailableSynergyInfo() {
        availableSynergyInfo = Global.getSettings().createCustom(210, mainPanel.getPosition().getHeight()-10, null);
        TooltipMakerAPI tooltipHeader = availableSynergyInfo.createUIElement(availableSynergyInfo.getPosition().getWidth(),20,false);
        if(market.hasIndustry("aotd_maglev")){
            tooltipHeader.addSectionHeading("Current Synergies", Alignment.MID,0f);
        }
        else{
            tooltipHeader.addSectionHeading("Available Synergies", Alignment.MID,0f);
        }
        TooltipMakerAPI properTooltip = availableSynergyInfo.createUIElement(availableSynergyInfo.getPosition().getWidth(), availableSynergyInfo.getPosition().getHeight()-25,true);
        float opad = 0f;
        for (BaseIndustrySynergy baseIndustrySynergy : IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarketInUI(market)) {
            ButtonAPI button = properTooltip.addButton(baseIndustrySynergy.getSynergyName(),baseIndustrySynergy, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE, availableSynergyInfo.getPosition().getWidth()-9,40,opad);
            buttons.add(button);
            opad =5f;
        }
        availableSynergyInfo.addUIElement(tooltipHeader).inTL(0,0);
        availableSynergyInfo.addUIElement(properTooltip).inTL(0,22);
        mainPanel.addComponent(availableSynergyInfo).inTL(5,5);
    }
    private void createUnavailableSynergyInfo() {
        unavailableSynergyList = Global.getSettings().createCustom(210, mainPanel.getPosition().getHeight()-10, null);
        TooltipMakerAPI tooltipHeader = unavailableSynergyList.createUIElement(unavailableSynergyList.getPosition().getWidth(),20,false);
        tooltipHeader.addSectionHeading("Other Synergies", Alignment.MID,0f);
        TooltipMakerAPI properTooltip = unavailableSynergyList.createUIElement(unavailableSynergyList.getPosition().getWidth(), unavailableSynergyList.getPosition().getHeight()-25,true);
        float opad = 0f;

        for (BaseIndustrySynergy baseIndustrySynergy : IndustrySynergiesManager.getInstance().getSynergiesNotValidForMarketInUI(market)) {
            ButtonAPI button = properTooltip.addButton(baseIndustrySynergy.getSynergyName(),baseIndustrySynergy, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE, availableSynergyInfo.getPosition().getWidth()-9,40,opad);
            buttons.add(button);
            opad =5f;
        }
        unavailableSynergyList.addUIElement(tooltipHeader).inTL(0,0);
        unavailableSynergyList.addUIElement(properTooltip).inTL(0,22);
        mainPanel.addComponent(unavailableSynergyList).inTL(availableSynergyInfo.getPosition().getWidth()+10,5);
    }

    public void updateUI(){
        mainPanel.removeComponent(synergyInfo);
        synergyInfo = Global.getSettings().createCustom(285, mainPanel.getPosition().getHeight()-10, null);
        TooltipMakerAPI tooltipHeader = synergyInfo.createUIElement(synergyInfo.getPosition().getWidth(),35,false);
        LabelAPI label = tooltipHeader.addSectionHeading("Synergy Info", Alignment.MID,0f);
        synergyInfo.addUIElement(tooltipHeader).inTL(0,0);
        BaseIndustrySynergy synergy = (BaseIndustrySynergy) currentlyChosen.getCustomData();
        TooltipMakerAPI properTooltip = synergyInfo.createUIElement(synergyInfo.getPosition().getWidth()+5, synergyInfo.getPosition().getHeight()-15,true);
        properTooltip.addTitle(synergy.getSynergyName());
        properTooltip.setBulletedListMode("   ");
        synergy.printReq(properTooltip,market);
        synergy.printEffects(properTooltip,market,1f);
        properTooltip.setBulletedListMode(null);
        properTooltip.addSectionHeading("Related Structures/Industries to Synergy", Alignment.MID,5f);
        properTooltip.setBulletedListMode("  -");
        for (String string : synergy.getIndustriesForSynergy()) {
            if(market.hasIndustry(string)){
                properTooltip.addPara(market.getIndustry(string).getCurrentName(), Color.ORANGE,3f);
            }
            else{
                properTooltip.addPara(Global.getSettings().getIndustrySpec(string).getNewPluginInstance(market).getCurrentName(), Color.ORANGE,3f);
            }
        }
        properTooltip.setBulletedListMode(null);
        synergyInfo.addUIElement(properTooltip).inTL(0,20);
        mainPanel.addComponent(synergyInfo).inTL(availableSynergyInfo.getPosition().getWidth()+10+unavailableSynergyList.getPosition().getWidth()+5,5);
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        unAvailRenderer.setBoxColor(Global.getSettings().getDarkPlayerColor());
        unAvailRenderer.render(alphaMult);
        availRenderer.setBoxColor(Global.getSettings().getDarkPlayerColor());
        availRenderer.render(alphaMult);
        synergyInfoRenderer.setBoxColor(Global.getSettings().getDarkPlayerColor());
        synergyInfoRenderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons) {
            button.unhighlight();
            if(button.isChecked()){
                button.setChecked(false);
                currentlyChosen = button;
                updateUI();
            }
        }
        if(currentlyChosen!=null){
            currentlyChosen.highlight();
        }


    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
