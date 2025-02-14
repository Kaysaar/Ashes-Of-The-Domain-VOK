package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.RowData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseOptionPanelManager implements OptionPanelInterface {
    CustomPanelAPI mainPanel;
    CustomPanelAPI panel;
    float padding;

    Color base = Global.getSector().getPlayerFaction().getBaseUIColor();
    Color bg = Global.getSector().getPlayerFaction().getDarkUIColor();
    Color bright = Global.getSector().getPlayerFaction().getBrightUIColor();
    ArrayList<ButtonAPI> buttonsPage = new ArrayList<>();
    CustomPanelAPI buttonPanel;
    CustomPanelAPI buttonSize;
    CustomPanelAPI buttonType;
    CustomPanelAPI buttonDesignPanel;
    CustomPanelAPI optionPanel;
    CustomPanelAPI buttonSortingPnael;
    CustomPanelAPI panelOfSearchBar;
    TextFieldAPI searchbar;
    ArrayList<ButtonAPI> orderButtons = new ArrayList<>();
    ArrayList<ButtonAPI> types = new ArrayList<>();
    ArrayList<ButtonAPI> sizes = new ArrayList<>();
    public HashMap<String, SortingState> mapOfButtonStates;
    boolean resetToText = false;
    String prevText = "";
    float YHeight;
    int currOffset = 0;
    int currPage = 0;
    float bottomHeight = UIData.HEIGHT - (UIData.HEIGHT * 0.45f) - 270;
    ArrayList<ButtonAPI> buttons = new ArrayList<>();
    ArrayList<String> chosenManu = new ArrayList<>();
    ArrayList<String> chosenType = new ArrayList<>();
    ArrayList<String> chosenSize = new ArrayList<>();
    ArrayList<ButtonAPI> sortingButtons = new ArrayList<>();
    boolean wantsAll = false;
    boolean canClose = true;
    public void createDesignButtons( LinkedHashMap<String,Integer> designs) {
        buttonDesignPanel = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 120, null);
        TooltipMakerAPI tooltipButDesigners = buttonDesignPanel.createUIElement(panel.getPosition().getWidth() * 0.70f, bottomHeight, true);
        float currY = 1;
        for (RowData calculateAmountOfRow : UIData.calculateAmountOfRows(UIData.WIDTH_OF_OPTIONS,designs, 5)) {
            float x = 0;
            tooltipButDesigners.setButtonFontDefault();
            for (Map.Entry<String, Integer> entry : calculateAmountOfRow.stringsInRow.entrySet()) {
                String manu = extractManufacturer(entry.getKey());
                ButtonAPI button = tooltipButDesigners.addAreaCheckbox("", manu, base, bg, bright, entry.getValue(), 30, 0f);
                button.getPosition().inTL(x, currY);
                buttons.add(button);
                tooltipButDesigners.addPara(entry.getKey(), Misc.getDesignTypeColor(manu), 0f).getPosition().inTL((x + 15), currY + 8);
                x += entry.getValue() + 5f;
            }
            currY += 35;
        }
        tooltipButDesigners.setHeightSoFar(currY);
        buttonDesignPanel.addUIElement(tooltipButDesigners).inTL(0, 0);
        panel.addComponent(buttonDesignPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, YHeight + 130);

    }
    public static String extractManufacturer(String input) {
        String[] parts = input.split("\\(");
        StringBuilder result = new StringBuilder(parts[0].trim());

        // List to store extracted sections
        ArrayList<String> extractedParts = new ArrayList<>();

        // Process all sections inside parentheses
        for (int i = 1; i < parts.length; i++) {
            String section = parts[i].replace(")", "").trim();
            extractedParts.add(section);
        }

        // Check the last section; remove it if it's purely numeric
        if (!extractedParts.isEmpty() && extractedParts.get(extractedParts.size() - 1).matches("\\d+")) {
            extractedParts.remove(extractedParts.size() - 1);
        }

        // Rebuild the string with valid sections
        for (String part : extractedParts) {
            result.append(" (").append(part).append(")");
        }

        return result.toString();
    }

    public void createSizeOptions(LinkedHashMap<String, Integer> sizeInfo) {
        buttonSize = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 30, null);
        TooltipMakerAPI tooltip = buttonSize.createUIElement(UIData.WIDTH_OF_OPTIONS, 30, false);
        float padding = 5f;
        float currentX = 0;
        float widthOfButton = 150;

        for (Map.Entry<String, Integer> category : sizeInfo.entrySet()) {
            ButtonAPI button = tooltip.addAreaCheckbox(category.getKey() + "(" + category.getValue() + ")", category.getKey(), base, bg, bright, widthOfButton, 30, 0f);
            button.getPosition().inTL(currentX, 0);
            currentX += widthOfButton + padding;
            sizes.add(button);
        }
        buttonSize.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(buttonSize).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, panel.getPosition().getHeight() - 50);

    }
    public void createTypeOptions(LinkedHashMap<String, Integer> typeInfo) {
        buttonType = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 80, null);
        TooltipMakerAPI tooltip = buttonType.createUIElement(UIData.WIDTH_OF_OPTIONS, 30, false);
        float padding = 5f;
        float currentX = 0;
        float widthOfButton = 150;
        for (Map.Entry<String, Integer> category : typeInfo.entrySet()) {
            ButtonAPI button = tooltip.addAreaCheckbox(category.getKey() + "(" + category.getValue() + ")", category.getKey(), base, bg, bright, widthOfButton, 30, 0f);
            button.getPosition().inTL(currentX, 0);
            currentX += widthOfButton + padding;
            types.add(button);
        }
        buttonType.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(buttonType).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, panel.getPosition().getHeight() - 85);
    }
    public void createSerachBarPanel() {
        panelOfSearchBar = panel.createCustomPanel(230, 20, null);
        TooltipMakerAPI tooltip = panelOfSearchBar.createUIElement(230, 20, false);
        searchbar = tooltip.addTextField(230, 20, Fonts.DEFAULT_SMALL, 0f);
        panelOfSearchBar.addUIElement(tooltip).inTL(0, 0);
        panel.addComponent(panelOfSearchBar).inTL(buttonSortingPnael.getPosition().getX() + buttonSortingPnael.getPosition().getWidth() - 245, 29);
    }
    public void createSortingButtons(boolean forFighter, boolean isWeapon) {
        buttonSortingPnael = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 20, null);
        mapOfButtonStates.put("Name", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Build time", SortingState.NON_INITIALIZED);
        if(!forFighter){
            mapOfButtonStates.put("Size", SortingState.NON_INITIALIZED);
        }

        mapOfButtonStates.put("Type", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Design Type", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Cost", SortingState.NON_INITIALIZED);
        mapOfButtonStates.put("Gp cost", SortingState.NON_INITIALIZED);
        String name = "Ship blueprint name";
        if(isWeapon){
            name = "Weapon blueprint name";
        }
        TooltipMakerAPI tooltip = buttonSortingPnael.createUIElement(UIData.WIDTH_OF_OPTIONS, 20, false);
        ArrayList<ButtonAPI> sortButtons = new ArrayList<>();
        sortButtons.add(tooltip.addAreaCheckbox(name, "Name", base, bg, bright, UIData.WIDTH_OF_NAME, 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Build time", "Build time", base, bg, bright, UIData.WIDTH_OF_BUILD_TIME, 20, 0f));
        if(!forFighter){
            sortButtons.add(tooltip.addAreaCheckbox("Size", "Size", base, bg, bright, UIData.WIDTH_OF_SIZE, 20, 0f));
            sortButtons.add(tooltip.addAreaCheckbox("Type", "Type", base, bg, bright, UIData.WIDTH_OF_TYPE, 20, 0f));
        }
        else{
            sortButtons.add(tooltip.addAreaCheckbox("Type", "Type", base, bg, bright, UIData.WIDTH_OF_TYPE+ UIData.WIDTH_OF_SIZE, 20, 0f));

        }

        sortButtons.add(tooltip.addAreaCheckbox("Design type", "Design Type", base, bg, bright, UIData.WIDTH_OF_DESIGN_TYPE, 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Cost (credits)", "Cost", base, bg, bright, UIData.WIDTH_OF_CREDIT_COST, 20, 0f));
        sortButtons.add(tooltip.addAreaCheckbox("Gp cost", "Gp cost", base, bg, bright, UIData.WIDTH_OF_GP, 20, 0f));
        float currentX = 0;
        for (ButtonAPI sortButton : sortButtons) {
            sortButton.getPosition().inTL(currentX, 0);
            currentX += sortButton.getPosition().getWidth();
        }
        buttonSortingPnael.addUIElement(tooltip).inTL(-5, 0);

        sortingButtons.addAll(sortButtons);
        panel.addComponent(buttonSortingPnael).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, 50);
    }
    public void pageInitalization(CustomPanelAPI panel, int maxPages, Pair<CustomPanelAPI, ArrayList<ButtonAPI>> orders) {
        optionPanel = orders.one;
        orderButtons.addAll(orders.two);
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
    public void reset(){

    }

    @Override
    public TextFieldAPI getTextField() {
        return searchbar;
    }

    @Override
    public boolean canClose() {
        return canClose;
    }

    @Override
    public CustomPanelAPI getOptionPanel() {
        return null;
    }

    @Override
    public CustomPanelAPI getDesignPanel() {
        return null;
    }

    @Override
    public ArrayList<ButtonAPI> getOrderButtons() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void reInit() {

    }

    public void advance(float amount) {
        boolean reset = false;

        if (searchbar != null) {
            canClose = !searchbar.hasFocus();
            if (!searchbar.getText().equals(prevText)) {
                if (searchbar.getText().isEmpty()) {
                    wantsAll = true;
                    currPage = 0;
                } else {
                    resetToText = true;
                }
                prevText = searchbar.getText();
                reset();
            }
        }
        for (ButtonAPI button : buttonsPage) {
            if (button.isChecked()) {

                button.setChecked(false);
                currPage = (int) button.getCustomData();
                reset = true;


                break;
            }
        }
        for (ButtonAPI button : buttons) {
            handleButtonHighlight(chosenManu, button);

            if (button.isChecked()) {
                if (button.getCustomData() instanceof String) {
                    button.setChecked(false);
                    if (button.getCustomData().equals("All designs")) {
                        wantsAll = true;
                        currPage = 0;
                        chosenManu.clear();
                        reset = true;
                        break;
                    }
                    handleDataList(chosenManu,button);
                    currPage = 0;
                    reset = true;
                }
                break;
            }
        }
        for (ButtonAPI button : sortingButtons) {
            if (button.isChecked()) {
                if (button.getCustomData() instanceof String) {
                    button.setChecked(false);
                    SortingState state = mapOfButtonStates.get(button.getCustomData());
                    if(state == SortingState.NON_INITIALIZED){
                        state = SortingState.ASCENDING;
                    }
                    else if(state == SortingState.ASCENDING){
                        state = SortingState.DESCENDING;
                    }
                    else if(state == SortingState.DESCENDING){
                        state = SortingState.NON_INITIALIZED;
                    }
                    for (String s : mapOfButtonStates.keySet()) {
                        mapOfButtonStates.put(s,SortingState.NON_INITIALIZED);
                    }
                    mapOfButtonStates.put((String) button.getCustomData(),state);
                    currPage = 0;
                    reset = true;
                }
                break;
            }
        }
        for (ButtonAPI button : sizes) {
            handleButtonHighlight(chosenSize, button);
            if (button.isChecked()) {
                if (button.getCustomData() instanceof String) {
                    button.setChecked(false);
                    if (button.getCustomData().equals("All sizes")) {
                        wantsAll = true;
                        currPage = 0;
                        chosenSize.clear();
                        reset = true;
                        break;
                    }
                    handleDataList(chosenSize,button);
                    currPage = 0;
                    reset = true;
                }
                break;
            }
        }
        for (ButtonAPI button : types) {
            handleButtonHighlight(chosenType, button);
            if (button.isChecked()) {
                if (button.getCustomData() instanceof String) {
                    button.setChecked(false);
                    if (button.getCustomData().equals("All types")) {
                        wantsAll = true;
                        currPage = 0;
                        chosenType.clear();
                        reset = true;
                        break;
                    }
                    handleDataList(chosenType,button);
                    currPage = 0;
                    reset = true;
                }
                break;
            }
        }

        if (reset) {
            reset();
        }
    }

    private void handleButtonHighlight(ArrayList<String> chosenManu, ButtonAPI button) {
        if (!chosenManu.isEmpty()) {
            if (AoTDMisc.arrayContains(chosenManu, (String) button.getCustomData())) {
                button.highlight();
            } else {
                button.unhighlight();
            }
        } else {
            button.highlight();
        }
    }

    private void handleDataList(ArrayList<String>array,ButtonAPI button) {
        if(AoTDMisc.arrayContains(array, (String) button.getCustomData())){
            array.remove((String) button.getCustomData());
        }
        else{
            array.add((String) button.getCustomData());
        }
    }

    private void clearButtons() {
        chosenManu.clear();
        chosenSize.clear();
        chosenType.clear();
    }

}
