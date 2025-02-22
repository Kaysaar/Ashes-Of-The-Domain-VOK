package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.newcomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.newcomps.ZoomPanelComponent;
import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;

import java.awt.*;
import java.util.List;

public class TestPlugin implements CustomUIPanelPlugin {

    CustomPanelAPI mainPanel;
    CustomPanelAPI pa2;
    float pos = 0;

    public TestPlugin(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerPlugin(mainPanel)));
        UILinesRenderer renderer = new UILinesRenderer(-1f);
        CustomPanelAPI panelToWorkWith = mainPanel.createCustomPanel(width, height, renderer);
        createUI(panelToWorkWith);
        mainPanel.addComponent(panelToWorkWith);
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerEndPlugin()));

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void createUI(CustomPanelAPI mainPanel) {
        pa2 = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        ZoomPanelComponent component = new ZoomPanelComponent(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),mainPanel.getPosition().getWidth()*3,mainPanel.getPosition().getHeight()*3);
        ButtonComponent component2= new ButtonComponent(100,40);
        component2.setText("test");

        ButtonComponent component3= new ButtonComponent(100,40);
        component3.setText("tes23t");

        ButtonComponent component4= new ButtonComponent(100,40);
        component4.setText("tes23t");
        component.addComponent(component2,100,20);

        component.addComponent(component3,mainPanel.getPosition().getWidth()-50,mainPanel.getPosition().getHeight() -20);
        component.addComponent(component4,100,mainPanel.getPosition().getHeight()*2);
        pa2.addComponent(component.getPluginPanel());
        mainPanel.addComponent(pa2).inTL(0,0);
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
