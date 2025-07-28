package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.AoTDAiScientistEvent;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.ForbiddenKnowledgeScientist;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;

import java.util.List;
import java.util.Map;

public class AoTDForbiddenScientist extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = params.get(0).getString(memoryMap);
        if(arg.equals("markDecision")){
            Global.getSector().getPlayerMemoryWithoutUpdate().set("$aotd_ai_core_good",true);
        }
        if(arg.equals("grantScientist")){
            AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(Global.getSector().getPlayerFaction());
            ScientistPerson scientist = new ForbiddenKnowledgeScientist(Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist()),manager.getFaction());
            manager.addScientist(scientist);
            if(manager.currentHeadOfCouncil==null){
                manager.currentHeadOfCouncil = scientist;
            }
            dialog.getInteractionTarget().getMarket().getCommDirectory().removePerson(Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist()));
        }
        if(arg.equals("declineScientist")){
            dialog.getInteractionTarget().getMarket().getCommDirectory().removePerson(Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist()));
        }
        return true;
    }
}
