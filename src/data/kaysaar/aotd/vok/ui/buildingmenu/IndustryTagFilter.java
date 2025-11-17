package data.kaysaar.aotd.vok.ui.buildingmenu;

import ashlib.data.plugins.ui.models.PopUpUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.buildingmenu.popup.TagFilterPopUp;

import java.util.List;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.placePopUpUI;

public class IndustryTagFilter implements CustomUIPanelPlugin {
    MarketDialog marketDialog;
    CustomPanelAPI mainPanel;
    ButtonAPI buttonToShowcaseFilters;
    ButtonAPI buttonReset;

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public IndustryTagFilter(float width, float height,MarketDialog marketDialog) {
        this.marketDialog = marketDialog;
        mainPanel = Global.getSettings().createCustom(width,height,this);
        float buttonWidth = ((width-5f) / 2f);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width,height,false);
        buttonReset =tooltip.addButton("Reset filters",null, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(), Alignment.MID,CutStyle.NONE,buttonWidth,height,0f);
        buttonToShowcaseFilters =tooltip.addButton("Change filters",null, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(), Alignment.MID,CutStyle.NONE,buttonWidth,height,0f);
        buttonToShowcaseFilters.getPosition().inTL(buttonWidth+10f,0);
        mainPanel.addUIElement(tooltip).inTL(0,0);

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
    if(buttonToShowcaseFilters !=null){
        if(buttonToShowcaseFilters.isChecked()){
            buttonToShowcaseFilters.setChecked(false);
            PopUpUI ui = new TagFilterPopUp(marketDialog);
            marketDialog.dissableExit = true;
            placePopUpUI(ui, buttonToShowcaseFilters,390,300);
        }
    }
        if(buttonReset !=null){
            if(buttonReset.isChecked()){
                buttonReset.setChecked(false);
                marketDialog.table.activeTags.clear();
                marketDialog.table.recreateTable();
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public void applyFilters(){

    }
}
