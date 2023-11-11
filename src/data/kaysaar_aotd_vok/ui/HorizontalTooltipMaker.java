package data.kaysaar_aotd_vok.ui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class HorizontalTooltipMaker {
    public TooltipMakerAPI mainTooltip;
    public CustomPanelAPI mainPanel;
    public HorizontalScrollbar horizontalScrollbar;

    public void init(CustomPanelAPI mainPanel,float width, float height, boolean hasVeritcalScrollbar,float trueWidth ){
        this.mainPanel = mainPanel;
        mainTooltip = mainPanel.createUIElement(width,height,hasVeritcalScrollbar);
        horizontalScrollbar = new HorizontalScrollbar(mainPanel.getPosition().getY(),mainPanel.getPosition().getX(),mainTooltip,trueWidth);
    }
    public TooltipMakerAPI getMainTooltip(){
        return this.mainTooltip;
    }
    public CustomPanelAPI getMainPanel(){
        return this.mainPanel;
    }
    public void addPanel( CustomPanelAPI subPanel ,float xPad, float yPad){
        mainTooltip.addComponent(subPanel).inTL(xPad, yPad);
        horizontalScrollbar.addPanel(subPanel,xPad,yPad);
    }
    public void addTooltipToMainPanel(float xpad,float ypad){
        mainPanel.addUIElement(mainTooltip).inTL(xpad,ypad);
    }
    public HorizontalScrollbar getHorizontalScrollbar(){
        return this.horizontalScrollbar;
    }
    public CustomPanelAPI createCustomPanel(float width, float height){
       return mainPanel.createCustomPanel(width,height,null);
    }

}
