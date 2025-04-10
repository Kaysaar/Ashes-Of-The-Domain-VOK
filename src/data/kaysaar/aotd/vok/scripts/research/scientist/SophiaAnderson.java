package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionSpec;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import java.util.Map;

public class SophiaAnderson extends ScientistAPI {

    public SophiaAnderson(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public void advance(float amount) {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
        for (ResearchOption researchOption : manager.getResearchRepoOfFaction()) {
            if(researchOption.ReqItemsToResearchFirst!=null){
              ResearchOptionSpec spec= researchOption.getSpec();
                for (Map.Entry<String, Integer> entry : researchOption.ReqItemsToResearchFirst.entrySet()) {
                    if(Global.getSettings().getSpecialItemSpec(entry.getKey())==null){
                        if(entry.getKey().equals("research_databank")){
                            entry.setValue(spec.getReqItemsToResearchFirst().get(entry.getKey())-1);
                        }
                        else{

                            entry.setValue(spec.getReqItemsToResearchFirst().get(entry.getKey())-100);
                            if(entry.getValue()<=0){
                                entry.setValue(0);
                            }
                        }

                    }
                }
            }

        }
    }

    @Override
    public void createBiographyDescription(TooltipMakerAPI tooltip) {
        super.createBiographyDescription(tooltip);
    }

    @Override
    public void createSkillDescription(TooltipMakerAPI tooltip) {
       tooltip.addPara("Skill - Resourceful", Misc.getHighlightColor(),5f);
       tooltip.addPara("Decrease cost of %s by: %s",2f,Misc.getTooltipTitleAndLightHighlightColor(),Misc.getHighlightColor(),"Research Databanks","1");
    }

}
