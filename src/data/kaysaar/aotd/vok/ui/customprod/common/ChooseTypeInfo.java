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

public class ChooseTypeInfo implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    LinkedHashMap<String, Integer> typeInfo;
    ArrayList<ButtonAPI> buttons = new ArrayList<>();
    boolean allMode = false;
    LinkedHashSet<String> currChosenSizes = new LinkedHashSet<>();
    boolean needsUpdate = false;
    public ChooseTypeInfo(float width, LinkedHashMap<String, Integer> typeInfo) {
        this.mainPanel = Global.getSettings().createCustom(width, 30, this);
        allMode = true;
        this.typeInfo = typeInfo;
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

        componentPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                mainPanel.getPosition().getHeight(),
                null
        );

        TooltipMakerAPI tooltip = componentPanel.createUIElement(
                componentPanel.getPosition().getWidth(),
                componentPanel.getPosition().getHeight()+8,
                true
        );

        float paddingX = 5f;
        float paddingY = 5f;
        float currentX = 0f;
        float currentY = 0f;
        float rowHeight = 30f;
        float widthOfButton = 150f;
        float availableWidth = componentPanel.getPosition().getWidth();

        float heightSoFar = rowHeight;

        for (Map.Entry<String, Integer> category : typeInfo.entrySet()) {
            String text = category.getKey() + "(" + category.getValue() + ")";

            // wrap to next row if this button would not fit
            if (currentX > 0 && currentX + widthOfButton > availableWidth) {
                currentX = 0f;
                currentY += rowHeight + paddingY;
                heightSoFar = currentY + rowHeight+5;
            }

            ButtonAPI button = tooltip.addAreaCheckbox(
                    text,
                    category.getKey(),
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    widthOfButton,
                    rowHeight,
                    0f
            );

            button.getPosition().inTL(currentX, currentY);
            buttons.add(button);

            currentX += widthOfButton + paddingX;
        }
        tooltip.setHeightSoFar(heightSoFar);
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

    public boolean isTypeChosen(String manu) {
        if(manu==null)return false;
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

                if(manu.equalsIgnoreCase("all types")){
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
            if (isTypeChosen((String) button.getCustomData())) {
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
