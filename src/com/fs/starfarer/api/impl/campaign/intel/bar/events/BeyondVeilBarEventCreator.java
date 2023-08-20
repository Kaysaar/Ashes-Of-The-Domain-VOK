package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;

public class BeyondVeilBarEventCreator extends  BaseBarEventCreator{

    public PortsideBarEvent createBarEvent() {
        return new BeyondVeilBarEvent();
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return 10000000000f; // one-time-only
    }

    @Override
    public float getBarEventFrequencyWeight() {

        return super.getBarEventFrequencyWeight()*30;
    }



}
