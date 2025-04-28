package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.industry.Commerce;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.ui.basecomps.LabelComponent;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.specialprojects.dialogs.PauseProjectDialog;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class CurrentSpecialProjectShowcase implements CustomUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    CustomPanelAPI insiderPanel;
    AoTDSpecialProject currProjectShowing;
    SpecialProjectUIManager uiManager;
    UILinesRenderer renderer;
    private ButtonAPI buttonAPI;
    private ButtonAPI buttonAPI2;
    IntervalUtil util = new IntervalUtil(2, 2);
    LabelAPI label;

    public CurrentSpecialProjectShowcase(float width, float height, SpecialProjectUIManager manager) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.uiManager = manager;
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        currProjectShowing = SpecialProjectManager.getInstance().getCurrentlyOnGoingProject();
        createUI();
    }

    public void createUI() {
        clearUI();
        if (currProjectShowing != null) {
            float width = mainPanel.getPosition().getWidth();
            insiderPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
            TooltipMakerAPI tooltip = insiderPanel.createUIElement(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), false);
            tooltip.setTitleFont(Fonts.ORBITRON_20AA);
            tooltip.addTitle(currProjectShowing.getNameOverride());
            tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(mainPanel.getPosition().getWidth() - 95, 24, 24, currProjectShowing.getGpCostFromStages(), new HashMap<>()), 2f);

            ProgressBarComponent component = new ProgressBarComponent(mainPanel.getPosition().getWidth() - 110, 18, currProjectShowing.getTotalProgress(), Misc.getBasePlayerColor().darker());
            tooltip.addCustom(component.getRenderingPanel(), 3f);
            LabelAPI labelAPI = tooltip.addSectionHeading("" + (int) (currProjectShowing.getTotalProgress() * 100) + "%", Misc.getTextColor(), null, Alignment.MID, mainPanel.getPosition().getWidth() - 110, -18f);
            HologramViewer viewer = SpecialProjectManager.createHologramViewer(currProjectShowing.getProjectSpec(), 95);
            viewer.setRenderLine(false);
            addCurrentStagesWork(tooltip);
            float y = tooltip.getHeightSoFar() + 7;
            buttonAPI = tooltip.addButton("Pause Project", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, ((width - 20) / 2) - 7, 20, 0f);
            buttonAPI2 = tooltip.addButton("Show additional info", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, ((width - 20) / 2) - 7, 20, 0f);

            buttonAPI2.getPosition().inTL(-88, y);
            label.flash(1f, 1f);
            buttonAPI.getPosition().inTL(mainPanel.getPosition().getWidth() - 95 - buttonAPI.getPosition().getWidth() - 12, y);
            insiderPanel.addUIElement(tooltip).inTL(95, 5);
            insiderPanel.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            mainPanel.addComponent(insiderPanel);
        }


    }

    public void addCurrentStagesWork(TooltipMakerAPI tooltip) {
        int amount = currProjectShowing.getCurrentlyAttemptedStages().size();
        if (amount == 0) {
            label = tooltip.addSectionHeading("Currently working on none of stages!", Misc.getNegativeHighlightColor(), null, Alignment.MID, mainPanel.getPosition().getWidth() - 110, 3f);
        } else if (amount == 1) {
            label = tooltip.addSectionHeading("Currently working on 1 stage", Misc.getTooltipTitleAndLightHighlightColor(), null, Alignment.MID, mainPanel.getPosition().getWidth() - 110, 3f);
        } else {
            label = tooltip.addSectionHeading("Currently working on " + amount + " stages", Misc.getPositiveHighlightColor(), null, Alignment.MID, mainPanel.getPosition().getWidth() - 110, 3f);
        }

    }

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
        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        util.advance(amount);
        if (util.intervalElapsed()) {
            if (label != null) {
                label.flash(1f, 1f);
            }
        }

        if (!SpecialProjectManager.getInstance().isCurrentOnGoing(currProjectShowing)) {
            currProjectShowing = SpecialProjectManager.getInstance().getCurrentlyOnGoingProject();
            createUI();
        }
        if (buttonAPI2 != null && buttonAPI2.isChecked()&&currProjectShowing!=null) {
            buttonAPI2.setChecked(false);
            uiManager.getListManager().createDetailedProjectMenu(currProjectShowing);
        }
        if (buttonAPI != null && buttonAPI.isChecked()&&currProjectShowing!=null) {
            buttonAPI.setChecked(false);
            BasePopUpDialog dialog = new PauseProjectDialog("Pause Special Project",uiManager,currProjectShowing);
            AoTDMisc.initPopUpDialog(dialog,750,160);
        }
    }

    private void clearUI() {
        label = null;
        buttonAPI = null;
        buttonAPI2 = null;
        mainPanel.removeComponent(insiderPanel);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
