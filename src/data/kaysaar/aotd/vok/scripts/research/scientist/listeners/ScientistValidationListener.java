package data.kaysaar.aotd.vok.scripts.research.scientist.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionEra;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class ScientistValidationListener implements EconomyTickListener {
    
    @Override
    public void reportEconomyTick(int iterIndex) {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
        for (ResearchOption researchOption : manager.getResearchRepoOfFaction()) {
            if(researchOption.isResearched()){
                if(researchOption.Tier.equals(ResearchOptionEra.ADVANCED)){
                    Global.getSector().getMemory().set("$aotd_can_sophia",true);
                    Global.getSector().getMemory().set("$aotd_passed_validation"+this.getClass().getName(),true);
                    Global.getSector().getListenerManager().removeListenerOfClass(this.getClass());
                }
            }
        }
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
