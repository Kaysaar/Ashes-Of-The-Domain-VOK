package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.*;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipInfoGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpecialProjectManager extends BaseOptionPanelManager implements OptionPanelInterface {
    CustomPanelAPI listOfOptions;
    CustomPanelAPI projectShowcase;
    ArrayList<ButtonAPI> buttonsOfStages = new ArrayList<>();
    ArrayList<ButtonAPI> buttonsOfProjects = new ArrayList<>();
    ;
    float width = UIData.WIDTH_OF_OPTIONS - 10;
    public float heightOfProgressionBar = 20f;
    ButtonAPI currentProjectButton;

    public ButtonAPI getCurrentProjectButton() {
        return currentProjectButton;
    }

    @Override
    public CustomPanelAPI getOptionPanel() {
        return null;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return null;
    }
    float paddingY = 18;
    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return null;
    }

    public SpecialProjectManager(CustomPanelAPI panel) {
        ;
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        YHeight = panel.getPosition().getHeight() - paddingY - 70-19;

    }

    public void createSpecialProjectListPanel() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        float width = this.width * 0.3f;
        float height = YHeight;
        listOfOptions = panel.createCustomPanel(width, YHeight, renderer);

        renderer.setPanel(listOfOptions);
        TooltipMakerAPI tooltip = listOfOptions.createUIElement(width, 20, false);
        tooltip.addSectionHeading("Project List", Alignment.MID, 0f);
        TooltipMakerAPI tooltip2 = listOfOptions.createUIElement(width, height - 25, false);
        float pad = 5f;
        for (GpSpecialProjectData specialProject : GPManager.getInstance().getSpecialProjects()) {
            if (specialProject.canShow) {
                tooltip2.addCustom(createProjectTab(specialProject, listOfOptions, width - 10, 80), pad);
                pad = 20f;
            }

        }
        listOfOptions.addUIElement(tooltip).inTL(0, 0);
        listOfOptions.addUIElement(tooltip2).inTL(0, 25);
        panel.addComponent(listOfOptions).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 15, 70-paddingY);


    }


    public void createSpecialProjectShowcase(GpSpecialProjectData option) {
        UILinesRenderer renederer = new UILinesRenderer(0f);

        float widthOfProjPanel = width * 0.7f;
        projectShowcase = panel.createCustomPanel(widthOfProjPanel, YHeight, renederer);
        CustomPanelAPI titlePanel = createTopPanelOfProject(option, projectShowcase, widthOfProjPanel, 20);
        CustomPanelAPI stagePanel = createProjectStagePanel(option, projectShowcase, widthOfProjPanel * 0.4f, YHeight - 200).one;
        CustomPanelAPI descriptionPanel = createDescriptionPanelOfProject(option, projectShowcase, widthOfProjPanel, 100);
        CustomPanelAPI rewardPanel = createRewardPanel(option, projectShowcase, widthOfProjPanel * 0.6f, widthOfProjPanel * 0.6f);
        CustomPanelAPI buttonPanel = createButtonBelow(option, projectShowcase, widthOfProjPanel * 0.3f, 30);
        projectShowcase.addComponent(titlePanel).inTL(0, 2);
        projectShowcase.addComponent(descriptionPanel).inTL(0, 30);
        projectShowcase.addComponent(stagePanel).inTL(5, YHeight - (YHeight - 190));
        projectShowcase.addComponent(rewardPanel).inTL(30 + widthOfProjPanel * 0.4f, 155);
        projectShowcase.addComponent(buttonPanel).inTL(widthOfProjPanel * 0.7f - 10, YHeight - 40);
        renederer.setPanel(projectShowcase);
        renederer.setPanel(stagePanel);
        panel.addComponent(projectShowcase).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS + width * 0.3f, 70-paddingY);

    }

    public CustomPanelAPI createProjectTab(GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        ButtonAPI button = tooltip.addAreaCheckbox("", option, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, width - 10, height, 0f);
        LabelAPI title = tooltip.addPara(option.getSpec().getNameOverride(), Color.ORANGE, 0f);
        title.getPosition().inTL(10, 10);

        LabelAPI label = tooltip.addPara("Status : " + option.getStatusString(), option.getStatusColor(), 0f);

        CustomPanelAPI shipImg = ShipInfoGenerator.getShipImage(Global.getSettings().getHullSpec(option.getSpec().getRewardId()), height - 10, null).one;
        tooltip.addCustom(shipImg, 5f).getPosition().inTL(width - 10 - shipImg.getPosition().getWidth(), 5);
        buttonsOfProjects.add(button);
        title.autoSizeToWidth(width-tooltip.getPrev().getPosition().getWidth()-20);
        label.getPosition().inTL(10, 10+title.computeTextHeight(title.getText())+20);
        panel.addUIElement(tooltip).inTL(0, 0);
        return panel;
    }

    private CustomPanelAPI getItemLabel(final GpSpecialProjectData data,Map.Entry<String, Integer> entry) {
        if (entry.getValue() == 0) return null;
        CustomPanelAPI panel = mainPanel.createCustomPanel(400, 60, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(60, 60, false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(320, 60, false);
        LabelAPI labelAPI1 = null;

        if (Global.getSettings().getCommoditySpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getCommoditySpec(entry.getKey()).getName() + " : " + entry.getValue(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 10f, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(entry.getKey(), Submarkets.SUBMARKET_STORAGE));

        }
        if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {
            tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName(), 60, 60, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(entry.getKey()).getName() + " : " + entry.getValue(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 10f, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(entry.getKey(), Submarkets.SUBMARKET_STORAGE));

        }
        if (GPManager.getInstance().haveMetReqForItem(entry.getKey(), entry.getValue()) ||data.havePaidInitalCost) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());

        } else {
            labelAPI1.setColor(Misc.getNegativeHighlightColor());
        }
        labelAPI1.autoSizeToWidth(320);
        panel.addUIElement(tooltipMakerAPI).inTL(-10, -20);
        panel.addUIElement(labelTooltip).inTL(60, -14);
        return panel;
    }

    public CustomPanelAPI createRewardPanel(GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        ShipHullSpecAPI hull = Global.getSettings().getHullSpec(option.getSpec().getRewardId());
        tooltip.setParaFont(Fonts.ORBITRON_12);
        ArrayList<String> hilights = new ArrayList<>();
        hilights.addAll(option.getSpec().getHighlights());
        hilights.add(option.getTotalProgressPercent() + "%");

        String[] arr = new String[hilights.size()];
        arr = hilights.toArray(arr);
        tooltip.addPara(option.getSpec().getProgressString() + " %s", 0f, Color.ORANGE, arr);
        tooltip.addCustom(ShipInfoGenerator.getShipImage(hull, height - 50,new Color(24, 24, 24)).one, 15f);
        PositionAPI pos = tooltip.getPrev().getPosition();
        tooltip.getPrev().getPosition().inTL(width / 2 - pos.getWidth() / 2, height / 1.5f - pos.getHeight() / 2);
        pos = tooltip.getPrev().getPosition();
        tooltip.addCustom(ShipInfoGenerator.getShipImage(hull, height - 50, null, option.getTotalProgress()).one, 5f);
        tooltip.getPrev().getPosition().inTL(pos.getX(), -pos.getY() - pos.getHeight());
        panel.addUIElement(tooltip).inTL(0, 0);
        return panel;
    }

    public CustomPanelAPI createTopPanelOfProject(GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        tooltip.setTitleFont(Fonts.ORBITRON_16);
        tooltip.addTitle("Project :" + option.getSpec().getNameOverride());
        panel.addUIElement(tooltip).inTL(0, 0);
        return panel;
    }



    public CustomPanelAPI createButtonBelow(final GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        SpecialProjectButtonData data = new SpecialProjectButtonData(option);

        currentProjectButton = tooltip.addButton(data.getNameForButton(), data, width - 5, height, 0f);
        boolean hasReq = !option.havePaidInitalCost&&(!option.getSpec().getItemInitCostMap().isEmpty()||option.getSpec().getCredistCost()>0);
        if(hasReq){
            tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return true;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 400;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addTitle("Project requirements");
                    if(option.getSpec().getCredistCost()>0){
                        tooltip.addPara("Credit Cost : %s",10f,Color.ORANGE,Misc.getDGSCredits(option.getSpec().getCredistCost()));
                    }
                    if (!option.getSpec().getItemInitCostMap().isEmpty()){
                        for (Map.Entry<String, Integer> entry : option.getSpec().getItemInitCostMap().entrySet()) {
                            tooltip.addCustom(getItemLabel(option,entry),15f);
                        }
                    }
                }
            }, TooltipMakerAPI.TooltipLocation.ABOVE);
            LabelAPI labela = tooltip.addPara("Warning! There is initial cost of project, hover over button to see cost!",Misc.getNegativeHighlightColor(),0f);
            labela.getPosition().inTL(10,-35-labela.computeTextHeight(labela.getText()));
        }
        if (option.isFinished() && !option.getSpec().isRepeatable()) {
            currentProjectButton.setEnabled(false);
        }
        if(!option.havePaidInitalCost&&hasReq){
            if(!GPManager.getInstance().haveMetReqForItems(option.getSpec().getProjectId())||Global.getSector().getPlayerFleet().getCargo().getCredits().get()<option.getSpec().getCredistCost()){
                currentProjectButton.setEnabled(false);
            }

        }
        panel.addUIElement(tooltip).inTL(0, 0);
        return panel;
    }

    public CustomPanelAPI createDescriptionPanelOfProject(GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, true);
        tooltip.addPara(option.getSpec().getDescriptionOfProject(), 0f);
        panel.addUIElement(tooltip).inTL(0, 0);
        return panel;
    }

    public Pair<CustomPanelAPI, ArrayList<ButtonAPI>> createProjectStagePanel(GpSpecialProjectData option, CustomPanelAPI parentPanel, float width, float height) {
        ProgressBarRender renderer = new ProgressBarRender();
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, renderer);
        ArrayList<ButtonAPI> buttons = new ArrayList<>();
        TooltipMakerAPI tooltip = panel.createUIElement(width, height - 25, true);
        TooltipMakerAPI tooltip2 = panel.createUIElement(width, 20, true);
        tooltip2.setTitleFont(Fonts.ORBITRON_16);
        tooltip2.addTitle("Production Stages", Color.ORANGE);
        float pad = 5f;
        for (int i = 0; i < option.getSpec().getAmountOfStages(); i++) {
            Pair<CustomPanelAPI, ButtonAPI> entity = getStagePanel(option, i, panel, width - 10, 120);
            tooltip.addCustom(entity.one, pad);
            buttons.add(entity.two);
            entity.two.setEnabled(false);
            pad = 30f;
        }
        renderer.buttons.addAll(buttons);
        buttonsOfStages.addAll(buttons);
        renderer.setPanelAPI(panel);
        panel.addUIElement(tooltip2).inTL(-5, -30);
        panel.addUIElement(tooltip).inTL(0, 25);
        return new Pair<>(panel, buttons);

    }

    public Pair<CustomPanelAPI, ButtonAPI> getStagePanel(GpSpecialProjectData option, int stage, CustomPanelAPI parentPanel, float width, float height) {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panel = parentPanel.createCustomPanel(width, height, null);
        CustomPanelAPI progressionBar = panel.createCustomPanel(width - 11, 20, renderer);
        CustomPanelAPI costOfStage = UIData.getGPCostPanelSpecialProjStage(width, 30, option.getSpec(), stage);
        renderer.enableProgressMode(option.getProgressOfStage(stage));
        renderer.setPanel(progressionBar);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        Color base = Misc.getPositiveHighlightColor();
        if (option.getCurrentStage() != stage) {
            base = Misc.getGrayColor();
        }
        ButtonAPI button = tooltip.addAreaCheckbox("", null, base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, width, height, 0f);
        button.setEnabled(false);
        button.setClickable(false);
        tooltip.addPara("Progress %s", 0f, Color.ORANGE, option.getProgressOfStagePercent(stage) + "%").getPosition().inTL(10, height - 50);
        LabelAPI labelStage = tooltip.addPara("Cost of stage: ", 0f);
        labelStage.getPosition().inTL(10, 37);
        LabelAPI label = tooltip.addSectionHeading(option.getSpec().getStageNameAndDecsMap().get(stage).one, Alignment.MID, 0f);
        label.getPosition().inTL(5, 0);
        tooltip.addCustom(progressionBar, 0f).getPosition().inTL(10, height - 30);
        tooltip.addCustom(costOfStage, 0f).getPosition().inTL(10 + labelStage.computeTextWidth(labelStage.getText()), 30);
        panel.addUIElement(tooltip).inTL(-5, 0);
        return new Pair<>(panel, button);
    }

    @Override
    public void init() {
        createSpecialProjectListPanel();
        this.mainPanel.addComponent(panel).inTL(0, 0);
    }

    @Override
    public void clear() {
        clearSpecProjectPanelOnly();
        buttonsOfProjects.clear();
        panel.removeComponent(listOfOptions);
        mainPanel.removeComponent(panel);
    }

    public void clearSpecProjectPanelOnly() {
        buttonsOfStages.clear();
        panel.removeComponent(projectShowcase);
    }

    public void clearListOfProjectsOnly() {
        buttonsOfProjects.clear();
        panel.removeComponent(listOfOptions);
    }

    @Override
    public void reInit() {
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        init();
    }

    @Override
    public void advance(float amount) {
        for (ButtonAPI buttonsOfProject : buttonsOfProjects) {
            if (buttonsOfProject.isChecked()) {
                buttonsOfProject.setChecked(false);
                GpSpecialProjectData option = (GpSpecialProjectData) buttonsOfProject.getCustomData();
                clearSpecProjectPanelOnly();
                createSpecialProjectShowcase(option);
                break;
            }
        }
        for (ButtonAPI buttonsOfProject : buttonsOfStages) {
            buttonsOfProject.highlight();
        }
        if (currentProjectButton != null && currentProjectButton.isChecked()) {
            currentProjectButton.setChecked(false);
            SpecialProjectButtonData data = (SpecialProjectButtonData) currentProjectButton.getCustomData();
            if (data.actionOfButton.equals(SpecialProjectButtonData.ACTION.CANCEL)) {
                GPManager.getInstance().setCurrentFocus(null);
                clearSpecProjectPanelOnly();
                clearListOfProjectsOnly();
                createSpecialProjectListPanel();
                createSpecialProjectShowcase(data.getSpecialProject());
            } else {
                GPManager.getInstance().setCurrentFocus(data.getSpecialProject());
                clearSpecProjectPanelOnly();
                clearListOfProjectsOnly();
                createSpecialProjectListPanel();
                createSpecialProjectShowcase(data.getSpecialProject());
            }

        }
    }
}
