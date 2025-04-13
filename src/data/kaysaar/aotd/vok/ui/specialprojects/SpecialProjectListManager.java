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
    ButtonAPI backProject;
    ButtonAPI cancelProject;
    ButtonAPI startProject;
    SpecialProjectUIManager manager;
    HashMap<ButtonAPI,AoTDSpecialProject> buttons = new HashMap<>();
    public boolean update= false;

    public SpecialProjectListManager(float width, float height, SpecialProjectUIManager manager) {
        this.manager = manager;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        tooltipPanel = Global.getSettings().createCustom(width, height, null);
        currentShowcasePanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = tooltipPanel.createUIElement(width, height, true);
        float opad = 0f;
        for (AoTDSpecialProject value : SpecialProjectManager.getInstance().getProjects().values()) {
            tooltip.addCustom(createSectionForProject(width - 10, height, value), opad);
            opad = 5f;
        }


        tooltipPanel.addUIElement(tooltip).inTL(0, 0);

        mainPanel.addComponent(tooltipPanel).inTL(0, 0);
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
                if(data.equals("show_info")){
                    mainPanel.removeComponent(tooltipPanel);
                    currentShowcasePanel = Global.getSettings().createCustom(currentShowcasePanel.getPosition().getWidth(), currentShowcasePanel.getPosition().getHeight(), null);
                    manager.getCurrProjectShowcase().setProject(buttons.get(button));
                    manager.getCurrProjectShowcase().createUI();
                    currentShowcasePanel.addComponent(createDetailedSectionOfProject(currentShowcasePanel.getPosition().getWidth(), currentShowcasePanel.getPosition().getHeight(), buttons.get(button)));
                    mainPanel.addComponent(currentShowcasePanel);
                }
            }
        }
        if (backProject != null && backProject.isChecked()) {
            backProject.setChecked(false);
            backProject = null;
            startProject = null;
            cancelProject = null;
            manager.getCurrProjectShowcase().setProject(SpecialProjectManager.getInstance().getCurrentlyOnGoingProject());
            manager.getCurrProjectShowcase().createUI();
            mainPanel.removeComponent(currentShowcasePanel);
            mainPanel.addComponent(tooltipPanel);

        }
        if (startProject != null && startProject.isChecked()) {
            backProject.setChecked(false);
            SpecialProjectManager.getInstance().setCurrentlyOnGoingProject((AoTDSpecialProject) startProject.getCustomData());
            backProject = null;
            startProject = null;
            cancelProject = null;
            mainPanel.removeComponent(currentShowcasePanel);
            mainPanel.addComponent(tooltipPanel);


        }
        if (cancelProject != null && cancelProject.isChecked()) {
            cancelProject.setChecked(false);
            SpecialProjectManager.getInstance().setCurrentlyOnGoingProject(null);
            backProject = null;
            startProject = null;
            cancelProject = null;
            manager.getCurrProjectShowcase().setProject(SpecialProjectManager.getInstance().getCurrentlyOnGoingProject());
            manager.getCurrProjectShowcase().createUI();
            mainPanel.removeComponent(currentShowcasePanel);
            mainPanel.addComponent(tooltipPanel);


        }
    }

    public void updateProjects() {
        if(update){
            update = false;
            for (ButtonAPI buttonAPI : buttons.keySet()) {
                String id = (String) buttonAPI.getCustomData();
                if(id.equals("start_project")){
                    if(!SpecialProjectManager.getInstance().isCurrentOnGoing(buttons.get(buttonAPI))){
                        if(buttonAPI.isEnabled()){
                            buttonAPI.setEnabled(false);
                        }

                    }
                    else{
                        buttonAPI.setEnabled(true);
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

    public CustomPanelAPI createSectionForProject(float width, float height, AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, new UILinesRenderer(0f));
        ((UILinesRenderer) test.getPlugin()).setPanel(test);
        TooltipMakerAPI tooltip = test.createUIElement(width, height, false);
        project.createTooltipForButton(tooltip, width);
        ButtonAPI button = tooltip.addButton("Show additional info", "show_info", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, width-20, 30, 10f);
        button.getPosition().setXAlignOffset(width/2-(button.getPosition().getWidth()/2)-5);
        buttons.put(button,project);
        HologramViewer viewer = SpecialProjectManager.createHologramViewer(project.getProjectSpec(), true);
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width - viewer.getComponentPanel().getPosition().getWidth(), 20);
        test.getPosition().setSize(width, tooltip.getHeightSoFar() + 5);
        test.addUIElement(tooltip).inTL(0, 0);
        return test;
    }

    public CustomPanelAPI createDetailedSectionOfProject(float width, float height, AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, new UILinesRenderer(0f));
        TooltipMakerAPI tooltip = test.createUIElement(width, height-35, true);
        TooltipMakerAPI tooltipButtons = test.createUIElement(width,30,false);
        project.createDetailedTooltipForButton(tooltip, width);
        backProject = tooltipButtons.addButton("Show other projects", "show_projects", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, 0);
        if(SpecialProjectManager.getInstance().isCurrentOnGoing(project)){
            cancelProject = tooltipButtons.addButton("Pause Project", project, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, -30f);
            cancelProject.getPosition().setXAlignOffset(width - cancelProject.getPosition().getWidth() - 10);
        }
        else{
            startProject = tooltipButtons.addButton("Start Project", project, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180, 30, -30f);
            if (SpecialProjectManager.getInstance().getCurrentlyOnGoingProject() != null) {
                startProject.setEnabled(false);
            }
            startProject.getPosition().setXAlignOffset(width - startProject.getPosition().getWidth() - 10);
        }


        float heights = tooltip.getHeightSoFar();
        HologramViewer viewer = SpecialProjectManager.createHologramViewer(project.getProjectSpec(), true);
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width - viewer.getComponentPanel().getPosition().getWidth(), 20);
        tooltip.setHeightSoFar(heights);
        test.addUIElement(tooltip).inTL(0, 0);
        test.addUIElement(tooltipButtons).inTL(0,height-35);
        return test;
    }

}
