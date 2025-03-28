package data.kaysaar.aotd.vok.ui.customprod.components.optionpanels;

import com.fs.starfarer.api.ui.*;
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

public class ShipOptionPanelInterface extends BaseOptionPanelManager {


    public ShipOptionPanelInterface(CustomPanelAPI panel,float padding,boolean isForProd) {
        GPManager.getInstance().getUIData().populateShipInfo();
        GPManager.getInstance().getUIData().populateShipSizeInfo();
        GPManager.getInstance().getUIData().populateShipTypeInfo();
        this.padding = padding;
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight()-padding, null);
        if(isForProd){
            YHeight = panel.getPosition().getHeight() * 0.45f-padding;
        }
        else {
            YHeight = panel.getPosition().getHeight() * 0.35f-padding;
        }

    }


    public void init() {
        createShipOptions(panel);
        createDesignButtons(GPManager.getInstance().getUIData().getShipManInfo());
        createSizeOptions(GPManager.getInstance().getUIData().getShipSizeInfo());
        createTypeOptions(GPManager.getInstance().getUIData().getShipTypeInfo());
        createSortingButtons(false, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, padding);
    }
    public void initForFleet() {
        createShipOptions(panel);
        createDesignButtons(GPManager.getInstance().getUIData().getShipManInfo());
        createSizeOptions(GPManager.getInstance().getUIData().getShipSizeInfo());
        createTypeOptions(GPManager.getInstance().getUIData().getShipTypeInfo());
        createSortingButtons(false, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, padding);
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




    private void createShipOptions(CustomPanelAPI panel) {
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedShipPackages();
        packages = GpOptionSorter.getShipPackagesBasedOnTags(chosenManu,chosenSize,chosenType);
        if (resetToText) {
            packages = GPManager.getInstance().getMatchingShipGps(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GpOptionSorter.getShipPackagesBasedOnData(option.getKey(),option.getValue(),packages);
            }
        }
        wantsAll = false;
        resetToText = false;
        currOffset = currPage * maxItemsPerPage;
        float size = packages.size();
        int maxPages = (int) (size / maxItemsPerPage);
        if ((float) maxPages != size / maxItemsPerPage) maxPages++;
        Pair<CustomPanelAPI,ArrayList<ButtonAPI>> orders = OptionPanelDesigner.createShipPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
        pageInitalization(panel, maxPages, orders);
    }

    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return orderButtons;
    }


    public void reset() {
        buttonsPage.clear();
        panel.removeComponent(optionPanel);
        panel.removeComponent(buttonPanel);
        createShipOptions(this.panel);
    }


    @Override
    public CustomPanelAPI getOptionPanel() {
        return optionPanel;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return buttonDesignPanel;
    }
}
