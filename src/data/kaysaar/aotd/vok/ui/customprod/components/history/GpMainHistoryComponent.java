package data.kaysaar.aotd.vok.ui.customprod.components.history;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpHistory;
import data.kaysaar.aotd.vok.ui.customprod.components.SortingState;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.ui.basecomps.ExtendUIPanelPlugin;

import java.util.ArrayList;
import java.util.List;

import static data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin.*;

public class GpMainHistoryComponent implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel, contentPanelHistory, contentPanelYears;
    public ButtonAPI currentlyChosen;
    public ArrayList<ButtonAPI>buttons = new ArrayList<>();
    public GpMainHistoryComponent(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);

        contentPanelHistory = Global.getSettings().createCustom(UIData.WIDTH_OF_ORDERS+3, height, null);
        createUI();
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanelYears==null){
            contentPanelYears = Global.getSettings().createCustom(mainPanel.getPosition().getWidth() - UIData.WIDTH_OF_ORDERS - 10, mainPanel.getPosition().getHeight(), null);
            TooltipMakerAPI tooltipHeader = contentPanelYears.createUIElement(contentPanelYears.getPosition().getWidth(),20,false);
            tooltipHeader.addSectionHeading("Datestamps", Alignment.MID,0f);
            TooltipMakerAPI properTooltip = contentPanelYears.createUIElement(contentPanelYears.getPosition().getWidth(),contentPanelYears.getPosition().getHeight()-25,true);
            float opad = 0f;
            for (GpHistory.GPFullHistoryData datum :   GPManager.getInstance().getProductionHistory().getData()) {
               ButtonAPI button = properTooltip.addButton(getShortMonthString(datum.month)+" "+datum.cycle+"c.",datum, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,contentPanelYears.getPosition().getWidth()-2,40,opad);
               buttons.add(button);
                opad =5f;
            }
            contentPanelYears.addUIElement(tooltipHeader).inTL(0,0);
            contentPanelYears.addUIElement(properTooltip).inTL(-1,20);
            mainPanel.addComponent(contentPanelYears).inTL(0,0);

            tooltipHeader = contentPanelHistory.createUIElement(contentPanelHistory.getPosition().getWidth(),20,false);
            LabelAPI label = tooltipHeader.addSectionHeading("Production Orders", Alignment.MID,0f);
            float y = -label.getPosition().getY() + 5;
            ArrayList<ButtonAPI> butt = new ArrayList<>();
            butt.add(tooltipHeader.addAreaCheckbox("Name", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_ORDER, 20, 0f));
            butt.add(tooltipHeader.addAreaCheckbox("Cost per unit", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_COST, 20, 0f));
            butt.add(tooltipHeader.addAreaCheckbox("Produced this month", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_QT+UIData.WIDTH_OF_AT_ONCE+1, 20, 0f));float x = 0;
            for (ButtonAPI buttonAPI : butt) {
                buttonAPI.getPosition().inTL(x, y);
                x += buttonAPI.getPosition().getWidth() + 1;
            }
            contentPanelHistory.addUIElement(tooltipHeader).inTL(0,0);
            mainPanel.addComponent(contentPanelHistory).inTL(contentPanelYears.getPosition().getWidth()+5,0);
        }


    }
    public void updateUI(){
        mainPanel.removeComponent(contentPanelHistory);
        contentPanelHistory = Global.getSettings().createCustom(UIData.WIDTH_OF_ORDERS+3, mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tooltipHeader = contentPanelHistory.createUIElement(contentPanelHistory.getPosition().getWidth(),35,false);
        LabelAPI label = tooltipHeader.addSectionHeading("Production Orders", Alignment.MID,0f);
        float y = -label.getPosition().getY() + 5;
        ArrayList<ButtonAPI> butt = new ArrayList<>();
        butt.add(tooltipHeader.addAreaCheckbox("Name", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_ORDER, 20, 0f));
        butt.add(tooltipHeader.addAreaCheckbox("Cost per unit", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_NAMES_COST, 20, 0f));
        butt.add(tooltipHeader.addAreaCheckbox("Produced this month", SortingState.NON_INITIALIZED, base, bg, bright, UIData.WIDTH_OF_QT+UIData.WIDTH_OF_AT_ONCE+3, 20, 0f));float x = 0;
        for (ButtonAPI buttonAPI : butt) {
            buttonAPI.getPosition().inTL(x, y);
            x += buttonAPI.getPosition().getWidth() + 1;
        }
        contentPanelHistory.addUIElement(tooltipHeader).inTL(0,0);
        TooltipMakerAPI properTooltip = contentPanelHistory.createUIElement(contentPanelHistory.getPosition().getWidth(),contentPanelHistory.getPosition().getHeight()-50,true);
        GpHistory.GPFullHistoryData history = (GpHistory.GPFullHistoryData) currentlyChosen.getCustomData();
        float opad =0f;
        for (GpHistory.GPOrderData value : history.data.values()) {
            properTooltip.addCustom(UIData.generateHistoryPanelForSpecificOrder(GPManager.getInstance().getSpec(value.getId()), value.amount ),opad);
            opad = 5f;
        }
        contentPanelHistory.addUIElement(properTooltip).inTL(0,45);
        mainPanel.addComponent(contentPanelHistory).inTL(contentPanelYears.getPosition().getWidth()+5,0);
    }
    public String getShortMonthString(int month) {
        switch (month) {
            case 1 -> {
                return "Jan";
            }
            case 2 -> {
                return "Feb";
            }
            case 3 -> {
                return "Mar";
            }
            case 4 -> {
                return "Apr";
            }
            case 5 -> {
                return "May";
            }
            case 6 -> {
                return "Jun";
            }
            case 7 -> {
                return "Jul";
            }
            case 8 -> {
                return "Aug";
            }
            case 9 -> {
                return "Sep";
            }
            case 10 -> {
                return "Oct";
            }
            case 11 -> {
                return "Nov";
            }
            case 12 -> {
                return "Dec";
            }
            default -> {
                return "Unk: [" + month + "]";
            }
        }
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

    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons) {
            button.unhighlight();
            if(button.isChecked()){
                button.setChecked(false);
                currentlyChosen = button;
                updateUI();
            }
        }
        if(currentlyChosen!=null){
            currentlyChosen.highlight();
        }


    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
