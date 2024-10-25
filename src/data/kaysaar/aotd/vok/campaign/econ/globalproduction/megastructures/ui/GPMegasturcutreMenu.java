package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.impl.HypershuntUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class GPMegasturcutreMenu implements CustomUIPanelPlugin, SoundUIManager {
    public CustomPanelAPI panel;
    public ArrayList<ButtonAPI> buttonsOfMegastructures;
    public GPManager manager;
    public CustomPanelAPI panelOfMarketData;
    public CustomPanelAPI currentMegastructureSectionsPanel;
    public CustomPanelAPI currentMegastructureSelected;
    public CustomPanelAPI totalCostOfMegastructuresPanel;
    public static float staticWidthOfMegaButtons = 400;
    public static float totalCostHeight = 100f;

    float spacerX = 0f; //Used for left panels
    public void createMegastructureList(){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSectionsPanel = panel.createCustomPanel(staticWidthOfMegaButtons,panel.getPosition().getHeight()-51-totalCostHeight-10, renderer);
        renderer.setPanel(currentMegastructureSectionsPanel);
        TooltipMakerAPI tooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(),currentMegastructureSectionsPanel.getPosition().getHeight(),false);
        tooltip.addSectionHeading("Megastructure List",Alignment.MID,0f);
        TooltipMakerAPI buttonTooltip = currentMegastructureSectionsPanel.createUIElement(currentMegastructureSectionsPanel.getPosition().getWidth(),currentMegastructureSectionsPanel.getPosition().getHeight()-20,true);
        HypershuntMegastrcutre hypershuntMegastrcutre = new HypershuntMegastrcutre("coronal_hypershunt");
        placeButton(hypershuntMegastrcutre, buttonTooltip);
        currentMegastructureSectionsPanel.addUIElement(tooltip).inTL(0,0);
        currentMegastructureSectionsPanel.addUIElement(buttonTooltip).inTL(0,20);
        panel.addComponent(currentMegastructureSectionsPanel).inTL(spacerX, 51);

    }

    private static void placeButton(HypershuntMegastrcutre hypershuntMegastrcutre, TooltipMakerAPI buttonTooltip) {
        CustomPanelAPI panelAPI = hypershuntMegastrcutre.createButtonSection(staticWidthOfMegaButtons-7);
        ButtonAPI buttonAPI =  buttonTooltip.addAreaCheckbox("",null, NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,staticWidthOfMegaButtons-7,panelAPI.getPosition().getHeight(),0f);
        buttonTooltip.addCustom(panelAPI,0f).getPosition().inTL(buttonAPI.getPosition().getX()-5,-buttonAPI.getPosition().getY()-buttonAPI.getPosition().getHeight());
    }

    public void createCurrentMegastructureTab(){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentMegastructureSelected = panel.createCustomPanel(panel.getPosition().getWidth()-staticWidthOfMegaButtons-10,panel.getPosition().getHeight()-51, renderer);
        HypershuntUI  ui = new HypershuntUI(null,currentMegastructureSelected,panel);
        renderer.setPanel(currentMegastructureSelected);
        TooltipMakerAPI tooltip = currentMegastructureSelected.createUIElement(currentMegastructureSelected.getPosition().getWidth(),currentMegastructureSelected.getPosition().getHeight(),true);
        tooltip.addSectionHeading("Current Megastructure",Alignment.MID,0f);

        ui.initUI();
        tooltip.addCustom(ui.getMainPanel(),0f);
        tooltip.setHeightSoFar(ui.getMainPanel().getPosition().getHeight());
        currentMegastructureSelected.addUIElement(tooltip).inTL(0,0);
        panel.addComponent(currentMegastructureSelected).inTL(spacerX+staticWidthOfMegaButtons+5, 51);

    }
    public void createTotalCostOfMegastructuresTab(){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        totalCostOfMegastructuresPanel = panel.createCustomPanel(staticWidthOfMegaButtons,totalCostHeight, renderer);
        renderer.setPanel(totalCostOfMegastructuresPanel);
        TooltipMakerAPI tooltip = totalCostOfMegastructuresPanel.createUIElement(totalCostOfMegastructuresPanel.getPosition().getWidth(),totalCostOfMegastructuresPanel.getPosition().getHeight(),false);
        tooltip.addSectionHeading("Total Cost of Operations",Alignment.MID,0f);
        totalCostOfMegastructuresPanel.addUIElement(tooltip).inTL(0,0);
        panel.addComponent(totalCostOfMegastructuresPanel).inTL(spacerX, panel.getPosition().getHeight()-totalCostHeight);

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
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false,manager.getProductionOrders()), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text + "(" + GPManager.getInstance().getReqResources(GPManager.getInstance().getProductionOrders()).get(entry.getKey()) + ")";
            tooltip.addPara("" + entry.getValue() + " %s", 0f, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "(" +  manager.getExpectedCostsFromManager().get(entry.getKey()) + ")").getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfMarketData).inTL(5 + width / 2, 5);
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
    public void init(CustomPanelAPI mainPanel) {
        this.panel = mainPanel;
        this.manager = GPManager.getInstance();
        createMarketResourcesPanel();
        createMegastructureList();
        createCurrentMegastructureTab();
        createTotalCostOfMegastructuresTab();
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

    }

    @Override
    public void pauseSound() {

    }
    public  void clearUI(){
        panel.removeComponent(panelOfMarketData);
    }
}
