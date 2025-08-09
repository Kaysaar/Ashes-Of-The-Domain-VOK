package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.awt.*;
import java.util.List;

public class AoTDCompoundShowcase implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;
    ProgressBarComponent component;
    LabelAPI updatingLabel;
    CustomPanelAPI tooltipPanel;
    ButtonAPI button;
    public static String memKey = "$aotd_compoun_on";
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public AoTDCompoundShowcase(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        ProgressBarComponent component = new ProgressBarComponent(width-1, height, 0.4f, new Color(122, 36, 245));
        this.component = component;
        ImageViewer viewer = new ImageViewer(24, 24, Global.getSettings().getSpriteName("aotd_icons", "compound_info"));
        viewer.setColorOverlay(new Color(138, 41, 255, 255));
        mainPanel.addComponent(viewer.getComponentPanel()).inTL(6, 1);
        mainPanel.addComponent(component.getRenderingPanel()).inTL(30, 0);
        createTooltipPanel(false);


    }

    public void createTooltipPanel(boolean updateButton) {
        if (tooltipPanel != null) {
            mainPanel.removeComponent(tooltipPanel);
        }

        tooltipPanel = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth() * 2, mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = tooltipPanel.createUIElement(tooltipPanel.getPosition().getWidth(), tooltipPanel.getPosition().getHeight(), false);
        float width, height;
        width = tooltipPanel.getPosition().getWidth();
        height = tooltipPanel.getPosition().getHeight();
        tooltip.setParaFont("graphics/fonts/orbitron12condensed.fnt");
        Color[] colors = new Color[2];
        colors[1] = new Color(248, 181, 145);
        colors[0] = new Color(201, 156, 255, 255);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        LabelAPI label = tooltip.addPara("%s / %s", 5f, colors, ""+AoTDFuelConsumptionScript.getCompound(Global.getSector().getPlayerFleet().getCargo()), "" + Global.getSector().getPlayerFleet().getCargo().getFuel());
        label.getPosition().inTL(component.getRenderingPanel().getPosition().getCenterX() - (label.computeTextWidth(label.getText()) / 2), height / 4 + 19);
        if(button==null){
            button = tooltip.addButton("Start Infusion",null,new Color(201, 156, 255, 255),new Color(90, 43, 148, 255),Alignment.MID,CutStyle.ALL,width/2+15,20,0f);
            button.getPosition().inTL(15,height+25);
        }
        else{
            tooltip.addCustom(button,5f).getPosition().inTL(15,height+25);
        }
        tooltipPanel.addUIElement(tooltip).inTL(-5, 0);
        mainPanel.addComponent(tooltipPanel).inTL(0, -20);

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

        if (component != null) {
            createTooltipPanel(false);
            float progres = AoTDFuelConsumptionScript.getCompound(Global.getSector().getPlayerFleet().getCargo()) / Global.getSector().getPlayerFleet().getCargo().getFuel();
            if (progres >= 1) progres = 1;
            component.progress = progres;
        }
        if(button!=null&&button.isChecked()){
            button.setChecked(false);
            if(!Global.getSector().getPlayerMemoryWithoutUpdate().contains(memKey)){
                Global.getSector().getPlayerMemoryWithoutUpdate().set(memKey,true);
                button.setText("Stop Infusion");
                createTooltipPanel(true);
            }
            else{
                boolean current = Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean(memKey);
                Global.getSector().getPlayerMemoryWithoutUpdate().set(memKey,!current);
                if(current){
                    button.setText("Start Infusion");
                }
                else{
                    button.setText("Stop Infusion");
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
}
