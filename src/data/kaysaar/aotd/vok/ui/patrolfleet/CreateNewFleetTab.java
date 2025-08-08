package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.PatrolFleetType;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.ui.customprod.components.optionpanels.ShipOptionPanelInterface;
import data.kaysaar.aotd.vok.ui.patrolfleet.fleet.FleetMembersShowcase;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CreateNewFleetTab implements CustomUIPanelPlugin {
    ButtonAPI current;
    public CustomPanelAPI mainPanel;
    CustomPanelAPI panelOfButtons;
    CustomPanelAPI infoPanel;
    public ShipOptionPanelInterface shipOptionPanelInterface;
    AoTDPatrolFleetData data;
    CustomPanelAPI panelAPI;
    ButtonAPI templateButtonManager;
    ButtonAPI saveTemplate;
    public ArrayList<ButtonAPI> buttons = new ArrayList<>();
    public FleetMembersShowcase currShowcase;

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public AoTDPatrolFleetData getData() {
        return data;
    }

    public void resetInfoUI() {
        mainPanel.removeComponent(infoPanel);
        createInfoUI(infoPanel.getPosition().getWidth(), infoPanel.getPosition().getHeight());
        mainPanel.addComponent(infoPanel).inTL(mainPanel.getPosition().getWidth() - 380, 365);

    }

    public LinkedHashMap<String, Integer> getCostOfFleet() {
        LinkedHashMap<String, Integer> costs = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : data.getExpectedVesselsInFleet().entrySet()) {
            for (Map.Entry<String, Integer> e : GPManager.getInstance().getSpec(entry.getKey()).getSupplyCost().entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(costs, e.getKey(), e.getValue() * entry.getValue());
            }
        }
        return costs;
    }

    public float getCreditCostOfFleet() {
        int am = 0;
        for (Map.Entry<String, Integer> entry : data.getExpectedVesselsInFleet().entrySet()) {
            am += (int) (entry.getValue() * GPManager.getInstance().getSpec(entry.getKey()).getCredistCost());
        }
        return am;
    }

    public int getDP() {
        if (current == null) {
            return 0;
        }
        PatrolFleetType.PatrolType type = (PatrolFleetType.PatrolType) current.getCustomData();
        return PatrolFleetType.getMaxDPPoints(type);
    }

    public void createInfoUI(float width, float height) {
        infoPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI header = infoPanel.createUIElement(width, 20, false);
        header.addSectionHeading("Fleet data", Alignment.MID, 0f);
        TooltipMakerAPI tooltip = infoPanel.createUIElement(width, height - 20, true);
        tooltip.addPara("DP points of this fleet : %s ", 5f, Color.ORANGE, data.getEstimatedDP() + " / " + getDP());
        tooltip.addPara("Vessels : %s", 5f, Color.ORANGE, data.getTotalExpectedVessels() + " / 30");
        tooltip.addPara("Officers : %s", 5f, Color.ORANGE, "1 / 5");
        tooltip.addSectionHeading("Fleet Cost", Alignment.MID, 5f);

        tooltip.addCustom(GPUIMisc.createResourcePanelForSmallTooltipCondensed(width + 20, height, 20, getCostOfFleet(), new HashMap<String, Integer>()), 5f);
        tooltip.addPara("Cost to deploy fleet : %s", 5f, Color.ORANGE, Misc.getDGSCredits(getCreditCostOfFleet()));
        tooltip.addPara("Monthly running cost : %s", 5f, Color.ORANGE, Misc.getDGSCredits(data.getEstimatedMonthlyUpkeep()));
        infoPanel.addUIElement(header).inTL(0, 0);
        infoPanel.addUIElement(tooltip).inTL(0, 20);
    }

    public void createButtonUI() {
        panelOfButtons = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), 130, null);
        TooltipMakerAPI tooltip = panelOfButtons.createUIElement(panelOfButtons.getPosition().getWidth(), panelOfButtons.getPosition().getHeight(), false);
        ButtonAPI smallFleet = tooltip.addAreaCheckbox("", PatrolFleetType.PatrolType.SMALL, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), (panelOfButtons.getPosition().getWidth() / 4) - 10, 100, 0f);
        ButtonAPI medFleet = tooltip.addAreaCheckbox("", PatrolFleetType.PatrolType.MEDIUM, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), (panelOfButtons.getPosition().getWidth() / 4) - 10, 100, 0f);
        ButtonAPI bigFleet = tooltip.addAreaCheckbox("", PatrolFleetType.PatrolType.LARGE, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), (panelOfButtons.getPosition().getWidth() / 4) - 10, 100, 0f);
        ButtonAPI automatedFleet = tooltip.addAreaCheckbox("", PatrolFleetType.PatrolType.AUTOMATED, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), (panelOfButtons.getPosition().getWidth() / 4) - 10, 100, 0f);
        smallFleet.getPosition().inTL(5, 0);
        smallFleet.setChecked(true);
        medFleet.getPosition().inTL(10 + smallFleet.getPosition().getWidth(), 0);
        bigFleet.getPosition().inTL(15 + smallFleet.getPosition().getWidth() * 2, 0);
        automatedFleet.getPosition().inTL(20 + smallFleet.getPosition().getWidth() * 3, 0);
        ImageViewer viewer = new ImageViewer(65, smallFleet.getPosition().getHeight() / 2, Global.getSettings().getSpriteName("systemMap", "icon_fleet1"));
        ImageViewer viewer2 = new ImageViewer(65, smallFleet.getPosition().getHeight() / 2, Global.getSettings().getSpriteName("systemMap", "icon_fleet2"));
        ImageViewer viewer3 = new ImageViewer(65, smallFleet.getPosition().getHeight() / 2, Global.getSettings().getSpriteName("systemMap", "icon_fleet3"));
        ImageViewer viewer4 = new ImageViewer(65, smallFleet.getPosition().getHeight() / 2, Global.getSettings().getSpriteName("aotd_icons", "fleet_ai"));
        viewer4.setColorOverlay(Global.getSector().getFaction(Factions.REMNANTS).getBaseUIColor());
        tooltip.addCustom(viewer.getComponentPanel(), 0f);
        tooltip.addCustom(viewer2.getComponentPanel(), 0f);
        tooltip.addCustom(viewer3.getComponentPanel(), 0f);
        tooltip.addCustom(viewer4.getComponentPanel(), 0f);
        viewer.getComponentPanel().getPosition().inTL(smallFleet.getPosition().getCenterX() - (viewer.getComponentPanel().getPosition().getWidth() / 2), 25);
        viewer2.getComponentPanel().getPosition().inTL(medFleet.getPosition().getCenterX() - (viewer2.getComponentPanel().getPosition().getWidth() / 2), 25);
        viewer3.getComponentPanel().getPosition().inTL(bigFleet.getPosition().getCenterX() - (viewer3.getComponentPanel().getPosition().getWidth() / 2), 25);
        viewer4.getComponentPanel().getPosition().inTL(automatedFleet.getPosition().getCenterX() - (viewer4.getComponentPanel().getPosition().getWidth() / 2), 25);
        LabelAPI labelAPI = tooltip.addPara("Squadron %s", 0f, Color.ORANGE, "(50 AP)");
        labelAPI.getPosition().inTL(smallFleet.getPosition().getCenterX() - (labelAPI.computeTextWidth(labelAPI.getText()) / 2), 105);
        LabelAPI labelAPI2 = tooltip.addPara("Flotilla %s", 0f, Color.ORANGE, "(120 AP)");
        LabelAPI labelAPI3 = tooltip.addPara("Armada %s ", 0f, Color.ORANGE, "(300 AP)");
        LabelAPI labelAPI4 = tooltip.addPara("Automated %s", 0f, Color.CYAN, Color.ORANGE, "(250 AP)");
        labelAPI2.getPosition().inTL(medFleet.getPosition().getCenterX() - (labelAPI2.computeTextWidth(labelAPI2.getText()) / 2), 105);
        labelAPI3.getPosition().inTL(bigFleet.getPosition().getCenterX() - (labelAPI3.computeTextWidth(labelAPI3.getText()) / 2), 105);
        labelAPI4.getPosition().inTL(automatedFleet.getPosition().getCenterX() - (labelAPI4.computeTextWidth(labelAPI4.getText()) / 2), 105);
        buttons.add(smallFleet);
        buttons.add(medFleet);
        buttons.add(bigFleet);
        buttons.add(automatedFleet);

        panelOfButtons.addUIElement(tooltip).inTL(0, 0);

    }

    public CreateNewFleetTab(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.data = new AoTDPatrolFleetData("");
        createButtonUI();
        mainPanel.addComponent(panelOfButtons).inTL(width / 2 - (panelOfButtons.getPosition().getWidth() / 2), 0);
        CustomPanelAPI panelSupport = Global.getSettings().createCustom(width - 85, height - 70, null);
        CustomPanelAPI buttonHolder = Global.getSettings().createCustom(340,30,null);
        TooltipMakerAPI tooltipButton = buttonHolder.createUIElement(350,30,false);
        float buttonWidth = (buttonHolder.getPosition().getWidth()/2) -5;
        templateButtonManager = tooltipButton.addButton("Choose template","choose_temp",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,buttonWidth,30,0f);
        saveTemplate = tooltipButton.addButton("Save Template","save_temp",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,buttonWidth,30,0f);
        templateButtonManager.getPosition().inTL(0,0);
        saveTemplate.getPosition().inTL(buttonWidth+10,0);
        buttonHolder.addUIElement(tooltipButton).inTL(-5,0);

        UIData.recomputeForFleetTab(790, height - 230);
        shipOptionPanelInterface = new ShipOptionPanelInterface(panelSupport, 0, false);
        shipOptionPanelInterface.init();
        createInfoUI(340, 180);
        mainPanel.addComponent(panelSupport).inTL(-(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10), 100);
        this.currShowcase = new FleetMembersShowcase(340, 210, data, true, this);
        panelAPI = currShowcase.panel;
        mainPanel.addComponent(panelAPI).inTL(width - 380, 129);
        mainPanel.addComponent(infoPanel).inTL(width - 380, 365);
        mainPanel.addComponent(buttonHolder).inTL(width - 380, 555);

    }


    public void resetFleet() {
        mainPanel.removeComponent(panelAPI);
        currShowcase = new FleetMembersShowcase(340, 210, data, true, this);
        panelAPI = currShowcase.panel;
        mainPanel.addComponent(panelAPI).inTL(mainPanel.getPosition().getWidth() - 380, 129);
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
        if (currShowcase != null) {
            currShowcase.advance(amount);
        }
        for (ButtonAPI button : buttons) {
            if (button.isChecked()) {
                button.setChecked(false);
                current = button;
                resetInfoUI();
            }
            button.unhighlight();
        }
        if (current != null) {
            current.highlight();
            data.setType((PatrolFleetType.PatrolType) current.getCustomData());
        }
        shipOptionPanelInterface.advance(amount);
        for (ButtonAPI orderButton : shipOptionPanelInterface.getOrderButtons()) {
            if (orderButton.isChecked()) {
                orderButton.setChecked(false);
                GPOption option = (GPOption) orderButton.getCustomData();
                data.addExpectedVessel(option.getSpec().getShipHullSpecAPI().getHullId());
                resetFleet();
                resetInfoUI();
                break;
            }
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

}
