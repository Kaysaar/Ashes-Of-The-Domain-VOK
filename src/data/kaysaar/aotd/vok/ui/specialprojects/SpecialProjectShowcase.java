package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.specialprojects.*;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.RightMouseTooltipMover;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;
import java.util.List;

public class SpecialProjectShowcase implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;

    CustomPanelAPI objectOfInterest;
    CustomPanelAPI subMainPanel;
    public HologramViewer mainObject;
    RightMouseTooltipMover mover;
    AoTDSpecialProject project;
    public static float widthExpected = 1400; //1750
    public static float heightExpected = 1400;

    public HologramViewer getMainObject() {
        return mainObject;
    }

    public void setProject(AoTDSpecialProject project) {
        this.project = project;
    }

    //effective range must be 1120;
    public SpecialProjectShowcase(float width, float height,AoTDSpecialProject project) {
        if (width > widthExpected) {
            width = widthExpected;
        }
        this.project = project;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        createUI();

        renderer = new UILinesRenderer(0f);
        renderer.setBoxColor(Color.ORANGE);
        renderer.setPanel(mainPanel);
    }

    public void createUI() {
        if(subMainPanel!=null){
            mainPanel.removeComponent(subMainPanel);
            mover = null;
        }
        if(project !=null){
            objectOfInterest = Global.getSettings().createCustom(widthExpected, heightExpected, null);
            mover = new RightMouseTooltipMover();
             subMainPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
            TooltipMakerAPI tooltip = subMainPanel.createUIElement(subMainPanel.getPosition().getWidth(), subMainPanel.getPosition().getHeight(), true);
            tooltip.addSpacer(heightExpected);
            mainObject = SpecialProjectManager.createHologramViewer(project.getProjectSpec(), false,false);
            if(!SpecialProjectManager.getInstance().isCurrentOnGoing(project)){
                mainObject.setRenderLine(false);
            }
            else{
                mainObject.setRenderLine(true);
            }
            mover.init(subMainPanel, tooltip);
            float leftX = widthExpected - mainPanel.getPosition().getWidth();
            objectOfInterest.addComponent(mainObject.getComponentPanel()).inTL(objectOfInterest.getPosition().getWidth() / 2 - (mainObject.componentPanel.getPosition().getWidth() / 2), objectOfInterest.getPosition().getHeight() / 2 - (mainObject.componentPanel.getPosition().getHeight() / 2));
            project.getStagesForUI(objectOfInterest);

            tooltip.addCustomDoNotSetPosition(objectOfInterest).getPosition().inTL(-leftX / 2, 0);
            float border = -leftX / 2 - 5;
            if (leftX <= 0) {
                mover.setBorders(0, 0);
            } else {
                mover.setBorders(border, leftX / 2);
            }
            subMainPanel.addUIElement(tooltip).inTL(0, 0);
            if (leftX > 0) {
                float diffX = widthExpected / 2 - (mainPanel.getPosition().getWidth() / 2);
                float move = diffX + border;
                mover.setCurrOffset(move);
            }
            mover.advance(1f);
            mainPanel.addComponent(subMainPanel).inTL(0, 0);


            tooltip.getExternalScroller().setYOffset(heightExpected / 2 - (subMainPanel.getPosition().getHeight() / 2));
            subMainPanel.updateUIElementSizeAndMakeItProcessInput(tooltip);

            if (tooltip.getExternalScroller() != null) {
                ReflectionUtilis.invokeMethodWithAutoProjection("setMaxShadowHeight", tooltip.getExternalScroller(), 0);
                ReflectionUtilis.invokeMethodWithAutoProjection("setShowScrollbars", tooltip.getExternalScroller(), false);
            }

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
        if(mover!=null){
            mover.advance(amount);
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
