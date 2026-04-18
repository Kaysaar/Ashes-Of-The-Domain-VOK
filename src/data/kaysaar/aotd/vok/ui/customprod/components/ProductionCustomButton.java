package data.kaysaar.aotd.vok.ui.customprod.components;

import ashlib.data.plugins.ui.models.CustomButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;


import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductionCustomButton extends CustomButton {

    boolean isCreated = false;
    boolean isOrderMode = false;
    public boolean isCreated() {
        return isCreated;
    }


    public ProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData,0f, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Misc.getBrightPlayerColor());
    }
    public ProductionCustomButton(float width, float height, AoTDProductionSpec buttonData,boolean isOrderMode) {
        super(width, height, buttonData,0f, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Misc.getBrightPlayerColor());
        this.isOrderMode = isOrderMode;
    }
    public AoTDProductionSpec getSpec(){
        return (AoTDProductionSpec) buttonData;
    }
    @Override
    public void createButtonContent(TooltipMakerAPI tooltip) {
        CustomPanelAPI container = Global.getSettings().createCustom(this.width,this.height,null);
        createContainerContent(container);
        isCreated = true;
        tooltip.addCustom(container,0f).getPosition().inTL(5,0);
        float centerY = height/2;
        if(isWithArrow){
            panelIndicator = Global.getSettings().createCustom(15,15,null);
//            tooltip.addCustom(panelIndicator,0f).getPosition().inTL((float) StarSystemHoldingTable.widthMap.get("name")*0.75f,centerY-7);

        }
    }
    public void createContainerContent(CustomPanelAPI container) {

    }
    public CustomPanelAPI createCostSection(float width, float height) {
        CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);

        ArrayList<CustomPanelAPI> panels = new ArrayList<>();
        float separatorX = 3f;
        float y = 5f;

        LinkedHashMap<String, Integer> orderedResources = getOrderedResourceMap(getSpec().getMapOfResourcesNeeded());

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
                Misc.getDGSCredits(getSpec().getProductionCost()),
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

            String toHighlight =  displayAmount;
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
        if (amount < 1000) {
            return String.valueOf(amount);
        }

        if (amount < 1_000_000) {
            return formatWithSuffix(amount / 1000f, "k");
        }

        return formatWithSuffix(amount / 1_000_000f, "m");
    }
    public static LinkedHashMap<String, Integer> getOrderedResourceMap(Map<String, Integer> input) {
        LinkedHashMap<String, Integer> ordered = new LinkedHashMap<>();
        if (input == null || input.isEmpty()) return ordered;

        // First: explicitly ordered items
        for (String orderedId : AoTDProductionSpecManager.orderedItemsForUI) {
            Integer amount = input.get(orderedId);
            if (amount != null && amount > 0) {
                ordered.put(orderedId, amount);
            }
        }

        // Then: everything else, stable and predictable
        input.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() > 0)
                .filter(e -> !ordered.containsKey(e.getKey()))
                .sorted((a, b) -> {
                    String nameA = getDisplayNameForResource(a.getKey());
                    String nameB = getDisplayNameForResource(b.getKey());
                    return nameA.compareToIgnoreCase(nameB);
                })
                .forEach(e -> ordered.put(e.getKey(), e.getValue()));

        return ordered;
    }

    private static String getDisplayNameForResource(String commodityId) {
        if (commodityId == null) return "";

        if (Global.getSettings().getCommoditySpec(commodityId) != null) {
            return Global.getSettings().getCommoditySpec(commodityId).getName();
        }
        if (Global.getSettings().getSpecialItemSpec(commodityId) != null) {
            return Global.getSettings().getSpecialItemSpec(commodityId).getName();
        }

        return commodityId;
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
