package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;

public class IcetoKafkanari extends ScientistPerson {
    public IcetoKafkanari(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    public void applyActiveSkill() {
        AoTDMainResearchManager.getInstance().getSpecificFactionManager(getTiedToFaction()).getFacilities(AoTDIndustries.BLACK_SITE).forEach(x->x.getUpkeep().modifyMult("iceto",0.5f,"Iceto Kafkanari"));
    }

    @Override
    public void unapplyActiveSkill() {
        AoTDMainResearchManager.getInstance().getSpecificFactionManager(getTiedToFaction()).getFacilities(AoTDIndustries.BLACK_SITE).forEach(x->x.getUpkeep().unmodifyMult("iceto"));

    }

    @Override
    public int getMonthlySalary() {
        return 20000;
    }

    @Override
    public String getPassiveSkillName() {
        return "Hyperspace Topologist";
    }

    @Override
    public void createPassiveSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Unlocks Project : Storm Nullifier",Misc.getTooltipTitleAndLightHighlightColor(),2f);
    }

    @Override
    public String getActiveSkillName() {
        return "Security Specialist";
    }

    @Override
    public void createActiveSkillDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Lowers Upkeep for all Black sites by %s",2f,Misc.getTooltipTitleAndLightHighlightColor(),Misc.getHighlightColor(),"50%");
    }
}
