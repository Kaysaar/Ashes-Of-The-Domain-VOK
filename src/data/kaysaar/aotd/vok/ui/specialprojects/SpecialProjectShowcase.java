package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.basecomps.StencilBlockerEndPlugin;
import data.kaysaar.aotd.vok.ui.basecomps.StencilBlockerPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.RightMouseTooltipMover;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialProjectShowcase implements CustomUIPanelPlugin {
    ArrayList<SpecialProjectStageWindow> stagesWindow = new ArrayList<>();
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;
    CustomPanelAPI objectOfInterest;
    public static float widthExpected = 1750;

    //effective range must be 1120;
    public SpecialProjectShowcase(float width, float height) {
        if(width>widthExpected){
            width = widthExpected;
        }
        mainPanel = Global.getSettings().createCustom(width, height, this);
        CustomPanelAPI blocker = mainPanel.createCustomPanel(width-2,height+1,null);
        mainPanel.addComponent(blocker).inTL(0,-1);
        StencilBlockerPlugin plugin = new StencilBlockerPlugin(blocker);
        StencilBlockerEndPlugin plugin1 = new StencilBlockerEndPlugin();
        CustomPanelAPI initalizer = mainPanel.createCustomPanel(width,height,plugin);
        CustomPanelAPI ender = mainPanel.createCustomPanel(width,height,plugin1);

        RightMouseTooltipMover mover = new RightMouseTooltipMover();
        CustomPanelAPI test = mainPanel.createCustomPanel(width,height-30,mover);
        TooltipMakerAPI tooltip  = test.createUIElement(test.getPosition().getWidth()+15,test.getPosition().getHeight(),true);
        tooltip.addSpacer(height*3f);
        ImageViewer viewer = new ImageViewer(200,200,Global.getSettings().getCommoditySpec("compound").getIconName());
       viewer.setColorOverlay(Color.cyan);
        mover.init(test,tooltip);
        float leftX = widthExpected-width;
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width/2-(100),widthExpected/2-200);

        if(leftX<=0){
            mover.setBorders(0,0);
        }
        else{
            mover.setBorders(-leftX/2,leftX/2);
        }
        mover.setCurrOffset(0);
        test.addUIElement(tooltip).inTL(-5,0);
        mainPanel.addComponent(initalizer);
        mainPanel.addComponent(test).inTL(0,20);
        mainPanel.addComponent(ender);
        renderer = new UILinesRenderer(0f);
        renderer.setBoxColor(Color.ORANGE);
        renderer.setPanel(mainPanel);
        SpecialProjectStageWindow window = new SpecialProjectStageWindow();
        stagesWindow.add(window);




    }


    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {
        for (SpecialProjectStageWindow window : stagesWindow) {
            window.drawLineFromObject(objectOfInterest);
        }
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
