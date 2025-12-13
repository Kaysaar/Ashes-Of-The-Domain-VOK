package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;


import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;

import java.awt.*;
import java.util.List;

public class RangeIncreaseDialog extends BaseSliderDialog {
    WormholeGenerator sectionToRestore;

    public RangeIncreaseDialog(WormholeGenerator section, BaseMegastructureMenu menu, String headerTitle) {
        super(menu,headerTitle,section.mult,section.sections,section.range,1);
        this.sectionToRestore = section;
        this.menu = menu;

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
        component = new ProgressBarComponentV2(panelToUpdate.getPosition().getWidth()-10,barHeight,currentSegment,maxSegment,Misc.getDarkPlayerColor().brighter().brighter(),1);
        panelToUpdate.addComponent(component.getRenderingPanel()).inTL(getBarX(),getBarY());
        createTooltipUI();

    }
    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_16
        );
        tooltip.addPara("Range of hypershunt : %s light years",5f, Color.ORANGE,""+(currentSegment*mult));
        tooltip.addPara("Upkeep : %s", 5f, Color.ORANGE, Misc.getDGSCredits(20000 * effectiveSegment));
        tooltip.addPara("Consumption of %s : %s",5f,Color.ORANGE,"purified transplutonics",""+(effectiveSegment *mult*2));
    }

    @Override
    public float getBarY() {
        return 110f;
    }

    @Override
    public float getBarX() {
        return 5f;
    }

    @Override
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_12);
        tooltip.addPara("Note! If the demand of purified transplutonics isn't met, the emergency systems of the hypershunt will reduce effective range to %s",10f,Color.ORANGE,"10 light years");
    }

    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip){
        return tooltip.addPara("Range of the hypershunt : "+currentSegment*mult+" / "+maxSegment*mult+" LY",Misc.getTooltipTitleAndLightHighlightColor(),5f);
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
