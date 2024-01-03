package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;

public class BeyondVeilBarEventCreator extends BaseBarEventCreator {

    public PortsideBarEvent createBarEvent() {
        return new BeyondVeilBarEvent();
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return 10000000000f; // one-time-only
    }

    @Override
    public float getBarEventFrequencyWeight() {

        return super.getBarEventFrequencyWeight()*50;
    }



}
