package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.SoundUIManager;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.HelpPopUpUINid;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.ui.customprod.components.onhover.CommodityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class GPMegastructureMenu implements CustomUIPanelPlugin, SoundUIManager {
    public CustomPanelAPI panel;
    public ArrayList<ButtonAPI> buttonsOfMegastructures;
    public GPManager manager;
    public CustomPanelAPI panelOfMarketData;
    public CustomPanelAPI currentMegastructureSectionsPanel;
    public CustomPanelAPI currentMegastructureSelected;
    public CustomPanelAPI totalCostOfMegastructuresPanel;
    public CustomPanelAPI helpButtonPanel;
    public ButtonAPI helpButton;
    public static float staticWidthOfMegaButtons = 450;
    public static float totalCostHeight = 100f;
    public GPIndividualMegastructreMenu currentOne;
    public GPBaseMegastructure megastructure;
    public TooltipMakerAPI buttonTooltipMaker;
    float spacerX = 0f; //Used for left panels

    public void createMegastructureList() {
        if(buttonsOfMegastructures==null){
            buttonsOfMegastructures = new ArrayList<>();
        }
        buttonsOfMegastructures.clear();
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSectionsPanel = panel.createCustomPanel(staticWidthOfMegaButtons, panel.getPosition().getHeight() - 51 , renderer);
        renderer.setPanel(currentMegastructureSectionsPanel);
        TooltipMakerAPI tooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(), currentMegastructureSectionsPanel.getPosition().getHeight(), false);
        tooltip.addSectionHeading("Megastructures", Alignment.MID, 0f);
        TooltipMakerAPI buttonTooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(), currentMegastructureSectionsPanel.getPosition().getHeight() - 20, true);
        float pad = 0f;
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
            placeButton(megastructure, buttonTooltip, pad);
            pad = 5f;

        }



        buttonTooltipMaker = buttonTooltip;
        currentMegastructureSectionsPanel.addUIElement(tooltip).inTL(0, 0);
        currentMegastructureSectionsPanel.addUIElement(buttonTooltip).inTL(0, 20);
        panel.addComponent(currentMegastructureSectionsPanel).inTL(spacerX, 51);

    }

    private void placeButton(GPBaseMegastructure baseMegastructure, TooltipMakerAPI buttonTooltip, float pad) {
        CustomPanelAPI panelAPI = baseMegastructure.createButtonSection(staticWidthOfMegaButtons - 7);
        ButtonAPI buttonAPI = buttonTooltip.addAreaCheckbox("", baseMegastructure, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, staticWidthOfMegaButtons - 7, panelAPI.getPosition().getHeight(), pad);
        buttonsOfMegastructures.add(buttonAPI);
        buttonTooltip.addCustom(panelAPI, 0f).getPosition().inTL(buttonAPI.getPosition().getX(), -buttonAPI.getPosition().getY() - buttonAPI.getPosition().getHeight());
        buttonTooltip.setHeightSoFar(Math.abs(buttonAPI.getPosition().getY()));
    }

    public void createCurrentMegastructureTab() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSelected = panel.createCustomPanel(panel.getPosition().getWidth() - staticWidthOfMegaButtons - 10, panel.getPosition().getHeight() - 51, renderer);
        renderer.setPanel(currentMegastructureSelected);
        TooltipMakerAPI tooltip = currentMegastructureSelected.createUIElement(currentMegastructureSelected.getPosition().getWidth(), currentMegastructureSelected.getPosition().getHeight(), true);
        String str = "";
        if (megastructure != null) {
            str += megastructure.getSpec().getName();
            if(megastructure.getEntityTiedTo()!=null){
                str+=" : "+megastructure.getEntityTiedTo().getStarSystem().getName();
            }
        }
        tooltip.addSectionHeading(str, Alignment.MID, 0f);
        if(megastructure!=null){
            currentOne = megastructure.createUIPlugin(currentMegastructureSelected,this);
            currentOne.initUI();
            tooltip.addCustom(currentOne.getMainPanel(), 0f);
            tooltip.setHeightSoFar(currentOne.getMainPanel().getPosition().getHeight());
        }
        currentMegastructureSelected.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(currentMegastructureSelected).inTL(spacerX + staticWidthOfMegaButtons + 5, 51);


    }
    private void createImage(TooltipMakerAPI tooltip, float iconsize, float topYImage, float x, Map.Entry<String, Integer> entry) {
        if(commodities.get(entry.getKey()).equals(GPManager.GPResourceType.COMMODITY)){
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
        }
        else{
            tooltip.addImage(Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
        }
        tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false,GPManager.getInstance().getProductionOrders()), TooltipMakerAPI.TooltipLocation.BELOW);
        UIComponentAPI image = tooltip.getPrev();
        image.getPosition().inTL(x, topYImage);

    }
    public void createMarketResourcesPanel() {
        float width = UIData.WIDTH / 2;
        panelOfMarketData = panel.createCustomPanel(width, 50, null);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(width, 50, false);
        float totalSize = width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 35;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        GPUIMisc.createIconSection(positions,tooltip,iconsize,topYImage,test,sections,GPManager.getInstance().getProductionOrders());
        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfMarketData).inTL(5 + width / 2, 5);
    }
    public void createHelpButtonPanel(){
        CustomPanelAPI panel = this.panel.createCustomPanel(30,30,null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(30, 30, false);
        helpButton =  tooltipMakerAPI.addAreaCheckbox("",null,Global.getSettings().getBasePlayerColor(), Global.getSettings().getBasePlayerColor(),Global.getSettings().getBrightPlayerColor(),29,30,0f);
        helpButton.getPosition().inTL(0,0);
        tooltipMakerAPI.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "question"), 30, 30, 0f);
        tooltipMakerAPI.getPrev().getPosition().inTL(0,0);
        panel.addUIElement(tooltipMakerAPI).inTL(0,0);
        this.panel.addComponent(panel).inTL(Global.getSettings().getScreenWidth()-45,0);

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
        if(helpButton!=null){
            if(helpButton.isChecked()){
                helpButton.setChecked(false);
                HelpPopUpUINid nid = new HelpPopUpUINid(false);
                AshMisc.placePopUpUI(nid,helpButton,700,400);
            }
        }
        if(buttonsOfMegastructures!=null){
            for (ButtonAPI buttonsOfMegastructure : buttonsOfMegastructures) {
                if(buttonsOfMegastructure.isChecked()){
                    buttonsOfMegastructure.setChecked(false);
                    if(!buttonsOfMegastructure.getCustomData().equals(megastructure)){
                        megastructure = (GPBaseMegastructure) buttonsOfMegastructure.getCustomData();
                        panel.removeComponent(currentMegastructureSelected);
                        createCurrentMegastructureTab();
                    }

                    reInitalizeButtonUI();
                    break;
                }
            }
        }
    }

    public void reInitalizeButtonUI(){
        buttonsOfMegastructures.clear();
        float currentOffset = buttonTooltipMaker.getExternalScroller().getYOffset();
        panel.removeComponent(currentMegastructureSectionsPanel);
        panel.removeComponent(totalCostOfMegastructuresPanel);
        buttonTooltipMaker=null;
        createMegastructureList();
        buttonTooltipMaker.getExternalScroller().setYOffset(currentOffset);
    }
    public void init(CustomPanelAPI mainPanel) {
        this.panel = mainPanel;
        this.manager = GPManager.getInstance();
        createMarketResourcesPanel();
        createMegastructureList();
        createHelpButtonPanel();
        createCurrentMegastructureTab();
    }

    public CustomPanelAPI getMainPanel() {
        return panel;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    @Override
    public void playSound() {
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_mega", true);
    }

    @Override
    public void pauseSound() {

    }
    public void resetMarketData(){
        panel.removeComponent(panelOfMarketData);
        createMarketResourcesPanel();
    }
    public void clearUI() {
        if(currentOne!=null) {
            currentOne.clearUI();
        }
        buttonsOfMegastructures.clear();
        panel.removeComponent(panelOfMarketData);
        panel.removeComponent(currentMegastructureSectionsPanel);
        panel.removeComponent(currentMegastructureSelected);
        panel.removeComponent(totalCostOfMegastructuresPanel);
    }
}
