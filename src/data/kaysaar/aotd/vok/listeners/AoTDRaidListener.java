package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.graid.GroundRaidObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.scripts.raids.SpecialProjectBlueprintPluginImpl;

import java.util.List;
import java.util.Map;

public class AoTDRaidListener implements GroundRaidObjectivesListener {
    @Override
    public void modifyRaidObjectives(MarketAPI market, SectorEntityToken entity, List<GroundRaidObjectivePlugin> objectives, MarketCMD.RaidType type, int marineTokens, int priority) {
        if (type == MarketCMD.RaidType.VALUABLE) {
            if(market.getMemory().is("$uaf_cherry_bp",true)&&market.getFactionId().equals("uaf")){
                market.getMemory().set("$bp_value","$uaf_cherry_bp");
                for (GroundRaidObjectivePlugin objective : objectives) {
                    if(objective instanceof SpecialProjectBlueprintPluginImpl){
                        return;
                    }
                }
                SpecialProjectBlueprintPluginImpl curr = new SpecialProjectBlueprintPluginImpl(market, AoTDItems.BASE_SHIP_BLUEPRINT,"uaf_cherry_core:$uaf_aqq_cherry");

                objectives.add(curr);
            }
            if(market.getMemory().is("$uaf_novaeria_bp",true)&&market.getFactionId().equals("uaf")){
                market.getMemory().set("$bp_value","$uaf_novaeria_bp");
                SpecialProjectBlueprintPluginImpl curr = new SpecialProjectBlueprintPluginImpl(market, AoTDItems.BASE_SHIP_BLUEPRINT,"uaf_novaeria:$uaf_aqq_nova");
                for (GroundRaidObjectivePlugin objective : objectives) {
                    if(objective instanceof SpecialProjectBlueprintPluginImpl){
                        return;
                    }
                }
                objectives.add(curr);
            }
        }
        }

    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {

    }
}
