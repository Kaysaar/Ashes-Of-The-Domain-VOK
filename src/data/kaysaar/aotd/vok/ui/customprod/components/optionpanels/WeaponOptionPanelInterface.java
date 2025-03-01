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
import static data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin.maxItemsPerPageWEP;

public class WeaponOptionPanelInterface extends BaseOptionPanelManager  {

    public WeaponOptionPanelInterface(CustomPanelAPI panel,float padding ) {

        GPManager.getInstance().getUIData().populateWeaponInfo();
        GPManager.getInstance().getUIData().populateWeaponSizeInfo();
        GPManager.getInstance().getUIData().populateWeaponTypeInfo();
        mapOfButtonStates = new HashMap<>();
        this.padding = padding;
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
    public void init() {
        createWeaponOptions(panel);
        createDesignButtons( GPManager.getInstance().getUIData().getWeaponManInfo());
        createSizeOptions( GPManager.getInstance().getUIData().getWeaponSizeInfo());
        createTypeOptions( GPManager.getInstance().getUIData().getWeaponTypeInfo());
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
        if (searchbar != null) {
            searchbar.deleteAll();
        }
        this.mainPanel.removeComponent(panel);
    }

    @Override
    public void reInit() {
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight()-padding, null);
        init();
    }

    private void createWeaponOptions(CustomPanelAPI panel) {
        if (orderButtons == null) orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedWeapons();
        packages = GpOptionSorter.getWeaponPackagesBasedOnTags(chosenManu, chosenSize, chosenType);
        if (resetToText) {
            packages = GPManager.getInstance().getMatchingWeaponGps(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if (option.getValue() != SortingState.NON_INITIALIZED) {
                packages = GpOptionSorter.getWeaponPackagesBasedOnData(option.getKey(), option.getValue(), packages);
            }
        }
        wantsAll = false;
        resetToText = false;
        currOffset = currPage * maxItemsPerPageWEP;
        float size = packages.size();
        int maxPages = (int) (size / maxItemsPerPageWEP);
        if ((float) maxPages != size / maxItemsPerPageWEP) maxPages++;
        Pair<CustomPanelAPI, ArrayList<ButtonAPI>> orders = OptionPanelDesigner.createWeaponPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
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
        createWeaponOptions(this.panel);
    }
}
