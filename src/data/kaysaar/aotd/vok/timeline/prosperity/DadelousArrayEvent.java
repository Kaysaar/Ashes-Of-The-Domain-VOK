package data.kaysaar.aotd.vok.timeline.prosperity;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class DadelousArrayEvent extends FirstIndustryEvent {
    public DadelousArrayEvent( String entityId) {
        super(AoTDIndustries.DAEDALUS_ARRAY, entityId);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.PROSPERITY;
    }

    @Override
    public String getTitleOfEvent() {
        return "Too close to Sun";
    }

    @Override
    public int getPointsForGoal() {
        return 70;
    }
}
