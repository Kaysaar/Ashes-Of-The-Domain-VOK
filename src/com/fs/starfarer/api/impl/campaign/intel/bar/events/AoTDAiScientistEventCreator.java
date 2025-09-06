package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;

public class AoTDAiScientistEventCreator extends ScientistAICoreBarEventCreator {
    @Override
    public PortsideBarEvent createBarEvent() {
        if(Global.getSector().getImportantPeople().getPerson(AoTDAiScientistEvent.getIDOfScientist())!=null) {
            return null;
        }

        return new AoTDAiScientistEvent();
    }


}
