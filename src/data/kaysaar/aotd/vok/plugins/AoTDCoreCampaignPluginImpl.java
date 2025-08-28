package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.BattleCreationPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoreCampaignPluginImpl;
import data.kaysaar.aotd.vok.plugins.battle.TierFourStationBattleCreatorPlugin;

public class AoTDCoreCampaignPluginImpl extends CoreCampaignPluginImpl {
    @Override
    public PluginPick<BattleCreationPlugin> pickBattleCreationPlugin(SectorEntityToken opponent) {
        if(opponent instanceof CampaignFleetAPI fleet){
            for (FleetMemberAPI memberAPI : fleet.getFleetData().getMembersListCopy()) {
                if(memberAPI.isStation()){
                    if(memberAPI.getVariant().getHullSpec().hasTag("tier4station")){
                        return new PluginPick<>(new TierFourStationBattleCreatorPlugin(),PickPriority.MOD_SPECIFIC);
                    }
                }
            }
            if(fleet.getBattle()!=null){
                BattleAPI battle = fleet.getBattle();
                if(fleet.getBattle().getStationSide()!=null){
                    CampaignFleetAPI station = fleet.getBattle().getStationSide().stream().filter(CampaignFleetAPI::isStationMode).findAny().orElse(null);
                    if(station!=null){
                        if(station.getFleetData().getMembersListCopy().stream().filter(FleetMemberAPI::isStation).anyMatch(x->x.getHullSpec().hasTag("tier4station"))){
                            return new PluginPick<>(new TierFourStationBattleCreatorPlugin(),PickPriority.MOD_SPECIFIC);
                        }
                    }
                }

            }
        }
        return super.pickBattleCreationPlugin(opponent);
    }
}
