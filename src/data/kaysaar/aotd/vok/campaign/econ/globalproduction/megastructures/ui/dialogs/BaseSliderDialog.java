package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;


import ashlib.data.plugins.ui.models.BasePopUpDialog;
import ashlib.data.plugins.ui.models.ProgressBarComponent;
import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;


import java.util.List;

public class BaseSliderDialog extends BasePopUpDialog {
    ProgressBarComponentV2 component;
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
        component = new ProgressBarComponentV2(panelToUpdate.getPosition().getWidth()-10,barHeight,currentSegment,maxSegment,Misc.getDarkPlayerColor().brighter().brighter(),minSection);
        panelToUpdate.addComponent(component.getRenderingPanel()).inTL(getBarX(),getBarY());
        createTooltipUI();

    }
    public  void createTooltipUI(){
        panelForTooltip = panelToUpdate.createCustomPanel(panelToUpdate.getPosition().getWidth(),panelToUpdate.getPosition().getHeight()-50,null);
        TooltipMakerAPI tooltipBottom = panelForTooltip.createUIElement(panelForTooltip.getPosition().getWidth(),panelToUpdate.getPosition().getHeight()-70-(getBarY()+barHeight),true);
        TooltipMakerAPI tooltipTop = panelForTooltip.createUIElement(panelForTooltip.getPosition().getWidth(),getBarY(),true);
        addTooltip(tooltipBottom);
        addTooltip(tooltipTop);
        float centerX = (panelToUpdate.getPosition().getWidth()-20)/2;
        float centerY = barHeight/2;
        TooltipMakerAPI tooltipOfBar = panelForTooltip.createUIElement(panelToUpdate.getPosition().getWidth()-20,barHeight,false);
        LabelAPI labelAPI = createLabelForBar(tooltipOfBar);
        labelAPI.getPosition().inTL(centerX-(labelAPI.computeTextWidth(labelAPI.getText())/2),centerY-(labelAPI.computeTextHeight(labelAPI.getText())/2));
        panelForTooltip.addUIElement(tooltipOfBar).inTL(getBarX(),getBarY()-barHeight/6);
        tooltipBottom.setParaInsigniaLarge();
        int effectiveSegment = currentSegment-1;
        populateTooltipTop(tooltipTop, effectiveSegment);
        populateTooltipBelow(tooltipBottom, effectiveSegment);
        panelForTooltip.addUIElement(tooltipTop).inTL(0,0f);
        panelForTooltip.addUIElement(tooltipBottom).inTL(0,getBarY()+barHeight+10f);
        panelToUpdate.addComponent(panelForTooltip).inTL(0,5);

    }
    public float getBarX(){
        return 0f;
    }
    public float getBarY(){
        return 0f;
    }

    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {

    }
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {

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
