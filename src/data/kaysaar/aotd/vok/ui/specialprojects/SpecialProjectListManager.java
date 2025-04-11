package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.ShipHologram;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class SpecialProjectListManager implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;
    CustomPanelAPI tooltipPanel;
    CustomPanelAPI currentShowcasePanel;
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    public SpecialProjectListManager(float width, float height){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        tooltipPanel = Global.getSettings().createCustom(width,height,null);
        currentShowcasePanel = Global.getSettings().createCustom(width,height,null);
        currentShowcasePanel.addComponent(createDetailedSectionOfProject(width,height));
        TooltipMakerAPI tooltip = tooltipPanel.createUIElement(width,height,true);
        tooltip.addCustom(createSectionForProject(width-10,height),0f);

        tooltipPanel.addUIElement(tooltip).inTL(0,0);

        mainPanel.addComponent(tooltipPanel).inTL(0,0);
        renderer= new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        for (ButtonAPI button : buttons) {
            if(button.isChecked()){
                button.setChecked(false);
                String str = (String) button.getCustomData();
                if(str.equals("show_detailed")){
                    mainPanel.removeComponent(tooltipPanel);
                    mainPanel.addComponent(currentShowcasePanel);
                }
                else{
                    mainPanel.removeComponent(currentShowcasePanel);
                    mainPanel.addComponent(tooltipPanel);
                }
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public CustomPanelAPI createSectionForProject(float width, float height) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,height,new UILinesRenderer(0f));
        ((UILinesRenderer)test.getPlugin()).setPanel(test);
        TooltipMakerAPI tooltip = test.createUIElement(width,height,false);
        tooltip.setTitleFont(Fonts.ORBITRON_16);
        tooltip.addTitle("Rebirth of Solvernia");
        tooltip.addSectionHeading("Upkeep",  Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,width-110,5f);

        tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(width-110,20,20, new HashMap<>(),new HashMap<>()),5f);

        LabelAPI labelAPI = tooltip.addSectionHeading("Project Progress",  Misc.getBasePlayerColor(),null,Alignment.MID,width-110,3f);
        ProgressBarComponent component = new ProgressBarComponent(width-110, 21, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 5f);
        buttons.add(tooltip.addButton("Show additional info","show_detailed",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,10f));
        ButtonAPI button =tooltip.addButton("Start Project",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,-30f);
        button.getPosition().setXAlignOffset(width-button.getPosition().getWidth()-10);
        HologramViewer viewer = new HologramViewer(100, 100, new ShipHologram("uaf_supercap_slv_core"));
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width-viewer.getComponentPanel().getPosition().getWidth(),20);
        test.getPosition().setSize(width,tooltip.getHeightSoFar()+5);
        test.addUIElement(tooltip).inTL(0,0);
        return test;
    }
    public CustomPanelAPI createDetailedSectionOfProject(float width, float height) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,height,new UILinesRenderer(0f));
        TooltipMakerAPI tooltip = test.createUIElement(width,height,true);
        tooltip.setTitleFont(Fonts.ORBITRON_16);
        tooltip.addTitle("Rebirth of Solvernia");
        tooltip.addSectionHeading("Upkeep",  Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,width-110,5f);

        tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(width-110,20,20, new HashMap<>(),new HashMap<>()),5f);

        tooltip.addSectionHeading("Project Progress",  Misc.getBasePlayerColor(),null,Alignment.MID,width-110,3f);
        ProgressBarComponent component = new ProgressBarComponent(width-110, 21, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 5f);

        tooltip.addCustom(createSubStageProgressMoved(width-110,"Dual XL-Grade Artillery Mount",10), 2f);
        tooltip.addCustom(createSubStageProgressMoved(width-110,"Twin Super-Capital Hulls",10), 2f);
        tooltip.addCustom(createSubStageProgressMoved(width-110,"Super-Capital Command Center",10), 2f);
        tooltip.addCustom(createSubStageProgressMoved(width-110,"XL-Grade Melody Power Plant",10), 2f);
        tooltip.addCustom(createSubStageProgressMoved(width-110,"Twin Super-Capital Melody Engines",10), 2f);
        tooltip.addPara("Solvernia, the once-proud vanguard of the November fleet, and a symbol of Auroran might and engineering. She stood as a stalwart defender of the United Aurora Federation, putting up fearsome resistance, her all-powerful railguns tearing ships in two, her armor withstanding an unholy amount of punishment. And yet she was brought low all the same. This massive wreck is all that remains. However, your engineers are confident in the research you have done into the Domain of Man. They believe that with your knowledge, the Solvernia can be returned to her rightful glory. They only require the material and enormous industrial capabilities...",5f);

        tooltip.addSectionHeading("Effects upon project completion",  Misc.getDarkHighlightColor(),null,Alignment.MID,width,3f);
        tooltip.addPara("Gain "+Global.getSettings().getHullSpec("uaf_supercap_slv_core").getHullNameWithDashClass(),Misc.getPositiveHighlightColor(),5f);
        buttons.add(tooltip.addButton("Show other projects","show_projects",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,10f));
        ButtonAPI button =tooltip.addButton("Start Project",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,-30f);
        button.getPosition().setXAlignOffset(width-button.getPosition().getWidth()-10);
        float heights = tooltip.getHeightSoFar();
        HologramViewer viewer = new HologramViewer(100, 100, new ShipHologram("uaf_supercap_slv_core"));
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width-viewer.getComponentPanel().getPosition().getWidth(),20);
        tooltip.setHeightSoFar(heights);
        test.addUIElement(tooltip).inTL(0,0);
        return test;
    }
    public CustomPanelAPI createSubStageProgressMoved(float width,String stageName,float opadX) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,1,null);
        CustomPanelAPI t = createSubStageProgress(width-opadX,1,stageName);

        test.addComponent(t).inTL(opadX,0);
        test.getPosition().setSize(width,t.getPosition().getHeight());
        return test;
    }
    public CustomPanelAPI createSubStageProgress(float width,float height,String stageName) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,height,null);
        TooltipMakerAPI tooltip = test.createUIElement(width,height,false);
        tooltip.addSectionHeading(stageName,  Misc.getBasePlayerColor(),null,Alignment.MID,width,0f);
        ProgressBarComponent component = new ProgressBarComponent(width-5, 15, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 2f);
        test.getPosition().setSize(width,tooltip.getHeightSoFar());
        test.addUIElement(tooltip).inTL(0,0);
        return test;
    }
}
