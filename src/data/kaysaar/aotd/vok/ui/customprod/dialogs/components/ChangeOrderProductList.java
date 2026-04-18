package data.kaysaar.aotd.vok.ui.customprod.dialogs.components;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContractManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.trade.ProductionTradeContract;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionDynamicPanelForScroll;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChangeOrderProductList implements ExtendedUIPanelPlugin {
    public static LinkedHashMap<String, Integer> widthMap = new LinkedHashMap<>();

    static {
        widthMap.put("name", 380);
        widthMap.put("cost_per_order", 280);
        widthMap.put("credits_per_unit", 150);

        widthMap.put("currently_in_production", 120);
        widthMap.put("produced", 150);
        widthMap.put("days", 90);
    }

    public static int getStartingX(String id) {
        int x = 0;
        for (Map.Entry<String, Integer> value : widthMap.entrySet()) {
            if (id.equals(value.getKey())) {
                break;
            }
            x += value.getValue() + 1;
        }
        return x;
    }

    public static float getWidth() {
        float x = 0;
        for (Map.Entry<String, Integer> value : widthMap.entrySet()) {
            x += value.getValue() + 1;
        }
        return x;
    }

    private ProductionDynamicPanelForScroll scrollBarV2;
    private CustomPanelAPI mainPanel;
    private CustomPanelAPI contentPanel;
    private CustomPanelAPI headerPanel;
    private CustomPanelAPI warningPanel;

    private final AoTDProductionOrderSnapshot orderSnapshot;
    private final ArrayList<AoTDProductionOrderData> copyOfDataOrder = new ArrayList<>();
    private final LinkedHashMap<String, AoTDProductionSpec.AoTDProductionSpecType> warningOrderIds = new LinkedHashMap<>();

    public ChangeOrderProductList(float height, AoTDProductionOrderSnapshot snapshot) {
        this.mainPanel = Global.getSettings().createCustom(getWidth(), height, this);
        this.orderSnapshot = snapshot;

        for (AoTDProductionOrderData data : snapshot.productionData) {
            copyOfDataOrder.add(copyOf(data));
        }

        createUI();
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if (headerPanel != null) {
            mainPanel.removeComponent(headerPanel);
        }
        if (contentPanel != null) {
            mainPanel.removeComponent(contentPanel);
        }
        if (warningPanel != null) {
            mainPanel.removeComponent(warningPanel);
        }

        float headerHeight = 20f;
        float warningHeight = 20f;
        float contentHeight = mainPanel.getPosition().getHeight() - headerHeight - warningHeight;

        createHeader(headerHeight);
        createContent(contentHeight, headerHeight);
        createWarning(headerHeight, contentHeight, warningHeight);
    }

    private void createHeader(float headerHeight) {
        headerPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                headerHeight,
                null
        );

        TooltipMakerAPI cols = headerPanel.createUIElement(
                headerPanel.getPosition().getWidth(),
                headerHeight,
                false
        );

        ButtonAPI nameHeader = cols.addAreaCheckbox(
                "Name",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("name"),
                headerHeight,
                0f
        );
        nameHeader.setClickable(false);
        nameHeader.getPosition().inTL(getStartingX("name"), 0);

        ButtonAPI costOrderHeader = cols.addAreaCheckbox(
                "Cost / order",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("cost_per_order"),
                headerHeight,
                0f
        );
        costOrderHeader.setClickable(false);
        costOrderHeader.getPosition().inTL(getStartingX("cost_per_order"), 0);

        ButtonAPI creditsUnitHeader = cols.addAreaCheckbox(
                "Credits / unit",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("credits_per_unit"),
                headerHeight,
                0f
        );
        creditsUnitHeader.setClickable(false);
        creditsUnitHeader.getPosition().inTL(getStartingX("credits_per_unit"), 0);

        ButtonAPI producedHeader = cols.addAreaCheckbox(
                "Remaining",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("produced"),
                headerHeight,
                0f
        );
        producedHeader.setClickable(false);
        producedHeader.getPosition().inTL(getStartingX("produced"), 0);

        ButtonAPI inProductionHeader = cols.addAreaCheckbox(
                "In production",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("currently_in_production"),
                headerHeight,
                0f
        );
        inProductionHeader.setClickable(false);
        inProductionHeader.getPosition().inTL(getStartingX("currently_in_production"), 0);

        ButtonAPI daysHeader = cols.addAreaCheckbox(
                "Days",
                null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                widthMap.get("days"),
                headerHeight,
                0f
        );
        daysHeader.setClickable(false);
        daysHeader.getPosition().inTL(getStartingX("days"), 0);

        headerPanel.addUIElement(cols).inTL(0, 0);
        mainPanel.addComponent(headerPanel).inTL(0, 0);
    }

    private void createContent(float contentHeight, float headerHeight) {
        contentPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                contentHeight,
                null
        );

        scrollBarV2 = new ProductionDynamicPanelForScroll(
                contentPanel.getPosition().getWidth(),
                contentPanel.getPosition().getHeight(),
                contentPanel,
                40,
                42,
                2
        );

        TooltipMakerAPI tlContent = contentPanel.createUIElement(
                contentPanel.getPosition().getWidth() + 9,
                contentPanel.getPosition().getHeight(),
                true
        );

        for (AoTDProductionOrderData data : copyOfDataOrder) {
            ChangeOrderProductButton bt = new ChangeOrderProductButton(
                    contentPanel.getPosition().getWidth(),
                    42f,
                    data
            );

            bt.setDecreaseListener((orderData, hasAssignedResources) -> {
                if (hasAssignedResources) {
                    addWarningOrder(orderData.getId(),orderData.getType());
                }
                decreaseOrder(orderData);
                bt.createUI();
                float headerHeight1 = 20f;
                float warningHeight1 = 20f;
                float contentHeight1 = mainPanel.getPosition().getHeight() - headerHeight1 - warningHeight1;
                createWarning(headerHeight1, contentHeight1, warningHeight1);;
            });

            bt.createUI();
            scrollBarV2.addItem(bt.getMainPanel());
        }

        scrollBarV2.createUI();
        tlContent.addCustom(scrollBarV2.getMainPanel(), 0f).getPosition().inTL(0, 0);

        contentPanel.addUIElement(tlContent).inTL(-5, 2);
        mainPanel.addComponent(contentPanel).inTL(0, headerHeight);
    }

    private void createWarning(float headerHeight, float contentHeight, float warningHeight) {
        warningPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                warningHeight,
                null
        );

        TooltipMakerAPI tlWarning = warningPanel.createUIElement(
                warningPanel.getPosition().getWidth(),
                warningHeight,
                false
        );

        if (!warningOrderIds.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String id : warningOrderIds.keySet()) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(AoTDProductionSpecManager.getSpecsBasedOnType(warningOrderIds.get(id)).get(id).getName());
                first = false;
            }

            tlWarning.addPara(
                    "Warning: one or multiple orders that will be canceled had already allocated resources that won't be able to reclaim: %s",
                    0f,
                    Misc.getNegativeHighlightColor(),
                    Misc.getHighlightColor(),
                    builder.toString()
            );
        }

        warningPanel.addUIElement(tlWarning).inTL(0, 0);
        mainPanel.addComponent(warningPanel).inTL(0, headerHeight + contentHeight);
    }

    private void addWarningOrder(String id, AoTDProductionSpec.AoTDProductionSpecType type) {
        if (id == null) return;
        if (!warningOrderIds.containsKey(id)) {
            warningOrderIds.put(id,type);
        }
    }

    private void decreaseOrder(AoTDProductionOrderData data) {
        if (data == null) return;

        int idx = copyOfDataOrder.indexOf(data);
        if (idx < 0) return;

        AoTDProductionOrderData current = copyOfDataOrder.get(idx);
        current.setAmount(Math.max(0, current.amountToProduce - 1));
    }

    private AoTDProductionOrderData copyOf(AoTDProductionOrderData original) {
        AoTDProductionSpec spec = original.getSpec();
        AoTDProductionOrderData copy = new AoTDProductionOrderData(original.getId(), spec, original.amountToProduce);

        copy.unitsRewarded = original.unitsRewarded;
        copy.activeUnits = original.activeUnits;
        copy.activeDays = original.activeDays;
        copy.hasStartedWorkOnThis = original.hasStartedWorkOnThis;

        copy.deliveredResources.clear();
        copy.deliveredResources.putAll(original.deliveredResources);

        return copy;
    }

    public ArrayList<AoTDProductionOrderData> getCopyOfDataOrder() {
        return copyOfDataOrder;
    }

    public AoTDProductionOrderSnapshot getOrderSnapshot() {
        return orderSnapshot;
    }



    @Override
    public void clearUI() {
        copyOfDataOrder.clear();
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

    public void onConfirm() {
        orderSnapshot.productionData.clear();

        for (AoTDProductionOrderData copy : copyOfDataOrder) {
            if (copy == null) continue;

            AoTDProductionOrderData confirmed = copyOf(copy);
            confirmed.reconcileStateAfterAmountChange();

            if (confirmed.amountToProduce > 0) {
                orderSnapshot.productionData.add(confirmed);
            }
        }

        ProductionTradeContract contract = (ProductionTradeContract) AoTDTradeContractManager
                .getInstance()
                .getActiveContracts()
                .get(orderSnapshot.getId());

        if (contract != null) {
            contract.reApplyChanges();
        }
    }
}