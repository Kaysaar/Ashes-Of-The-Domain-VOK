package data.kaysaar.aotd.vok.ui.customprod.common;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.RowData;
import data.kaysaar.aotd.vok.ui.UIData;


import java.util.*;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.extractManufacturer;

public class ChooseManufacturerPanel implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    LinkedHashMap<String, Integer> manufactures;
    ArrayList<ButtonAPI> buttons = new ArrayList<>();
    boolean allMode = false;
    LinkedHashSet<String> currentlyChosenManufacturers = new LinkedHashSet<>();
    boolean needsUpdate = false;

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    public ChooseManufacturerPanel(float width, float height, LinkedHashMap<String, Integer> manufactures) {
        this.mainPanel = Global.getSettings().createCustom(width, height, this);
        this.manufactures = manufactures;
        allMode = true;
        createUI();

    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        buttons.clear();
        if (componentPanel != null) {
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tooltipButDesigners = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), true);
        float currY = 1;
        for (RowData calculateAmountOfRow : UIData.calculateAmountOfRows(componentPanel.getPosition().getWidth(), manufactures, 5)) {
            float x = 0;
            tooltipButDesigners.setButtonFontDefault();
            for (Map.Entry<String, Integer> entry : calculateAmountOfRow.stringsInRow.entrySet()) {
                String manu = extractManufacturer(entry.getKey());

                ButtonAPI button = tooltipButDesigners.addAreaCheckbox("", manu, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), entry.getValue(), 30, 0f);
                button.getPosition().inTL(x, currY);
                if (isManufacturerChosen((String) button.getCustomData())) {
                    button.highlight();
                } else {
                    button.unhighlight();
                }
                buttons.add(button);
                tooltipButDesigners.addPara(entry.getKey(), Misc.getDesignTypeColor(manu), 0f).getPosition().inTL((x + 15), currY + 8);
                x += entry.getValue() + 5f;
            }
            currY += 35;
        }
        tooltipButDesigners.setHeightSoFar(currY);
        componentPanel.addUIElement(tooltipButDesigners).inTL(0, 0);
        mainPanel.addComponent(componentPanel).inTL(0, 0);

    }

    @Override
    public void clearUI() {

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    public boolean isManufacturerChosen(String manu) {
        return currentlyChosenManufacturers.contains(manu) || allMode;
    }

    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons) {
            String manu = (String) button.getCustomData();
            if (button.isChecked()) {
                button.setChecked(false);
                if(manu.equalsIgnoreCase("all designs")){
                    if(!allMode){
                        needsUpdate = true;
                    }
                    allMode = true;
                    currentlyChosenManufacturers.clear();
                }
                else{
                    if (allMode) {
                        allMode = false;
                    }
                    if(currentlyChosenManufacturers.contains(button.getCustomData())){
                        currentlyChosenManufacturers.remove((String) button.getCustomData());
                        if(currentlyChosenManufacturers.isEmpty()){
                            allMode = true;
                        }
                        needsUpdate  = true;
                    }
                    else{
                        currentlyChosenManufacturers.add((String) button.getCustomData());
                        needsUpdate  = true;
                    }
                }

            }
            if (isManufacturerChosen((String) button.getCustomData())) {
                button.highlight();
            } else {
                button.unhighlight();
            }
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
