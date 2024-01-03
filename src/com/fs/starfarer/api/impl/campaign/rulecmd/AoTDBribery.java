package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDBribery extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        String arg = params.get(0).getString(memoryMap);
        if (arg.equals("proposeBribery")) {
            dialog.getOptionPanel().clearOptions();
            dialog.getTextPanel().addPara("");
            dialog.getOptionPanel().addOption("Pay 500.000 Credits.","aotd_bribery_suceed");
            dialog.getOptionPanel().addOption("Cut the comm link","cutCommLink");
            if(Global.getSector().getPlayerFleet().getCargo().getCredits().get()<500000){
                dialog.getOptionPanel().setEnabled("aotd_bribery_suceed",false);
            }
            return true;
        }
        return false;
    }

}
