package data.kaysaar.aotd.vok.ui.customprod.dialogs.components;

import ashlib.data.plugins.ui.models.CustomButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.customprod.AoTDIconUtilis;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ChangeOrderProductButton extends CustomButton {

    public interface DecreaseListener {
        void onDecreaseRequested(AoTDProductionOrderData data, boolean hasAssignedResources);
    }

    private DecreaseListener decreaseListener;

    public ChangeOrderProductButton(float width, float height, AoTDProductionOrderData buttonData) {
        super(width, height, buttonData, 0f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor());
    }

    public AoTDProductionOrderData getData() {
        return (AoTDProductionOrderData) buttonData;
    }

    public void setDecreaseListener(DecreaseListener decreaseListener) {
        this.decreaseListener = decreaseListener;
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
        AoTDProductionOrderData data = getData();
        AoTDProductionSpec spec = data.getSpec();

        if (spec == null) {
            TooltipMakerAPI invalid = container.createUIElement(container.getPosition().getWidth(), container.getPosition().getHeight(), false);
            invalid.addPara("Invalid order", Misc.getNegativeHighlightColor(), 0f);
            container.addUIElement(invalid).inTL(0, 0);
            return;
        }

        float h = container.getPosition().getHeight();
        float iconSize = h - 6f;
        float textPad = 7f;

        CustomPanelAPI icon = AoTDIconUtilis.getIcon(spec.getId(), spec.getProductionType(), iconSize);
        container.addComponent(icon).inTL(5, 3);

        TooltipMakerAPI tlName = container.createUIElement(
                ChangeOrderProductList.widthMap.get("name") - iconSize - 8f,
                h,
                false
        );
        tlName.addPara(spec.getName(), textPad);
        if (data.amountToProduce <= 0) {
            tlName.addPara("Cancel production", 1f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
        } else {
            tlName.addPara(
                    "Ordered: %s",
                    1f,
                    Misc.getGrayColor(),
                    Misc.getHighlightColor(),
                    String.valueOf(data.amountToProduce)
            );
        }
        container.addUIElement(tlName).inTL(2 + iconSize + 8f, 0);

        TooltipMakerAPI tlCostPerOrder = container.createUIElement(
                ChangeOrderProductList.widthMap.get("cost_per_order"),
                h,
                false
        );
        tlCostPerOrder.addCustom(
                createCostSection(ChangeOrderProductList.widthMap.get("cost_per_order"), h),
                0f
        ).getPosition().inTL(0, 0);
        container.addUIElement(tlCostPerOrder).inTL(ChangeOrderProductList.getStartingX("cost_per_order"), 0);

        TooltipMakerAPI tlCredits = container.createUIElement(
                ChangeOrderProductList.widthMap.get("credits_per_unit"),
                h,
                false
        );
        tlCredits.addPara(
                Misc.getDGSCredits(spec.getProductionCost()),
                Color.ORANGE,
                textPad + 5
        ).setAlignment(Alignment.MID);
        container.addUIElement(tlCredits).inTL(ChangeOrderProductList.getStartingX("credits_per_unit"), 0);

        TooltipMakerAPI tlProduced = container.createUIElement(
                ChangeOrderProductList.widthMap.get("produced"),
                h,
                false
        );
        tlProduced.addPara(
                String.valueOf(data.unitsRewarded),
                textPad + 5
        ).setAlignment(Alignment.MID);
        container.addUIElement(tlProduced).inTL(ChangeOrderProductList.getStartingX("produced"), 0);

        int activeUnits = Math.max(0, data.activeUnits);
        int notStarted = Math.max(0, data.amountToProduce - data.unitsRewarded - activeUnits);

        TooltipMakerAPI tlInProduction = container.createUIElement(
                ChangeOrderProductList.widthMap.get("currently_in_production"),
                h,
                false
        );
        if(getData().amountToProduce>0){
            if(notStarted!=0){
                tlInProduction.addPara(
                        activeUnits + "/" + notStarted,
                        textPad + 5
                ).setAlignment(Alignment.MID);
            }
            else{
                tlInProduction.addPara(
                        activeUnits+"",
                        textPad + 5
                ).setAlignment(Alignment.MID);
            }

        }
        else{
            tlInProduction.addPara(
                    "None",Misc.getNegativeHighlightColor(),
                    textPad + 5
            ).setAlignment(Alignment.MID);
        }
        container.addUIElement(tlInProduction).inTL(ChangeOrderProductList.getStartingX("currently_in_production"), 0);

        TooltipMakerAPI tlDays = container.createUIElement(
                ChangeOrderProductList.widthMap.get("days"),
                h,
                false
        );
        tlDays.addPara(
                getEarliestCompletionString(data),
                textPad + 5
        ).setAlignment(Alignment.MID);
        container.addUIElement(tlDays).inTL(ChangeOrderProductList.getStartingX("days"), 0);
    }

    public CustomPanelAPI createCostSection(float width, float height) {
        CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);

        ArrayList<CustomPanelAPI> panels = new ArrayList<>();
        float separatorX = 3f;
        float iconSize = 18f;
        float y = Math.max(0f, (height - iconSize) * 0.5f);

        LinkedHashMap<String, Integer> orderedResources =
                ProductionCustomButton.getOrderedResourceMap(getData().getSpec().getMapOfResourcesNeeded());

        orderedResources.forEach((commodityId, amount) ->
                panels.add(ProductionCustomButton.createRowForItem(iconSize, commodityId, amount))
        );

        if (panels.isEmpty()) {
            return mainPanel;
        }

        float totalWidth = 0f;
        for (CustomPanelAPI panel : panels) {
            totalWidth += panel.getPosition().getWidth();
        }
        totalWidth += separatorX * Math.max(0, panels.size() - 1);

        float startX = Math.max(0f, (width - totalWidth) * 0.5f);

        float currX = startX;
        for (CustomPanelAPI panel : panels) {
            mainPanel.addComponent(panel).inTL(currX, y);
            currX += panel.getPosition().getWidth() + separatorX;
        }

        return mainPanel;
    }

    private String getEarliestCompletionString(AoTDProductionOrderData data) {
        if (data.amountToProduce <= 0) {
            return "—";
        }

        if (data.unitsRewarded >= data.amountToProduce) {
            return "Done";
        }

        if (data.activeUnits > 0) {
            float daysLeft = Math.max(0f, data.daysPerUnit - data.activeDays);
            return Misc.getRoundedValueMaxOneAfterDecimal(daysLeft);
        }

        return "—";
    }

    private boolean hasAssignedResources(AoTDProductionOrderData data) {
        if (data.deliveredResources == null || data.deliveredResources.isEmpty()) {
            return false;
        }

        for (Integer value : data.deliveredResources.values()) {
            if (value != null && value > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (isChecked()) {
            setChecked(false);

            if (decreaseListener != null) {
                decreaseListener.onDecreaseRequested(getData(), hasAssignedResources(getData()));
            }
        }
    }
}