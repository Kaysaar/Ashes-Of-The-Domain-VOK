package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.ArrayList;
import java.util.List;

public class DropDownButton implements CustomUIPanelPlugin {
    CustomButton mainButton;
    ArrayList<CustomButton>buttons;
    boolean isDropped = false;
    UITableImpl tableOfReference;
    CustomPanelAPI panelOfImpl;
    CustomPanelAPI mainPanel;
    TooltipMakerAPI tooltipOfImpl;
    float width,height,maxWidth,maxHeight;
    public boolean droppableMode;
    public CustomPanelAPI getPanelOfImpl() {
        return mainPanel;
    }

    public DropDownButton(UITableImpl tableOfReference, float width, float height, float maxWidth, float maxHeight,boolean droppableMode){
        this.tableOfReference = tableOfReference;
        this.width = width;
        this.height = height;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        mainPanel = Global.getSettings().createCustom(width,height,this);
        panelOfImpl = mainPanel.createCustomPanel(width,height,null);
        this.droppableMode = droppableMode;
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
    public void resetUI(){
        mainPanel.removeComponent(panelOfImpl);
        if(!isDropped){
            mainPanel.getPosition().setSize(width,height);
        }
    }
    public void createUI(){
        panelOfImpl = mainPanel.createCustomPanel(width,height,null);
        tooltipOfImpl = panelOfImpl.createUIElement(panelOfImpl.getPosition().getWidth(),panelOfImpl.getPosition().getHeight(),false);
        createUIContent();
        panelOfImpl.addUIElement(tooltipOfImpl).inTL(0,0);
        mainPanel.addComponent(panelOfImpl).inTL(0,0);
    }
    public void createUIContent(){
        if(buttons==null){
            buttons  = new ArrayList<>();
        }
    }
    @Override
    public void advance(float amount) {
        if(mainButton==null)return;
        for (CustomButton button : buttons) {
            if(button.isChecked()){
                button.setChecked(false);
                tableOfReference.buttonPressed(button);
            }
        }
        if(mainButton.isChecked()){
            mainButton.setChecked(false);
            if(droppableMode){
                isDropped = !isDropped;
                tableOfReference.recreateTable();
            }
            else{
                tableOfReference.buttonPressed(mainButton);
            }


        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public void clear(){
        buttons.clear();
        mainButton = null;
    }
}
