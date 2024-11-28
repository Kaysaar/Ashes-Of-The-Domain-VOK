package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;

import java.awt.*;

public class HelpPanelPopUp extends PopUpUI {
    CustomPanelAPI mainPanel;
    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }

    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(panelAPI.getPosition().getWidth()+5,panelAPI.getPosition().getHeight(),true);
        tooltip.addSectionHeading("Research Mechanic", Alignment.MID,0f);
        tooltip.addPara("With the collapse, much of the technology used has disappeared or is kept from the public. To restore and advance your faction's technology, you will need to research it back (or even advance past the sector's current technologies).",5f);
        tooltip.addSectionHeading("How to acquire necessary things to progress",Alignment.MID,5f);
        tooltip.addPara("Two elements are incidental to success in research : %s, buildable on any colonies, and %s.",5f, Color.ORANGE,"Research facilities","Databanks");
        tooltip.addPara("These Domain-era caches of information can be found on planets with the %s condition",5f,Color.ORANGE," \"Pre-collapse facility\"");
        tooltip.addPara("Keep note, those facilities pre-date first AI war, we don't know what horrors we might found there!", Misc.getNegativeHighlightColor(),5f);
        tooltip.addPara("Building %s on planet with %s condition, will result in monthly increase of %s by %s",5f,Color.ORANGE,"Research Facility","Pre-Collapse Facility","Research Databanks","1");
        tooltip.addSectionHeading("How to research",Alignment.MID,5f);
        tooltip.addPara("Once enough databanks have been deposited in the special storage provided by the Research facility : %s, opening the research tab in your Command menu will show you the tech tree, with the technologies you can research in green.",5f,Color.ORANGE,"Research Storage");
        tooltip.addPara("If tech is highlighted as red, then it means one of requirements have not been met, press %s to check what you are missing, if some requirement is missing, then it will be highlighted in red color.",5f,Color.ORANGE,"More info");
        tooltip.addPara("Clicking the research button will start the discovery process of that technology, and will incur extra monthly cost depending on how far that technology is in the tech tree.",5f);
        tooltip.addPara("Once the countdown is complete, the technology is forever researched, and you unlock the buildings and passive advantages associated with that technology.",5f);
        tooltip.addPara("Be aware that some technologies might require some \"extra investments\".",5f);
        tooltip.addSectionHeading("Tips",Alignment.MID,5f);
        tooltip.addPara("Building more research facilities %s will result in research speed bonus, up to %s",5f,Color.ORANGE,"(up to 8)","80% : 10% for each Research Facility");
        tooltip.addSpacer(5f);
        tooltip.getPosition().setSize(panelAPI.getPosition().getWidth(),tooltip.getHeightSoFar());
        mainPanel.getPosition().setSize(panelAPI.getPosition().getWidth(),tooltip.getHeightSoFar());
        mainPanel.addUIElement(tooltip).inTL(0,0);
        return tooltip.getHeightSoFar();


    }
}
