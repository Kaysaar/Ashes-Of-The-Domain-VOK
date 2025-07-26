package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;

public class AoTDAiScientistEvent extends ScientistAICoreBarEvent{
    public static String getIDOfScientist(){
        return "aotd_scientist_forbidden";
    }
    @Override
    protected void doExtraConfirmActions() {
        person.setId(getIDOfScientist());
        Global.getSector().getImportantPeople().addPerson(person);


    }
}
