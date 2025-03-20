package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.PatrolFleetType;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.manager.FactionPatrolFleetManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.patrolfleet.fleet.PatrolFleetCreationPopUP;

import java.awt.*;
import java.util.List;

public class PatrolFleetFactionData implements CustomUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    public UILinesRenderer renderer;
    public CustomPanelAPI armadaDataPanel;
    ButtonAPI addFleet;

    public void refresh(){

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }
    public PatrolFleetDataManager dataManager;
    public PatrolFleetFactionData(float width, float height,PatrolFleetDataManager dataManager){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.dataManager = dataManager;
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        armadaDataPanel = createArmadaDataPanel(width,height);
        mainPanel.addComponent(armadaDataPanel).inTL(0,0);
    }
    public void refreshArmadaPanel(){
        mainPanel.removeComponent(armadaDataPanel);
        armadaDataPanel = createArmadaDataPanel(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight());
        mainPanel.addComponent(armadaDataPanel).inTL(0,0);
    }
    public CustomPanelAPI createArmadaDataPanel(float width,float height){
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width,height,null);
        TooltipMakerAPI tooltip = panelAPI.createUIElement(width,height,false);
        tooltip.addSectionHeading("Fleet Data", Alignment.MID,0f);

        Color[]colors = new Color[2];
        colors[0] = Misc.getTextColor();
        colors[1] = Color.ORANGE;
        int total = FactionPatrolFleetManager.getInstance().getArmadaPoints();
        int available = FactionPatrolFleetManager.getInstance().getAvailableArmadaPoints();
        ProgressBarComponent component = new ProgressBarComponent(width-15,25, (float) available /total, Misc.getDarkPlayerColor().brighter().brighter());
        tooltip.addCustom(component.getRenderingPanel(),0f).getPosition().inTL(7,25);

        LabelAPI labelAPI1 =     tooltip.addPara("Armada points : %s / %s",5f,colors,""+available,""+total);
        labelAPI1.getPosition().inTL(width/2-(labelAPI1.computeTextWidth(labelAPI1.getText())/2),30);
        ImageViewer viewer = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet1"));
        ImageViewer viewer2 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet2"));
        ImageViewer viewer3 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("systemMap", "icon_fleet3"));

        ImageViewer viewer5 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("aotd_icons", "fleet_ai"));
        ImageViewer viewer4 = new ImageViewer(40, 35, Global.getSettings().getSpriteName("aotd_icons", "fleet_merc"));
        viewer4.setColorOverlay(Global.getSector().getFaction(Factions.INDEPENDENT).getDarkUIColor());
        viewer5.setColorOverlay(Global.getSector().getFaction(Factions.REMNANTS).getBaseUIColor());
        tooltip.addCustom(viewer.getComponentPanel(),5f).getPosition().inTL(15,65);
        tooltip.addCustom(viewer2.getComponentPanel(),-5f).getPosition().inTL(width/2 -20 ,65);
        tooltip.addCustom(viewer3.getComponentPanel(),-5f).getPosition().inTL(width-55,65);
        tooltip.addCustom(viewer4.getComponentPanel(),-5f).getPosition().inTL(viewer2.getComponentPanel().getPosition().getX()/2,120);
        tooltip.addCustom(viewer5.getComponentPanel(),-5f).getPosition().inTL((viewer2.getComponentPanel().getPosition().getX()/2)*3,120);
        tooltip.addSectionHeading("Logistics",Alignment.MID,5f).getPosition().inTL(0,185);;
        tooltip.addPara("Current cost of operations : %s",5f,Color.ORANGE,Misc.getDGSCredits(100000)).getPosition().inTL(5,215);
        addFleet = tooltip.addButton("Create new fleet",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Hire Mercenary Fleet",null,Global.getSector().getFaction(Factions.INDEPENDENT).getBaseUIColor().brighter(),Global.getSector().getFaction(Factions.INDEPENDENT).getDarkUIColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Manage Officers",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);
        tooltip.addButton("Manage fleet templates",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);

        tooltip.addButton("Replenish all fleets",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.NONE,width-10,30,10f);

        LabelAPI label1 = tooltip.addPara(""+FactionPatrolFleetManager.getInstance().getFleetsOfCertainType(PatrolFleetType.PatrolType.SMALL),Color.cyan,0f);
        LabelAPI label2 = tooltip.addPara(""+FactionPatrolFleetManager.getInstance().getFleetsOfCertainType(PatrolFleetType.PatrolType.MEDIUM),Color.cyan,0f);
        LabelAPI label3 = tooltip.addPara(""+FactionPatrolFleetManager.getInstance().getFleetsOfCertainType(PatrolFleetType.PatrolType.LARGE),Color.cyan,0f);
        LabelAPI label4 = tooltip.addPara(""+FactionPatrolFleetManager.getInstance().getFleetsOfCertainType(PatrolFleetType.PatrolType.MERCENARY),Global.getSector().getFaction(Factions.INDEPENDENT).getBaseUIColor().brighter(),0f);
        LabelAPI label5 = tooltip.addPara(""+FactionPatrolFleetManager.getInstance().getFleetsOfCertainType(PatrolFleetType.PatrolType.AUTOMATED),Global.getSector().getFaction(Factions.REMNANTS).getBaseUIColor(),0f);
        setPosition(label1, viewer);
        setPosition(label2, viewer2);
        setPosition(label3, viewer3);
        setPosition(label4, viewer4);
        setPosition(label5, viewer5);
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
        if(addFleet!=null){
            if(addFleet.isChecked()){
                addFleet.setChecked(false);
                BasePopUpDialog ui = new PatrolFleetCreationPopUP("Create Fleet",dataManager);
                AoTDMisc.initPopUpDialog(ui,1240,740);
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
