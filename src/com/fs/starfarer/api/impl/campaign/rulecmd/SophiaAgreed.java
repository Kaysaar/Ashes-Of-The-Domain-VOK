package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import data.kaysaar.aotd.vok.scripts.research.scientist.SophiaAnderson;

import java.util.List;
import java.util.Map;

public class SophiaAgreed extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null) return false;
        if(!ruleId.equals("aotdSophiaAgree"))return false;
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getSpecificFactionManager(Global.getSector().getPlayerFaction());
        ScientistPerson scientist = new SophiaAnderson(Global.getSector().getImportantPeople().getPerson("aotd_sophia"),manager.getFaction());
        manager.addScientist(scientist);
        if(manager.currentHeadOfCouncil==null){
            manager.currentHeadOfCouncil = scientist;
        }

        Global.getSector().getMemory().set("$aotd_can_sophia",false);
        return true;
    }
}
