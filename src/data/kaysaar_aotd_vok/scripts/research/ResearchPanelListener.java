package data.kaysaar_aotd_vok.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.input.InputEventAPI;

import data.kaysaar_aotd_vok.ui.ResearchUIDP;
import org.lwjgl.input.Keyboard;

import java.util.List;

import static data.kaysaar_aotd_vok.plugins.AoDCoreModPlugin.aodTech;

public class ResearchPanelListener implements CampaignInputListener {

    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);

    @Override
    public int getListenerInputPriority() {
        return 0;
    }


    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {
        if (researchAPI.getResearchFacilitiesQuantity() != 0) {
            for (InputEventAPI event : events) {
                if (event.isConsumed()) continue;
                if (event.isCtrlDown() && event.getEventValue() == Keyboard.KEY_T) {
                    event.consume();
                    Global.getSector().getCampaignUI().showInteractionDialog(new ResearchUIDP(), null);
                }
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
