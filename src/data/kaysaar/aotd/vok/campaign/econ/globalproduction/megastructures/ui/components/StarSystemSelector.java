package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StarSystemSelector implements CustomUIPanelPlugin {
    public StarSystemAPI currentlyChosenStarSystem;
    public ArrayList<StarSystemAPI> starSystems = new ArrayList<>();

    public StarSystemAPI getCurrentlyChosenStarSystem() {
        return currentlyChosenStarSystem;
    }

    public StarSystemSelector(ArrayList<StarSystemAPI>systems, CustomPanelAPI panel, StarSystemSelectorOtherInfoData data){
        this.starSystems = systems;
        this.mainPanel = panel;
        setColors();
        this.data = data;
    }
    Color base,bg,highlight;
    StarSystemSelectorOtherInfoData data;
    public float currentOffset;
    public Color getBase() {
        return base;
    }

    public Color getBg() {
        return bg;
    }

    public Color getHighlight() {
        return highlight;
    }
    public void setColors(){
        base = Global.getSettings().getFactionSpec(Factions.PLAYER).getBaseUIColor();
        bg = Global.getSettings().getFactionSpec(Factions.PLAYER).getDarkUIColor();
        highlight = Global.getSettings().getFactionSpec(Factions.PLAYER).getBrightUIColor();
    }
    public

    CustomPanelAPI mainPanel;
    CustomPanelAPI holderPanel;
    TooltipMakerAPI tooltip;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }
    public void init(){
        holderPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltipMakerOfLabels = holderPanel.createUIElement(mainPanel.getPosition().getWidth(),20,false);
        tooltip = holderPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight()-20,true);
        ButtonAPI buttonDis = tooltipMakerOfLabels.addAreaCheckbox("Star system",null,base,bg,highlight,125,20,0f);
        buttonDis.setClickable(false);
        buttonDis.setButtonDisabledPressedSound(null);
        buttonDis = tooltipMakerOfLabels.addAreaCheckbox("Markets owned",null,base,bg,highlight,200,20,0f);
        buttonDis.setClickable(false);
        buttonDis.setButtonDisabledPressedSound(null);
        buttonDis.getPosition().inTL(130,0);

        if(data!=null){
           buttonDis =  tooltipMakerOfLabels.addAreaCheckbox(data.getNameForLabel(),null,base,bg,highlight,250,20,0f);
            buttonDis.setClickable(false);
            buttonDis.setButtonDisabledPressedSound(null);
            buttonDis.getPosition().inTL(330,0);
        }
        float pad  = 0f;
            for (StarSystemAPI starSystem : starSystems) {
                tooltip.addCustom(createButtonPanel(575,80,starSystem),pad);
            pad = 5f;
        }
        holderPanel.addUIElement(tooltipMakerOfLabels).inTL(0,0);
        holderPanel.addUIElement(tooltip).inTL(0,20);
        mainPanel.addComponent(holderPanel).inTL(0,0);
    }
    public void clearUI(){
        this.buttons.clear();
        mainPanel.removeComponent(holderPanel);
    }
    public void setMainPanel(CustomPanelAPI mainPanel) {
        this.mainPanel = mainPanel;
    }


    public ArrayList<ButtonAPI>buttons = new ArrayList<>();

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
        if(buttons!=null){
            for (ButtonAPI button : buttons) {
                StarSystemAPI system = (StarSystemAPI) button.getCustomData();
                if(button.isChecked()){
                    currentlyChosenStarSystem = system;
                    button.setChecked(false);
                }
                if(button.getCustomData().equals(currentlyChosenStarSystem)){
                    button.highlight();
                }
                else{
                    button.unhighlight();
                }
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public CustomPanelAPI createButtonPanel(float width, float iconsize,StarSystemAPI system) {
        //Star system // Amount of markets
        CustomPanelAPI panel  = Global.getSettings().createCustom(width,iconsize,null);
        TooltipMakerAPI tooltip = panel.createUIElement(width,iconsize,false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(200,iconsize,false);
        TooltipMakerAPI tooltipOther = panel.createUIElement(250,iconsize,false);


        ButtonAPI button = tooltip.addAreaCheckbox("",system,base,bg,highlight,width,iconsize,0f);
        button.getPosition().inTL(0,0);
        TooltipMakerAPI tooltipOfPlanet = panel.createUIElement(iconsize,iconsize,false);
        tooltipOfPlanet.showPlanetInfo(system.getStar(),iconsize,iconsize,true ,-5f);
        String markets = "";
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(system)) {
            if(market.isPlayerOwned()||market.getFaction().isPlayerFaction()){
                markets+=market.getName()+",";
            }
        }
        if(markets.endsWith(",")){
            markets = markets.substring(0,markets.length()-1);
        }
        LabelAPI label = labelTooltip.addPara(markets,0f);
        label.getPosition().inTL(100-(label.computeTextWidth(label.getText())/2),iconsize/2-(label.computeTextHeight(label.getText())/2));
        panel.addUIElement(tooltip).inTL(0,0);
        panel.addUIElement(tooltipOfPlanet).inTL(iconsize/4,0);
        panel.addUIElement(labelTooltip).inTL(130,0);
        if(data!=null){
            data.populateLabel(tooltipOther,system,250,iconsize);
            panel.addUIElement(tooltipOther).inTL(330,0);
        }

        buttons.add(button);
        return panel;
    }

}
