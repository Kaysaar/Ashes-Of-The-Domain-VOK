package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Pair;
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
    public CustomPanelAPI parentPanel;
    public CustomPanelAPI mainPanel;
    public  float width,height;
    public GPMegasturcutreMenu mainMenu;
    public LinkedHashMap<String,CustomPanelAPI>panelsOfSections = new LinkedHashMap<>();
    public CustomPanelAPI titlePanel;
    public CustomPanelAPI mainTitlePanel;
    public CustomPanelAPI toolTipPanel;
    public TooltipMakerAPI tooltipOfSections;
    public ArrayList<ButtonPackage>buttons = new ArrayList<>();
    public  float offset =0f;
    public float lastY = 0f;
    public float lastYForSection = 0f;
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
        mainTitlePanel = MegastructureUIMisc.createTitleSection(mainPanel, megastructureReferedTo,"Megastructure Sections");
        mainPanel.addComponent(mainTitlePanel).inTL(-5,lastY);
        lastY+=mainTitlePanel.getPosition().getHeight();

    }
    public void createSectionMenu(GPMegaStructureSection section,float offsetOpt,float offsetOther){
        ButtonPackage Bpackage = MegastructureUIMisc.createWidgetForSection(mainPanel,section,offsetOpt,offsetOther);
        CustomPanelAPI sectionsPanel = Bpackage.getPanelOfButtons();
        buttons.add(Bpackage);
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
            createSectionMenu(megaStructureSection,0f,0f);
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
        LinkedHashMap<String, Pair<Float,Float>> offsets = new LinkedHashMap<>();
        for (ButtonPackage buttonPackage : buttons) {
            offsets.put(buttonPackage.section.getSpec().getSectionID(),new Pair<>(buttonPackage.getTooltipHeightOptions(),buttonPackage.getTooltipHeightOther()));
        }
        buttons.clear();
        toolTipPanel.removeComponent(tooltipOfSections);
        mainPanel.removeComponent(toolTipPanel);
        panelsOfSections.clear();
        toolTipPanel = mainPanel.createCustomPanel(width,height-lastYForSection-10f,null);
        tooltipOfSections = toolTipPanel.createUIElement(width,height-lastYForSection-10f,true);

        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            Pair<Float,Float> pair = offsets.get(megaStructureSection.getSpec().getSectionID());
            createSectionMenu(megaStructureSection,pair.one,pair.two);
        }
        offsets.clear();
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
        for (ButtonPackage b : buttons) {
            for (ButtonAPI button : b.getButtonsPlaced()) {
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
                        if(buttonData.getCustomCommand().contains("moreInfo")){
                            OtherInfoPopUp dialog = new OtherInfoPopUp((GPMegaStructureSection) buttonData.getCustomData());
                            float width1 = 500;
                            float height1 = dialog.createUIMockup(Global.getSettings().createCustom(width1, 200, null));
                            CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, dialog);
                            float x = button.getPosition().getX()-(width1/4);
                            float y = button.getPosition().getY();
                            if (x + width1 >= Global.getSettings().getScreenWidth()) {
                                float diff = x + width1 - Global.getSettings().getScreenWidth();
                                x = x - diff - 5;

                            }
                            if (y - height1 <= 0) {
                                y = height1;
                            }
                            if (y > Global.getSettings().getScreenHeight()) {
                                y = Global.getSettings().getScreenHeight() - 10;
                            }

                            dialog.init(panelAPI, x, y, false);

                        }
                        buttonHasBeenPressed(buttonData);
                    }

                }
            }
        }

    }

    public void buttonHasBeenPressed(ButtonData data){

    }
    @Override
    public void processInput(List<InputEventAPI> events) {

    }
    @Deprecated
    @Override
    public void buttonPressed(Object buttonId) {

    }
}
