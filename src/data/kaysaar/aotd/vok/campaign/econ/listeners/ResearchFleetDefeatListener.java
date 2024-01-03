package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.intel.ResearchExpeditionIntel;
import data.kaysaar.aotd.vok.scripts.research.ResearchFleetRouteManager;

public class ResearchFleetDefeatListener implements FleetEventListener {
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(ResearchExpeditionIntel.class)) {
            ResearchExpeditionIntel intel = (ResearchExpeditionIntel)intelInfoPlugin;
            if(intel.idOfIntel.split("_")[1].equals(fleet.getFaction().getId())){
                if(reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE)){
                    intel.setFailed(true);
                }
                if(reason.equals(CampaignEventListener.FleetDespawnReason.REACHED_DESTINATION)){
                    intel.setSuccess(true);
                }


            }
        }


    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }
}
