package data.kaysaar.aotd.vok.timeline.prosperity;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class ResortCenterWorld extends FirstIndustryEvent {
    public ResortCenterWorld( String entityId) {
        super(AoTDIndustries.RESORT, entityId);
    }

    @Override
    public String getTitleOfEvent() {
        return "Jewel of the Sector";
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.PROSPERITY;
    }

    @Override
    public int getPointsForGoal() {
        return 40;
    }
}
