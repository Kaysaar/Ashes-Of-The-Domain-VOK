package data.kaysaar.aotd.vok.scripts.research.scientist.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.ScientistAppearIntel;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.AoTDAiScientistEvent;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class ForbiddenScientistUnlock implements ScientistScriptUnlock {
    @Override
    public void run() {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
        if(manager.getAmountOfResearchFacilities()>0&&manager.haveResearched(AoTDTechIds.STREAMLINED_PRODUCTION)&&Global.getSector().getPlayerMemoryWithoutUpdate().is("$aotd_ai_core_good",true)&& Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist())!=null){
            Global.getSector().getPlayerMemoryWithoutUpdate().set("$aotd_ai_core_init",true);
            PersonAPI person = Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist());
            person.setRankId(Ranks.CITIZEN);
            person.setPostId(Ranks.POST_SCIENTIST);
            person.setImportance(PersonImportance.VERY_HIGH);
            Misc.makeImportant(person, "forbidden_scientist");
            ScientistAppearIntel intel = new ScientistAppearIntel(person);
            Global.getSector().getIntelManager().addIntel(intel);

        }
    }

    @Override
    public boolean shouldRun() {
        return !Global.getSector().getPlayerMemoryWithoutUpdate().is("$aotd_ai_core_init",true);
    }
}
