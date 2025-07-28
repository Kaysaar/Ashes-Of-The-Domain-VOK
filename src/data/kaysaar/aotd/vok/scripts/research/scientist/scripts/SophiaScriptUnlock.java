package data.kaysaar.aotd.vok.scripts.research.scientist.scripts;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionEra;

public class SophiaScriptUnlock implements ScientistScriptUnlock {

    @Override
    public void run() {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
        if(manager.getAmountOfResearchFacilities()>0){
            if(manager.getResearchRepoOfFaction().stream().filter(x->x.getSpec().getTier().equals(ResearchOptionEra.SOPHISTICATED)).anyMatch(ResearchOption::isResearched)){
                Global.getSector().getMemory().set("$aotd_can_sophia",true);
            }

        }
    }

    @Override
    public boolean shouldRun() {
        return !Global.getSector().getMemory().is("$aotd_can_sophia",true);
    }
}
