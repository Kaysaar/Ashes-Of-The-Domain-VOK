package data.kaysaar.aotd.vok.ui.newcomps;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.AoTDResearchNewPlugin;
import data.kaysaar.aotd.vok.ui.AoTDResearchUI;

public class ModDropDown extends PopUpUI {
    AoTDResearchNewPlugin researchUI;
    String currId;
    CustomPanelAPI mainPanel;
    public ModDropDown(String currModId, AoTDResearchNewPlugin plugin){
        this.currId = currModId;
        this.researchUI = plugin;
    }
    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }
    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        float lastY = 0;
        TooltipMakerAPI tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth(),panelAPI.getPosition().getHeight(),true);
        float opad = 0f;
        for (String s : AoTDMainResearchManager.getInstance().getModIDsRepo()) {
            if(s.equals(currId))continue;
            TechTreeModButton button = new TechTreeModButton(panelAPI.getPosition().getWidth()-10,40,s,researchUI,false,this);
            tooltip.addCustom(button.getPanelOfButton(),5f);
        }
        tooltip.addSpacer(5f);
        float heightCalculated = tooltip.getHeightSoFar();
        if(heightCalculated<=mainPanel.getPosition().getHeight()){
            mainPanel.getPosition().setSize(mainPanel.getPosition().getWidth(),heightCalculated);
            tooltip.getPosition().setSize(mainPanel.getPosition().getWidth(),heightCalculated);
        }
        mainPanel.addUIElement(tooltip).inTL(0,0);
        return mainPanel.getPosition().getHeight();

    }

    @Override
    public void onExit() {
        if(!researchUI.getZoomPanel().getCurrentModID().equals(currId)){
            researchUI.getZoomPanel().setCurrentModID(currId);
            researchUI.getZoomPanel().createTechTree(currId);
            researchUI.unlockButtonsFromHover();
            researchUI.getModButton().updateModButton(currId);
        }
        researchUI.unlockButtonsFromHover();

        super.onExit();
    }
}
