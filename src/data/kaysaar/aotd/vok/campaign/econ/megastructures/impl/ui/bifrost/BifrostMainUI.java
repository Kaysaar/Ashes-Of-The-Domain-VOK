package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import java.util.List;

public class BifrostMainUI implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel,componentPanel;
    BifrostInfoSection infoSection;
    BifrostGateListSection gateSection;
    public BifrostMainUI(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        createUI();
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if (componentPanel != null) {
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        if(infoSection!=null){
            infoSection.clearUI();
            gateSection.clearUI();
        }
        infoSection = new BifrostInfoSection(this);
        gateSection = new BifrostGateListSection();
        componentPanel.addComponent(infoSection.getMainPanel()).inTL(componentPanel.getPosition().getWidth()-infoSection.getMainPanel().getPosition().getWidth(),0);
        componentPanel.addComponent(gateSection.getMainPanel()).inTL(0,0);


        mainPanel.addComponent(componentPanel).inTL(0,0);
    }

    @Override
    public void clearUI() {
        infoSection.clearUI();
        gateSection.clearUI();
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
