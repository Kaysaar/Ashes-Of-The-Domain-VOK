package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.CoreAutoresolveListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BattleAutoresolverPluginImpl;

public class AoTDTierFourAutoresolveListener implements CoreAutoresolveListener {
    @Override
    public void modifyDataForFleet(BattleAutoresolverPluginImpl.FleetAutoresolveData data) {
        if(data.fleet!=null){
            CampaignFleetAPI fleet = data.fleet;
            for (FleetMemberAPI memberAPI : fleet.getFleetData().getMembersListCopy()) {
                if(memberAPI.isStation()&&memberAPI.getHullSpec().hasTag("tier4station")){
                    data.fightingStrength*=30;
                }
            }
        }
    }
}
