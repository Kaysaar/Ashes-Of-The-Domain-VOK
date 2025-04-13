package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.util.HashMap;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class CurrentSpecialProjectShowcase implements CustomUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    CustomPanelAPI insiderPanel;
    AoTDSpecialProject currProjectShowing;
    SpecialProjectUIManager uiManager;
    UILinesRenderer renderer;
    public CurrentSpecialProjectShowcase (float width, float height,SpecialProjectUIManager manager){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.uiManager = manager;
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        currProjectShowing = SpecialProjectManager.getInstance().getCurrentlyOnGoingProject();
        createUI();
    }
    public void createUI(){
        if(currProjectShowing!=null){
            insiderPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
            TooltipMakerAPI tooltip = insiderPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),false);
            tooltip.setTitleFont(Fonts.ORBITRON_16);
            tooltip.addTitle(currProjectShowing.getProjectSpec().getName());
            tooltip.addSectionHeading("Upkeep", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, mainPanel.getPosition().getWidth() - 110, 5f);
            tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(mainPanel.getPosition().getWidth() - 110, 20, 20, new HashMap<>(), new HashMap<>()), 5f);

            ProgressBarComponent component = new ProgressBarComponent(mainPanel.getPosition().getWidth() - 110, 18, currProjectShowing.getTotalProgress(), Misc.getBasePlayerColor().darker().darker());
            tooltip.addCustom(component.getRenderingPanel(), 5f);
            LabelAPI labelAPI = tooltip.addSectionHeading("Project Progress : "+(int)(currProjectShowing.getTotalProgress()*100)+"%", Misc.getDarkHighlightColor(), null, Alignment.MID, mainPanel.getPosition().getWidth() - 110, -18f);
            ButtonAPI button = tooltip.addButton("")
            HologramViewer viewer = SpecialProjectManager.createHologramViewer(currProjectShowing.getProjectSpec(), 95);
            viewer.setRenderLine(false);
            tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(mainPanel.getPosition().getWidth() - viewer.getComponentPanel().getPosition().getWidth(), 10);
            insiderPanel.addUIElement(tooltip);
            mainPanel.addComponent(insiderPanel);
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
