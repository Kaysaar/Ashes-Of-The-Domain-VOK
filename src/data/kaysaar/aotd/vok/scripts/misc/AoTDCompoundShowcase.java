package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.awt.*;
import java.util.List;

public class AoTDCompoundShowcase implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    ProgressBarComponent component;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public AoTDCompoundShowcase(float width, float height){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        ProgressBarComponent component = new ProgressBarComponent(width-30,height-5,0.4f,new Color(181, 119, 255));
        this.component = component;
        ImageViewer viewer = new ImageViewer(20,20,Global.getSettings().getSpriteName("fleetScreen","icon_logistics_fuel"));
        viewer.setColorOverlay(new Color(138, 41, 255, 255));
        mainPanel.addComponent(viewer.getComponentPanel()).inTL(2,-5);
        mainPanel.addComponent(component.getRenderingPanel()).inTL(25,0);
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
        if(component!=null){
            component.progress = Global.getSector().getPlayerFleet().getCargo().getFuel()/120;
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
