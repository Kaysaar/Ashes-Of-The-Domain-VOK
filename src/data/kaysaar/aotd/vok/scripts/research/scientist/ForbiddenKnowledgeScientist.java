package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;

import java.awt.*;

public class ForbiddenKnowledgeScientist extends ScientistPerson {
    public ForbiddenKnowledgeScientist(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public String getPassiveSkillName() {
        return "Forbidden Knowledge";
    }

    @Override
    public String getActiveSkillName() {
        return "AI Assistant";
    }

    @Override
    public void applyPassiveSkill() {
        Global.getSector().getMemory().set("$aotd_experimental_tier", true);
    }

    @Override
    public void createPassiveSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Unlock Experimental Tier of Tech tree, bypassing requirement for Hyperdimensional Processor.", Misc.getTooltipTitleAndLightHighlightColor(),2f);
    }

    @Override
    public void createActiveSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Reduces amount of GP resources needed for projects by %s",2f,Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE,"20%");
    }

    @Override
    public int getMonthlySalary() {
        return 40000;
    }
}
