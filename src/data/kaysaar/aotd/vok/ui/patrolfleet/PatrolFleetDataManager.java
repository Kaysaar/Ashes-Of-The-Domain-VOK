package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.manager.FactionPatrolFleetManager;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.util.List;

public class PatrolFleetDataManager implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;
    CustomPanelAPI pForFleets;
    public PatrolFleetFactionData data;
    public PatrolFleetDataManager(float width,float height) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        createFleetsTab();
        data=new PatrolFleetFactionData(450,height,this);
        mainPanel.addComponent(data.mainPanel).inTL(0,0);
        renderer.setPanel(mainPanel);
    }

    public void createFleetsTab() {
        if(pForFleets!=null) {
            mainPanel.removeComponent(pForFleets);
        }
         pForFleets = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth() -455, mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltip = pForFleets.createUIElement(mainPanel.getPosition().getWidth() -455, mainPanel.getPosition().getHeight(),true);
        renderer = new UILinesRenderer(0f);
        for (AoTDPatrolFleetData patrolFleet : FactionPatrolFleetManager.getInstance().getPatrolFleets()) {
            PatrolFleetInfoTab info = new PatrolFleetInfoTab(patrolFleet,this);
            tooltip.addCustom(info.mainPanel,5f);
        }
        pForFleets.addUIElement(tooltip).inTL(0,0);
        mainPanel.addComponent(pForFleets).inTL(455,0);
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
