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

public class BaseSliderDialog extends BasePopUpDialog{
    ProgressBarComponent component;
    BaseMegastrucutreMenu menu;
    CustomPanelAPI panelToUpdate;
    CustomPanelAPI panelForTooltip;
    int currentSegment;
    int mult;
    int maxSegment;
    int minSection;
    float barHeight = 27;

    public BaseSliderDialog(BaseMegastrucutreMenu menu, String headerTitle,int mult,int maxSegments,int currSegment, int minSection) {
        super(headerTitle);
        this.menu = menu;
        this.mult = mult;
        this.maxSegment = maxSegments;
        this.minSection = minSection;
       this.currentSegment = currSegment;
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
        component = new ProgressBarComponent(panelToUpdate.getPosition().getWidth()-10,barHeight,currentSegment,maxSegment,Misc.getDarkPlayerColor().brighter().brighter(),minSection);
        panelToUpdate.addComponent(component.getRenderingPanel()).inTL(0,20);
        createTooltipUI();

    }
    public  void createTooltipUI(){
        panelForTooltip = panelToUpdate.createCustomPanel(panelToUpdate.getPosition().getWidth(),panelToUpdate.getPosition().getHeight()-50,null);
        TooltipMakerAPI tooltip = panelForTooltip.createUIElement(panelForTooltip.getPosition().getWidth(),panelToUpdate.getPosition().getHeight(),true);
        float centerX = (panelToUpdate.getPosition().getWidth()-20)/2;
        float centerY = barHeight/2;
        TooltipMakerAPI tooltipOfBar = panelForTooltip.createUIElement(panelToUpdate.getPosition().getWidth()-20,barHeight,false);
        LabelAPI labelAPI = createLabelForBar(tooltipOfBar);
        labelAPI.getPosition().inTL(centerX-(labelAPI.computeTextWidth(labelAPI.getText())/2),centerY-(labelAPI.computeTextHeight(labelAPI.getText())/2));
        panelForTooltip.addUIElement(tooltipOfBar).inTL(0,0);
        tooltip.setParaInsigniaLarge();
        int effectiveSegment = currentSegment-1;
        populateTooltipBelowBar(tooltip, effectiveSegment);
        panelForTooltip.addUIElement(tooltip).inTL(0,40);
        panelToUpdate.addComponent(panelForTooltip).inTL(0,20);

    }

    public void populateTooltipBelowBar(TooltipMakerAPI tooltip, int effectiveSegment) {

    }

    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip){
        return tooltip.addPara("Currently assigned manpower : "+currentSegment*mult+" / "+maxSegment*mult,Misc.getTooltipTitleAndLightHighlightColor(),5f);

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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(component!=null){
            component.getRenderingPanel().processInput(events);
        }
        super.processInput(events);
    }
}
