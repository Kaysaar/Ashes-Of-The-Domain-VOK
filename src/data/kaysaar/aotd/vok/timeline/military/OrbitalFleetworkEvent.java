package data.kaysaar.aotd.vok.timeline.military;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class OrbitalFleetworkEvent extends FirstIndustryEvent {
    public OrbitalFleetworkEvent( String entityId) {
        super(AoTDIndustries.ORBITAL_FLEETWORK, entityId);
    }

    @Override
    public String getTitleOfEvent() {
        return "500 Onslaughts";
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.MILITARY;
    }
}
