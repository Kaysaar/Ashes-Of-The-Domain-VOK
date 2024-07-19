package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;

public class AoTDCollabSpScript implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if(Misc.getCommissionFaction()!=null && Misc.getCommissionFaction().getId().equals(Factions.PIRATES)){
            Global.getSector().getPlayerFaction().getMemory().set("$pmm_aqq_champ",true);
            Global.getSector().removeTransientScript(this);
        }
    }
}
