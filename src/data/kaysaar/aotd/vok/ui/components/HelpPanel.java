package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class HelpPanel extends UiPanel{
    ButtonAPI button ;
    @Override
    public void createUI() {
        button =  tooltip.addAreaCheckbox("",null, Global.getSettings().getBasePlayerColor(), Global.getSettings().getBasePlayerColor(),Global.getSettings().getBrightPlayerColor(),29,30,0f);
        button.getPosition().inTL(0,0);
        tooltip.addImage(Global.getSettings().getSpriteName("ui_campaign_components", "question"), 30, 30, 0f);
        tooltip.getPrev().getPosition().inTL(0,0);
    }

    public ButtonAPI getButton() {
        return button;
    }
}
