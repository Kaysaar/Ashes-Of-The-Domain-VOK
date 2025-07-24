package data.kaysaar.aotd.vok.scripts.research.scientist.models;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class ScientistPerson {
    PersonAPI scientist;
    FactionAPI tiedToFaction;
    public ScientistPerson(PersonAPI person, FactionAPI tiedToFaction){
        this.scientist = person;
        this.tiedToFaction = tiedToFaction;
    }
    public int getMonthlySalary(){
        return 10000;
    }
    public void advance(float amount){

    }
    public void createActiveSkillDescription(TooltipMakerAPI tooltip){

    }

    public void applyPassiveSkill(){

    }
    public void unapplyPassiveSkill(){

    }
    public void applyActiveSkill(){

    }
    public void unapplyActiveSkill(){

    }
    public String getPassiveSkillName(){
        return "";
    }
    public String getActiveSkillName(){
        return "";
    }
    public void createPassiveSkillDescription(TooltipMakerAPI tooltip){

    }
    public void endOfMonth(){

    }
    public void createBiographyDescription(TooltipMakerAPI tooltip){
        tooltip.addPara(scientist.getNameString(), Color.cyan,5f);
    }

    public PersonAPI getScientistPerson() {
        return scientist;
    }
}
