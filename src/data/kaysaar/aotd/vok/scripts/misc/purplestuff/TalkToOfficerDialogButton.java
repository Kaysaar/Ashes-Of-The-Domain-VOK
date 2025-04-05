package data.kaysaar.aotd.vok.scripts.misc.purplestuff;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class TalkToOfficerDialogButton implements CustomUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    public ButtonAPI buttonToTalk;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }
    public TalkToOfficerDialogButton(float width, float height){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(width,height,false);
        buttonToTalk = tooltip.addButton("Talk to your officers",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(), Alignment.MID,CutStyle.TL_BR,width,height,0f);
        buttonToTalk.setShortcut(Keyboard.KEY_T,true);
        mainPanel.addUIElement(tooltip).inTL(-5,0);

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
        if(buttonToTalk!=null){
            if(buttonToTalk.isChecked()){
                buttonToTalk.setChecked(false);
                BasePopUpDialog dialog = new BasePopUpDialog("Officer Quarters");
                AoTDMisc.initPopUpDialog(dialog,600,400);
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
