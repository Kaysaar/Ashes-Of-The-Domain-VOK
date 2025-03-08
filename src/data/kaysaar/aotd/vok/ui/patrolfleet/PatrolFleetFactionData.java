package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;
import java.util.List;

public class PatrolFleetFactionData implements CustomUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    public UILinesRenderer renderer;
    public CustomPanelAPI armadaDataPanel;
    public void refresh(){

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public PatrolFleetFactionData(float width, float height){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        armadaDataPanel = createArmadaDataPanel(width,height);
        mainPanel.addComponent(armadaDataPanel).inTL(0,0);
    }
    public CustomPanelAPI createArmadaDataPanel(float width,float height){
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width,height,null);
        TooltipMakerAPI tooltip = panelAPI.createUIElement(width,height,false);
        tooltip.addSectionHeading("Fleet Data", Alignment.MID,0f);

        Color[]colors = new Color[2];
        colors[0] = Misc.getTextColor();
        colors[1] = Color.ORANGE;
        ProgressBarComponent component = new ProgressBarComponent(width-15,25,20f/400f, Misc.getDarkPlayerColor().brighter().brighter());
        tooltip.addCustom(component.getRenderingPanel(),0f).getPosition().inTL(7,25);
        LabelAPI labelAPI1 =     tooltip.addPara("Armada points : %s / %s",5f,colors,"20","400");
        labelAPI1.getPosition().inTL(width/2-(labelAPI1.computeTextWidth(labelAPI1.getText())/2),30);
        ImageViewer viewer = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet1"));
        ImageViewer viewer2 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet2"));
        ImageViewer viewer3 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet3"));
        ImageViewer viewer4 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet0"));
        viewer4.setColorOverlay(Global.getSector().getFaction(Factions.INDEPENDENT).getDarkUIColor());
        tooltip.addCustom(viewer.getComponentPanel(),5f).getPosition().inTL(15,65);
        tooltip.addCustom(viewer2.getComponentPanel(),-5f).getPosition().inTL(width/2 -20 ,65);
        tooltip.addCustom(viewer3.getComponentPanel(),-5f).getPosition().inTL(width-55,65);
        tooltip.addCustom(viewer4.getComponentPanel(),-5f).getPosition().inTL(width/2 -20,120);
        tooltip.addSectionHeading("Logistics",Alignment.MID,5f).getPosition().inTL(0,185);;
        tooltip.addPara("Current cost of operations : %s",5f,Color.ORANGE,Misc.getDGSCredits(100000)).getPosition().inTL(5,215);
        tooltip.addButton("Create new fleet",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Hire Mercenary Fleet",null,Global.getSector().getFaction(Factions.INDEPENDENT).getBaseUIColor().brighter(),Global.getSector().getFaction(Factions.INDEPENDENT).getDarkUIColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Manage Officers",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Manage fleet templates",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);

        tooltip.addButton("Replenish all fleets",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);

        LabelAPI label1 = tooltip.addPara("0",Color.cyan,0f);
        LabelAPI label2 = tooltip.addPara("0",Color.cyan,0f);
        LabelAPI label3 = tooltip.addPara("1",Color.cyan,0f);
        LabelAPI label4 = tooltip.addPara("0",Misc.getPositiveHighlightColor(),0f);
        setPosition(label1, viewer);
        setPosition(label2, viewer2);
        setPosition(label3, viewer3);
        setPosition(label4, viewer4);
        panelAPI.addUIElement(tooltip);
        return panelAPI;
    }

    private void setPosition(LabelAPI label1, ImageViewer viewer) {
         label1.getPosition().inTL(viewer.getComponentPanel().getPosition().getCenterX() - (label1.computeTextWidth(label1.getText())/2), -viewer.getComponentPanel().getPosition().getY());
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
