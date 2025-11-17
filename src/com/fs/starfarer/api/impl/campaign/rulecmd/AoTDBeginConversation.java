package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDBeginConversation extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        String id = null;
        PersonAPI person = null;

        Object o = dialog.getInteractionTarget();

        if (o != null) {
            person = ((CampaignFleetAPI) o).getCommander();

        }
        if(person==null)return false;
        dialog.getOptionPanel().clearOptions();
        if (person.getFaction().getId().equals(Factions.LUDDIC_PATH) || person.getFaction().getId().equals(Factions.LUDDIC_CHURCH)) {
            dialog.getTextPanel().addPara("Stop right there intruder. This place has been deemed to be infested with Moloch's abominations. We shall cleanse this planet from all of its influence. Do not interrupt our glorious duty!", Misc.getNegativeHighlightColor());
            dialog.getOptionPanel().addOption("Cut comm link", "cutCommLink");
        }
       else if (person.getFaction().getId().equals(Factions.INDEPENDENT)) {
            dialog.getTextPanel().addPara("We are currently conducting excavation operations on behalf of our contractor. Any disturbance to our operations will result in consequences.");
            dialog.getOptionPanel().addOption("What if we pay you more than your contractor?", "aotd_bribery_without_sp");
            dialog.getOptionPanel().addOption("Cut comm link", "cutCommLink");
        } else if (person.getFaction().getId().equals(Factions.REMNANTS)) {
            dialog.getTextPanel().addPara("We ... must [CORRUPTED] ... defend");
            dialog.getOptionPanel().addOption("Cut comm link", "cutCommLink");

        } else{
            dialog.getTextPanel().addPara("We are currently conducting excavation operations on behalf of "+person.getFaction().getDisplayName()+". Any disturbance in our operations will result in consequences.");
            dialog.getOptionPanel().addOption("Cut comm link", "cutCommLink");

        }

        return true;
    }
}




