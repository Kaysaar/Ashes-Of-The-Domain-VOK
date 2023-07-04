package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import data.plugins.AoDUtilis;
import data.scripts.research.ResearchAPI;

import java.util.List;
import java.util.Map;

public class SophiaAgreed extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null) return false;
        if(!ruleId.equals("aotdSophiaAgree"))return false;
        ResearchAPI researchAPI = AoDUtilis.getResearchAPI();
        if(researchAPI==null) return false;
        PersonAPI personAPI = Global.getSector().getImportantPeople().getPerson("sophia");
        researchAPI.addResearchersInPossetion(personAPI);
        researchAPI.setCurrentResearcher(personAPI);
        Global.getSector().getMemory().set("$aotd_sophia",true);
        return true;
    }
}
