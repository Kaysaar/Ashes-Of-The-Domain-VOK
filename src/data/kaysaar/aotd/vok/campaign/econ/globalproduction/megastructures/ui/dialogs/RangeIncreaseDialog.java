package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;

import java.awt.*;
import java.util.List;

public class RangeIncreaseDialog extends BasePopUpDialog{
    ProgressBarComponent component;
    WormholeGenerator sectionToRestore;
    BaseMegastrucutreMenu menu;
    CustomPanelAPI panelToUpdate;
    CustomPanelAPI panelForTooltip;
    int currentSegment;
    int mult = 10;
    int maxSegment = 7;
    float barHeight = 27;

    public RangeIncreaseDialog(WormholeGenerator section, BaseMegastrucutreMenu menu, String headerTitle) {
        super(headerTitle);
        this.sectionToRestore = section;
        this.menu = menu;
        mult = section.mult;
        maxSegment = section.sections;
        currentSegment = (section.range);
    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
       createHeaader(panelAPI);
       panelToUpdate = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y,null);
       createUIForFirstTime();
       panelAPI.addComponent(panelToUpdate).inTL(x,y);
       createConfirmAndCancelSection(panelAPI);
    }
    public void createUIForFirstTime(){
        component = new ProgressBarComponent(panelToUpdate.getPosition().getWidth()-10,barHeight,currentSegment,maxSegment,Misc.getDarkPlayerColor().brighter().brighter(),1);
        panelToUpdate.addComponent(component.getRenderingPanel()).inTL(0,20);
        createTooltipUI();

    }
    public  void createTooltipUI(){
        panelForTooltip = panelToUpdate.createCustomPanel(panelToUpdate.getPosition().getWidth(),panelToUpdate.getPosition().getHeight()-50,null);
        TooltipMakerAPI tooltip = panelForTooltip.createUIElement(panelForTooltip.getPosition().getWidth(),panelToUpdate.getPosition().getHeight(),true);
        float centerX = (panelToUpdate.getPosition().getWidth()-20)/2;
        float centerY = barHeight/2;
        TooltipMakerAPI tooltipOfBar = panelForTooltip.createUIElement(panelToUpdate.getPosition().getWidth()-20,barHeight,false);
        LabelAPI labelAPI = tooltipOfBar.addPara("Range of hypershunt : "+currentSegment*mult+" / "+maxSegment*mult+" LY",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        labelAPI.getPosition().inTL(centerX-(labelAPI.computeTextWidth(labelAPI.getText())/2),centerY-(labelAPI.computeTextHeight(labelAPI.getText())/2));
        panelForTooltip.addUIElement(tooltipOfBar).inTL(0,0);
        tooltip.setParaInsigniaLarge();
        int effectiveSegment = currentSegment-1;
        tooltip.addPara("Range of hypershunt %s light years",5f, Color.ORANGE,""+(currentSegment*mult));
        tooltip.addPara("Upkeep : %s", 5f, Color.ORANGE, Misc.getDGSCredits(20000 * (effectiveSegment)));
        tooltip.addPara("Consumption of %s : %s",5f,Color.ORANGE,"purified transplutonics",""+(effectiveSegment*mult*2));
        tooltip.setParaFontDefault();
        tooltip.addPara("Note! if demand of purified transplutonics won't be met, emergency systems of hypershunt will reduce effetive range to %s",10f,Color.ORANGE,"10 light years");
        panelForTooltip.addUIElement(tooltip).inTL(0,40);
        panelToUpdate.addComponent(panelForTooltip).inTL(0,20);

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(component!=null){
            if(component.haveMovedToAnotherSegment()){
                component.setHaveMovedToAnotherSegment(false);
                recreateUIForPanel();
            }
        }
    }

    public void recreateUIForPanel(){
        panelToUpdate.removeComponent(panelForTooltip);
        currentSegment = component.currentSection;
        createTooltipUI();
    }

    @Override
    public void applyConfirmScript() {
        sectionToRestore.range = currentSegment;
        menu.resetSection(sectionToRestore.getSpec().getSectionID());
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(component!=null){
            component.getRenderingPanel().processInput(events);
        }
        super.processInput(events);
    }
}
