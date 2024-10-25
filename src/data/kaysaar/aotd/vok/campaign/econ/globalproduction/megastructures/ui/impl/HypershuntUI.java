package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.ui.ImagePanel;

import java.awt.*;
import java.util.List;

public class HypershuntUI implements GPIndividualMegastructreMenu {
    public GPBaseMegastructure megastructureReferedTo;
    //This is where main panel is placed
    CustomPanelAPI parentPanel;

    CustomPanelAPI mainPanel;
    //This is where parent panel is placed you can basically place there sort of dialog panel so it is intercepting ALL events that would go to mainPanel
    CustomPanelAPI absoluteParent;
    float width,height;
    CustomPanelAPI sectionsPanel;
    CustomPanelAPI titlePanel;
    CustomPanelAPI mainTitlePanel;
    float lastY = 0f;
    public HypershuntUI(GPBaseMegastructure megastructure,CustomPanelAPI parentPanel,CustomPanelAPI absoluteParent) {
        this.megastructureReferedTo = megastructure;
        this.parentPanel = parentPanel;
        this.absoluteParent = absoluteParent;
    }
    public void createTitleMenu(){
        float widthForUsage  = parentPanel.getPosition().getWidth()-20;
        float sectionWidth = widthForUsage/6;
        float height = 0f;
        UILinesRenderer renderer = new UILinesRenderer(0f);
        titlePanel = mainPanel.createCustomPanel(parentPanel.getPosition().getWidth(),40,renderer);
        renderer.setPanel(titlePanel);
        TooltipMakerAPI imageTootlip,mapTooltip,descriptionTooltip;

        imageTootlip = titlePanel.createUIElement(sectionWidth*2,100,false);
        imageTootlip.addImage("graphics/illustrations/coronal_tap.jpg",sectionWidth*2,5f);
        height = imageTootlip.getPrev().getPosition().getHeight();
        imageTootlip.getPosition().setSize(sectionWidth*2,height);
        titlePanel.getPosition().setSize(titlePanel.getPosition().getWidth(),height);
        mapTooltip = titlePanel.createUIElement(sectionWidth,height,false);
        mapTooltip.addSectorMap(sectionWidth,height-20,Global.getSector().getPlayerFleet().getStarSystem(), 0f);
        descriptionTooltip = titlePanel.createUIElement(sectionWidth*3,height,true);
        descriptionTooltip.addPara(Global.getSettings().getDescription("coronal_tap", Description.Type.CUSTOM).getText1FirstPara(),5f);

        titlePanel.addUIElement(imageTootlip).inTL(0,0);
        titlePanel.addUIElement(descriptionTooltip).inTL(sectionWidth*2+5,0);
        titlePanel.addUIElement(mapTooltip).inTL(sectionWidth*5+10,0);
        mainPanel.addComponent(titlePanel).inTL(-5,lastY);
        lastY += titlePanel.getPosition().getHeight();

    }
    public void createTitleSection(){
        mainTitlePanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),20,null);
        TooltipMakerAPI tooltip = mainTitlePanel.createUIElement(mainTitlePanel.getPosition().getWidth(),20,false);
        tooltip.addSectionHeading("Megastructure Sections",Alignment.MID,0f);
        mainTitlePanel.addUIElement(tooltip).inTL(0,0);
        mainPanel.addComponent(mainTitlePanel).inTL(-5,lastY);
        lastY+=mainTitlePanel.getPosition().getHeight();

    }
    public void createSectionMenu(String titleOfSection ,String icon){
        float imageWidth,imageHeight;
        imageWidth = 250;
        imageHeight= 150;

        UILinesRenderer renderer = new UILinesRenderer(0f);
        sectionsPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),imageHeight,renderer);
        renderer.setPanel(sectionsPanel);
        TooltipMakerAPI tooltipOfImage = sectionsPanel.createUIElement(imageWidth,imageHeight,false);
        LabelAPI label = tooltipOfImage.addTitle(titleOfSection);
        ImagePanel panelPl = new ImagePanel();
        CustomPanelAPI panelImage = sectionsPanel.createCustomPanel(imageWidth,imageHeight-20,panelPl);
        panelPl.init(panelImage,Global.getSettings().getSprite(icon));
        tooltipOfImage.addCustom(panelImage,10f);
        tooltipOfImage.getPrev().getPosition().inTL(label.getPosition().getX()-5,label.getPosition().getY()+33);
        tooltipOfImage.setTitleFont(Fonts.INSIGNIA_LARGE);
        sectionsPanel.addUIElement(tooltipOfImage).inTL(0,0);
        CustomPanelAPI panel = sectionsPanel.createCustomPanel(sectionsPanel.getPosition().getWidth()-imageWidth,imageHeight,null);
        renderer.setPanel(panel);
        TooltipMakerAPI titleTooltip,optionTooltip,descriptionTooltip,otherInfoTooltip;
        titleTooltip = panel.createUIElement(panel.getPosition().getWidth(),20,false);
        titleTooltip.addSectionHeading("Options", Misc.getTextColor(),Misc.getDarkPlayerColor(),Alignment.MID,panel.getPosition().getWidth()*0.4f-15f,0f);
        titleTooltip.addSectionHeading("Other Info", Misc.getTextColor(),Misc.getDarkPlayerColor(),Alignment.MID,panel.getPosition().getWidth()*0.4f-15f,0f).getPosition().inTL(panel.getPosition().getWidth()*0.4f,0);

        titleTooltip.addSectionHeading("Description", Misc.getTextColor(),Misc.getDarkPlayerColor(),Alignment.MID,panel.getPosition().getWidth()*0.2f,0f).getPosition().inTL(panel.getPosition().getWidth()*0.8f,0);
        optionTooltip = panel.createUIElement(panel.getPosition().getWidth()*0.4f-15f,imageHeight-25,true);
        descriptionTooltip = panel.createUIElement(panel.getPosition().getWidth()*0.2f,imageHeight-25,true);
        float pad =0f;
        for (int i = 0; i < 3; i++) {
            optionTooltip.addAreaCheckbox("Test"+i,null,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,panel.getPosition().getWidth()*0.4f-20f,40,pad);
            pad= 5f;
        }
        descriptionTooltip.addPara("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",0f);
        panel.addUIElement(titleTooltip).inTL(0, 0);
        panel.addUIElement(descriptionTooltip).inTL(panel.getPosition().getWidth()*0.8f,25);
        panel.addUIElement(optionTooltip).inTL(0,25);
        sectionsPanel.addComponent(panel).inTL(imageWidth,0);
        mainPanel.addComponent(sectionsPanel).inTL(-5,lastY);
        lastY += sectionsPanel.getPosition().getHeight();
    }


    @Override
    public void clearUI() {

    }

    @Override
    public void initUI() {
        mainPanel = parentPanel.createCustomPanel(parentPanel.getPosition().getWidth(),parentPanel.getPosition().getHeight(),null);
        width = parentPanel.getPosition().getWidth();
        height = parentPanel.getPosition().getHeight();
        createTitleMenu();
        createTitleSection();
        createSectionMenu("Coronal Wormhole","graphics/icons/industry/plasma_shield_generator.png");
        createSectionMenu("Coronal Collector","graphics/icons/industry/coronal_reciver.png");
        createSectionMenu("Drone Bay","graphics/icons/industry/coronalNetworkHub.png");
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
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
