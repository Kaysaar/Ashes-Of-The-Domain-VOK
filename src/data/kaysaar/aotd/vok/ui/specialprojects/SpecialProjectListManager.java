package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.*;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.util.HashMap;
import java.util.List;

public class SpecialProjectListManager implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;
    CustomPanelAPI tooltipPanel;
    CustomPanelAPI currentShowcasePanel;
    CustomPanelAPI majorPanel;
    ButtonAPI backProject;
    ButtonAPI cancelProject;
    ButtonAPI startProject;
    ButtonAPI filterButton;
    SpecialProjectUIManager manager;
    TextFieldAPI field;
    HashMap<ButtonAPI, AoTDSpecialProject> buttons = new HashMap<>();
    public boolean update = false;
    int opads = 0;

    public SpecialProjectListManager(float width, float height, SpecialProjectUIManager manager) {
        this.manager = manager;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        tooltipPanel = Global.getSettings().createCustom(width, height - opads, null);
        currentShowcasePanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = tooltipPanel.createUIElement(width, height - opads, true);
        majorPanel = Global.getSettings().createCustom(width, opads, null);
        TooltipMakerAPI tooltip2 = majorPanel.createUIElement(width, opads, false);
//        filterButton = tooltip2.addButton("Change filters", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, (width / 2) - 10, 20, 0f);
//        filterButton.getPosition().inTL((width / 2) + 5, 5);
        float opad = 5f;
        boolean haveAtLeastOne = false;
        for (AoTDSpecialProject value : SpecialProjectManager.getInstance().getProjects().values()) {
            if(!value.wasEverDiscovered())continue;
            tooltip.addCustom(createSectionForProject(width - 10, 80, value), opad);
            haveAtLeastOne = true;
            opad = 5f;
        }
        if(!haveAtLeastOne){
            LabelAPI labelAPI = tooltip.addSectionHeading("No Project Available",Misc.getTooltipTitleAndLightHighlightColor(),null,Alignment.MID,0f);
            labelAPI.getPosition().inTL(0,height/2-(labelAPI.getPosition().getHeight()/2));
//            field = tooltip2.addTextField((width / 2) - 10, 20, Fonts.DEFAULT_SMALL, 5);

        }


        tooltipPanel.addUIElement(tooltip).inTL(0, 0);
        majorPanel.addUIElement(tooltip2).inTL(0, 0);

        mainPanel.addComponent(tooltipPanel).inTL(0, opads);
        mainPanel.addComponent(majorPanel).inTL(0, 0);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons.keySet()) {
            if (button.isChecked()) {
                button.setChecked(false);
                String data = (String) button.getCustomData();
                if (data.equals("show_info")) {
                    AoTDSpecialProject project = buttons.get(button);
                    createDetailedProjectMenu(project);
                }
            }
        }
        if (backProject != null && backProject.isChecked()) {
            backProject.setChecked(false);
            backProject = null;
            startProject = null;
            cancelProject = null;
            AoTDSpecialProject project = SpecialProjectManager.getInstance().getCurrentlyOnGoingProject();
            if (project != null && manager.getCurrProjectShowcase().getProject() != null && !manager.getCurrProjectShowcase().getProject().getProjectSpec().getId().equals(project.getProjectSpec().getId())) {
                manager.getCurrProjectShowcase().setProject(project);
                manager.getCurrProjectShowcase().createUI();
            }
            createListUI();
            ;

        }
        if (startProject != null && startProject.isChecked()) {
            backProject.setChecked(false);
            SpecialProjectManager.getInstance().setCurrentlyOnGoingProject((AoTDSpecialProject) startProject.getCustomData());
            backProject = null;
            startProject = null;
            cancelProject = null;
            manager.getCurrProjectShowcase().getMainObject().setRenderLine(true);
            createListUI();
            ;


        }
        if (cancelProject != null && cancelProject.isChecked()) {
            cancelProject.setChecked(false);
            SpecialProjectManager.getInstance().setCurrentlyOnGoingProject(null);
            backProject = null;
            startProject = null;
            cancelProject = null;
            manager.getCurrProjectShowcase().setProject(SpecialProjectManager.getInstance().getCurrentlyOnGoingProject());
            manager.getCurrProjectShowcase().createUI();
            createListUI();


        }
    }

    public void createListUI() {
        mainPanel.removeComponent(currentShowcasePanel);
        mainPanel.removeComponent(majorPanel);
        mainPanel.removeComponent(tooltipPanel);
        mainPanel.addComponent(majorPanel).inTL(0, 0);
        mainPanel.addComponent(tooltipPanel).inTL(0, opads);
    }

    public void createDetailedProjectMenu(AoTDSpecialProject project) {

        mainPanel.removeComponent(tooltipPanel);
        mainPanel.removeComponent(majorPanel);
        mainPanel.removeComponent(currentShowcasePanel);
        currentShowcasePanel = Global.getSettings().createCustom(currentShowcasePanel.getPosition().getWidth(), currentShowcasePanel.getPosition().getHeight(), null);
        if (manager.getCurrProjectShowcase().getProject() != null && !manager.getCurrProjectShowcase().getProject().getProjectSpec().getId().equals(project.getProjectSpec().getId())) {
            manager.getCurrProjectShowcase().setProject(project);
            manager.getCurrProjectShowcase().createUI();
        }
        if(manager.getCurrProjectShowcase().getProject()==null){
            manager.getCurrProjectShowcase().setProject(project);
            manager.getCurrProjectShowcase().createUI();
        }
        currentShowcasePanel.addComponent(createDetailedSectionOfProject(currentShowcasePanel.getPosition().getWidth(), currentShowcasePanel.getPosition().getHeight(), project));
        mainPanel.addComponent(currentShowcasePanel).inTL(0, 0);
        ;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public CustomPanelAPI createSectionForProject(float width, float height, AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, new UILinesRenderer(0f));
        TooltipMakerAPI tooltip = test.createUIElement(width, height, false);
        HologramViewer viewer = SpecialProjectManager.createHologramViewer(project.getProjectSpec(), true, false);

        TooltipMakerAPI tooltip2 = test.createUIElement(width - viewer.getComponentPanel().getPosition().getWidth() - 5, height, false);

        ButtonAPI button = tooltip.addAreaCheckbox(null, "show_info", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), width, height, 0f);
        button.getPosition().inTL(5, 0);
        project.createTooltipForButton(tooltip2, width, true);
        button.getPosition().setXAlignOffset(width / 2 - (button.getPosition().getWidth() / 2) - 5);
        buttons.put(button, project);
        viewer.setRenderLine(false);

        test.addUIElement(tooltip).inTL(5, 0);
        test.addUIElement(tooltip2).inTL(viewer.getComponentPanel().getPosition().getWidth() + 10, 5);
        test.addComponent(viewer.getComponentPanel()).inTL(0, 5);
        return test;
    }

    public CustomPanelAPI createDetailedSectionOfProject(float width, float height, AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, new UILinesRenderer(0f));
        TooltipMakerAPI tooltip = test.createUIElement(width, height - 40, true);
        TooltipMakerAPI tooltipButtons = test.createUIElement(width, 30, false);
        project.createDetailedTooltipForButton(tooltip, width);
        backProject = tooltipButtons.addButton("Show other projects", "show_projects", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, 0);
        if (SpecialProjectManager.getInstance().isCurrentOnGoing(project)) {
            cancelProject = tooltipButtons.addButton("Pause Project", project, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, -30f);
            cancelProject.getPosition().setXAlignOffset(width - cancelProject.getPosition().getWidth() - 10);
        } else {
            startProject = tooltipButtons.addButton("Start Project", project, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, -30f);
            if (SpecialProjectManager.getInstance().getCurrentlyOnGoingProject() != null) {
                startProject.setEnabled(false);
            }
            startProject.getPosition().setXAlignOffset(width - startProject.getPosition().getWidth() - 10);
        }


        float heights = tooltip.getHeightSoFar();
        tooltip.setHeightSoFar(heights);
        test.addUIElement(tooltip).inTL(0, 5);
        test.addUIElement(tooltipButtons).inTL(0, height - 35);
        return test;
    }

}
