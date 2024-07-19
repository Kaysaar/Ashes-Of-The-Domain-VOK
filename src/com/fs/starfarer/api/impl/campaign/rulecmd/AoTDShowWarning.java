package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDShowWarning extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getTextPanel().addPara("Warning, shields are unstable, and they should be repaired as soon as possible",Misc.getNegativeHighlightColor());
        return true;
    }
}
