package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.HistorianBarEventCreator;

public class PreCollapseFacBarEventCreator  extends  BaseBarEventCreator {
    public PortsideBarEvent createBarEvent() {
        return new PreCollapseFacBarEvent();
    }

    @Override
    public boolean isPriority() {
        return Global.getSettings().isDevMode();
    }

    @Override
    public float getBarEventFrequencyWeight() {
        return super.getBarEventFrequencyWeight()*40;
    }

    public float getBarEventActiveDuration() {
        return 15f + (float) Math.random() * 15f;
    }

    public float getBarEventTimeoutDuration() {
        return Math.max(0, 30f - (float) Math.random() * 50f);
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return 30f + (float) Math.random() * 30f;
    }
}
