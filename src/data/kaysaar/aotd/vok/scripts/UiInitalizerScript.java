package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.input.InputEventAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.AoTDResearchUIDP;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class UiInitalizerScript implements CampaignInputListener {


    @Override
    public int getListenerInputPriority() {
        return 0;
    }

    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().howManyFacilitiesFactionControlls()==0){
            return;
        }
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isCtrlDown() && event.getEventValue() == Keyboard.KEY_T) {
                event.consume();
                AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getQueuedReesarchOptions();
                Global.getSector().getCampaignUI().showInteractionDialog(new AoTDResearchUIDP(), null);
            }
        }
    }

    @Override
    public void processCampaignInputPreFleetControl(List<InputEventAPI> events) {

    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }
}
