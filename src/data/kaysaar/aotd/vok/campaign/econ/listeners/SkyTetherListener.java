package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class SkyTetherListener implements AoTDResearchListener{
    @Override
    public void finishedResearchOfTechnology(String id, FactionAPI faction) {
        if(faction.isPlayerFaction()&&id.equals("aotd_tech_daedalus")){
            Global.getSector().getPlayerFaction().getMemoryWithoutUpdate().set("$aotd"+ Items.FULLERENE_SPOOL,true);
        }
    }
}
