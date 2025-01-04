package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.ArrayList;
import java.util.List;

public class IndustrySearchPanel implements CustomUIPanelPlugin {
    IndustryTable tableInstance;
    TextFieldAPI textField;
    CustomPanelAPI mainPanel;
    boolean haveAlreadyReseted = true;
    String prevString = "";
    public IndustrySearchPanel (float width,float height,IndustryTable instance){
        this.tableInstance = instance;
        mainPanel = Global.getSettings().createCustom(width,height,this);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width,height,false);
        textField = tooltip.addTextField(width,height,Fonts.DEFAULT_SMALL,0f);
        mainPanel.addUIElement(tooltip).inTL(0,0);

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
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
        if(textField!=null){
            if(!AoTDMisc.isStringValid(textField.getText())&&!haveAlreadyReseted){
                haveAlreadyReseted = true;
                tableInstance.recreateOldListBasedOnPrevSort();
            }
            if(AoTDMisc.isStringValid(textField.getText())){
                if(!prevString.equals(textField.getText())){
                    haveAlreadyReseted = false;
                    prevString = textField.getText();
                    tableInstance.dropDownButtons.clear();
                    tableInstance.dropDownButtons.addAll(BuildingMenuMisc.searchIndustryByName(tableInstance.copyOfButtons,textField.getText(),2));
                    tableInstance.recreateTable();
                }


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
