package data.kaysaar.aotd.vok.ui.customprod.orders;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionDynamicPanelForScroll;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CurrentOrderList implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel, componentPanel, buttonPanel, headerPanel,confirmPanel;
    LinkedHashMap<String, AoTDProductionOrderData> confirmedOrders = new LinkedHashMap<>();
    ProductionDynamicPanelForScroll scrollBarV2;
    ButtonAPI name, costs, days,confirm,cancel;
    public float buttonHeight = 40f;
    public boolean needsToUpdateAnotherList = false;
    public AoTDProductionOrderSnapshot currentSnapshot;

    public LinkedHashMap<String, AoTDProductionOrderData> getConfirmedOrders() {
        return confirmedOrders;
    }

    public static final LinkedHashMap<String, Float> WIDTHS = new LinkedHashMap<>();

    static {
        WIDTHS.put("name", 0.55f);
        WIDTHS.put("cost", 0.35f);
        WIDTHS.put("days", 0.10f);
    }

    public CurrentOrderList(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);

        createUI();
    }
    public CurrentOrderList(float width, float height,AoTDProductionOrderSnapshot snapshot) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        currentSnapshot = snapshot;
        snapshot.productionData.forEach(x->confirmedOrders.put(x.getId(),x));
        createUI();
    }
    public void addOrder(String id, AoTDProductionSpec spec) {
        if(confirmedOrders.containsKey(id)) {
            confirmedOrders.get(id).setAmount(confirmedOrders.get(id).amountToProduce+1);
        }
        else{
            confirmedOrders.put(id,new AoTDProductionOrderData(id,spec));
        }

        createListSection();
        if(currentSnapshot==null){
            createConfirmSection();
        }

    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    /**
     * Distributes integer widths so their sum exactly equals effective width.
     */
    private static LinkedHashMap<String, Integer> getDistributedColumnWidths(float totalPanelWidth, int gap) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        LinkedHashMap<String, Float> fractionalParts = new LinkedHashMap<>();

        int gaps = Math.max(0, WIDTHS.size() - 1);
        int effectiveWidth = Math.round(totalPanelWidth - (gaps * gap));

        int used = 0;
        for (Map.Entry<String, Float> entry : WIDTHS.entrySet()) {
            String key = entry.getKey();
            float exact = entry.getValue() * effectiveWidth;

            int base = (int) Math.floor(exact);
            result.put(key, base);
            fractionalParts.put(key, exact - base);
            used += base;
        }

        int remainder = effectiveWidth - used;

        while (remainder > 0) {
            String bestKey = null;
            float bestFraction = -1f;

            for (Map.Entry<String, Float> entry : fractionalParts.entrySet()) {
                if (entry.getValue() > bestFraction) {
                    bestFraction = entry.getValue();
                    bestKey = entry.getKey();
                }
            }

            if (bestKey == null) break;

            result.put(bestKey, result.get(bestKey) + 1);
            fractionalParts.put(bestKey, 0f);
            remainder--;
        }

        return result;
    }

    public static int getColumnWidth(String id, float totalPanelWidth, int gap) {
        return getDistributedColumnWidths(totalPanelWidth, gap).getOrDefault(id, 0);
    }

    public static int getColumnStartX(String id, float totalPanelWidth, int gap) {
        LinkedHashMap<String, Integer> widths = getDistributedColumnWidths(totalPanelWidth, gap);

        int x = 0;
        for (String key : widths.keySet()) {
            if (id.equals(key)) {
                break;
            }
            x += widths.get(key) + gap;
        }
        return x;
    }

    private String getButtonLabel(String key) {
        switch (key) {
            case "name":
                return "Name";
            case "cost":
                return "Cost";
            case "days":
                return "Days";
            default:
                return key;
        }
    }

    @Override
    public void createUI() {
        if (componentPanel != null) {
            mainPanel.removeComponent(componentPanel);
            mainPanel.removeComponent(buttonPanel);
            mainPanel.removeComponent(headerPanel);
            mainPanel.removeComponent(confirmPanel);
        }

        name = null;
        costs = null;
        days = null;

        headerPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                20,
                null
        );
        TooltipMakerAPI tlHeader = headerPanel.createUIElement(
                headerPanel.getPosition().getWidth(),
                headerPanel.getPosition().getHeight(),
                false
        );
        String header = "Order Draft";
        if(currentSnapshot!=null){
            header = "Current Order";
        }
        tlHeader.addSectionHeading(header, Alignment.MID, 0f);
        headerPanel.addUIElement(tlHeader).inTL(0, 0);

        buttonPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                20,
                null
        );
        TooltipMakerAPI tooltipButton = buttonPanel.createUIElement(
                buttonPanel.getPosition().getWidth(),
                buttonPanel.getPosition().getHeight(),
                false
        );



        int gap = 1;
        float usableHeaderWidth = buttonPanel.getPosition().getWidth();
        LinkedHashMap<String, Integer> distributedWidths = getDistributedColumnWidths(usableHeaderWidth, gap);

        ButtonAPI previous = null;
        for (String col : WIDTHS.keySet()) {
            int buttonWidth = distributedWidths.getOrDefault(col, 0);

            ButtonAPI btn = tooltipButton.addAreaCheckbox(
                    getButtonLabel(col),
                    null,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    buttonWidth,
                    20,
                    0f
            );

            if (previous == null) {
                btn.getPosition().inTL(0, 0);
            } else {
                btn.getPosition().rightOfMid(previous, gap);
            }

            if ("name".equals(col)) {
                name = btn;
            } else if ("cost".equals(col)) {
                costs = btn;
            } else if ("days".equals(col)) {
                days = btn;
            }

            previous = btn;
        }

        buttonPanel.addUIElement(tooltipButton).inTL(0, 0);
        mainPanel.addComponent(headerPanel).inTL(0, 0);
        mainPanel.addComponent(buttonPanel).inTL(0, 21);

        createListSection();
        if(currentSnapshot==null){
            createConfirmSection();
        }


    }
    public int calculateTotalCostOfOrder(){
        int am = 0;
        for (AoTDProductionOrderData value : confirmedOrders.values()) {
            am+=value.getMoneyForAllUnits();
        }
        return am;
    }
    public void createConfirmSection() {
        if (confirmPanel != null) {
            mainPanel.removeComponent(confirmPanel);
        }

        float panelWidth = mainPanel.getPosition().getWidth();
        float panelHeight = 150f;

        confirmPanel = Global.getSettings().createCustom(panelWidth, panelHeight, null);
        TooltipMakerAPI tl = confirmPanel.createUIElement(panelWidth, panelHeight, false);

        tl.setParaFont(Fonts.ORBITRON_20AABOLD);
        tl.addPara("Total Cost of Order", 2f).setAlignment(Alignment.MID);
        tl.addPara(Misc.getDGSCredits(calculateTotalCostOfOrder()), Color.ORANGE, 3f).setAlignment(Alignment.MID);
        tl.addSectionHeading("Resource Cost", Alignment.MID, 5f);

        CustomPanelAPI resourceCostPanel = createResourceCostPanel(panelWidth - 8f, confirmedOrders);
        if (resourceCostPanel != null) {
            tl.addCustom(resourceCostPanel, 8f);
        }
        tl.setButtonFontOrbitron20();
        confirm = tl.addButton("Confirm Order",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,(confirmPanel.getPosition().getWidth()-10)/2,25,15);
        if(AshMisc.getMarketsUnderPlayer().isEmpty()){
            tl.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 400;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara("You need to establish your own faction first to use custom production!",3f);
                }
            }, TooltipMakerAPI.TooltipLocation.RIGHT,false);
        }
        confirm.getPosition().inTL(0,confirmPanel.getPosition().getHeight()-confirm.getPosition().getHeight());
        cancel = tl.addButton("Cancel",null,Misc.getBasePlayerColor(),Misc.getNegativeHighlightColor().darker().darker(),Alignment.MID,CutStyle.TL_BR,(confirmPanel.getPosition().getWidth()-10)/2,25,5f);
        cancel.getPosition().rightOfMid(confirm,10);
        confirm.setEnabled(!confirmedOrders.isEmpty()&& !AshMisc.getMarketsUnderPlayer().isEmpty());
        cancel.setEnabled(!confirmedOrders.isEmpty());
        confirmPanel.addUIElement(tl).inTL(0, 0);
        mainPanel.addComponent(confirmPanel).inTL(0, mainPanel.getPosition().getHeight() - panelHeight);
    }

    private CustomPanelAPI createResourceCostPanel(float width, LinkedHashMap<String, AoTDProductionOrderData> confirmedOrders) {
        LinkedHashMap<String, Integer> totalResources = new LinkedHashMap<>();

        for (AoTDProductionOrderData order : confirmedOrders.values()) {
            if (order == null || order.reqResourcesPerUnit == null) continue;

            int amount = Math.max(1, order.amountToProduce);

            for (Map.Entry<String, Integer> entry : order.reqResourcesPerUnit.entrySet()) {
                String commodityId = entry.getKey();
                int perUnitAmount = entry.getValue();

                if (commodityId == null || perUnitAmount <= 0) continue;

                int totalAmount = perUnitAmount * amount;
                totalResources.put(commodityId, totalResources.getOrDefault(commodityId, 0) + totalAmount);
            }
        }
        totalResources = ProductionCustomButton.getOrderedResourceMap(totalResources);
        if (totalResources.isEmpty()) {
            CustomPanelAPI emptyPanel = Global.getSettings().createCustom(width, 20f, null);
            TooltipMakerAPI emptyTl = emptyPanel.createUIElement(width, 20f, false);
            emptyTl.addPara("No resource cost", Misc.getGrayColor(), 0f).setAlignment(Alignment.MID);
            emptyPanel.addUIElement(emptyTl).inTL(0, 0);
            return emptyPanel;
        }

        float iconSize = 30;
        float itemGap = 6f;
        float rowGap = 10f;
        float usableWidth = width;

        ArrayList<CustomPanelAPI> itemPanels = new ArrayList<>();
        ArrayList<Float> itemWidths = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : totalResources.entrySet()) {
            CustomPanelAPI itemPanel = ProductionCustomButton.createRowForItem(iconSize, entry.getKey(), entry.getValue());
            itemPanels.add(itemPanel);
            itemWidths.add(itemPanel.getPosition().getWidth());
        }

        ArrayList<ArrayList<CustomPanelAPI>> rows = new ArrayList<>();
        ArrayList<ArrayList<Float>> rowWidths = new ArrayList<>();

        ArrayList<CustomPanelAPI> currentRow = new ArrayList<>();
        ArrayList<Float> currentRowWidths = new ArrayList<>();
        float currentWidth = 0f;

        for (int i = 0; i < itemPanels.size(); i++) {
            CustomPanelAPI item = itemPanels.get(i);
            float itemWidth = itemWidths.get(i);

            float extra = currentRow.isEmpty() ? itemWidth : itemGap + itemWidth;

            if (currentWidth + extra > usableWidth && !currentRow.isEmpty()) {
                rows.add(currentRow);
                rowWidths.add(currentRowWidths);

                currentRow = new ArrayList<>();
                currentRowWidths = new ArrayList<>();
                currentWidth = 0f;
            }

            currentRow.add(item);
            currentRowWidths.add(itemWidth);
            currentWidth += currentRow.size() == 1 ? itemWidth : itemGap + itemWidth;
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
            rowWidths.add(currentRowWidths);
        }

        float rowHeight = iconSize;
        float totalHeight = rows.size() * rowHeight + Math.max(0, rows.size() - 1) * rowGap;

        CustomPanelAPI wrapper = Global.getSettings().createCustom(width, totalHeight, null);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            ArrayList<CustomPanelAPI> row = rows.get(rowIndex);
            ArrayList<Float> widths = rowWidths.get(rowIndex);

            float rowUsedWidth = 0f;
            for (int i = 0; i < widths.size(); i++) {
                rowUsedWidth += widths.get(i);
                if (i > 0) rowUsedWidth += itemGap;
            }

            float startX = Math.max(0f, (usableWidth - rowUsedWidth) * 0.5f);
            float y = rowIndex * (rowHeight + rowGap);

            float x = startX;
            for (int i = 0; i < row.size(); i++) {
                CustomPanelAPI item = row.get(i);
                wrapper.addComponent(item).inTL(x, y);
                x += widths.get(i) + itemGap;
            }
        }

        return wrapper;
    }
    private void createListSection() {
        if(componentPanel!=null){
            componentPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                mainPanel.getPosition().getHeight() - 195,
                null
        );
        if (scrollBarV2 == null) {
            scrollBarV2 = new ProductionDynamicPanelForScroll(
                    componentPanel.getPosition().getWidth(),
                    componentPanel.getPosition().getHeight(),
                    componentPanel,
                    40,
                    buttonHeight,
                    2
            );
        } else {
            scrollBarV2.clearUI();
            scrollBarV2 = new ProductionDynamicPanelForScroll(
                    componentPanel.getPosition().getWidth(),
                    componentPanel.getPosition().getHeight(),
                    componentPanel,
                    40,
                    buttonHeight,
                    2
            );
        }

        TooltipMakerAPI tl = componentPanel.createUIElement(
                componentPanel.getPosition().getWidth()+9,
                componentPanel.getPosition().getHeight(),
                true
        );

        for (AoTDProductionOrderData value : confirmedOrders.values()) {
            OrderCustomButton bt = new OrderCustomButton(
                    componentPanel.getPosition().getWidth(),
                    buttonHeight,
                    value
            );
            bt.setListener(new CustomButton.ButtonEventListener() {
                @Override
                public void onButtonClicked() {
                    removeOrder(bt.getData().getId());
                }
            });
            scrollBarV2.addItem(bt.getMainPanel());
        }

        scrollBarV2.createUI();

        tl.addCustom(scrollBarV2.getMainPanel(), 0f).getPosition().inTL(0, 2);
        if(tl.getHeightSoFar()>componentPanel.getPosition().getHeight()){
            tl.addSpacer(2f);
        }
        componentPanel.addUIElement(tl).inTL(0, 0);
        mainPanel.addComponent(componentPanel).inTL(-5, 42);
    }

    @Override
    public void clearUI() {

    }
    public void removeOrder(String id ){
        if(confirmedOrders.containsKey(id)){
            AoTDProductionOrderData data = confirmedOrders.get(id);
            int newAm = data.amountToProduce-1;
            if(newAm<=0){
                confirmedOrders.remove(id);
            }
            else{
                data.setAmount(newAm);
            }
            createListSection();
            if(currentSnapshot==null){
                createConfirmSection();
            }

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

    }

    @Override
    public void advance(float amount) {
        if(cancel!=null&&cancel.isChecked()){
            cancel.setChecked(false);
            confirmedOrders.clear();
            createListSection();
            createConfirmSection();
        }
        if(confirm!=null&&confirm.isChecked()){
            confirm.setChecked(false);
            AoTDProductionOrderSnapshot snapshot = new AoTDProductionOrderSnapshot();
            snapshot.productionData.addAll(confirmedOrders.values());
            confirmedOrders.clear();
            snapshot.initSnapshotConsumption();
            createListSection();
            createConfirmSection();
        }

    }

    public void setNeedsToUpdateAnotherList(boolean needsToUpdateAnotherList) {
        this.needsToUpdateAnotherList = needsToUpdateAnotherList;
    }

    public boolean isNeedsToUpdateAnotherList() {
        return needsToUpdateAnotherList;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}