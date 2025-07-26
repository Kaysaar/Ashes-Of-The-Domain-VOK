package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;

public class AoTDAiScientistEventCreator extends ScientistAICoreBarEventCreator {
    @Override
    public PortsideBarEvent createBarEvent() {
        return new AoTDAiScientistEvent();
    }

}
