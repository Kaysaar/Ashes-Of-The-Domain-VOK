package data.kaysaar.aotd.vok.campaign.econ.produciton.trade;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContract;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.UIData;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

import java.util.LinkedHashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.ui.UIData.createFighterTooltip;

public class ProductionTradeContract extends AoTDTradeContract {
    AoTDProductionOrderSnapshot snapshot;

    public ProductionTradeContract(AoTDProductionOrderSnapshot snapshot) {
        super(snapshot.getId(), null, Factions.PLAYER, Integer.MAX_VALUE, true);
        this.snapshot = snapshot;
        reApplyChanges();
    }

    public void reApplyChanges() {
        getContractData().clear();

        for (AoTDProductionOrderData order : snapshot.productionData) {
            order.reconcileStateAfterAmountChange();
        }

        for (AoTDProductionOrderData order : snapshot.productionData) {
            if (!order.isCompleted()) {
                order.consumeSpecialItemsIfNeeded();
            }
        }

        for (AoTDProductionOrderData productionDatum : snapshot.productionData) {
            for (Map.Entry<String, Integer> entry : productionDatum.getReqResources().entrySet()) {
                if (AoTDProductionOrderSnapshot.isItemSpecial(entry.getKey())) {
                    continue;
                }
                int curr = getMonthlyAmountNeeded(entry.getKey());
                addContractData(entry.getKey(), entry.getValue() + curr, 0f);
            }
        }

        runCleanUp();
    }

    @Override
    public String getContractType() {
        return "Custom Production";
    }

    @Override
    public String getContractTypeId() {
        return "custom_production";
    }

    @Override
    public String getSubTypeOfContractString() {
        return "Order " + snapshot.getId();
    }

    @Override
    public void printCustomSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara(
                "Every month this contract attempts to consume the required resources for all queued production orders.",
                3f
        );
        tooltip.addSectionHeading("Production queue data", Alignment.MID, 5f);

        if (snapshot == null || snapshot.productionData.isEmpty()) {
            tooltip.addPara("No active production orders.", Misc.getGrayColor(), 5f);
            return;
        }

        for (AoTDProductionOrderData productionDatum : snapshot.productionData) {
            CustomPanelAPI row = createProductionRowSection(width - 10f, productionDatum);
            tooltip.addCustom(row, 4f);
        }
    }

    public CustomPanelAPI createProductionRowSection(float width, AoTDProductionOrderData data) {
        float rowHeight = 72f;
        float iconSize = 46f;
        float resourceIconSize = 18f;

        CustomPanelAPI row = Global.getSettings().createCustom(width, rowHeight, null);

        AoTDProductionSpec spec = data.getSpec();
        if (spec == null) {
            TooltipMakerAPI invalid = row.createUIElement(width, rowHeight, false);
            invalid.addPara("Invalid production order", Misc.getNegativeHighlightColor(), 0f);
            row.addUIElement(invalid).inTL(0, 0);
            return row;
        }

        CustomPanelAPI iconPanel = createProductionIconPanel(iconSize, spec);
        row.addComponent(iconPanel).inTL(2, 10);

        float textX = iconSize + 12f;
        float textWidth = width - textX - 4f;

        TooltipMakerAPI text = row.createUIElement(textWidth, rowHeight, false);
        text.addPara(
                "%s x %s",
                0f,
                Misc.getHighlightColor(),
                Misc.getHighlightColor(),
                spec.getName(),
                String.valueOf(data.amountToProduce)
        );
        row.addUIElement(text).inTL(textX, 4f);

        LinkedHashMap<String, Integer> remaining = ProductionCustomButton.getOrderedResourceMap(data.getReqResources());

        if (remaining == null || remaining.isEmpty()) {
            TooltipMakerAPI delivered = row.createUIElement(textWidth, 18f, false);
            delivered.addPara("Resources required for this order have been delivered", Misc.getPositiveHighlightColor(), 0f);
            row.addUIElement(delivered).inTL(textX, 20);
        } else {
            TooltipMakerAPI label = row.createUIElement(textWidth, 16f, false);
            label.addPara("Resources required for this part of the order:", Misc.getGrayColor(), 0f);
            row.addUIElement(label).inTL(textX, 20);

            CustomPanelAPI resourcePanel = createResourceIconRow(
                    textWidth,
                    22f,
                    remaining,
                    resourceIconSize,
                    6f
            );
            row.addComponent(resourcePanel).inTL(textX + 3, 38);
        }

        return row;
    }

    private CustomPanelAPI createResourceIconRow(
            float width,
            float height,
            Map<String, Integer> resources,
            float iconSize,
            float gap
    ) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);

        float x = 0f;

        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            if (entry.getValue() == null || entry.getValue() <= 0) continue;

            CustomPanelAPI item = ProductionCustomButton.createRowForItem(
                    iconSize,
                    entry.getKey(),
                    entry.getValue()
            );

            panel.addComponent(item).inTL(x, 0);
            x += item.getPosition().getWidth() + gap;
        }

        return panel;
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
            SpecialItemSpecAPI specAPI = (SpecialItemSpecAPI) spec.getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(size, size, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
            return container;
        }

        if (spec.getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.COMMODITY_ITEM) {
            CommoditySpecAPI specAPI = (CommoditySpecAPI) spec.getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(size, size, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(0, 0);
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
            CustomPanelAPI weaponPanel = WeaponInfoGenerator.getImageOfWeapon(specAPI, size - 8f).one;
            container.addComponent(weaponPanel).inTL(2, 4);
            UIData.createWeaponTooltip(specAPI, weaponPanel);
            return container;
        }

        return container;
    }

    @Override
    public boolean canEditContract() {
        return false;
    }

    @Override
    public boolean canTerminateContract() {
        return false;
    }

    @Override
    public void executeMonthEndForCommodity(int delivered, String commodityId) {
        if (delivered <= 0) return;

        int remaining = delivered;

        for (AoTDProductionOrderData order : snapshot.productionData) {
            if (order.isCompleted()) continue;

            int taken = order.takeResources(remaining, commodityId);
            remaining -= taken;

            if (remaining <= 0) {
                break;
            }
        }
    }

    @Override
    public void executeMonthEnd(float percentageOfEntireContractMet) {
        contractData.clear();

        for (AoTDProductionOrderData order : snapshot.productionData) {
            if (order.isCompleted()) continue;

            for (Map.Entry<String, Integer> reqResource : order.getReqResources().entrySet()) {
                String commodityId = reqResource.getKey();
                int amountNeeded = reqResource.getValue();

                if (amountNeeded <= 0) continue;

                int alreadyRequestedThisPass = getMonthlyAmountNeeded(commodityId);

                if (AshMisc.isStringValid(Misc.getCommissionFactionId()) && AoTDMisc.checkForQolEnabled()) {
                    addContractData(commodityId, amountNeeded + alreadyRequestedThisPass, 0.1f);
                } else {
                    addContractData(commodityId, amountNeeded + alreadyRequestedThisPass, 0f);
                }
            }
        }

        for (AoTDProductionOrderData order : snapshot.productionData) {
            if (!order.isCompleted()) {
                order.consumeSpecialItemsIfNeeded();
            }
        }

        this.runCleanUp();
    }

    @Override
    public boolean isExpired() {
        for (AoTDProductionOrderData order : snapshot.productionData) {
            if (!order.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canFreezeContract() {
        return true;
    }
}