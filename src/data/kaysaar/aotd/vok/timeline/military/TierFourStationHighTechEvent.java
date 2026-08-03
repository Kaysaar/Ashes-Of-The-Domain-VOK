package data.kaysaar.aotd.vok.timeline.military;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class TierFourStationHighTechEvent extends FirstIndustryEvent {
    public TierFourStationHighTechEvent( String entityId) {
        super(AoTDIndustries.STAR_CITADEL_HIGH, entityId);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.MILITARY;
    }

    @Override
    public String getTitleOfEvent() {
        return "Culann's Shield";
    }

    @Override
    public int getPointsForGoal() {
        return 90;
    }
}
