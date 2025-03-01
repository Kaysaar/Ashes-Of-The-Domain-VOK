package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.ui.customprod.components.SortingState;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagManager;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagSpec;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin.*;

public class IndustryTable extends UITableImpl {
    ArrayList<IndustrySpecAPI> specs;
    ArrayList<DropDownButton>copyOfButtons = new ArrayList<>();
    MarketDialog dialog;
    MarketAPI market;
    public ButtonAPI buttonName, buttonType, buttonDays, buttonCosts;
    ButtonAPI lastCheckedState;
    float currYPos = 0;
    public LinkedHashMap<String, IndustryTagSpec> activeTags = new LinkedHashMap<>();
    public IndustrySpecAPI specToBuilt;
    public IndustryTable(float width, float height, CustomPanelAPI panelToPlace, boolean doesHaveScroller, float xCord, float yCord,MarketDialog dialog) {
        super(width, height, panelToPlace, doesHaveScroller, xCord, yCord);
        specs = new ArrayList<>();
        this.dialog = dialog;
        this.market = dialog.market;
        if (dropDownButtons.isEmpty()) {

            ArrayList<IndustrySpecAPI> specs = BuildingMenuMisc.getAllSpecsWithoutDowngrade();
            BuildingMenuMisc.sortIndustrySpecsByName(specs);

            for (IndustrySpecAPI industrySpecAPI : specs) {

                IndustryDropDownButton button = new IndustryDropDownButton(this, width, 40, 0, 0, industrySpecAPI, BuildingMenuMisc.getSpecsOfParent(industrySpecAPI.getData()), market);
                dropDownButtons.add(button);

            }


        }
        copyOfButtons.addAll(dropDownButtons);


    }

    @Override
    public void createSections() {
        buttonName = tooltipOfButtons.addAreaCheckbox("Name", SortingState.ASCENDING, base, bg, bright, 270, 20, 0f);
        buttonType = tooltipOfButtons.addAreaCheckbox("Type", SortingState.NON_INITIALIZED, base, bg, bright, 90, 20, 0f);
        buttonDays = tooltipOfButtons.addAreaCheckbox("Build time", SortingState.NON_INITIALIZED, base, bg, bright, 125, 20, 0f);
        buttonCosts = tooltipOfButtons.addAreaCheckbox("Cost", SortingState.NON_INITIALIZED, base, bg, bright, 116, 20, 0f);
        buttonName.getPosition().inTL(10, 0);
        buttonType.getPosition().inTL(283, 0);
        buttonDays.getPosition().inTL(376, 0);
        buttonCosts.getPosition().inTL(504, 0);
        mainPanel.addUIElement(tooltipOfButtons).inTL(0, 0);
        lastCheckedState = buttonName;
    }
    public void recreateOldListBasedOnPrevSort(){
        dropDownButtons.clear();
        dropDownButtons.addAll(copyOfButtons);
        sortDBList();
        this.recreateTable();

    }

    private void sortDBList() {
        if(lastCheckedState!=null){
            SortingState state = (SortingState) buttonName.getCustomData();
            boolean ascending = false;
            if (state == SortingState.ASCENDING) {
                ascending = true;
            }

            if(lastCheckedState.equals(buttonName)){
                BuildingMenuMisc.sortDropDownButtonsByName(dropDownButtons, ascending);
            }
            if(lastCheckedState.equals(buttonCosts)){
                BuildingMenuMisc.sortDropDownButtonsByCost(dropDownButtons, ascending);
            }
            if(lastCheckedState.equals(buttonDays)){
                BuildingMenuMisc.sortDropDownButtonsByDays(dropDownButtons, ascending);
            }
            if(lastCheckedState.equals(buttonType)){
                BuildingMenuMisc.sortDropDownButtonsByType(dropDownButtons, ascending);
            }
        }
    }

    public ArrayList<DropDownButton> getIndustriesFillingCriteria(){
        if(activeTags.isEmpty()){
            return dropDownButtons;
        }
        ArrayList<IndustryTagSpec> tagsGen = IndustryTagManager.getTagsSpecBasedOnType(IndustryTagType.GENERIC, activeTags);
        ArrayList<IndustryTagSpec> tagsMod = IndustryTagManager.getTagsSpecBasedOnType(IndustryTagType.MOD, activeTags);
        Set<String> btGen = new LinkedHashSet<>();
        Set<String>btMod = new LinkedHashSet<>();
        Set<String>allMods = new LinkedHashSet<>();
        for (IndustryTagSpec tagSpec : tagsGen) {
            allMods.addAll(tagSpec.getSpecIdsForMatchup(market, getListConverted()));
            btGen.addAll(tagSpec.getSpecIdsForMatchup(market, getListConverted()));
        }
        for (IndustryTagSpec tagSpec : tagsMod) {
            allMods.addAll(tagSpec.getSpecIdsForMatchup(market, getListConverted()));
            btMod.addAll(tagSpec.getSpecIdsForMatchup(market, getListConverted()));
        }
        Set<String>together = new LinkedHashSet<>();
        for (String allMod : allMods) {
            boolean fit = true;
            if(!btGen.isEmpty()){
                if(!btGen.contains(allMod)){
                    fit = false;
                }
            }
            if(!btMod.isEmpty()){
                if(!btMod.contains(allMod)){
                    fit = false;
                }
            }
            if(fit){
                together.add(allMod);
            }
        }
        sortDBList();
        ArrayList<DropDownButton>bt = new ArrayList<>();
        for (String s : together) {
            if(!bt.contains(getButtonForSpec(s))){
                bt.add(getButtonForSpec(s));

            }
        }
        return bt;


    }

    public IndustryDropDownButton getButtonForSpec(String id){
        for (IndustryDropDownButton industryDropDownButton : getListConverted()) {
            for (IndustrySpecAPI spec : industryDropDownButton.getSpecs()) {
                if(spec.getId().equals(id)){
                    return industryDropDownButton;
                }
            }
        }
        return null;
    }
    @Override
    public void createTable() {
        super.createTable();
        for (DropDownButton dropDownButton : getIndustriesFillingCriteria()) {
            IndustryDropDownButton button = (IndustryDropDownButton) dropDownButton;
            if (cantPlace(button)) continue;

            button.resetUI();
            button.createUI();
            tooltipOfImpl.addCustom(dropDownButton.getPanelOfImpl(), 2f);
        }

        panelToWorkWith.addUIElement(tooltipOfImpl).inTL(0, 0);
        if (tooltipOfImpl.getExternalScroller() != null) {
            if (currYPos + panelToWorkWith.getPosition().getHeight() - 22 >= tooltipOfImpl.getHeightSoFar()) {
                currYPos = tooltipOfImpl.getHeightSoFar() - panelToWorkWith.getPosition().getHeight() + 22;
            }
            if(currYPos<=0){
                currYPos = 0;
            }
            tooltipOfImpl.getExternalScroller().setYOffset(currYPos);
        }

        mainPanel.addComponent(panelToWorkWith).inTL(0, 22);

    }
    public boolean cantPlaceSubIndustry(IndustryButton button) {
        Industry ind = button.spec.getNewPluginInstance(market);

        return !showIndustry(ind,ind.getSpec());
    }
    public boolean cantPlace(IndustryDropDownButton button) {
        if (button.droppableMode) {
            ArrayList<IndustrySpecAPI>available = new ArrayList<>();
            for (IndustrySpecAPI subSpec : button.subSpecs) {
                if (showIndustry(subSpec.getNewPluginInstance(market),subSpec)) {
                    available.add(subSpec);
                }
            }
            if (available.isEmpty()){
                available.clear();
                return true;
            }
        } else {
            Industry ind = button.mainSpec.getNewPluginInstance(market);
            if (!showIndustry(ind, button.mainSpec)) return true;
        }
        return false;
    }

    private boolean showIndustry(Industry ind, IndustrySpecAPI spec) {
        return shouldShowIndustry(ind) && !BuildingMenuMisc.isIndustryFromTreePresent(spec, market);
    }

    public boolean shouldShowIndustry(Industry ind) {
        if (ind.isAvailableToBuild()) {
            return !market.hasIndustry(ind.getSpec().getId());
        } else {
            return ind.showWhenUnavailable() && !market.hasIndustry(ind.getSpec().getId());
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (tooltipOfImpl != null && tooltipOfImpl.getExternalScroller() != null) {
            currYPos = tooltipOfImpl.getExternalScroller().getYOffset();
        }
        if (buttonName.isChecked()) {
            buttonName.setChecked(false);
            lastCheckedState = buttonName;
            SortingState state = (SortingState) buttonName.getCustomData();
            state = changeState(state);
            boolean ascending = false;
            if (state == SortingState.ASCENDING) {
                ascending = true;
            }
            BuildingMenuMisc.sortDropDownButtonsByName(dropDownButtons, ascending);
            buttonName.setCustomData(state);

            this.recreateTable();

        }
        if (buttonCosts.isChecked()) {
            buttonCosts.setChecked(false);
            lastCheckedState = buttonCosts;
            SortingState state = (SortingState) buttonCosts.getCustomData();
            state = changeState(state);
            boolean ascending = false;
            if (state == SortingState.ASCENDING) {
                ascending = true;
            }
            BuildingMenuMisc.sortDropDownButtonsByCost(dropDownButtons, ascending);
            buttonCosts.setCustomData(state);

            this.recreateTable();
        }
        if (buttonDays.isChecked()) {
            buttonDays.setChecked(false);
            lastCheckedState = buttonDays;
            SortingState state = (SortingState) buttonDays.getCustomData();
            state = changeState(state);
            boolean ascending = false;
            if (state == SortingState.ASCENDING) {
                ascending = true;
            }
            BuildingMenuMisc.sortDropDownButtonsByDays(dropDownButtons, ascending);
            buttonDays.setCustomData(state);
            this.recreateTable();
        }
        if (buttonType.isChecked()) {
            buttonType.setChecked(false);
            lastCheckedState = buttonType;
            SortingState state = (SortingState) buttonType.getCustomData();
            state = changeState(state);
            boolean ascending = false;
            if (state == SortingState.ASCENDING) {
                ascending = true;
            }
            BuildingMenuMisc.sortDropDownButtonsByType(dropDownButtons, ascending);

            buttonType.setCustomData(state);
            this.recreateTable();

        }
    }

    public ArrayList<IndustryDropDownButton> getListConverted() {
        ArrayList<IndustryDropDownButton> buttons = new ArrayList<>();
        for (DropDownButton dropDownButton : dropDownButtons) {
            if(!cantPlace((IndustryDropDownButton) dropDownButton)){
                buttons.add((IndustryDropDownButton) dropDownButton);
            }

        }
        return buttons;
    }
    public ArrayList<IndustryDropDownButton> getListCopyConverted() {
        ArrayList<IndustryDropDownButton> buttons = new ArrayList<>();
        for (DropDownButton dropDownButton : copyOfButtons) {
            if(!cantPlace((IndustryDropDownButton) dropDownButton)){
                buttons.add((IndustryDropDownButton) dropDownButton);
            }

        }
        return buttons;
    }
    public ArrayList<IndustryDropDownButton> getListConvertedAndFiltered() {
        ArrayList<IndustryDropDownButton> buttons = new ArrayList<>();
        for (DropDownButton dropDownButton : getIndustriesFillingCriteria()) {
            if(!cantPlace((IndustryDropDownButton) dropDownButton)){
                buttons.add((IndustryDropDownButton) dropDownButton);
            }

        }
        return buttons;
    }
    private SortingState changeState(SortingState state) {
        if (state == SortingState.NON_INITIALIZED) {
            return SortingState.ASCENDING;
        }
        if (state == SortingState.ASCENDING) {
            return SortingState.DESCENDING;
        }
        if (state == SortingState.DESCENDING) {
            return SortingState.ASCENDING;
        }
        return SortingState.NON_INITIALIZED;
    }

    @Override
    public void reportButtonPressed(CustomButton buttonPressed) {
        IndustryButton bt = (IndustryButton) buttonPressed;
        specToBuilt = bt.spec;
        dialog.showcaseUI.setCurrentSpec(specToBuilt);
        dialog.showcaseUI.recreateIndustryPanel();
    }
}