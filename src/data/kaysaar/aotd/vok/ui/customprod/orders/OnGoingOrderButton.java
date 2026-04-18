package data.kaysaar.aotd.vok.ui.customprod.orders;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
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
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.UIData;
import data.kaysaar.aotd.vok.ui.onhover.ProducitonHoverInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.ui.UIData.createFighterTooltip;
import static data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton.getOrderedResourceMap;

public class OnGoingOrderButton extends CustomButton {

    public OnGoingOrderButton(float width, float height, AoTDProductionOrderSnapshot buttonData) {
        super(width, height, buttonData, 0f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor());
    }

    public AoTDProductionOrderSnapshot getSnapshot() {
        return (AoTDProductionOrderSnapshot) buttonData;
    }

    @Override
    public void createButtonContent(TooltipMakerAPI tooltip) {
        CustomPanelAPI container = Global.getSettings().createCustom(this.width, this.height, null);
        createContainerContent(container);
        isCreated = true;
        tooltip.addCustom(container, 0f).getPosition().inTL(5, 0);

        if (isWithArrow) {
            panelIndicator = Global.getSettings().createCustom(15, 15, null);
        }
    }

    public void createContainerContent(CustomPanelAPI container) {
        float titleHeight = 20f;
        float iconSectionHeight = 25f;
        float headerSectionHeight = 20;
        float contentHeight = container.getPosition().getHeight() - titleHeight-iconSectionHeight-headerSectionHeight-8-20f;

        TooltipMakerAPI tooltip = container.createUIElement(container.getPosition().getWidth()-4, container.getPosition().getHeight(), false);
        tooltip.setParaFont(Fonts.ORBITRON_12);
        tooltip.addPara("Order nr: %s", 2f, Color.ORANGE, getSnapshot().getId());
        CustomPanelAPI iconArea = createProductionGroupsPanel(container.getPosition().getWidth(), contentHeight);
        tooltip.addCustom(iconArea,3f);
        tooltip.addSectionHeading("Required resources for order", Alignment.MID,1f);
        tooltip.addCustom(createCostSection(container.getPosition().getWidth(),iconSectionHeight,iconSectionHeight,getSnapshot().getReqResources()),4f);
        tooltip.addPara("Remaining order cost: %s", 4f, Color.ORANGE,
                        Misc.getDGSCredits(getSnapshot().getMoneyThatWillBeConsumed()))
                .setAlignment(Alignment.MID);

        container.addUIElement(tooltip).inTL(2, 0);
    }
    public static CustomPanelAPI createCostSection(float width, float height, float iconSize, Map<String,Integer> resources) {
        LinkedHashMap<String, Integer> orderedResources = getOrderedResourceMap(resources);

        if (orderedResources.isEmpty()) {
            CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);
            TooltipMakerAPI tl = mainPanel.createUIElement(width-4, height, false);
            tl.addPara("All resources have been delivered!", Misc.getPositiveHighlightColor(), 3f)
                    .setAlignment(Alignment.MID);
            mainPanel.addUIElement(tl).inTL(-5, 0);
            return mainPanel;
        }

        CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);

        ArrayList<CustomPanelAPI> panels = new ArrayList<>();
        float separatorX = 3f;
        float y = 0f;

        orderedResources.forEach((commodityId, amount) ->
                panels.add(createRowForItem(iconSize, commodityId, amount))
        );

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
           ImageViewer viewer =  new ImageViewer(iconSize, iconSize, Global.getSettings().getSpecialItemSpec(commodityId).getIconName());
            main.addComponent(viewer.getComponentPanel()).inTL(0, 0);

            String toHighlight =   displayAmount;
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
    private CustomPanelAPI createProductionGroupsPanel(float width, float height) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);

        float leftPad = 2f;
        float topPad = 2f;
        float iconSize = 40f;
        float overlapGap = Math.max(4f, iconSize * 0.25f);   // same-group overlap step
        float groupGap = 3f;                                // between groups
        float rowGap = 4f;
        float rowHeight = iconSize;
        float maxX = width - 15f;
        float maxY = height;
        CustomPanelAPI prev = null;
        float x = leftPad;
        float y = topPad;

        boolean ellipsisPlaced = false;

        for (AoTDProductionOrderData productionDatum : getSnapshot().productionData) {
            int count = Math.max(0, productionDatum.getRemainingUnits());
            if (count <= 0) continue;

            AoTDProductionSpec spec = productionDatum.getSpec();
            if (spec == null) continue;

            float groupWidth = iconSize + Math.max(0, count - 1) * overlapGap;

            if (x + groupWidth > maxX && x > leftPad) {
                x = leftPad;
                y += rowHeight + rowGap;
            }

            if (y + rowHeight > maxY) {
                placeEllipsis(panel,prev);
                ellipsisPlaced = true;
                break;
            }

            for (int i = 0; i < count; i++) {
                float iconX = x + i * overlapGap;
                CustomPanelAPI icon = createProductionIconPanel(iconSize, spec);
                prev = icon;
                panel.addComponent(icon).inTL(iconX, y);
            }

            x += groupWidth + groupGap;
        }

        if (!ellipsisPlaced && isEmptySnapshot()) {
            TooltipMakerAPI empty = panel.createUIElement(width, 14f, false);
            empty.addPara("No active production", Misc.getGrayColor(), 0f);
            panel.addUIElement(empty).inTL(4f, 2f);
        }

        return panel;
    }

    private boolean isEmptySnapshot() {
        for (AoTDProductionOrderData productionDatum : getSnapshot().productionData) {
            if (productionDatum != null && productionDatum.getRemainingUnits() > 0) {
                return false;
            }
        }
        return true;
    }

    private void placeEllipsis(CustomPanelAPI panel,CustomPanelAPI prev) {
        LabelAPI label = Global.getSettings().createLabel("...", Fonts.DEFAULT_SMALL);
        label.setColor(Misc.getHighlightColor());
        label.getPosition().setSize(label.computeTextWidth(label.getText()), label.computeTextHeight(label.getText()));
        panel.addComponent((UIComponentAPI) label).rightOfMid(prev,5);
    }

    private CustomPanelAPI createProductionIconPanel(float size, AoTDProductionSpec spec) {
        CustomPanelAPI container = Global.getSettings().createCustom(size, size, null);

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.FIGHTER) {
            FighterWingSpecAPI specAPI = (FighterWingSpecAPI) spec.getUnderlyingSpec();
            CustomPanelAPI fighterPanel = FighterInfoGenerator
                    .createFormationPanel(specAPI, FormationType.BOX, (int) size, specAPI.getNumFighters())
                    .one;
            container.addComponent(fighterPanel).inTL(0, 0);

            FleetMemberAPI fleetMember = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, spec.getId());
            createFighterTooltip(fleetMember, specAPI, fighterPanel);
            return container;
        }

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            TooltipMakerAPI inserter = container.createUIElement(1, 1, false);

            SpecialItemSpecAPI specAPI = (SpecialItemSpecAPI) spec.getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(size, size, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            inserter.addTooltipTo(new ProducitonHoverInfo(spec),viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW,false);

            return container;
        }

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.COMMODITY_ITEM) {
            TooltipMakerAPI inserter = container.createUIElement(1, 1, false);

            CommoditySpecAPI specAPI = (CommoditySpecAPI) spec.getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(size, size, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            inserter.addTooltipTo(new ProducitonHoverInfo(spec),viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW,false);

            return container;
        }

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.SHIP) {
            ShipHullSpecAPI specAPI = Global.getSettings().getHullSpec(spec.getId());
            CustomPanelAPI shipPanel = ShipInfoGenerator.getShipImage(specAPI, size, null).one;
            container.addComponent(shipPanel).inTL(0, 0);

            FleetMemberAPI fleetMemberAPI = Global.getFactory().createFleetMember(
                    FleetMemberType.SHIP,
                    AshMisc.getVaraint((ShipHullSpecAPI) spec.getUnderlyingSpec())
            );
            fleetMemberAPI.getRepairTracker().setCR(0.7f);
            fleetMemberAPI.getCrewComposition().addCrew(fleetMemberAPI.getMinCrew());
            fleetMemberAPI.updateStats();
            UIData.createTooltipForShip(fleetMemberAPI, shipPanel);

            return container;
        }

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.WEAPON) {
            WeaponSpecAPI specAPI = (WeaponSpecAPI) spec.getUnderlyingSpec();
            CustomPanelAPI weaponPanel = WeaponInfoGenerator.getImageOfWeapon(specAPI, size - 6f).one;
            container.addComponent(weaponPanel).inTL(1f, 3f);
            UIData.createWeaponTooltip(specAPI, weaponPanel);
            return container;
        }

        return container;
    }
}