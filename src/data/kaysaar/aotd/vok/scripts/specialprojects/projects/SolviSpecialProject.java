package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureSectionCompletedIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class SolviSpecialProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("uaf_supercap_slv_core").getHullNameWithDashClass(), Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return Global.getSector().getPlayerFaction().getMemory().is("$uaf_defeated_slvv", true);
    }



}
