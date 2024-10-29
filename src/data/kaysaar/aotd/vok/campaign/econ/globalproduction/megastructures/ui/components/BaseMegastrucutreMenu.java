package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.PauseRestoration;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.RestorationDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.*;

public class BaseMegastrucutreMenu implements GPIndividualMegastructreMenu {
    public GPBaseMegastructure megastructureReferedTo;
    //This is where main panel is placed
    CustomPanelAPI parentPanel;
    CustomPanelAPI mainPanel;
    float width,height;
    GPMegasturcutreMenu mainMenu;
    LinkedHashMap<String,CustomPanelAPI>panelsOfSections = new LinkedHashMap<>();
    CustomPanelAPI titlePanel;
    CustomPanelAPI mainTitlePanel;
    CustomPanelAPI toolTipPanel;
    TooltipMakerAPI tooltipOfSections;
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    float offset =0f;
    float lastY = 0f;
    float lastYForSection = 0f;
    public BaseMegastrucutreMenu(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel,GPMegasturcutreMenu menu) {
        this.megastructureReferedTo = megastructure;
        this.parentPanel = parentPanel;
        this.mainMenu = menu;
    }
    public void createTitleMenu(){
        titlePanel = MegastructureUIMisc.createTitleMenu(mainPanel, megastructureReferedTo);
        mainPanel.addComponent(titlePanel).inTL(-5,lastY);
        lastY += titlePanel.getPosition().getHeight();

    }
    public void createTitleSection(){
        mainTitlePanel = MegastructureUIMisc.createTitleSection(mainPanel, megastructureReferedTo);
        mainPanel.addComponent(mainTitlePanel).inTL(-5,lastY);
        lastY+=mainTitlePanel.getPosition().getHeight();

    }
    public void createSectionMenu(GPMegaStructureSection section){
        ButtonPackage Bpackage = MegastructureUIMisc.createWidgetForSection(mainPanel,section);
        CustomPanelAPI sectionsPanel = Bpackage.getPanelOfButtons();
        buttons.addAll(Bpackage.getButtonsPlaced());
        tooltipOfSections.addCustom(sectionsPanel,5f);

    }


    @Override
    public void clearUI() {
        buttons.clear();
        for (CustomPanelAPI value : panelsOfSections.values()) {
            tooltipOfSections.removeComponent(value);
        }
        mainPanel.removeComponent(tooltipOfSections);
    }
    public void addUI(){
        width = parentPanel.getPosition().getWidth();
        height = parentPanel.getPosition().getHeight();
        createTitleMenu();
        createTitleSection();
        buttons.clear();
        toolTipPanel = mainPanel.createCustomPanel(width,height-lastY-10f,null);

        tooltipOfSections = toolTipPanel.createUIElement(width,height-lastY-10f,true);
        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            createSectionMenu(megaStructureSection);
        }
        lastYForSection = lastY;
        toolTipPanel.addUIElement(tooltipOfSections).inTL(0,0);
        mainPanel.addComponent(toolTipPanel).inTL(-5,lastYForSection);
    }
    @Override
    public void initUI() {
        mainPanel = parentPanel.createCustomPanel(parentPanel.getPosition().getWidth(),parentPanel.getPosition().getHeight(),this);
        addUI();
    }
    public void resetEntireUI(){
        clearUI();
        addUI();
    }
    public void resetSection(String sectionID){
        offset = tooltipOfSections.getExternalScroller().getYOffset();
        buttons.clear();
        toolTipPanel.removeComponent(tooltipOfSections);
        mainPanel.removeComponent(toolTipPanel);
        panelsOfSections.clear();
        toolTipPanel = mainPanel.createCustomPanel(width,height-lastYForSection-10f,null);
        tooltipOfSections = toolTipPanel.createUIElement(width,height-lastYForSection-10f,true);

        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            createSectionMenu(megaStructureSection);
        }
        toolTipPanel.addUIElement(tooltipOfSections).inTL(0,0);
        mainPanel.addComponent(toolTipPanel).inTL(-5,lastYForSection);
        tooltipOfSections.getExternalScroller().setYOffset(offset);
        mainMenu.reInitalizeButtonUI();
        mainMenu.resetMarketData();


    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public GPBaseMegastructure getMegastructure() {
        return megastructureReferedTo;
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
        for (ButtonAPI button : buttons) {
            if(button.isChecked()){
                button.setChecked(false);
                ButtonData buttonData = (ButtonData) button.getCustomData();
                if(AoTDMisc.isStringValid(buttonData.getCustomCommand())){
                    if(buttonData.getCustomCommand().contains("restore")){
                        BasePopUpDialog dialog = new RestorationDialog((GPMegaStructureSection) buttonData.getCustomData(),this,"Megastructure Restoration");
                        CustomPanelAPI panelAPI = Global.getSettings().createCustom(800,360,dialog);
                        UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
                        dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);
                    }
                    if(buttonData.getCustomCommand().contains("pauseRestore")){
                        BasePopUpDialog dialog = new PauseRestoration((GPMegaStructureSection) buttonData.getCustomData(),this,null);
                        CustomPanelAPI panelAPI = Global.getSettings().createCustom(800,200,dialog);
                        UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
                        dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);
                    }
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
}
