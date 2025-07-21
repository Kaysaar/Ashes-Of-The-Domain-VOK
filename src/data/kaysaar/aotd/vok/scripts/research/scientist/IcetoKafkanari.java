package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

public class IcetoKafkanari extends ScientistAPI {
    public IcetoKafkanari(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    public void createSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Skill - Security Specialist", Misc.getHighlightColor(),5f);
        tooltip.addPara("Lowers Upkeep for all Black sites by %s",2f,Misc.getTooltipTitleAndLightHighlightColor(),Misc.getHighlightColor(),"50%");
    }
}
