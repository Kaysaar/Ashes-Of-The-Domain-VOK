package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;

import java.awt.*;
import java.util.ArrayList;

public class ProductionDataPanel {
    class ProductionButtonData {
        int valueOfEq;
        String type;

        public int getValueOfEq() {
            return valueOfEq;
        }

        public String getType() {
            return type;
        }

        public ProductionButtonData(int value, String type){
            this.valueOfEq = value;
            this.type= type;
        }
    }

    ArrayList<ButtonAPI> buttons;
    CustomPanelAPI parentPanel;
    CustomPanelAPI panel;
    public boolean resets;
    public float widthOfUI;
    public float heightOfUI;
    public float x;
    public float y;

    public ProductionDataPanel(float width, float height, CustomPanelAPI currentPanel,float x, float y) {
        this.widthOfUI = width;
        heightOfUI = height;
        buttons = new ArrayList<>();
        this.parentPanel = currentPanel;
        this.x = x;
        this.y=y;

    }

    public void createUI() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        panel = parentPanel.createCustomPanel(widthOfUI,heightOfUI,renderer);
        TooltipMakerAPI tooltip = panel.createUIElement(widthOfUI,heightOfUI,false);
        tooltip.addSectionHeading("Production Data", Alignment.MID,0f);
        tooltip.addCustom(createButtonComponent(30,"ship",20,60),5f);
        tooltip.addCustom(createButtonComponent(30,"weapon",20,60),5f);
        tooltip.addCustom(createButtonComponent(30,"fighter",20,60),5f);

        panel.addUIElement(tooltip).inTL(0,0);
        parentPanel.addComponent(panel).inTL(x,y);
        renderer.setPanel(panel);
    }

    public CustomPanelAPI createButtonComponent(float height ,String type,float buttonWidth,float displayWidth){
        float seperator = 10f;
        float buttNumb = 3;

        CustomPanelAPI mainPanel = Global.getSettings().createCustom(UIData.WIDTH_OF_ORDERS,height,null);
        TooltipMakerAPI tooltipOfButtonSection  = mainPanel.createUIElement(UIData.WIDTH_OF_ORDERS+displayWidth,height,false);
        String signPlus = "+";
        String signMinus = "-";
        int valFirst = 1;
        int valSecond = 10;


        ProductionButtonData data = new ProductionButtonData(-valSecond,type);
        ProductionButtonData data2 = new ProductionButtonData(-valFirst,type);
        ProductionButtonData data3 = new ProductionButtonData(valFirst,type);
        ProductionButtonData data4 = new ProductionButtonData(valSecond,type);
        ButtonAPI button = tooltipOfButtonSection.addButton(signMinus+" "+signMinus,data,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg, Alignment.MID,CutStyle.NONE,buttonWidth,20,10f);
        ButtonAPI button2 =tooltipOfButtonSection.addButton(signMinus,data2,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg, Alignment.MID,CutStyle.NONE,buttonWidth,20,10f);
        ButtonAPI button3 = tooltipOfButtonSection.addButton(signPlus,data3,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg, Alignment.MID,CutStyle.NONE,buttonWidth,20,10f);
        ButtonAPI button4 = tooltipOfButtonSection.addButton(signPlus+" "+signPlus,data4,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg, Alignment.MID,CutStyle.NONE,buttonWidth,20,10f);
        ButtonAPI buttonOfDisplay = tooltipOfButtonSection.addAreaCheckbox("",null, NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,NidavelirMainPanelPlugin.bright,displayWidth,30,0f);
        button.getPosition().inTL(0,5);
        button2.getPosition().inTL(buttonWidth+seperator,5);
        button3.getPosition().inTL(buttonWidth*2+seperator*3+displayWidth,5);
        button4.getPosition().inTL(buttonWidth*3+seperator*4+displayWidth,5);


        buttonOfDisplay.setEnabled(false);
        buttonOfDisplay.setButtonDisabledPressedSound(null);
        buttonOfDisplay.setClickable(false);
        buttonOfDisplay.setHighlightBrightness(0f);
        buttonOfDisplay.getPosition().inTL(buttonWidth*2+seperator*2,0);
        LabelAPI label = tooltipOfButtonSection.addPara("", Color.ORANGE,0f);
        LabelAPI label2 =  tooltipOfButtonSection.addPara(getStringForType(type)+" Produced Per Order", Color.ORANGE,0f);
        float centerX = buttonOfDisplay.getPosition().getX()+buttonOfDisplay.getPosition().getWidth()/2;
        label.getPosition().inTL(centerX-(label.computeTextWidth(label.getText())/2),15-(label.computeTextHeight(label.getText())/2));
        label2.getPosition().inTL(buttonWidth*4+seperator*4+displayWidth+10,15-(label.computeTextHeight(label.getText())/2));
        buttons.add(button);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        mainPanel.addUIElement(tooltipOfButtonSection).inTL(0,0);
        return mainPanel;
    }
    public  String getStringForType(String type){
        if(type.equals("ship")){
            return "Ships";
        }
        if(type.equals("weapon")){
            return "Weapons";
        }
        if(type.equals("fighter")){
            return "Fighters";
        }
        return "";
    }

    public void advance() {
        resets= false;
        if(buttons!=null){
            for (ButtonAPI button : buttons) {
                if (button.isChecked()) {
                    button.setChecked(false);
                }

            }
        }
        if(resets){
            resetUI();
        }

    }

    public void clearUI() {
        buttons.clear();
        parentPanel.removeComponent(panel);
        panel = null;
    }
    public void resetUI(){
        clearUI();
        createUI();
    }

}
