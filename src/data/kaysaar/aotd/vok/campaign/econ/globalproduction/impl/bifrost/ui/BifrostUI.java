package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class BifrostUI extends BaseMegastrucutreMenu  {
    public BifrostUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        super(megastructure, parentPanel, menu);
    }
    ButtonAPI addGate;
    public TooltipMakerAPI tooltipOfActions;
    @Override
    public void createTitleSection() {
        mainTitlePanel = MegastructureUIMisc.createTitleSection(mainPanel, megastructureReferedTo,"Bifrost Gates");
        mainPanel.addComponent(mainTitlePanel).inTL(-5,lastY);
        lastY+=mainTitlePanel.getPosition().getHeight();
    }

    public void addUI(){
        width = parentPanel.getPosition().getWidth();
        height = parentPanel.getPosition().getHeight();
        createTitleMenu();
        createTitleSection();
        buttons.clear();
        float totalHeight  = height-lastY-10;
        toolTipPanel = mainPanel.createCustomPanel(width,totalHeight,null);
        tooltipOfActions = mainPanel.createUIElement(width,40,false);

        tooltipOfSections = toolTipPanel.createUIElement(width,totalHeight-45,true);
        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            createSectionMenu(megaStructureSection,0f,0f);
        }
        lastYForSection = lastY;
        toolTipPanel.addUIElement(tooltipOfSections).inTL(0,0);
        mainPanel.addComponent(toolTipPanel).inTL(-5,lastYForSection);
    }

    @Override
    public void advance(float amount) {

    }
}
