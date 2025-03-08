package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PatrolFleetPanelInfo implements CustomUIPanelPlugin {
    private  int height;
    private  int width;
    CustomPanelAPI mainPanel;
    ButtonAPI manageButton;
    ButtonAPI replenishButton;
    ButtonAPI createTemplateButton;
    UILinesRenderer renderer;

    public PatrolFleetPanelInfo() {
        width = 800;
        height = 250;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width, height, false);
        ShipWithOfficerShowcase showcase = new ShipWithOfficerShowcase(100, true, "uaf_supercap_slv_core");
        tooltip.addCustom(showcase.getPanelOfButton(), 0f).getPosition().inTL(15, 30);
        tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
        LabelAPI labelAPI = tooltip.addPara("1-st Strike Force : Alpha", Color.ORANGE, 5f);
        CustomPanelAPI panel = Global.getSettings().createCustom(390, 110, null);
        TooltipMakerAPI tool = panel.createUIElement(panel.getPosition().getWidth(), 110, true);

        addFleetMembers(tool, panel.getPosition().getWidth());
//        TooltipMakerAPI tool2 = panel.createUIElement(panel.getPosition().getWidth(), tool.getHeightSoFar(), false);
//                panel.getPosition().setSize(panel.getPosition().getWidth(),tool.getHeightSoFar());
//        addFleetMembers(tool2, panel.getPosition().getWidth()
//        );

//        tooltip.getPosition().setSize(mainPanel.getPosition().getWidth(),Math.max(height,tool.getHeightSoFar()+105));
//        mainPanel.getPosition().setSize(mainPanel.getPosition().getWidth(),Math.max(height,tool.getHeightSoFar()+105));
        labelAPI.getPosition().inTL(15, 5);
        panel.addUIElement(tool).inTL(0,0);
        createTemplateButton = tooltip.addButton("Copy Template", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 160, 20, 0f);
        createTemplateButton.getPosition().inTL(mainPanel.getPosition().getWidth() - 480, mainPanel.getPosition().getHeight() - 30);

        manageButton = createTemplateButton = tooltip.addButton("Manage Fleet", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 200, 20, 0f);
        manageButton.getPosition().inTL(mainPanel.getPosition().getWidth() - 200, mainPanel.getPosition().getHeight() - 30);


        replenishButton = createTemplateButton = tooltip.addButton("De-commission fleet", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 200, 20, 0f);
        replenishButton.getPosition().inTL(10, mainPanel.getPosition().getHeight() - 30);
        tooltip.addCustom(panel, 0f).getPosition().inTL(130, 35);
        tooltip.addCustom(getLogisticsPanel(480,40),0f).getPosition().inTL(15,150);
        CustomPanelAPI panelOfMap = getMapAndOrder();
        tooltip.addCustom(panelOfMap, 0f).getPosition().inTL(530,5);
        mainPanel.addUIElement(tooltip).inTL(-5, 0);

    }

    public CustomPanelAPI getLogisticsPanel(float width,float height){
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI headerTootlip = panelAPI.createUIElement(width,20,false);
        TooltipMakerAPI contentTootlip = panelAPI.createUIElement(width,height-20,true);
        headerTootlip.addSectionHeading("Fleet Logistic Data",Alignment.MID,0f);
        contentTootlip.addPara("Running monthly cost of fleet : %s",0f,Color.ORANGE,Misc.getDGSCredits(100000));
        panelAPI.addUIElement(headerTootlip).inTL(0,0);
        panelAPI.addUIElement(contentTootlip).inTL(0,20);
    return panelAPI;
    }
    public   CustomPanelAPI getMapAndOrder() {
        CustomPanelAPI panelOfMap = Global.getSettings().createCustom(270,150,null);
        TooltipMakerAPI tooltipM = panelOfMap.createUIElement(270,150,false);
        MapParams params = new MapParams();
        params.positionToShowAllMarkersAndSystems(false,130);
        params.entityToShow = Global.getSector().getPlayerFleet();
        params.filterData.names = false;
        UIPanelAPI map =tooltipM.createSectorMap(270,130,params,"Current Location and Order");
        tooltipM.addCustom(map,0f);
        tooltipM.addPara("Hyperspace - In Transit to Oaris",5f);
        tooltipM.addPara("Order : Defend Colony Oaris-1",Color.ORANGE,5f);
        panelOfMap.addUIElement(tooltipM).inTL(-5,0);
        return panelOfMap;
    }

    public void addFleetMembers(TooltipMakerAPI tooltip, float width) {
        float currX = 0;
        float currY = 0;
        float seperatorX = 5;
        float seperatorY = 5;
        ArrayList<String> strs = new ArrayList<>();
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("onslaught");
        strs.add("legion");
        strs.add("legion");
        strs.add("legion");
        strs.add("legion");

        strs.add("mora");
        strs.add("mora");
        tooltip.addSpacer(60);
        for (String str : strs) {
            ShipWithOfficerShowcase showcase = new ShipWithOfficerShowcase(50, false, str);
            if (currX + showcase.originalWidth > width) {
                currY += showcase.originalHeight + seperatorY;
                tooltip.addSpacer(showcase.originalHeight + seperatorY);
                currX = 0;
            }
            tooltip.addCustomDoNotSetPosition(showcase.getComponentPanel()).getPosition().inTL(currX, currY);
            currX += showcase.originalWidth + seperatorX;

        }


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
