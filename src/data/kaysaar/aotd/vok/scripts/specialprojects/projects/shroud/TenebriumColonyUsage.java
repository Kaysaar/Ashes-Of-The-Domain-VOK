package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;

public class TenebriumColonyUsage extends ShroudBasedProject {
    public static ArrayList<String> TENEBRIUM_ITEMS = new ArrayList<>();
    public transient boolean hasGrantedRewardPerSave = false;
    static {
        TENEBRIUM_ITEMS.add("aotd_shrouded_nanoforge");
    }

    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain ability to produce Tenebrium Nanoforge, Tenebrium Catalyst and Tenebrium Refinement Matrix, new generation of colony items, that will heavily benefit us!", Misc.getPositiveHighlightColor(), 5f);

    }

    @Override
    public void doCheckForProjectUnlock() {
        super.doCheckForProjectUnlock();
        if(!hasGrantedRewardPerSave){
            if(wasCompleted){
                grantReward();
                hasGrantedRewardPerSave = true;
            }
        }

    }

    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return super.checkIfProjectShouldUnlock() && AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
    }

    @Override
    public Object grantReward() {
        for (SpecialItemSpecAPI allSpecialItemSpec : Global.getSettings().getAllSpecialItemSpecs()) {
            if(allSpecialItemSpec.hasTag("aotd_ignore_gp"))continue;
            if(allSpecialItemSpec.getManufacturer().equals("Abyss-Tech")){
                Global.getSector().getPlayerFaction().getMemory().set("$aotd" + allSpecialItemSpec.getId(), true);
            }
        }

        return null;
    }
}
