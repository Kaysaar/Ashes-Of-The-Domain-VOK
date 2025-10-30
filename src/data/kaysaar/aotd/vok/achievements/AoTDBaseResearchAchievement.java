package data.kaysaar.aotd.vok.achievements;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import org.jetbrains.annotations.NotNull;
import org.magiclib.achievements.MagicAchievement;

import java.awt.*;
import java.util.ArrayList;

public class AoTDBaseResearchAchievement extends MagicAchievement {
    ArrayList<String>technologies = new ArrayList<>();
    boolean needsToResearchOnce = false;
    @Override
    public void onSaveGameLoaded(boolean isComplete) {
        super.onSaveGameLoaded(isComplete);
        if (isComplete) return;
        Global.getSector().getListenerManager().addListener(this, true);
    }

    @Override
    public void advanceAfterInterval(float amount) {
        boolean researchedAll = true;
        for (String technology : technologies) {
            if(!AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(technology)&&!needsToResearchOnce){
                researchedAll = false;
                break;
            }
            else{
                completeAchievement();
                return;
            }
        }
        if(technologies.isEmpty()){
            for (ResearchOption researchOption : AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchRepoOfFaction()) {
                if(researchOption.getSpec().getId().equals(AoTDTechIds.INDUSTRIAL_BASE_TEMPLATE)||researchOption.getSpec().getId().equals(AoTDTechIds.RUDIMENTARY_EQUIPMENT))continue;
                if(researchOption.isResearched()){
                    completeAchievement();
                    return;
                }
            }
        }
        else{
            if (researchedAll) {
                completeAchievement();
            }
        }

    }

    @Override
    public boolean hasTooltip() {
        return true;
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Global.getSector().getListenerManager().removeListener(this);
    }

    @Override
    public void createTooltip(@NotNull TooltipMakerAPI tooltipMakerAPI, boolean isExpanded, float width) {
        super.createTooltip(tooltipMakerAPI, isExpanded, width);
        if(!technologies.isEmpty()){
            tooltipMakerAPI.addSectionHeading("Current progress", Alignment.MID,0f);
            for (String technology : technologies) {
                Pair<String, Color>status = new Pair<>();
                if(!AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(technology)&&!isComplete()){
                    status.two = Misc.getNegativeHighlightColor();
                    status.one = "Not researched!";
                }
                else{
                    status.two = Misc.getPositiveHighlightColor();
                    status.one = "Researched!";

                }

                tooltipMakerAPI.addPara(AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(technology).getName() + " : %s",5f,status.two,status.one);
            }
        }

    }
}
