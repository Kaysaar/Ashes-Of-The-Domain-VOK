package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.table;

import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.DropDownButton;
import ashlib.data.plugins.ui.plugins.UITableImpl;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityTableDropDownButton;
import data.kaysaar.aotd.tot.ui.commoditypanel.CommodityButtonOnHover;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static ashlib.data.plugins.misc.AshMisc.sortByState;

public class BifrostGateUITableList extends UITableImpl {
    public static LinkedHashMap<String, Integer> widthMap = new LinkedHashMap<>();
    static {
        widthMap.put("name", 246);
        widthMap.put("status", 250);
        widthMap.put("demand", 200);
        widthMap.put("bonus", 100);
    }


    public static int getStartingX(String id) {
        int x = 0;
        for (Map.Entry<String, Integer> value : widthMap.entrySet()) {

            if (id.equals(value.getKey())) {
                break;
            }
            x += value.getValue() + 1;
        }
        return x;
    }
    public static float getWidth(){
        float x = 0;
        for (Map.Entry<String, Integer> value : widthMap.entrySet()) {
            x += value.getValue() + 1;

        }
        return x;
    }
    ButtonAPI lastCheckedState;
    private void handleSortButton(ButtonAPI button, Comparator<AoTDCommodityTableDropDownButton> comparator) {
        if (button == null || comparator == null) return;
        if (!button.isChecked()) return;

        button.setChecked(false);

        SortingState current = (SortingState) button.getCustomData();
        SortingState newState = this.switchState(current);

        sortByState(dropDownButtons, newState, comparator);

        button.setCustomData(newState);
        recreateTable();
    }

    float currYPos = 0;
    public ButtonAPI buttonStarSystem, buttonStatus, buttonDemand, buttonBonus;
    public BifrostGateUITableList(float width, float height, boolean doesHaveScroller, float xCord, float yCord) {
        super(width, height, doesHaveScroller, xCord, yCord);
        if(dropDownButtons.isEmpty()){
            for (BifrostSection commodity : BifrostMegastructureManager.getInstance().getMegastructure().getSections()) {
                BifrostTableDropDownButton bt = new BifrostTableDropDownButton(this,width-1,40,0,0,commodity);
                dropDownButtons.add(bt);
            }
        }
    }
    @Override
    public void createSections() {
        Color base = Misc.getBasePlayerColor();
        Color bg = Misc.getDarkPlayerColor();
        Color bright = Misc.getBrightPlayerColor();
        buttonStarSystem = tooltipOfButtons.addAreaCheckbox("Star System", SortingState.NON_INITIALIZED, base, bg, bright, widthMap.get("name"), 20, 0f);
        buttonStatus = tooltipOfButtons.addAreaCheckbox("Status", SortingState.NON_INITIALIZED, base, bg, bright, widthMap.get("status"), 20, 0f);
        buttonDemand = tooltipOfButtons.addAreaCheckbox("Demand", SortingState.NON_INITIALIZED, base, bg, bright, widthMap.get("demand"), 20, 0f);
        buttonBonus = tooltipOfButtons.addAreaCheckbox("Acc. Bonus", SortingState.NON_INITIALIZED, base, bg, bright, widthMap.get("bonus"), 20, 0f);
        buttonStarSystem.getPosition().inTL(1, 0);
        buttonStatus.getPosition().inTL(buttonStarSystem.getPosition().getWidth() + 1, 0);
        float x = buttonStarSystem.getPosition().getWidth() + 2 + buttonStatus.getPosition().getWidth() + 1;
        buttonDemand.getPosition().inTL(x, 0);
        x += buttonDemand.getPosition().getWidth() + 1;
        buttonBonus.getPosition().inTL(x, 0);
        x+= buttonBonus.getPosition().getWidth() + 1;
        mainPanel.addUIElement(tooltipOfButtons).inTL(0, 0);
        lastCheckedState = buttonStarSystem;
    }

    @Override
    public void createTable() {
        super.createTable();
        tooltipOfImpl.addSpacer(0f).getPosition().inTL(-4,0);
        for (DropDownButton dropDownButton : dropDownButtons) {
            dropDownButton.resetUI();
            dropDownButton.createUI();
            tooltipOfImpl.addCustom(dropDownButton.getPanelOfImpl(),2f);
        }
        panelToWorkWith.addUIElement(tooltipOfImpl).inTL(0, 0);
        if (tooltipOfImpl.getExternalScroller() != null) {
            if (currYPos + panelToWorkWith.getPosition().getHeight() - 2 >= tooltipOfImpl.getHeightSoFar()) {
                currYPos = tooltipOfImpl.getHeightSoFar() - panelToWorkWith.getPosition().getHeight() + 2;
            }
            if (currYPos <= 0) {
                currYPos = 0;
            }
            tooltipOfImpl.getExternalScroller().setYOffset(currYPos);
        }

        mainPanel.addComponent(panelToWorkWith).inTL(0, 22);
    }

    @Override
    public void reportButtonPressed(CustomButton buttonPressed) {
        super.reportButtonPressed(buttonPressed);
    }
}
