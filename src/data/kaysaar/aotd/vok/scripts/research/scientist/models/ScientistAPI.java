package data.kaysaar.aotd.vok.scripts.research.scientist.models;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class ScientistAPI {
    PersonAPI scientist;
    FactionAPI tiedToFaction;
    public ScientistAPI(PersonAPI person,FactionAPI tiedToFaction){
        this.scientist = person;
        this.tiedToFaction = tiedToFaction;
    }
    public void advance(float amount){

    }
    public void createSkillDescription(TooltipMakerAPI tooltip){

    }
    public void createBiographyDescription(TooltipMakerAPI tooltip){
        tooltip.addPara(scientist.getNameString(), Color.cyan,5f);
    }

    public PersonAPI getScientistPerson() {
        return scientist;
    }
}
