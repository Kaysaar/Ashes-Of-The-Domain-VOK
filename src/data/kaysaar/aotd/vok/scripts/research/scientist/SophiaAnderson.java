package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionSpec;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;

import java.util.Map;

public class SophiaAnderson extends ScientistPerson {
    float daysSince =0f;

    public SophiaAnderson(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void applyActiveSkill() {
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
    public void unapplyActiveSkill() {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
        for (ResearchOption researchOption : manager.getResearchRepoOfFaction()) {
            if(researchOption.ReqItemsToResearchFirst!=null){
                ResearchOptionSpec spec= researchOption.getSpec();
                for (Map.Entry<String, Integer> entry : researchOption.ReqItemsToResearchFirst.entrySet()) {
                    if(Global.getSettings().getSpecialItemSpec(entry.getKey())==null){
                        if(entry.getKey().equals("research_databank")){
                            entry.setValue(spec.getReqItemsToResearchFirst().get(entry.getKey()));
                        }
                        else{

                            entry.setValue(spec.getReqItemsToResearchFirst().get(entry.getKey()));
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
    public void createActiveSkillDescription(TooltipMakerAPI tooltip) {
       tooltip.addPara("Decrease cost of %s by: %s",2f,Misc.getTooltipTitleAndLightHighlightColor(),Misc.getHighlightColor(),"Research Databanks","1");
    }

    @Override
    public String getPassiveSkillName() {
        return "Archivist";
    }

    @Override
    public String getActiveSkillName() {
        return "Resourceful";
    }

    @Override
    public void createPassiveSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Every two months, discover a new possible location of a Pre-Collapse Facility.", Misc.getTooltipTitleAndLightHighlightColor(), 2f);
    }
}
