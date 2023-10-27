package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import data.Ids.AodResearcherSkills;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;
import data.kaysaar_aotd_vok.scripts.research.ResearchAPI;

import java.util.List;
import java.util.Map;

public class OpScientistAgreed extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null) return false;
        if(!ruleId.equals("opScientistAgree"))return false;
        ResearchAPI researchAPI = AoDUtilis.getResearchAPI();
        if(researchAPI==null) return false;
        PersonAPI personAPI = Global.getSector().getImportantPeople().getPerson("opScientist");
        personAPI.getTags().add(AodResearcherSkills.SEEKER_OF_KNOWLEDGE);
        personAPI.addTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE);
        researchAPI.addResearchersInPossetion(personAPI);
        if(researchAPI.getCurrentResearcher()==null){
            researchAPI.setCurrentResearcher(personAPI);
        }
        Global.getSector().getMemory().set("$aotd_op_scientist",true);
        return true;

    }
}
