package data.kaysaar.aotd.vok.ui.customprod.components.optionpanels;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpOptionSorter;
import data.kaysaar.aotd.vok.ui.customprod.components.OptionPanelDesigner;
import data.kaysaar.aotd.vok.ui.customprod.components.SortingState;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin.maxItemsPerPage;
import static data.kaysaar.aotd.vok.ui.customprod.components.UIData.*;

public class ItemOptionPanelManager extends BaseOptionPanelManager {
    public ItemOptionPanelManager(CustomPanelAPI panel , float padding){
        GPManager.getInstance().getUIData().populateItemInfo();
        this.padding = padding;
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight()-padding, null);
        YHeight = panel.getPosition().getHeight() * 0.45f-padding;
    }
    @Override
    public CustomPanelAPI getOptionPanel() {
        return optionPanel;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return buttonDesignPanel;
    }

    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return orderButtons;
    }


    public void reset() {
        buttonsPage.clear();
        panel.removeComponent(optionPanel);
        panel.removeComponent(buttonPanel);
        createItemPanels(this.panel);
    }

    @Override
    public void init() {
        createItemPanels(panel);
        createDesignButtons(GPManager.getInstance().getUIData().getItemManInffo());
        createSortingButtons(false, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, padding);
    }
    public void createSortingButtons(boolean forFighter, boolean isWeapon) {
        buttonSortingPnael = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 20, null);
        mapOfButtonStates.put("Name", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Build time", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Design Type", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Cost", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Gp cost", SortingState.NON_INITIALIZED);
        String name = "Item name";
        TooltipMakerAPI tooltip = buttonSortingPnael.createUIElement(UIData.WIDTH_OF_OPTIONS, 20, false);
        ArrayList<ButtonAPI> sortButtons = new ArrayList<>();
        sortButtons.add(tooltip.addAreaCheckbox(name, "Name", base, bg, bright, UIData.WIDTH_OF_NAME+WIDTH_OF_TYPE, 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Build time", "Build time", base, bg, bright, UIData.WIDTH_OF_BUILD_TIME, 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Design type", "Design Type", base, bg, bright,WIDTH_OF_DESIGN_TYPE , 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Total cost", "Cost", base, bg, bright, UIData.WIDTH_OF_CREDIT_COST+UIData.WIDTH_OF_GP+WIDTH_OF_SIZE, 20, 0f));

        float currentX = 0;
        for (ButtonAPI sortButton : sortButtons) {
            sortButton.getPosition().inTL(currentX, 0);
            currentX += sortButton.getPosition().getWidth();
        }
        buttonSortingPnael.addUIElement(tooltip).inTL(-5, 0);

        sortingButtons.addAll(sortButtons);
        panel.addComponent(buttonSortingPnael).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, 50);
    }
    @Override
    public void clear() {
        buttonsPage.clear();
        buttons.clear();
        sortingButtons.clear();
        chosenManu.clear();
        if(searchbar!=null){
            searchbar.deleteAll();
        }
        this.mainPanel.removeComponent(panel);
    }

    @Override
    public void reInit() {
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight()-padding, null);
        init();
    }
    public void createItemPanels(CustomPanelAPI panel){
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages;
        packages = GpOptionSorter.getItemsBasedOnTag(chosenManu);
        if (resetToText) {
            packages = GPManager.getInstance().getMatchingItemGps(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GpOptionSorter.getItemSortedBasedOnData(option.getKey(),option.getValue(),packages);
            }
        }
        wantsAll = false;
        resetToText = false;
        currOffset = currPage * maxItemsPerPage;
        float size = packages.size();
        int maxPages = (int) (size / maxItemsPerPage);
        if ((float) maxPages != size / maxItemsPerPage) maxPages++;
        Pair<CustomPanelAPI,ArrayList<ButtonAPI>> orders = OptionPanelDesigner.createItemPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
        pageInitalization(panel, maxPages, orders);
    }
}
