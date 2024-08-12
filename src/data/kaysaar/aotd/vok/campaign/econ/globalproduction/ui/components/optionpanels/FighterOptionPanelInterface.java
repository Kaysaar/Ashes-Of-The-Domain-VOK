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

public class FighterOptionPanelInterface extends BaseOptionPanelManager implements OptionPanelInterface {
    @Override
    public CustomPanelAPI getOptionPanel() {
        return null;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return null;
    }

    public FighterOptionPanelInterface(CustomPanelAPI panel){
        GPManager.getInstance().populateFighterInfo();
        GPManager.getInstance().populateFighterTypeInfo();
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        YHeight = panel.getPosition().getHeight() * 0.45f;
    }


    @Override
    public void init() {
        createFighterOptions(panel);
        createDesignButtons(GPManager.getInstance().getFighterManInfo());
        createTypeOptions(GPManager.getInstance().getFighterTypeInfo());
        createSortingButtons(true, false);
        createSerachBarPanel();
        this.mainPanel.addComponent(panel).inTL(0, 0);
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
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        init();
    }
    private void createFighterOptions(CustomPanelAPI panel) {
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedFighters();
        packages= GPManager.getInstance().getFighterBasedOnData("Cost",SortingState.ASCENDING,packages);
        packages = GPManager.getInstance().getFighterPackagesBasedOnTags(chosenManu,chosenSize,chosenType);
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GPManager.getInstance().getFighterBasedOnData(option.getKey(),option.getValue(),packages);
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
