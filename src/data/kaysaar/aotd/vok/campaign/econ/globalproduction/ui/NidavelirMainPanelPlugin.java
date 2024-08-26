package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOrder;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.ProductionDataPanel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.ButtonOnHoverInfo;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels.*;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipInfoGenerator;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class NidavelirMainPanelPlugin implements CustomUIPanelPlugin {
    PositionAPI p;
    InteractionDialogAPI dialog;
    CustomVisualDialogDelegate.DialogCallbacks callbacks;
    CustomPanelAPI panel;
    public static int maxItemsPerPage = 70;
    public static int maxItemsPerPageWEP = 45;
    boolean showProjectList;
    ArrayList<GPOrder> ordersQueued = new ArrayList<>();

    public NidavelirMainPanelPlugin(boolean showProjectList) {
        this.showProjectList = showProjectList;
    }

    public static Color base = Global.getSector().getPlayerFaction().getBaseUIColor();
    public static Color bg = Global.getSector().getPlayerFaction().getDarkUIColor();
    public static Color bright = Color.white;
    TooltipMakerAPI tooltipOfOrders;
    float spacerX = 7f; //Used for left panels
    ShipOptionPanelInterface shipPanelManager;
    WeaponOptionPanelInterface weaponPanelManager;
    FighterOptionPanelInterface fighterPanelInterface;
    SpecialProjectManager specialProjectManager;
    OptionPanelInterface currentManager;
    CustomPanelAPI panelOfMarketData;
    CustomPanelAPI panelOfOrders;
    CustomPanelAPI sortingButtonsPanel;
    CustomPanelAPI topPanel;
    CustomPanelAPI currentProjectPanel;
    CustomPanelAPI panelOfProdData;
    ArrayList<ButtonAPI> switchingButtons = new ArrayList<>();
    ArrayList<ButtonAPI> orderSortingButtons = new ArrayList<>();
    ArrayList<ButtonAPI> orders = new ArrayList<>();
    // 30 for top buttons , 60 for bottom ones rest is padding
    float leftHeight = UIData.HEIGHT - 30 - 145 - 20;
    float offset = 0f;
    boolean isPressingShift = false;
    ProductionDataPanel panelOfProdDatas;
    ButtonAPI projectReference;
    CustomPanelAPI costConfirmOrders;
    ButtonAPI confirmButton;
    ButtonAPI cancelButtono;

    public void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {

        this.panel = panel;
        this.callbacks = callbacks;
        copyFromOriginal();
        maxItemsPerPage = AoTDSettingsManager.getIntValue("aotd_shipyard_pag_per_page");
        maxItemsPerPageWEP = maxItemsPerPage;
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_shipyard", true);
        float padding  = 40f;
        shipPanelManager = new ShipOptionPanelInterface(this.panel,padding);
        weaponPanelManager = new WeaponOptionPanelInterface(this.panel,padding);
        fighterPanelInterface = new FighterOptionPanelInterface(this.panel,padding);
        specialProjectManager = new SpecialProjectManager(this.panel,padding);
        currentManager = shipPanelManager;
        if (showProjectList) {
            currentManager = specialProjectManager;
        }
        this.dialog = dialog;
        panelOfProdDatas = new ProductionDataPanel(UIData.WIDTH_OF_ORDERS, 130, panel, spacerX, 71);
        currentManager.init();
        createTopBar(padding);
        createMarketResourcesPanel();
        createSpecialProjectBar();
        createOrders();
        isPressingShift = false;
        panelOfProdDatas.createUI();
    }

    private void copyFromOriginal() {
        for (GPOrder productionOrder : GPManager.getInstance().getProductionOrders()) {
            ordersQueued.add(productionOrder.cloneOrder());
        }
    }


    public CustomPanelAPI createPaymentConfirm(float width, float height) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        tooltip.setParaFont(Fonts.ORBITRON_16);
        tooltip.addPara("Cost :%s", -20f, Color.ORANGE, Misc.getDGSCredits(calculateDifference()));
        tooltip.addPara("Currently has :%s", 4f, Color.ORANGE, Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get()));
        ButtonAPI button = tooltip.addButton("Confirm", null, base, bg, Alignment.MID, CutStyle.NONE, width/2, 30, 15f);
        ButtonAPI caancelBut = tooltip.addButton("Cancel", null, base, bg, Alignment.MID, CutStyle.NONE, width/2, 30, 15f);
        float diff = calculateDifference();

        if (!isThereDifferenceBetweenQueueAndOriginal()) {
            button.setEnabled(false);
            caancelBut.setEnabled(false);
        }
        if (diff > Global.getSector().getPlayerFleet().getCargo().getCredits().get()) {
            button.setEnabled(false);

        }
        confirmButton = button;
        cancelButtono = caancelBut;
        panel.addUIElement(tooltip).inTL(width/4, 0);
        return panel;
    }

    public boolean isThereDifferenceBetweenQueueAndOriginal() {
        int size = GPManager.getInstance().getProductionOrders().size();
        int queueSize = ordersQueued.size();
        if (size != queueSize) return true;
        for (int i = 0; i < size; i++) {
            GPOrder order = GPManager.getInstance().getProductionOrders().get(i);
            GPOrder queueOrder = ordersQueued.get(i);
            if (!order.getSpecFromClass().getProjectId().equals(queueOrder.getSpecFromClass().getProjectId())) {
                return true;
            } else {
                if (order.getAmountToProduce() != queueOrder.getAmountToProduce()) {
                    return true;
                }
            }
        }
        return false;
    }

    public float calculateDifference() {
        ArrayList<GPOrder> notFoundIdsOriginalProdOrder = new ArrayList<>();
        ArrayList<GPOrder> notFoundIdsQueue = new ArrayList<>();
        float credits = 0f;
        for (GPOrder productionOrder : GPManager.getInstance().getProductionOrders()) {
            boolean found = false;
            for (GPOrder gpOrder : ordersQueued) {
                if (productionOrder.getSpecFromClass().getProjectId().equals(gpOrder.getSpecFromClass().getProjectId())) {
                    int queue = gpOrder.getAmountToProduce();
                    int inProd = productionOrder.getAmountToProduce();
                    int diff = queue - inProd;
                    credits += diff * gpOrder.getSpecFromClass().getCredistCost();
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFoundIdsQueue.add(productionOrder);

            }
        }
        for (GPOrder productionOrder : ordersQueued) {
            boolean found = false;
            for (GPOrder gpOrder : GPManager.getInstance().getProductionOrders()) {
                if (productionOrder.getSpecFromClass().getProjectId().equals(gpOrder.getSpecFromClass().getProjectId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFoundIdsOriginalProdOrder.add(productionOrder);
            }
        }
        for (GPOrder gpOrder : notFoundIdsQueue) {
            credits -= gpOrder.getAmountToProduce() * gpOrder.getSpecFromClass().getCredistCost();
        }
        for (GPOrder gpOrder : notFoundIdsOriginalProdOrder) {
            credits += gpOrder.getAmountToProduce() * gpOrder.getSpecFromClass().getCredistCost();
        }
        return credits;
    }


    public CustomPanelAPI createCurrentSpecialProjectShowcase(float width, float height) {
        GpSpecialProjectData data = GPManager.getInstance().getCurrProjOnGoing();
        UILinesRenderer progressionRenderer = new UILinesRenderer(0f);
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        CustomPanelAPI progressionBar = panel.createCustomPanel(width / 2, 20, progressionRenderer);
        progressionRenderer.setPanel(progressionBar);
        progressionRenderer.enableProgressMode(GPManager.getInstance().getCurrProjOnGoing().getCurrentProgressOfStage());
        projectReference = tooltip.addAreaCheckbox("", GPManager.getInstance().getCurrProjOnGoing(), base, bg, bright, width, height, 0f);
        LabelAPI title = tooltip.addPara("Project :" + data.getSpec().getNameOverride(), Color.ORANGE, 0f);
        CustomPanelAPI shipPanel = ShipInfoGenerator.getShipImage(Global.getSettings().getHullSpec(data.getSpec().getRewardId()), height - 10, null).one;
        tooltip.addCustom(shipPanel, 5f).getPosition().inTL(5, 5);
        title.getPosition().inTL(40, height / 2 - title.computeTextHeight(title.getText()) / 2);

        panel.addUIElement(tooltip).inTL(-4, 0);
        return panel;

    }

    public void createSpecialProjectBar() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        currentProjectPanel = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, 70, renderer);
        TooltipMakerAPI tooltip = currentProjectPanel.createUIElement(UIData.WIDTH_OF_ORDERS, 70, false);
        tooltip.addSectionHeading("On-going special project", Alignment.MID, 0f);
        if (GPManager.getInstance().getCurrProjOnGoing() == null) {
            projectReference = null;
            tooltip.addPara("None", 15f);
        } else {
            tooltip.addCustom(createCurrentSpecialProjectShowcase(UIData.WIDTH_OF_ORDERS - 10, 40), 5f);
        }
        renderer.setPanel(currentProjectPanel);
        currentProjectPanel.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(currentProjectPanel).inTL(spacerX, 210);
    }

    public void clearSpecProjBar() {
        panel.removeComponent(currentProjectPanel);
    }

    public void createTopBar(float padding) {
        topPanel = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 20, null);
        TooltipMakerAPI tooltip = topPanel.createUIElement(UIData.WIDTH_OF_OPTIONS, 20, false);
        ArrayList<ButtonAPI> butt = new ArrayList<>();
        LabelAPI text = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        butt.add(tooltip.addButton("Ships", "ship", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Ships") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order ships to be build in dockyards, and they will be delivered to gathering point when completed.", null, null, null, "Ship building section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        butt.add(tooltip.addButton("Weapons", "weapon", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Weapons") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order weapons to be crafted in factories, and they will be delivered to gathering point when completed.", null, null, null, "Weapon crafting section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        butt.add(tooltip.addButton("Fighters", "fighter", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Fighters") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order fighters to be assembled in shipyards, and they will be delivered to gathering point when completed.", null, null, null, "Fighter assembly section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        butt.add(tooltip.addButton("Special Projects", "sp", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Special Projects") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(
                new ButtonOnHoverInfo(
                        400,
                        !GPManager.getInstance().hasAtLestOneProjectUnlocked(),
                        "No special projects unlocked to access this tab!",
                        "This section leads to our most challenging projects.",
                        "These projects involve our most expensive ships and complex undertakings.",
                        "Attempting these can drain all faction resources.",
                        "With our industrial might, we can succeed.",
                        "Special Projects Section"
                ),
                TooltipMakerAPI.TooltipLocation.BELOW,
                false
        );
        butt.add(tooltip.addButton("Items", "sp", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Items") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, true, "Work In Progress", null, null, null, null, "Colony item forge section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        butt.add(tooltip.addButton("Megastructures", "sp", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Megastructures") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, true, "Work In Progress", null, null, null, null, "Megastrucutre construction section"), TooltipMakerAPI.TooltipLocation.BELOW, false);
        float currX = 0;
        float paddingX = 5f;
        for (ButtonAPI buttonAPI : butt) {
            buttonAPI.getPosition().inTL(currX, 0);
            currX += buttonAPI.getPosition().getWidth() + paddingX;
        }
        if (!GPManager.getInstance().hasAtLestOneProjectUnlocked()) {
            butt.get(3).setEnabled(false);
        }
        butt.get(4).setEnabled(false);
        butt.get(5).setEnabled(false);
        switchingButtons.addAll(butt);
        topPanel.addUIElement(tooltip).inTL(-5, 0);
        panel.addComponent(topPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, padding+30);

    }

    public void createOrders() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        float yPad = 290;
        float height = panel.getPosition().getHeight() - 20 - yPad - 150;
        sortingButtonsPanel = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, 50, renderer);
        panelOfOrders = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, height-50, renderer);
        TooltipMakerAPI tooltip = sortingButtonsPanel.createUIElement(UIData.WIDTH_OF_ORDERS, 50, false);
        TooltipMakerAPI tooltip2 = panelOfOrders.createUIElement(UIData.WIDTH_OF_ORDERS + 5, height-50, true);
        LabelAPI label = tooltip.addSectionHeading("On-going production orders", Alignment.MID, 0f);
        float y = -label.getPosition().getY() + 5;
        ArrayList<ButtonAPI> butt = new ArrayList<>();
        butt.add(tooltip.addAreaCheckbox("Name", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_ORDER, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("Qty", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_QTY, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("Days", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_DAYS, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("Cost(Credits)", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_COST, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("Cost(GP)", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_GP + 1, 20, 0f));
        float x = 0;
        for (ButtonAPI buttonAPI : butt) {
            buttonAPI.getPosition().inTL(x, y);
            x += buttonAPI.getPosition().getWidth() + 1;
        }
        for (GPOrder productionOrder : ordersQueued) {
            Pair<CustomPanelAPI, ButtonAPI> pair = UIData.getOrderPanel(productionOrder);
            tooltip2.addCustom(pair.one, 5f);
            orders.add(pair.two);
        }
        if (orderSortingButtons == null) orderSortingButtons = new ArrayList<>();
        orderSortingButtons.addAll(butt);


        tooltipOfOrders = tooltip2;
        panelOfOrders.addUIElement(tooltip2).inTL(0, 0);
        sortingButtonsPanel.addUIElement(tooltip).inTL(0, 0);
        costConfirmOrders = createPaymentConfirm(UIData.WIDTH_OF_ORDERS, 80);
        tooltip2.getExternalScroller().setYOffset(offset);
        panel.addComponent(sortingButtonsPanel).inTL(spacerX, yPad);
        panel.addComponent(panelOfOrders).inTL(spacerX, yPad + 50);
        panel.addComponent(costConfirmOrders).inTL(spacerX, yPad + height + 35);
    }

    public void resetPanelOfOrders() {
        orderSortingButtons.clear();
        orders.clear();
        panel.removeComponent(panelOfOrders);
        panel.removeComponent(sortingButtonsPanel);
        panel.removeComponent(costConfirmOrders);
        resetPanelOfMarketData();
        createOrders();

    }

    public void clearPanelOfMarketData() {
        panel.removeComponent(panelOfMarketData);
    }

    public void clearAll() {
        orderSortingButtons.clear();
        clearPanelOfMarketData();
        panel.removeComponent(sortingButtonsPanel);
        panel.removeComponent(costConfirmOrders);
    }

    public void resetPanelOfMarketData() {
        clearPanelOfMarketData();
        createMarketResourcesPanel();
    }

    public LinkedHashMap<String, Integer> getExpectedCosts() {
        LinkedHashMap<String, Integer> reqResources = new LinkedHashMap<>();
        reqResources.clear();
        for (String s : commodities) {
            reqResources.put(s, 0);
        }
        if (GPManager.getInstance().getCurrProjOnGoing() != null && !GPManager.getInstance().getCurrProjOnGoing().isFinished()) {
            for (Map.Entry<String, Integer> entry : GPManager.getInstance().getCurrProjOnGoing().retrieveCostForCurrStage().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue());
                }
            }
        }
        for (GPOrder productionOrder : ordersQueued) {
            for (Map.Entry<String, Integer> entry : productionOrder.getReqResources().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue() * GPManager.getInstance().getAmountForOrder(productionOrder));
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() * GPManager.getInstance().getAmountForOrder(productionOrder));
                }
            }
        }

        return reqResources;
    }

    public void createMarketResourcesPanel() {
        float width = UIData.WIDTH/2;
        panelOfMarketData = panel.createCustomPanel(width, 50, null);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(width, 145, false);
        float totalSize =width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 40;
        float topYImage = 5;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()){
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text =""+entry.getValue();
            String text2 = text+"("+GPManager.getInstance().getReqResources().get(entry.getKey())+")";
            tooltip.addPara(""+entry.getValue()+" %s",0f,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE, "("+GPManager.getInstance().getReqResources().get(entry.getKey())+")").getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;

        }

        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfMarketData).inTL(5+width/2, 5);
    }

    public void reset() {

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

        if (tooltipOfOrders != null) {
            offset = tooltipOfOrders.getExternalScroller().getYOffset();
        }
        if (currentManager != null) {
            boolean replace = false;
            if (currentManager instanceof SpecialProjectManager) {
                if (((SpecialProjectManager) currentManager).getCurrentProjectButton() != null) {
                    if (((SpecialProjectManager) currentManager).getCurrentProjectButton().isChecked()) {
                        replace = true;
                    }
                }
            }
            currentManager.advance(amount);
            if (currentManager.getOrderButtons() != null) {
                for (ButtonAPI orderButton : currentManager.getOrderButtons()) {
                    if (orderButton.isChecked()) {
                        orderButton.setChecked(false);
                        GPOption option = (GPOption) orderButton.getCustomData();
                        GPManager.getInstance().addOrderToDummy(option.getSpec().getProjectId(), 1, ordersQueued);
                        resetPanelOfOrders();
                        break;
                    }
                }
            }
            if (replace) {
                clearSpecProjBar();
                createSpecialProjectBar();
                resetPanelOfMarketData();
            }

        }
        for (ButtonAPI switchingButton : switchingButtons) {
            if (switchingButton.isChecked()) {
                switchingButton.setChecked(false);
                if (switchingButton.getCustomData() instanceof String) {
                    String match = (String) switchingButton.getCustomData();
                    if (match.equals("ship") && !currentManager.getClass().isInstance(shipPanelManager)) {
                        currentManager.clear();
                        currentManager = shipPanelManager;
                        shipPanelManager.reInit();
                        break;
                    }
                    if (match.equals("weapon") && !currentManager.getClass().isInstance(weaponPanelManager)) {
                        currentManager.clear();
                        currentManager = weaponPanelManager;
                        weaponPanelManager.reInit();
                        break;
                    }
                    if (match.equals("fighter") && !currentManager.getClass().isInstance(fighterPanelInterface)) {
                        currentManager.clear();
                        currentManager = fighterPanelInterface;
                        fighterPanelInterface.reInit();
                        break;
                    }
                    if (match.equals("sp") && !currentManager.getClass().isInstance(specialProjectManager)) {
                        currentManager.clear();
                        currentManager = specialProjectManager;
                        specialProjectManager.reInit();
                        break;
                    }
                }
            }
        }
        for (ButtonAPI order : orders) {
            if (order.isChecked()) {
                order.setChecked(false);
                GPOrder ordera = (GPOrder) order.getCustomData();
                GPManager.getInstance().addOrderToDummy(ordera.getSpecFromClass().getProjectId(), 1, ordersQueued);
                ArrayList<Integer> offsetOfOrdersToBeRemoved = GPManager.getInstance().retrieveOrdersToBeRemovedFromDummy(ordersQueued);
                if (!offsetOfOrdersToBeRemoved.isEmpty()) {
                    GPManager.getInstance().removeDoneOrdersDummy(offsetOfOrdersToBeRemoved, ordersQueued);
                }

                resetPanelOfOrders();
                break;
            }
        }
        if (projectReference != null) {
            if (projectReference.isChecked()) {
                projectReference.setChecked(false);
                if (!currentManager.getClass().isInstance(specialProjectManager)) {
                    currentManager.clear();
                    currentManager = specialProjectManager;
                    specialProjectManager.reInit();
                    specialProjectManager.createSpecialProjectShowcase((GpSpecialProjectData) projectReference.getCustomData());
                }
            }
        }
        if (confirmButton != null) {
            if (confirmButton.isChecked()) {
                confirmButton.setChecked(false);
                float money = calculateDifference();
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(money);
                GPManager.getInstance().getProductionOrders().clear();
                for (GPOrder order : ordersQueued) {
                    GPManager.getInstance().getProductionOrders().add(order.cloneOrder());
                }
                ordersQueued.clear();
                copyFromOriginal();
                resetPanelOfOrders();
            }
        }
        if (cancelButtono != null) {
            if (cancelButtono.isChecked()) {
                cancelButtono.setChecked(false);
                ordersQueued.clear();
                copyFromOriginal();
                resetPanelOfOrders();
            }
        }
        if (panelOfProdDatas != null) {
            panelOfProdDatas.advance();
            if (panelOfProdDatas.resets) {
                resetPanelOfOrders();
            }
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isRMBDownEvent() && !event.isLMBEvent()) {
                for (ButtonAPI buttonAPI : orders) {
                    if (AoTDMisc.isHoveringOverButton(buttonAPI, 0f)) {
                        buttonAPI.setChecked(false);
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                        GPOrder order = (GPOrder) buttonAPI.getCustomData();
                        GPManager.getInstance().removeOrderFromDummy(order.getSpecFromClass().getProjectId(), 1, ordersQueued);
                        ArrayList<Integer> offsetOfOrdersToBeRemoved = GPManager.getInstance().retrieveOrdersToBeRemovedFromDummy(ordersQueued);
                        if (!offsetOfOrdersToBeRemoved.isEmpty()) {
                            GPManager.getInstance().removeDoneOrdersDummy(offsetOfOrdersToBeRemoved, ordersQueued);
                        }
                        resetPanelOfOrders();
                        break;
                    }
                }
            }
            if (event.isShiftDown()) {
                isPressingShift = true;
            }
            if (event.getEventValue() == Keyboard.KEY_ESCAPE && !event.isRMBEvent()) {
                specialProjectManager.clear();
                shipPanelManager.clear();
                fighterPanelInterface.clear();
                weaponPanelManager.clear();
                switchingButtons.clear();
                ordersQueued.clear();
                panel.removeComponent(panelOfOrders);
                panel.removeComponent(sortingButtonsPanel);
                panel.removeComponent(costConfirmOrders);
                panel.removeComponent(topPanel);
                panelOfProdDatas.clearUI();
                orders.clear();
                orderSortingButtons.clear();
                clearAll();
                clearSpecProjBar();
                Global.getSoundPlayer().pauseCustomMusic();
                Global.getSoundPlayer().restartCurrentMusic();
                dialog.dismiss();

            }
        }
    }

    public GPOrder getOrderFromDummy(String id) {
        for (GPOrder gpOrder : ordersQueued) {
            if (gpOrder.getSpecFromClass().getProjectId().equals(id)) {
                return gpOrder;
            }
        }
        return null;
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
