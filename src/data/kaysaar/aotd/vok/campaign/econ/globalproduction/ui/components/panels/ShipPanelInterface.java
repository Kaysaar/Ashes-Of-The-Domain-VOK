package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.RowData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.OptionPanelDesigner;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin.maxItemsPerPage;

public class ShipPanelInterface extends BasePanelManager implements BasePanelInterface {


    public ShipPanelInterface(CustomPanelAPI panel) {
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
        searchbar.deleteAll();
        this.mainPanel.removeComponent(panel);
    }

    @Override
    public void reInit() {
        this.panel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        init();
    }




    private void createShipOptions(CustomPanelAPI panel) {

        ArrayList<GPOption> packages = GPManager.getInstance().getLearnedShipPackages();
        if (!chosenManu.isEmpty() && !wantsAll && !resetToText) {
            packages = GPManager.getInstance().getShipPackagesByManu(chosenManu);
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

        optionPanel = OptionPanelDesigner.createShipPanel(UIData.WIDTH_OF_OPTIONS, YHeight, this.panel, packages, currOffset, maxItemsPerPage);
        buttonPanel = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 40, null);
        TooltipMakerAPI tooltipBut = buttonPanel.createUIElement(panel.getPosition().getWidth() * 0.70f, bottomHeight, false);
        if (maxPages > 1) {
            ArrayList<ButtonAPI> buttons = new ArrayList<>();
            float buttonSeperator = 5f;
            float buttonSize = 30f;
            for (int i = 0; i < maxPages; i++) {
                ButtonAPI buttonAPI = tooltipBut.addButton("" + (i + 1), i, base, bg, Alignment.MID, CutStyle.NONE, buttonSize, buttonSize, 0f);
                buttons.add(buttonAPI);
            }
            float width = panel.getPosition().getWidth() * 0.70f;
            float buttomCombinedWidth = buttons.size() * buttonSize + (buttons.size() - 1) * buttonSeperator;
            float beginX = (width - buttomCombinedWidth) / 2;
            for (ButtonAPI button : buttons) {
                button.getPosition().inTL(beginX, 0);
                beginX += buttonSize + buttonSeperator;
            }
            this.buttonsPage.addAll(buttons);

        }
        buttonPanel.addUIElement(tooltipBut).inTL(-5, 0);
        panel.addComponent(optionPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, 70);
        panel.addComponent(buttonPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, YHeight + 85);
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
