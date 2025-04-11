package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.scripts.specialprojects.*;
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
        AoTDSpecialProject project = SpecialProjectManager.getInstance().getProject("uaf_supercap_slv_project");
        mainPanel = Global.getSettings().createCustom(width,height,this);
        tooltipPanel = Global.getSettings().createCustom(width,height,null);
        currentShowcasePanel = Global.getSettings().createCustom(width,height,null);
        currentShowcasePanel.addComponent(createDetailedSectionOfProject(width,height,project));
        TooltipMakerAPI tooltip = tooltipPanel.createUIElement(width,height,true);
        tooltip.addCustom(createSectionForProject(width-10,height,project),0f);

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
    public CustomPanelAPI createSectionForProject(float width, float height, AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,height,new UILinesRenderer(0f));
        ((UILinesRenderer)test.getPlugin()).setPanel(test);
        TooltipMakerAPI tooltip = test.createUIElement(width,height,false);
        project.createTooltipForButton(tooltip,width);
        buttons.add(tooltip.addButton("Show additional info","show_detailed",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,10f));
        ButtonAPI button =tooltip.addButton("Start Project",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,-30f);
        button.getPosition().setXAlignOffset(width-button.getPosition().getWidth()-10);
        HologramViewer viewer = SpecialProjectManager.createHologramViewer(project.getProjectSpec(),true);
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width-viewer.getComponentPanel().getPosition().getWidth(),20);
        test.getPosition().setSize(width,tooltip.getHeightSoFar()+5);
        test.addUIElement(tooltip).inTL(0,0);
        return test;
    }
    public CustomPanelAPI createDetailedSectionOfProject(float width, float height,AoTDSpecialProject project) {
        CustomPanelAPI test = Global.getSettings().createCustom(width,height,new UILinesRenderer(0f));
        TooltipMakerAPI tooltip = test.createUIElement(width,height,true);
        project.createDetailedTooltipForButton(tooltip,width);
        buttons.add(tooltip.addButton("Show other projects","show_projects",Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,10f));
        ButtonAPI button =tooltip.addButton("Start Project",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,180,30,-30f);
        button.getPosition().setXAlignOffset(width-button.getPosition().getWidth()-10);
        float heights = tooltip.getHeightSoFar();
        HologramViewer viewer = SpecialProjectManager.createHologramViewer(project.getProjectSpec(),true);
        viewer.setRenderLine(false);
        tooltip.addCustomDoNotSetPosition(viewer.getComponentPanel()).getPosition().inTL(width-viewer.getComponentPanel().getPosition().getWidth(),20);
        tooltip.setHeightSoFar(heights);
        test.addUIElement(tooltip).inTL(0,0);
        return test;
    }

}
