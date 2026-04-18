package data.kaysaar.aotd.vok.ui.customprod.common;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;


import java.util.*;

public class ChooseSizePanel implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    LinkedHashMap<String, Integer> sizes;
    ArrayList<ButtonAPI> buttons = new ArrayList<>();
    boolean allMode = false;
    LinkedHashSet<String> currChosenSizes = new LinkedHashSet<>();
    boolean needsUpdate = false;

    public ChooseSizePanel(float width, LinkedHashMap<String, Integer> sizes) {
        this.mainPanel = Global.getSettings().createCustom(width, 30, this);
        allMode = true;
        this.sizes = sizes;
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
        TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), false);
        float padding = 5f;
        float currentX = 0;
        float widthOfButton = 150;

        for (Map.Entry<String, Integer> category : sizes.entrySet()) {
            ButtonAPI button = tooltip.addAreaCheckbox(category.getKey() + "(" + category.getValue() + ")", category.getKey(), Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), widthOfButton, 30, 0f);
            button.getPosition().inTL(currentX, 0);
            currentX += widthOfButton + padding;
            buttons.add(button);
        }
        componentPanel.addUIElement(tooltip).inTL(0, 0);
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

    public boolean isSizeChosen(String manu) {
        return currChosenSizes.contains(manu) || allMode;
    }
    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }
    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons) {
            String manu = (String) button.getCustomData();
            if (button.isChecked()) {
                button.setChecked(false);
                if(manu.equalsIgnoreCase("all sizes")){
                    if(!allMode){
                        needsUpdate  = true;
                    }
                    allMode = true;
                    currChosenSizes.clear();
                }
                else{
                    if (allMode) {
                        allMode = false;
                    }
                    if(currChosenSizes.contains(button.getCustomData())){
                        currChosenSizes.remove((String) button.getCustomData());
                        if(currChosenSizes.isEmpty()){
                            allMode = true;
                        }
                        needsUpdate  = true;
                    }
                    else{
                        currChosenSizes.add((String) button.getCustomData());
                        needsUpdate  = true;
                    }
                }

            }
            if (isSizeChosen((String) button.getCustomData())) {
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
