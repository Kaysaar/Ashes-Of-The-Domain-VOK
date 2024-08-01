package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.util.ArrayList;

public class CleanerScript implements EveryFrameScript {
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
        if(!Global.getSector().getMemory().is("$aotd_2.2.2_fix",true)){
            Global.getSector().getMemory().set("$aotd_2.2.2_fix",true);
            GPManager.getInstance().getProductionOrders().clear();
            Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).unmodifyMult("aotd_gp");
            ArrayList<EveryFrameScript> scriptToRemove = new ArrayList<>();
            for (EveryFrameScript script : Global.getSector().getScripts()) {
                if(script instanceof CleanerScript)continue;
                script.advance(1);
                if(Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).getMultBonus("aotd_gp")!=null){
                    scriptToRemove.add(script);
                    Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).unmodifyMult("aotd_gp");
                }
            }

                for (EveryFrameScript everyFrameScript : scriptToRemove) {
                    Global.getSector().removeScript(everyFrameScript);
                }

            GPManager.isEnabled=false;
            Global.getSector().getPersistentData().remove(GPManager.memkey);
            Global.getSector().removeTransientScriptsOfClass(this.getClass());
        }
    }
}
