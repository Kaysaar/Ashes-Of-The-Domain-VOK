package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class OtherInfoPopUp  extends PopUpUI{
    GPMegaStructureSection section;
    float lastYPos;
    CustomPanelAPI mainPanel;
    public OtherInfoPopUp(GPMegaStructureSection section) {
        this.section = section;
    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }

    public float createUIMockup(CustomPanelAPI panelAPI){
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltipMakerAPI = mainPanel.createUIElement(panelAPI.getPosition().getWidth(),50,true);
        section.createTooltipForOtherInfoSection(tooltipMakerAPI,panelAPI.getPosition().getWidth());
        TooltipMakerAPI tooltip = mainPanel.createUIElement(panelAPI.getPosition().getWidth(),tooltipMakerAPI.getHeightSoFar(),false);
        section.createTooltipForOtherInfoSection(tooltip,panelAPI.getPosition().getWidth());
        mainPanel.addUIElement(tooltip).inTL(0,0);
        return tooltipMakerAPI.getHeightSoFar();
    }
}
