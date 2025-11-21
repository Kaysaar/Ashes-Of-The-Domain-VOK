package data.kaysaar.aotd.vok.ui.buildingmenu.popup;

import ashlib.data.plugins.ui.models.PopUpUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.kaysaar.aotd.vok.ui.buildingmenu.IndustryTagFilter;
import data.kaysaar.aotd.vok.ui.buildingmenu.MarketDialog;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagManager;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagSpec;
import data.kaysaar.aotd.vok.ui.buildingmenu.industrytags.IndustryTagType;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TagFilterPopUp extends PopUpUI {
    MarketDialog marketDialog;
    CustomPanelAPI mainPanel;
    LinkedHashMap<String,IndustryTagSpec>tagFilterLinkedHashMap = new LinkedHashMap<>();
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    public TagFilterPopUp(MarketDialog dialog) {
        this.marketDialog = dialog;
        if(marketDialog.table.activeTags!=null){
            this.tagFilterLinkedHashMap.putAll(marketDialog.table.activeTags);
        }


    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }

    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(panelAPI.getPosition().getWidth() + 5, panelAPI.getPosition().getHeight(), true);
        tooltip.addSectionHeading("Generic Filters", Alignment.MID, 0f);
        for (IndustryTagSpec spec : IndustryTagManager.getTagsSpecBasedOnType(IndustryTagType.GENERIC)) {
            ArrayList<String>sp = spec.getSpecIdsForMatchup(marketDialog.market, marketDialog.table.getListCopyConverted());
            if (sp.isEmpty()) continue;
            createLabel(panelAPI, spec, sp, tooltip);

        }
        tooltip.addSectionHeading("Mods", Alignment.MID, 5f);
        for (IndustryTagSpec spec : IndustryTagManager.getTagsSpecBasedOnType(IndustryTagType.MOD)) {
            ArrayList<String>sp = spec.getSpecIdsForMatchup(marketDialog.market, marketDialog.table.getListCopyConverted());
            if (sp.isEmpty()) continue;
            createLabel(panelAPI, spec, sp, tooltip);

        }
        tooltip.addSectionHeading("Other", Alignment.MID, 5f);
        createLabel(panelAPI,"Showcase from which mod industries are in their tooltips",tooltip);
        tooltip.addSpacer(5f);
        mainPanel.getPosition().setSize(panelAPI.getPosition().getWidth(), tooltip.getHeightSoFar());
        mainPanel.addUIElement(tooltip).inTL(0, 0);
        float height =   tooltip.getHeightSoFar()+4;
        if(height>320){
            height = 320;
        }
        return height;


    }

    private  void createLabel(CustomPanelAPI panelAPI, IndustryTagSpec spec, ArrayList<String> sp, TooltipMakerAPI tooltip) {
        CustomPanelAPI panelToCreate = Global.getSettings().createCustom(panelAPI.getPosition().getWidth(), 20, null);
        TooltipMakerAPI tooltipButton = panelToCreate.createUIElement(panelAPI.getPosition().getWidth(), 20, false);
        ButtonAPI button  = tooltipButton.addCheckbox(20, 20, spec.tagName+" ("+ sp.size()+")", spec, ButtonAPI.UICheckboxSize.SMALL, 0f);
        if(tagFilterLinkedHashMap.containsKey(spec.tag)){
            button.setChecked(true);
        }
        buttons.add(button);

        panelToCreate.addUIElement(tooltipButton).inTL(0, 0);
        tooltip.addCustom(panelToCreate, 5f);
    }
    private  void createLabel(CustomPanelAPI panelAPI,String title, TooltipMakerAPI tooltip) {
        CustomPanelAPI panelToCreate = Global.getSettings().createCustom(panelAPI.getPosition().getWidth(), 20, null);
        TooltipMakerAPI tooltipButton = panelToCreate.createUIElement(panelAPI.getPosition().getWidth(), 20, false);
        ButtonAPI button = tooltipButton.addCheckbox(20, 20, title, null, ButtonAPI.UICheckboxSize.SMALL, 0f);
        panelToCreate.addUIElement(tooltipButton).inTL(0, 0);
        tooltip.addCustom(panelToCreate, 5f);
    }

    @Override
    public void onExit() {
        tagFilterLinkedHashMap.clear();
        for (ButtonAPI button : buttons) {
            if(button.isChecked()){
                button.setChecked(false);
                IndustryTagSpec tagSpec = (IndustryTagSpec) button.getCustomData();
                tagFilterLinkedHashMap.put(tagSpec.tag,tagSpec);
            }
        }
        buttons.clear();
        marketDialog.table.activeTags.clear();
        marketDialog.table.activeTags.putAll(tagFilterLinkedHashMap);
        tagFilterLinkedHashMap.clear();

        marketDialog.util = new IntervalUtil(0.2f, 0.2f);

        marketDialog.table.recreateTable();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }
}
