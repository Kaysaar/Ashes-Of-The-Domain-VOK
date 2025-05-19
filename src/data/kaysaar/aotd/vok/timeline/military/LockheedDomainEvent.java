package data.kaysaar.aotd.vok.timeline.military;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class LockheedDomainEvent extends FirstIndustryEvent {
    public LockheedDomainEvent( String entityId) {
        super(AoTDIndustries.ORBITAL_SKUNKWORK, entityId);
    }

    @Override
    public String getTitleOfEvent() {
        return "Lockheed Domain";
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.MILITARY;
    }
}
