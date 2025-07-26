package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.ForbiddenKnowledgeScientist;

import java.util.List;
import java.util.Map;

import static com.fs.starfarer.api.impl.campaign.intel.bar.events.AoTDAiScientistEvent.getIDOfScientist;

public class AoTDForbiddenScientist extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        ForbiddenKnowledgeScientist scientist = new ForbiddenKnowledgeScientist(Global.getSector().getImportantPeople().getPerson(getIDOfScientist()),Global.getSector().getPlayerFaction());
        AoTDMainResearchManager.getInstance().getManagerForPlayer().addScientist(scientist);
        return true;
    }
}
