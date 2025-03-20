package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.patrolfleet.fleet.FleetMembersShowcase;

import java.awt.*;
import java.util.List;

public class PatrolFleetInfoTab implements CustomUIPanelPlugin {
    private  int height;
    private  int width;
    CustomPanelAPI mainPanel;
    ButtonAPI manageButton;
    ButtonAPI replenishButton;
    ButtonAPI createTemplateButton;
    UILinesRenderer renderer;
    AoTDPatrolFleetData data;
    PatrolFleetDataManager manager;
    FleetMembersShowcase currShowcase;
    public PatrolFleetInfoTab(AoTDPatrolFleetData data,PatrolFleetDataManager manager) {
        this.manager= manager;
        this.data = data;
        width = 800;
        height = 250;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width, height, false);
        tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
//        TooltipMakerAPI tool2 = panel.createUIElement(panel.getPosition().getWidth(), tool.getHeightSoFar(), false);
//                panel.getPosition().setSize(panel.getPosition().getWidth(),tool.getHeightSoFar());
//        addFleetMembers(tool2, panel.getPosition().getWidth()
//        );

//        tooltip.getPosition().setSize(mainPanel.getPosition().getWidth(),Math.max(height,tool.getHeightSoFar()+105));
//        mainPanel.getPosition().setSize(mainPanel.getPosition().getWidth(),Math.max(height,tool.getHeightSoFar()+105));
        createTemplateButton = tooltip.addButton("Copy Template", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 160, 20, 0f);
        createTemplateButton.getPosition().inTL(mainPanel.getPosition().getWidth() - 480, mainPanel.getPosition().getHeight() - 30);

        manageButton = createTemplateButton = tooltip.addButton("Manage Fleet", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 200, 20, 0f);
        manageButton.getPosition().inTL(mainPanel.getPosition().getWidth() - 200, mainPanel.getPosition().getHeight() - 30);


        replenishButton = createTemplateButton = tooltip.addButton("De-commission fleet", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 200, 20, 0f);
        replenishButton.getPosition().inTL(10, mainPanel.getPosition().getHeight() - 30);
        currShowcase = new FleetMembersShowcase(505, 145,data,false, manager);
        tooltip.addCustom(currShowcase.getPanel(), 0f).getPosition().inTL(5, 5);
        tooltip.addCustom(getLogisticsPanel(480,40),0f).getPosition().inTL(15,160);
        CustomPanelAPI panelOfMap = getMapAndOrder();
        tooltip.addCustom(panelOfMap, 0f).getPosition().inTL(530,5);
        mainPanel.addUIElement(tooltip).inTL(-5, 0);

    }

    public CustomPanelAPI getLogisticsPanel(float width,float height){
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI headerTootlip = panelAPI.createUIElement(width,20,false);
        TooltipMakerAPI contentTootlip = panelAPI.createUIElement(width,height-20,true);
        headerTootlip.addSectionHeading("Fleet Logistic Data",Alignment.MID,0f);
        contentTootlip.addPara("Running monthly cost of fleet : %s",0f,Color.ORANGE,Misc.getDGSCredits(data.getCurrentlyMonthlyUpkeep()));
        panelAPI.addUIElement(headerTootlip).inTL(0,0);
        panelAPI.addUIElement(contentTootlip).inTL(0,20);
    return panelAPI;
    }
    public   CustomPanelAPI getMapAndOrder() {
        CustomPanelAPI panelOfMap = Global.getSettings().createCustom(270,150,null);
        TooltipMakerAPI tooltipM = panelOfMap.createUIElement(270,150,false);
        MapParams params = new MapParams();
        params.positionToShowAllMarkersAndSystems(false,130);
        params.entityToShow =data.getFleet();
        params.filterData.names = false;
        UIPanelAPI map =tooltipM.createSectorMap(270,130,params,"Current Location and Order");
        tooltipM.addCustom(map,0f);
        tooltipM.addPara("Hyperspace - In Transit to Oaris",5f);
        tooltipM.addPara("Order : Defend Colony Oaris-1",Color.ORANGE,5f);
        panelOfMap.addUIElement(tooltipM).inTL(-5,0);
        return panelOfMap;
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
        if(currShowcase!=null){
            currShowcase.advance(amount);
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
