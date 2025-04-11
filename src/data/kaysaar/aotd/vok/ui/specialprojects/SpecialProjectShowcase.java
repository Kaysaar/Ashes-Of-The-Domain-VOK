package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.specialprojects.*;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.ShipHologram;
import data.kaysaar.aotd.vok.ui.customprod.components.RightMouseTooltipMover;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialProjectShowcase implements CustomUIPanelPlugin {
    ArrayList<SpecialProjectStageWindow> stagesWindow = new ArrayList<>();
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;

    CustomPanelAPI objectOfInterest;
    HologramViewer mainObject;
    RightMouseTooltipMover mover;

    public static float widthExpected = 1400; //1750
    public static float heightExpected = 1400;

    //effective range must be 1120;
    public SpecialProjectShowcase(float width, float height) {
        if (width > widthExpected) {
            width = widthExpected;
        }
        mainPanel = Global.getSettings().createCustom(width, height, this);
        objectOfInterest = Global.getSettings().createCustom(widthExpected, heightExpected, null);
        AoTDSpecialProject project = SpecialProjectManager.getInstance().getProject("uaf_supercap_slv_project");
        mover = new RightMouseTooltipMover();
        float subWidth = (width - 20);
        float subWidthMain = 600;
        float subWidthSub = (width - 20)-600;
        CustomPanelAPI test = mainPanel.createCustomPanel(width, height , null);
        TooltipMakerAPI tooltip = test.createUIElement(test.getPosition().getWidth(), test.getPosition().getHeight(), true);
        tooltip.addSpacer(heightExpected);
        mainObject = new HologramViewer(500, 500, new ShipHologram("uaf_supercap_slv_core"));
        mainObject.setColorOverlay(Color.red);
        mover.init(test, tooltip);
        float leftX = widthExpected - width;
        objectOfInterest.addComponent(mainObject.getComponentPanel()).inTL(objectOfInterest.getPosition().getWidth() / 2 - (mainObject.componentPanel.getPosition().getWidth() / 2), objectOfInterest.getPosition().getHeight() / 2 - (mainObject.componentPanel.getPosition().getHeight() / 2));
        project.getStagesForUI(objectOfInterest);

        tooltip.addCustomDoNotSetPosition(objectOfInterest).getPosition().inTL(-leftX / 2, 0);
        float border = -leftX / 2 - 5;
        if (leftX <= 0) {
            mover.setBorders(0, 0);
        } else {
            mover.setBorders(border, leftX / 2);
        }
        test.addUIElement(tooltip).inTL(0, 0);
        if (leftX > 0) {
            float diffX = widthExpected / 2 - (width / 2);
            float move = diffX + border;
            mover.setCurrOffset(move);
        }
        mover.advance(1f);
        mainPanel.addComponent(test).inTL(0, 0);


        tooltip.getExternalScroller().setYOffset(heightExpected / 2 - (test.getPosition().getHeight() / 2));
        test.updateUIElementSizeAndMakeItProcessInput(tooltip);
        renderer = new UILinesRenderer(0f);
        renderer.setBoxColor(Color.ORANGE);
        renderer.setPanel(mainPanel);
        if (tooltip.getExternalScroller() != null) {
            ReflectionUtilis.invokeMethodWithAutoProjection("setMaxShadowHeight", tooltip.getExternalScroller(), 0);
            ReflectionUtilis.invokeMethodWithAutoProjection("setShowScrollbars", tooltip.getExternalScroller(), false);
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
        mover.advance(amount);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
