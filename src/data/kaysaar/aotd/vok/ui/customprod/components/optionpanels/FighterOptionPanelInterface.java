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

public class FighterOptionPanelInterface extends BaseOptionPanelManager {
    @Override
    public CustomPanelAPI getOptionPanel() {
        return null;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return null;
    }

    public FighterOptionPanelInterface(CustomPanelAPI panel,float padding){
        GPManager.getInstance().getUIData().populateFighterInfo();
        GPManager.getInstance().getUIData().populateFighterTypeInfo();
        mapOfButtonStates = new HashMap<>();
        this.padding = padding;
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight()-padding, null);
        YHeight = panel.getPosition().getHeight() * 0.45f-40;
    }


    @Override
    public void init() {
        createFighterOptions(panel);
        createDesignButtons(GPManager.getInstance().getUIData().getFighterManInfo());
        createTypeOptions(GPManager.getInstance().getUIData().getFighterTypeInfo());
        createSortingButtons(true, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, padding);
    }

    @Override
    public void clear() {
        buttonsPage.clear();
        orderButtons.clear();
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
    private void createFighterOptions(CustomPanelAPI panel) {
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedFighters();
        packages= GpOptionSorter.getFighterBasedOnData("Cost",SortingState.ASCENDING,packages);
        packages = GpOptionSorter.getFighterPackagesBasedOnTags(chosenManu,chosenSize,chosenType);
        if (resetToText) {
            packages = GPManager.getInstance().getMatchingFighterGPs(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GpOptionSorter.getFighterBasedOnData(option.getKey(),option.getValue(),packages);
            }
        }
        wantsAll = false;
        resetToText = false;
        currOffset = currPage * maxItemsPerPage;
        float size = packages.size();
        int maxPages = (int) (size / maxItemsPerPage);
        if ((float) maxPages != size / maxItemsPerPage) maxPages++;
        Pair<CustomPanelAPI,ArrayList<ButtonAPI>>orders = OptionPanelDesigner.createFighterPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
        optionPanel = orders.one;
        pageInitalization(panel, maxPages, orders);
    }

    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return orderButtons;
    }

    @Override
    public void reset() {
        buttonsPage.clear();
        panel.removeComponent(optionPanel);
        panel.removeComponent(buttonPanel);
        createFighterOptions(this.panel);
    }
}
