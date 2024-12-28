package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.ArrayList;
import java.util.List;

public class UITableImpl implements CustomUIPanelPlugin {
    ArrayList<DropDownButton> dropDownButtons;
    TooltipMakerAPI tooltipOfImpl;
    CustomPanelAPI panelToWorkWith;
    CustomPanelAPI mainPanel;
    float width,height,xCord,yCord;
    boolean doesHaveScroller;
    public UITableImpl(float width, float height, CustomPanelAPI panelToPlace, boolean doesHaveScroller, float xCord, float yCord) {
        this.width = width;
        this.height = height;
        this.doesHaveScroller = doesHaveScroller;
        this.xCord = xCord;
        this.yCord = yCord;
        dropDownButtons = new ArrayList<>();
        mainPanel = panelToPlace;
        panelToWorkWith = mainPanel.createCustomPanel(width,height,null);

        tooltipOfImpl = panelToWorkWith.createUIElement(width, height, doesHaveScroller);

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
        for (DropDownButton dropDownButton : dropDownButtons) {
            dropDownButton.advance(amount);
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public void createTable() {


    }

    public void recreateTable() {
        clearTable();
        createTable();
    }

    public void clearTable() {
        panelToWorkWith.removeComponent(tooltipOfImpl);
        mainPanel.removeComponent(panelToWorkWith);
        panelToWorkWith = mainPanel.createCustomPanel(width,height,null);
        tooltipOfImpl = panelToWorkWith.createUIElement(width,height,doesHaveScroller);
    }

    public void reportButtonPressed(ButtonAPI buttonPressed) {

    }
}
