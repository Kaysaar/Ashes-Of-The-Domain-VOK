package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.OptionPanelDesigner;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin.maxItemsPerPage;

public class ShipOptionPanelInterface extends BaseOptionPanelManager implements OptionPanelInterface {


    public ShipOptionPanelInterface(CustomPanelAPI panel) {
        GPManager.getInstance().populateShipInfo();
        GPManager.getInstance().populateShipSizeInfo();
        GPManager.getInstance().populateShipTypeInfo();

        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        YHeight = panel.getPosition().getHeight() * 0.45f;

    }


    public void init() {
        createShipOptions(panel);
        createDesignButtons(GPManager.getInstance().getShipManInfo());
        createSizeOptions(GPManager.getInstance().getShipSizeInfo());
        createTypeOptions(GPManager.getInstance().getShipTypeInfo());
        createSortingButtons(false, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, 0);
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
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        init();
    }




    private void createShipOptions(CustomPanelAPI panel) {
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedShipPackages();
        packages= GPManager.getInstance().getShipPackagesBasedOnData("Cost",SortingState.ASCENDING,packages);
        if (!chosenManu.isEmpty() && !wantsAll && !resetToText&&chosenType.isEmpty()&&chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getShipPackagesByManu(chosenManu);
        }
        if (chosenManu.isEmpty() && !wantsAll && !resetToText&&!chosenType.isEmpty()&&chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getShipBasedOnType(chosenType.get(0));
        }
        if (chosenManu.isEmpty() && !wantsAll && !resetToText&&chosenType.isEmpty()&&!chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getShipsBasedOnSize(chosenSize.get(0));
        }
        if (resetToText) {
            packages = GPManager.getInstance().getMatchingShipGps(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GPManager.getInstance().getShipPackagesBasedOnData(option.getKey(),option.getValue(),packages);
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
