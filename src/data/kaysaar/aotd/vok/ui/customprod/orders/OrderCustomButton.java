package data.kaysaar.aotd.vok.ui.customprod.orders;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.CustomButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.UIData;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.onhover.ProducitonHoverInfo;


import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static data.kaysaar.aotd.vok.ui.UIData.createFighterTooltip;
import static data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton.getOrderedResourceMap;

public class OrderCustomButton extends CustomButton {

    public AoTDProductionOrderData getData() {
        return (AoTDProductionOrderData) buttonData;
    }

    public OrderCustomButton(float width, float height, AoTDProductionOrderData buttonData) {
        super(width, height, buttonData, 0f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor());
    }

    @Override
    public void createButtonContent(TooltipMakerAPI tooltip) {
        CustomPanelAPI container = Global.getSettings().createCustom(this.width, this.height, null);
        createContainerContent(container);
        isCreated = true;
        tooltip.addCustom(container, 0f).getPosition().inTL(5, 0);
        float centerY = height / 2;
        if (isWithArrow) {
            panelIndicator = Global.getSettings().createCustom(15, 15, null);
//            tooltip.addCustom(panelIndicator,0f).getPosition().inTL((float) StarSystemHoldingTable.widthMap.get("name")*0.75f,centerY-7);

        }
    }

    public void createContainerContent(CustomPanelAPI container) {
        AoTDProductionSpec.AoTDProductionSpecType type = getData().getType();
        float opadText = 12;
        if (type == AoTDProductionSpec.AoTDProductionSpecType.SHIP) {
            ShipHullSpecAPI specAPI = Global.getSettings().getHullSpec(getData().getId());
            CustomPanelAPI shipPanel = ShipInfoGenerator.getShipImage(specAPI, container.getPosition().getHeight() - 4, null).one;
            container.addComponent(shipPanel).inTL(2, 2);
            FleetMemberAPI fleetMemberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP, AshMisc.getVaraint((ShipHullSpecAPI) getData().getSpec().getUnderlyingSpec()));
            fleetMemberAPI.getRepairTracker().setCR(0.7f);
            fleetMemberAPI.getCrewComposition().addCrew(fleetMemberAPI.getMinCrew());
            fleetMemberAPI.updateStats();
            UIData.createTooltipForShip(fleetMemberAPI, shipPanel);
        }
        if (type == AoTDProductionSpec.AoTDProductionSpecType.FIGHTER) {
            FighterWingSpecAPI specAPI = (FighterWingSpecAPI) getData().getSpec().getUnderlyingSpec();
            CustomPanelAPI shipPanel = FighterInfoGenerator.createFormationPanel(specAPI, FormationType.BOX, (int) (container.getPosition().getHeight() - 4f), specAPI.getNumFighters()).one;
            container.addComponent(shipPanel).inTL(2, 2);
            FleetMemberAPI fleetMember = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, getData().getSpec().getId());
            createFighterTooltip(fleetMember, (FighterWingSpecAPI) getData().getSpec().getUnderlyingSpec(), shipPanel);
        }
        if (type == AoTDProductionSpec.AoTDProductionSpecType.WEAPON) {
            WeaponSpecAPI specAPI = (WeaponSpecAPI) getData().getSpec().getUnderlyingSpec();
            CustomPanelAPI shipPanel = WeaponInfoGenerator.getImageOfWeapon(specAPI, container.getPosition().getHeight() - 14).one;
            container.addComponent(shipPanel).inTL(5, 7);
            UIData.createWeaponTooltip(specAPI, shipPanel);
        }
        if (type == AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            TooltipMakerAPI inserter = container.createUIElement(1, 1, false);
            SpecialItemSpecAPI specAPI = (SpecialItemSpecAPI) getData().getSpec().getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(container.getPosition().getHeight() - 4, container.getPosition().getHeight() - 4, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2, 2);
            inserter.addTooltipTo(new ProducitonHoverInfo(getData().getSpec()), viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW, false);
        }
        if (type == AoTDProductionSpec.AoTDProductionSpecType.COMMODITY_ITEM) {
            TooltipMakerAPI inserter = container.createUIElement(1, 1, false);
            CommoditySpecAPI specAPI = (CommoditySpecAPI) getData().getSpec().getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(container.getPosition().getHeight() - 4, container.getPosition().getHeight() - 4, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2, 2);
            inserter.addTooltipTo(new ProducitonHoverInfo(getData().getSpec()), viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW, false);
        }
        TooltipMakerAPI tl = container.createUIElement(container.getPosition().getWidth() - container.getPosition().getHeight() - 5, container.getPosition().getHeight(), false);
        tl.addPara(getData().getSpec().getName(), 3);
        tl.addPara("Amount to produce : %s", 3f, Color.ORANGE, getData().amountToProduce + "");
        container.addUIElement(tl).inTL(container.getPosition().getHeight(), 0);

        TooltipMakerAPI tlCost = container.createUIElement(CurrentOrderList.getColumnWidth("cost", container.getPosition().getWidth(), 1), container.getPosition().getHeight(), false);
        tlCost.addCustom(this.createCostSection(CurrentOrderList.getColumnWidth("cost", container.getPosition().getWidth(), 1), height), 2f).getPosition().inTL(0, 1);
        container.addUIElement(tlCost).inTL(CurrentOrderList.getColumnStartX("cost", container.getPosition().getWidth(), 1), 0);

        TooltipMakerAPI tlDays = container.createUIElement(CurrentOrderList.getColumnWidth("days", container.getPosition().getWidth(), 1), container.getPosition().getHeight(), false);
        tlDays.addPara("" + getData().getSpec().getDaysToBeCreated(), opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlDays).inTL(CurrentOrderList.getColumnStartX("days", container.getPosition().getWidth(), 1), 0);
    }

    public CustomPanelAPI createCostSection(float width, float height) {
        CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);

        ArrayList<CustomPanelAPI> panels = new ArrayList<>();
        float separatorX = 3f;
        float y = 5f;

        LinkedHashMap<String, Integer> orderedResources = getOrderedResourceMap(getData().getReqResources());

        orderedResources.forEach((commodityId, amount) ->
                panels.add(createRowForItem(15, commodityId, amount))
        );

        if (panels.isEmpty()) {
            return mainPanel;
        }

        CustomPanelAPI centralized = Global.getSettings().createCustom(1, 1, null);
        mainPanel.addComponent(centralized).inTL(mainPanel.getPosition().getWidth() / 2f, 0);

        float totalWidth = 0f;
        for (CustomPanelAPI panel : panels) {
            totalWidth += panel.getPosition().getWidth();
        }
        totalWidth += separatorX * (panels.size() - 1);

        float startX = Math.max(0f, (width - totalWidth) * 0.5f);

        float currX = startX;
        for (CustomPanelAPI panel : panels) {
            mainPanel.addComponent(panel).inTL(currX, y);
            currX += panel.getPosition().getWidth() + separatorX;
        }

        LabelAPI labelAPI = Global.getSettings().createLabel(
                Misc.getDGSCredits(getData().getMoneyForAllUnits()),
                Fonts.DEFAULT_SMALL
        );
        labelAPI.setColor(Color.ORANGE);
        labelAPI.getPosition().setSize(
                labelAPI.computeTextWidth(labelAPI.getText()),
                labelAPI.computeTextHeight(labelAPI.getText())
        );
        mainPanel.addComponent((UIComponentAPI) labelAPI).belowMid(centralized, 20);

        return mainPanel;
    }

    public static CustomPanelAPI createRowForItem(float iconSize, String commodityId, int amount) {
        String displayAmount = formatCompactAmount(amount);

        if (Global.getSettings().getCommoditySpec(commodityId) != null) {
            CustomPanelAPI main = Global.getSettings().createCustom(iconSize * 3, iconSize, null);
            ImageViewer viewer = new ImageViewer(iconSize, iconSize, Global.getSettings().getCommoditySpec(commodityId).getIconName());
            main.addComponent(viewer.getComponentPanel()).inTL(0, 0);

            String toHighlight = displayAmount;
            LabelAPI label = Global.getSettings().createLabel(toHighlight, Fonts.DEFAULT_SMALL);
            label.setHighlight(toHighlight);
            label.setHighlightColor(Color.ORANGE);
            label.getPosition().setSize(label.computeTextWidth(label.getText()), label.computeTextHeight(label.getText()));

            float newWidth = iconSize + 2 + label.getPosition().getWidth();
            main.getPosition().setSize(newWidth, main.getPosition().getHeight());
            main.addComponent((UIComponentAPI) label).rightOfMid(viewer.getComponentPanel(), 2);

            return main;
        } else {
            CustomPanelAPI main = Global.getSettings().createCustom(iconSize * 3, iconSize, null);
            ImageViewer viewer = new ImageViewer(iconSize, iconSize, Global.getSettings().getSpecialItemSpec(commodityId).getIconName());
            main.addComponent(viewer.getComponentPanel()).inTL(0, 0);

            String toHighlight =  displayAmount;
            LabelAPI label = Global.getSettings().createLabel(toHighlight, Fonts.DEFAULT_SMALL);
            label.setHighlight(toHighlight);
            label.setHighlightColor(Misc.getDesignTypeColor(Global.getSettings().getSpecialItemSpec(commodityId).getManufacturer()));

            label.getPosition().setSize(label.computeTextWidth(label.getText()), label.computeTextHeight(label.getText()));

            float newWidth = iconSize + 2 + label.getPosition().getWidth();
            main.getPosition().setSize(newWidth, main.getPosition().getHeight());
            main.addComponent((UIComponentAPI) label).rightOfMid(viewer.getComponentPanel(), 2);

            return main;
        }
    }

    private static String formatCompactAmount(int amount) {

        return String.valueOf(amount);

    }

    private static String formatWithSuffix(float value, String suffix) {
        String formatted;

        if (value >= 100f) {
            formatted = String.valueOf((int) value);
        } else if (value >= 10f) {
            formatted = trimTrailingZeros(String.format(java.util.Locale.US, "%.1f", value));
        } else {
            formatted = trimTrailingZeros(String.format(java.util.Locale.US, "%.2f", value));
        }

        return formatted + suffix;
    }

    private static String trimTrailingZeros(String value) {
        if (!value.contains(".")) return value;

        while (value.endsWith("0")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.endsWith(".")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

}
