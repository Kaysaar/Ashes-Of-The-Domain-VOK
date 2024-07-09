package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.ProgressBarRender;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.RightMouseTooltipMover;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipInfoGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpecialProjectManager extends BaseOptionPanelManager implements OptionPanelInterface {
    CustomPanelAPI listOfOptions;
    CustomPanelAPI projectShowcase;
    ArrayList<ButtonAPI>buttonsOfStages = new ArrayList<>();
    ArrayList<ButtonAPI>buttonsOfProjects= new ArrayList<>();;
    float width =UIData.WIDTH_OF_OPTIONS - 10;
    public float heightOfProgressionBar = 20f;
    @Override
    public CustomPanelAPI getOptionPanel() {
        return null;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return null;
    }

    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return null;
    }
    public SpecialProjectManager(CustomPanelAPI panel){;
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        YHeight = panel.getPosition().getHeight() -40-70;

    }
    public void createSpecialProjectListPanel(){
        listOfOptions =  panel.createCustomPanel(width*0.3f,panel.getPosition().getHeight(),null);
        panel.addComponent(listOfOptions).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10,70);

    }
    public void createSpecialProjectShowcase(GPOption option){
        UILinesRenderer renederer = new UILinesRenderer(0f);

        float widthOfProjPanel = width * 0.8f;
        projectShowcase = panel.createCustomPanel(widthOfProjPanel,YHeight,renederer);
        CustomPanelAPI titlePanel = createTopPanelOfProject(option,projectShowcase,widthOfProjPanel,20);
        CustomPanelAPI stagePanel = createProjectStagePanel(option,projectShowcase,widthOfProjPanel*0.3f,YHeight-250).one;
        CustomPanelAPI descriptionPanel = createDescriptionPanelOfProject(option,projectShowcase,widthOfProjPanel,100);
        CustomPanelAPI rewardPanel = createRewardPanel(option,projectShowcase,widthOfProjPanel,YHeight-250);
        CustomPanelAPI buttonPanel = createButtonBelow(option,projectShowcase,widthOfProjPanel*0.2f,30);
        CustomPanelAPI progressionBar = createProgressionBar(option,projectShowcase,widthOfProjPanel-10,20);
        projectShowcase.addComponent(titlePanel).inTL(0,0);
        projectShowcase.addComponent(descriptionPanel).inTL(0,30);
        projectShowcase.addComponent(stagePanel).inTL(5,155);
        projectShowcase.addComponent(rewardPanel).inTL(30+widthOfProjPanel*0.3f,155);
        projectShowcase.addComponent(buttonPanel).inTL(widthOfProjPanel*0.8f-10,YHeight-80);
        projectShowcase.addComponent(progressionBar).inTL(5,YHeight-30);
        renederer.setPanel(projectShowcase);
        renederer.setPanel(stagePanel);
        panel.addComponent(projectShowcase).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 15,70);

    }
    public CustomPanelAPI createRewardPanel(GPOption option , CustomPanelAPI parentPanel,float width, float height){
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,false);
        ShipHullSpecAPI hull = Global.getSettings().getHullSpec(option.spec.getRewardId());
        String vesselName=hull.getHullName();
        tooltip.setParaFont(Fonts.ORBITRON_12);
        tooltip.addPara("Current state of renovation of "+vesselName+" %s",0f,Color.ORANGE,"0%");
        tooltip.addCustom(ShipInfoGenerator.getShipImage(hull,height-50, Color.gray).one,15f);
        PositionAPI pos = tooltip.getPrev().getPosition();
        tooltip.addCustom(ShipInfoGenerator.getShipImage(hull,height-50, null,0f).one,5f);
        tooltip.getPrev().getPosition().inTL(pos.getX(),-pos.getY()-pos.getHeight());
        panel.addUIElement(tooltip).inTL(0,0);
        return panel;
    }
    public CustomPanelAPI createTopPanelOfProject(GPOption option , CustomPanelAPI parentPanel,float width, float height){
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,false);
        tooltip.setTitleFont(Fonts.ORBITRON_16);
        tooltip.addTitle("Project :"+option.spec.getNameOverride());
        panel.addUIElement(tooltip).inTL(0,0);
        return panel;
    }
    public CustomPanelAPI createProgressionBar(GPOption option , CustomPanelAPI parentPanel,float width, float height){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,renderer);
        renderer.setPanel(panel);
        renderer.enableProgressMode(0f);
        return panel;
    }
    public CustomPanelAPI createButtonBelow(GPOption option , CustomPanelAPI parentPanel,float width, float height){
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,false);
        tooltip.addButton("Start project",null,width-5,height,0f);
        panel.addUIElement(tooltip).inTL(0,0);
        return panel;
    }
    public CustomPanelAPI createDescriptionPanelOfProject(GPOption option , CustomPanelAPI parentPanel,float width, float height){
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,null);
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,true);
        tooltip.addPara(option.getSpec().getDescriptionOfProject(),0f);
        panel.addUIElement(tooltip).inTL(0,0);
        return panel;
    }
    public Pair<CustomPanelAPI,ArrayList<ButtonAPI>> createProjectStagePanel(GPOption option, CustomPanelAPI parentPanel, float width, float height){
        ProgressBarRender renderer = new ProgressBarRender();
        CustomPanelAPI panel = parentPanel.createCustomPanel(width,height,renderer);
        ArrayList<ButtonAPI>buttons = new ArrayList<>();
        TooltipMakerAPI tooltip = panel.createUIElement(width,height-25,true);
        TooltipMakerAPI tooltip2 = panel.createUIElement(width,20,true);
        tooltip2.setTitleFont(Fonts.ORBITRON_16);
        tooltip2.addTitle("Production Stages",Color.ORANGE);
        for (Map.Entry<Integer, Pair<String, String>> entry : option.getSpec().getStageNameAndDecsMap().entrySet()) {
            String name = entry.getValue().one;
            ButtonAPI button = tooltip.addButton(name,null,width-10,30,30f);
            button.setEnabled(false);
            buttons.add(button);

        }
        renderer.buttons.addAll(buttons);
        renderer.setPanelAPI(panel);
        panel.addUIElement(tooltip2).inTL(-5,-30);
        panel.addUIElement(tooltip).inTL(0,25);
        return new Pair<>(panel,buttons);

    }
    @Override
    public void init() {
        createSpecialProjectListPanel();
        createSpecialProjectShowcase(GPManager.getInstance().getSpecialProjects().get(0));
        this.mainPanel.addComponent(panel).inTL(0, 0);
    }

    @Override
    public void clear() {
        buttonsOfStages.clear();
        buttonsOfProjects.clear();
        panel.removeComponent(listOfOptions);
        panel.removeComponent(projectShowcase);
        mainPanel.removeComponent(panel);
    }

    @Override
    public void reInit() {
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        init();
    }
}
