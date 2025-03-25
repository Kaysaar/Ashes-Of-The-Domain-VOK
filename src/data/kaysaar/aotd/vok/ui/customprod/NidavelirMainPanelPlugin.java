package data.kaysaar.aotd.vok.ui.customprod;

import ashlib.data.plugins.info.ShipInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOrder;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.ui.customprod.components.*;
import data.kaysaar.aotd.vok.ui.customprod.components.gatheringpoint.AoTDGatehringPointPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.onhover.ButtonOnHoverInfo;
import data.kaysaar.aotd.vok.ui.customprod.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.ui.customprod.components.onhover.GuideTootltip;
import data.kaysaar.aotd.vok.ui.customprod.components.optionpanels.*;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.lwjgl.input.Keyboard;
import java.awt.*;
import java.util.*;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class NidavelirMainPanelPlugin implements CustomUIPanelPlugin, SoundUIManager {
    InteractionDialogAPI dialog;
    CustomVisualDialogDelegate.DialogCallbacks callbacks;
    CustomPanelAPI panel;
    CoreUITabId prevCore;
    Object param;
    ButtonAPI helpButton;
    public static int maxItemsPerPage = 70;
    public static int maxItemsPerPageWEP = 45;
    boolean showProjectList;
    RightMouseInterceptor interceptor = new RightMouseInterceptor();
    ArrayList<GPOrder> ordersQueued = new ArrayList<>();
    boolean isPressingShift = false;
    boolean isPressingCtrl = false;

    public NidavelirMainPanelPlugin(boolean showProjectList, CoreUITabId prevCore, Object param) {
        this.showProjectList = showProjectList;
        this.prevCore = prevCore;
        this.param = param;
    }

    public static Color base = Global.getSector().getPlayerFaction().getBaseUIColor();
    public static Color bg = Global.getSector().getPlayerFaction().getDarkUIColor();
    public static Color bright = Color.white;
    TooltipMakerAPI tooltipOfOrders;
    float spacerX = 7f; //Used for left panels
    public static boolean isShowingUI = false;
    ShipOptionPanelInterface shipPanelManager;
    WeaponOptionPanelInterface weaponPanelManager;
    FighterOptionPanelInterface fighterPanelInterface;
    ItemOptionPanelManager itemOptionPanelManager;
    SpecialProjectManager specialProjectManager;
    OptionPanelInterface currentManager;
    CustomPanelAPI panelOfMarketData;
    CustomPanelAPI panelOfOrders;
    CustomPanelAPI sortingButtonsPanel;
    CustomPanelAPI topPanel;
    ArrayList<ButtonAPI> switchingButtons = new ArrayList<>();
    ArrayList<ButtonAPI> orderSortingButtons = new ArrayList<>();
    ArrayList<ButtonAPI> orders = new ArrayList<>();
    ArrayList<ButtonAPI> coreUITabs = new ArrayList<>();
    ButtonAPI gatheringPoint;
    // 30 for top buttons , 60 for bottom ones rest is padding
    float leftHeight = UIData.HEIGHT - 30 - 145 - 20;
    float offset = 0f;
    ButtonAPI projectReference;
    CustomPanelAPI costConfirmOrders;
    ButtonAPI confirmButton;
    ButtonAPI cancelButtono;
    TooltipMakerAPI tooltipMakerAPI;
    CustomPanelAPI panelOfGatheringPoint;
    UIPanelAPI mainPanel;
    CustomPanelAPI currentProjectPanel;
    public void setMainPanel(UIPanelAPI mainPanel) {
        this.mainPanel = mainPanel;
    }

    public UIPanelAPI getMainPanel() {
        return mainPanel;
    }

    public CustomPanelAPI getPanel() {
        return panel;
    }

    public void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {

        this.panel = panel;
        tooltipMakerAPI = panel.createUIElement(30, 30, false);
         helpButton =  tooltipMakerAPI.addAreaCheckbox("",null,Global.getSettings().getBasePlayerColor(), Global.getSettings().getBasePlayerColor(),Global.getSettings().getBrightPlayerColor(),29,30,0f);
        helpButton.getPosition().inTL(0,0);
        tooltipMakerAPI.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "question"), 30, 30, 0f);
        tooltipMakerAPI.getPrev().getPosition().inTL(0,0);
        //tooltipMakerAPI.addCustom(AoTDGatehringPointPlugin.getMarketEntitySpriteWithName(200,60,55,Misc.getPlayerMarkets(true).get(1)),5f);
        tooltipMakerAPI.addTooltipToPrevious(new GuideTootltip(), TooltipMakerAPI.TooltipLocation.BELOW);
        isShowingUI = true;
        this.callbacks = callbacks;
        copyFromOriginal();
        GPManager.getInstance().advance(ordersQueued);
        maxItemsPerPage = AoTDSettingsManager.getIntValue("aotd_shipyard_pag_per_page");
        maxItemsPerPageWEP = maxItemsPerPage;
        float padding = 20f;
        shipPanelManager = new ShipOptionPanelInterface(this.panel, padding,true);
        weaponPanelManager = new WeaponOptionPanelInterface(this.panel, padding);
        fighterPanelInterface = new FighterOptionPanelInterface(this.panel, padding);
        itemOptionPanelManager = new ItemOptionPanelManager(this.panel, padding);
        specialProjectManager = new SpecialProjectManager(this.panel,padding);
        currentManager = shipPanelManager;
        this.dialog = dialog;
        currentManager.init();
        createTopBar(padding);
        createMarketResourcesPanel();
        createGatheringPointBar();
        createSpecialProjectBar();
        createOrders();

        isPressingShift = false;
        isPressingCtrl = false;
        panel.addUIElement(tooltipMakerAPI).inTL(panel.getPosition().getWidth() - 35, 0);
    }

    private void copyFromOriginal() {
        for (GPOrder productionOrder : GPManager.getInstance().getProductionOrders()) {
            ordersQueued.add(productionOrder.cloneOrder());
        }
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
        panel.addComponent(currentProjectPanel).inTL(spacerX, 161);
    }

    public void clearSpecProjBar() {
        panel.removeComponent(currentProjectPanel);
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

    public CustomPanelAPI createResourceCostAfterTransaction(float width, float height) {
        CustomPanelAPI customPanel = panel.createCustomPanel(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 20;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getExpectedCosts(ordersQueued).entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false,ordersQueued), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text;
            Color col = Misc.getPositiveHighlightColor();
            if (entry.getValue() > GPManager.getInstance().getTotalResources().get(entry.getKey()))
                col = Misc.getNegativeHighlightColor();
            tooltip.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        customPanel.addUIElement(tooltip).inTL(0, 0);
        return customPanel;
    }

    public CustomPanelAPI createPaymentConfirm(float width, float height) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panel.createUIElement(width, height, false);
        tooltip.setParaFont(Fonts.ORBITRON_16);
        tooltip.addPara("Costs :%s", -20f, Color.ORANGE, Misc.getDGSCredits(calculateDifference()));
        LabelAPI label = tooltip.addPara("Owned :%s", 4f, Color.ORANGE, Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get()));
        ButtonAPI button = tooltip.addButton("Confirm", null, base, bg, Alignment.MID, CutStyle.NONE, width / 3, 30, 15f);
        ButtonAPI caancelBut = tooltip.addButton("Cancel", null, base, bg, Alignment.MID, CutStyle.NONE, width / 3, 30, 15f);
        tooltip.addPara("Estimated resource cost", 5f).getPosition().inTL(label.getPosition().getX(), -label.getPosition().getY() + 5);
        tooltip.addCustom(createResourceCostAfterTransaction(width, 30), 5f);
        float diff = calculateDifference();

        if (!isThereDifferenceBetweenQueueAndOriginal()) {
            button.setEnabled(false);
            caancelBut.setEnabled(false);
        }
        if (diff > Global.getSector().getPlayerFleet().getCargo().getCredits().get()) {
            button.setEnabled(false);

        }
        float pos = -caancelBut.getPosition().getY() - 30;
        button.getPosition().inTL(width - width / 3, pos);
        caancelBut.getPosition().inTL(0, pos);
        confirmButton = button;
        cancelButtono = caancelBut;
        panel.addUIElement(tooltip).inTL(0, 0);
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
                if (order.getAtOnce() != queueOrder.getAtOnce()) {
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




    public void createGatheringPointBar() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        panelOfGatheringPoint = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, 100, renderer);
        TooltipMakerAPI tooltip = panelOfGatheringPoint.createUIElement(UIData.WIDTH_OF_ORDERS, 100, false);
        tooltip.addSectionHeading("Production gathering point", Alignment.MID, 0f);
        if (!Misc.getPlayerMarkets(true).isEmpty()) {
            if(Global.getSector().getPlayerFaction().getProduction().getGatheringPoint()!=null){
                Pair<CustomPanelAPI, ButtonAPI> pair = AoTDGatehringPointPlugin.getMarketEntitySpriteButton(UIData.WIDTH_OF_ORDERS  - 25f, 75, 75, Global.getSector().getPlayerFaction().getProduction().getGatheringPoint());
                tooltip.addCustom(pair.one, 5f);
                gatheringPoint = pair.two;
            }
        }


        panelOfGatheringPoint.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfGatheringPoint).inTL(spacerX, 51);
    }

    public void refreshGatheringPointBar(){
        panel.removeComponent(panelOfGatheringPoint);
        createGatheringPointBar();
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
        butt.add(tooltip.addButton("Items", "items", base, bg, Alignment.MID, CutStyle.TOP, text.computeTextWidth("Items") + 30, 20, 0f));
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 500;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addTitle("Colony item forge section");
                if (GPManager.getInstance().getLearnedItems().isEmpty()) {
                    tooltip.addPara("We have no schematics of any colony item!", Misc.getNegativeHighlightColor(), 10f);
                    tooltip.addPara("To gain access we need to either find blueprints located in %s", 5f, Color.ORANGE, "Pre Collapse Facilities");
                    tooltip.addPara("Or research %s", 5f, Color.ORANGE, AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getName());

                } else {
                    tooltip.addPara("This section leads to colony item production", 10f);
                    tooltip.addPara("With knowledge we have found and might of our industries we rise from Ashes of The Domain", 10f);
                    tooltip.addPara("To expand items we can craft we need either find blueprints located in %s", 5f, Color.ORANGE, "Pre Collapse Facilities");
                    tooltip.addPara("Or research %s", 5f, Color.ORANGE, AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getName());
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW, false);
        float currX = 0;
        float paddingX = 5f;
        for (ButtonAPI buttonAPI : butt) {
            buttonAPI.getPosition().inTL(currX, 0);
            currX += buttonAPI.getPosition().getWidth() + paddingX;
        }
        if (!GPManager.getInstance().hasAtLestOneProjectUnlocked()) {
            butt.get(3).setEnabled(false);
        }
        if (GPManager.getInstance().getLearnedItems().isEmpty()&&!AoTDMisc.doesPlayerHaveTuringEngine()) {
            butt.get(4).setEnabled(false);
        }

        switchingButtons.addAll(butt);
        topPanel.addUIElement(tooltip).inTL(-5, 0);
        panel.addComponent(topPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, padding + 30);

    }

    public void createOrders() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        float yPad = 251;
        float height = panel.getPosition().getHeight() - 20 - yPad - 150;
        sortingButtonsPanel = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, 50, renderer);
        panelOfOrders = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, height - 50, renderer);
        CustomPanelAPI panelInterceptor = panelOfOrders.createCustomPanel(UIData.WIDTH_OF_ORDERS, height - 50, interceptor);
        interceptor.setPanelPos(panelInterceptor);
        TooltipMakerAPI tooltip = sortingButtonsPanel.createUIElement(UIData.WIDTH_OF_ORDERS, 50, false);
        TooltipMakerAPI tooltip2 = panelOfOrders.createUIElement(UIData.WIDTH_OF_ORDERS + 5, height - 50, true);
        LabelAPI label = tooltip.addSectionHeading("On-going production orders", Alignment.MID, 0f);
        float y = -label.getPosition().getY() + 5;
        ArrayList<ButtonAPI> butt = new ArrayList<>();
        butt.add(tooltip.addAreaCheckbox("Name", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_ORDER, 20, 0f));
        ;
        butt.add(tooltip.addAreaCheckbox("Cost per unit", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_COST, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("To produce", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_QT, 20, 0f));
        butt.add(tooltip.addAreaCheckbox("Producing", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_AT_ONCE, 20, 0f));
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
        panelOfOrders.addComponent(panelInterceptor).inTL(0,0);
        sortingButtonsPanel.addUIElement(tooltip).inTL(0, 0);
        costConfirmOrders = createPaymentConfirm(UIData.WIDTH_OF_ORDERS, 80);
        tooltip2.getExternalScroller().setYOffset(offset);
        panel.addComponent(sortingButtonsPanel).inTL(spacerX, yPad);
        panel.addComponent(panelOfOrders).inTL(spacerX, yPad + 50);
        panel.addComponent(costConfirmOrders).inTL(spacerX, yPad + height + 35);

    }

    public void resetPanelOfOrders() {
        GPManager.getInstance().advance(ordersQueued);
        orderSortingButtons.clear();
        orders.clear();
        interceptor.setPanelPos(null);
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



    public void createMarketResourcesPanel() {
        float width = UIData.WIDTH / 2;
        panelOfMarketData = panel.createCustomPanel(width, 50, null);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(width, 50, false);
        float totalSize = width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 35;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false,ordersQueued), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text + "(" + GPManager.getInstance().getReqResources(ordersQueued).get(entry.getKey()) + ")";
            tooltip.addPara("" + entry.getValue() + " %s", 0f, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "(" +  GPManager.getInstance().getExpectedCosts(ordersQueued).get(entry.getKey()) + ")").getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfMarketData).inTL(5 + width / 2, 5);
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
        if(helpButton!=null){
            if(helpButton.isChecked()){
                helpButton.setChecked(false);
                HelpPopUpUINid nid = new HelpPopUpUINid(true);
                AoTDMisc.placePopUpUI(nid,helpButton,700,400);
            }
        }
        if (tooltipOfOrders != null) {
            offset = tooltipOfOrders.getExternalScroller().getYOffset();
        }
        if (gatheringPoint!=null) {
            if(gatheringPoint.isChecked()){
                gatheringPoint.setChecked(false);
                GatheringPointDialog dialogGather = new GatheringPointDialog("Choose gathering point",this);
                BasePopUpDialog.popUpDialog(dialogGather,400,400);
            }
        }
//        for (ButtonAPI coreUITab : coreUITabs) {
//            if (coreUITab.isChecked()) {
//                coreUITab.setChecked(false);
//                clearUI();
//                Global.getSoundPlayer().pauseCustomMusic();
//                callbacks.dismissDialog();
//                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
//                dialog.getVisualPanel().showCore((CoreUITabId) coreUITab.getCustomData(), null, null, new CoreDismisserListener(dialog, true));
//                isShowingUI = false;
//                return;
//            }
//        }

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
                int amountClick = 1;
                if (isPressingCtrl) {
                    amountClick = 10;
                }
                for (ButtonAPI orderButton : currentManager.getOrderButtons()) {

                    if (orderButton.isChecked()) {
                        orderButton.setChecked(false);
                        GPOption option = (GPOption) orderButton.getCustomData();
                        GPManager.getInstance().addOrderToDummy(option.getSpec().getProjectId(), amountClick, ordersQueued);
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
                    if (match.equals("items") && !currentManager.getClass().isInstance(itemOptionPanelManager)) {
                        currentManager.clear();
                        currentManager = itemOptionPanelManager;
                        itemOptionPanelManager.reInit();
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
                int amountClick = 1;
                if (isPressingCtrl) {
                    amountClick = 10;
                }
                if (isPressingShift) {
                    GPOrder ordera = (GPOrder) order.getCustomData();
                    ordera.setAtOnce(ordera.getAtOnce() + amountClick);
                    resetDaysIfMoreAtOnce();
                    resetPanelOfOrders();
                    break;
                } else {
                    GPOrder ordera = (GPOrder) order.getCustomData();
                    GPManager.getInstance().addOrderToDummy(ordera.getSpecFromClass().getProjectId(), amountClick, ordersQueued);
                    ArrayList<Integer> offsetOfOrdersToBeRemoved = GPManager.getInstance().retrieveOrdersToBeRemovedFromDummy(ordersQueued);
                    if (!offsetOfOrdersToBeRemoved.isEmpty()) {
                        GPManager.getInstance().removeDoneOrdersDummy(offsetOfOrdersToBeRemoved, ordersQueued);
                    }
                    resetDaysIfMoreAtOnce();
                    resetPanelOfOrders();
                    break;
                }

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
                for (GPOrder productionOrder : GPManager.getInstance().getProductionOrders()) {
                    for (GPOrder gpOrder : ordersQueued) {
                        if(gpOrder.getSpecFromClass().equals(productionOrder.getSpecFromClass())){
                            if(productionOrder.getAtOnce()<gpOrder.getAtOnce()){
                                gpOrder.setDaysSpentDoingOrder(0);
                            }
                        }
                    }
                }
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


    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        boolean pressedShift = false;
        boolean pressedCtrl = false;
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isShiftDown()) {
                pressedShift = true;
            }
            if (event.isCtrlDown()) {
                pressedCtrl = true;
            }
            if (event.getEventValue() == Keyboard.KEY_ESCAPE && !event.isRMBEvent()) {
//                if(!currentManager.canClose()){
//                    continue;
//                }
//                clearUI(true);
//                isShowingUI = false;
//                if(dialog!=null){
//                    dialog.dismiss();
//                }



            }

        }
        isPressingShift = pressedShift;
        isPressingCtrl = pressedCtrl;

        if(interceptor.isInterceptingRightMouseEvent){
            for (ButtonAPI buttonAPI : orders) {
                if (AoTDMisc.isHoveringOverButton(buttonAPI, 0f)) {
                    int amountClick = 1;
                    if (isPressingCtrl) {
                        amountClick = 10;
                    }
                    if (isPressingShift) {

                        buttonAPI.setChecked(false);
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                        GPOrder order = (GPOrder) buttonAPI.getCustomData();
                        order.setAtOnce(order.getAtOnce() - amountClick);
                        resetDaysIfMoreAtOnce();
                        resetPanelOfOrders();
                        resetPanelOfMarketData();
                        break;
                    } else {
                        buttonAPI.setChecked(false);
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                        GPOrder order = (GPOrder) buttonAPI.getCustomData();
                        GPManager.getInstance().removeOrderFromDummy(order.getSpecFromClass().getProjectId(), amountClick, ordersQueued);
                        ArrayList<Integer> offsetOfOrdersToBeRemoved = GPManager.getInstance().retrieveOrdersToBeRemovedFromDummy(ordersQueued);
                        if (!offsetOfOrdersToBeRemoved.isEmpty()) {
                            GPManager.getInstance().removeDoneOrdersDummy(offsetOfOrdersToBeRemoved, ordersQueued);
                        }
                        resetDaysIfMoreAtOnce();
                        resetPanelOfOrders();
                        resetPanelOfMarketData();
                        break;
                    }

                }

            }
        }
    }

    private void resetDaysIfMoreAtOnce() {
        for (GPOrder productionOrder : GPManager.getInstance().getProductionOrders()) {
            for (GPOrder gpOrder : ordersQueued) {
                if(gpOrder.getSpecFromClass().equals(productionOrder.getSpecFromClass())){
                    if(productionOrder.getAtOnce()<gpOrder.getAtOnce()){
                        gpOrder.setDaysSpentDoingOrder(0);
                    }
                    else{
                        gpOrder.setDaysSpentDoingOrder(productionOrder.getDaysSpentDoingOrder());
                    }
                }
            }
        }
    }

    public void clearUI( boolean clearCoreUI) {

        ordersQueued.clear();
        copyFromOriginal();
        shipPanelManager.clear();
        fighterPanelInterface.clear();
        weaponPanelManager.clear();
        switchingButtons.clear();
        ordersQueued.clear();
        interceptor.setPanelPos(null);
        panel.removeComponent(panelOfOrders);
        panel.removeComponent(sortingButtonsPanel);
        panel.removeComponent(costConfirmOrders);
        panel.removeComponent(topPanel);
        panel.removeComponent(panelOfGatheringPoint);;
        coreUITabs.clear();
        orders.clear();
        orderSortingButtons.clear();
        panel.removeComponent(tooltipMakerAPI);
        clearAll();

        if(mainPanel!=null){
            mainPanel.removeComponent(panel);
        }
        Object core = ProductionUtil.getCoreUI();
        if(core!=null&&clearCoreUI){

            ReflectionUtilis.invokeMethod("dismiss",core,1);
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

    @Override
    public void playSound() {
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_shipyard", true);

    }

    @Override
    public void pauseSound() {
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().restartCurrentMusic();
    }
}
