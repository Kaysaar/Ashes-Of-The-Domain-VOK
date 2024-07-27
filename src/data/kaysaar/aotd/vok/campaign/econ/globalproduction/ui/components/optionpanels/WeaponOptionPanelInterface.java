package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.campaign.FactionProductionAPI;
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
import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin.maxItemsPerPageWEP;

public class WeaponOptionPanelInterface extends BaseOptionPanelManager implements OptionPanelInterface {

    public WeaponOptionPanelInterface(CustomPanelAPI panel){
        GPManager.getInstance().populateWeaponInfo();
        GPManager.getInstance().populateWeaponSizeInfo();
        GPManager.getInstance().populateWeaponTypeInfo();
        mapOfButtonStates = new HashMap<>();
        this.mainPanel = panel;
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        YHeight = panel.getPosition().getHeight() * 0.45f;
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
        createDesignButtons(GPManager.getInstance().getWeaponManInfo());
        createSizeOptions(GPManager.getInstance().getWeaponSizeInfo());
        createTypeOptions(GPManager.getInstance().getWeaponTypeInfo());
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
    private void createWeaponOptions(CustomPanelAPI panel) {
        if(orderButtons==null)orderButtons = new ArrayList<>();
        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedWeapons();
        if (!chosenManu.isEmpty() && !wantsAll && !resetToText&&chosenType.isEmpty()&&chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getWeaponsByManu(chosenManu);
        }
        if (chosenManu.isEmpty() && !wantsAll && !resetToText&&!chosenType.isEmpty()&&chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getWeaponBasedOnType(chosenType.get(0));
        }
        if (chosenManu.isEmpty() && !wantsAll && !resetToText&&chosenType.isEmpty()&&!chosenSize.isEmpty()) {
            packages = GPManager.getInstance().getWeaponBasedOnSize(chosenSize.get(0));
        }


            if (resetToText) {
            packages = GPManager.getInstance().getMatchingWeaponGps(searchbar.getText());
        }
        for (Map.Entry<String, SortingState> option : mapOfButtonStates.entrySet()) {
            if(option.getValue()!=SortingState.NON_INITIALIZED){
                packages= GPManager.getInstance().getWeaponPackagesBasedOnData(option.getKey(),option.getValue(),packages);
            }
        }
        wantsAll = false;
        resetToText = false;
        currOffset = currPage * maxItemsPerPageWEP;
        float size = packages.size();
        int maxPages = (int) (size / maxItemsPerPageWEP);
        if ((float) maxPages != size / maxItemsPerPageWEP) maxPages++;
        Pair<CustomPanelAPI,ArrayList<ButtonAPI>> orders=OptionPanelDesigner.createWeaponPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
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
