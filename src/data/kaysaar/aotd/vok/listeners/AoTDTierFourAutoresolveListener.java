package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.CoreAutoresolveListener;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BattleAutoresolverPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class AoTDTierFourAutoresolveListener implements CoreAutoresolveListener {
    @Override
    public void modifyDataForFleet(BattleAutoresolverPluginImpl.FleetAutoresolveData data) {
        if(data.fleet!=null){
            CampaignFleetAPI fleet = data.fleet;
            for (FleetMemberAPI memberAPI : fleet.getFleetData().getMembersListCopy()) {
                if(memberAPI.isStation()&&memberAPI.getHullSpec().hasTag("tier4station")){
                    data.fightingStrength*=100;
                }
            }
            for (BattleAutoresolverPluginImpl.FleetMemberAutoresolveData memberAPI : data.members) {
                if(memberAPI.member.isStation()&&memberAPI.member.getHullSpec().hasTag("tier4station")){
                    memberAPI.strength*=20;
                }
            }
        }
    }
}
