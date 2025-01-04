package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;

public class CustomButton implements CustomUIPanelPlugin {
    ButtonAPI mainButton;
    public CustomPanelAPI panel;
    Object buttonData;
    float width,height;
    public float indent;
    Color base,bg,bright;
    public CustomPanelAPI getPanel() {
        return panel;
    }

    public CustomButton(float width, float height, Object buttonData,float indent,Color base,Color bg,Color bright ) {
        panel = Global.getSettings().createCustom(width,height,this);
        this.width = width;
        this.height = height;
        this.buttonData = buttonData;
        this.indent = indent;
        this.base = base;
        this.bg = bg;
        this.bright = bright;

    }
    public void initializeUI(){
        TooltipMakerAPI tooltip = panel.createUIElement(width,height,false);
        TooltipMakerAPI tooltipActualButton = panel.createUIElement(width,height,false);
        mainButton = createButton(tooltipActualButton);
        createButtonContent(tooltip);

        panel.addUIElement(tooltipActualButton).inTL(0,0);
        panel.addUIElement(tooltip).inTL(0,0);
    }
    public void createButtonContent(TooltipMakerAPI tooltip){

    }
    public ButtonAPI createButton(TooltipMakerAPI tooltip){
        return tooltip.addAreaCheckbox("",null,base,bg,bright,panel.getPosition().getWidth(),panel.getPosition().getHeight(),0f,true);
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public boolean isChecked(){
        return mainButton.isChecked();
    }
    public void setChecked(boolean checked){
        mainButton.setChecked(checked);
    }
}
