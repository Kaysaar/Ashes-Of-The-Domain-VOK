package data.listeners;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.graid.GroundRaidObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.graid.SpecialItemRaidObjectivePluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import data.scripts.research.items.VokDatabankRaid;

import java.util.List;
import java.util.Map;

public class VokDatabankGroundRaidCreator implements GroundRaidObjectivesListener {
    @Override
    public void modifyRaidObjectives(MarketAPI market, SectorEntityToken entity, List<GroundRaidObjectivePlugin> objectives, MarketCMD.RaidType type, int marineTokens, int priority) {
        if (priority != 0) return;
        if (market == null) return;
        if(type== MarketCMD.RaidType.VALUABLE){
            if(market.getMemory().contains("$aotd_vok_databank")){
                String databank = (String) market.getMemory().get("$aotd_vok_databank");
                SpecialItemData sid = new SpecialItemData("aotd_vok_databank_pristine",databank);
                Industry ind = market.getIndustry("vault_aotd");
                VokDatabankRaid vok = new VokDatabankRaid(market,
                        sid.getId(), sid.getData(), ind);
                objectives.add(vok);
            }
        }
    }

    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {

    }
}
