package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDPCFFailed extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        String arg = params.get(0).getString(memoryMap);
        if(arg.equals("removeCondition")){
            dialog.getInteractionTarget().getMarket().removeCondition("pre_collapse_facility");
            dialog.getInteractionTarget().getMarket().reapplyConditions();
            dialog.getInteractionTarget().getMarket().getMemory().set("$aotd_fac_explored",true);
            return true;
        }
        if(arg.equals("dismiss")){
            dialog.dismiss();
        }
        return false;
    }
}
